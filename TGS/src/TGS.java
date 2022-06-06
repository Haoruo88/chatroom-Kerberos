import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import des.MainBody;
import pack.Authenticator;
import pack.PackageCtoTgs;
import pack.PackageTgstoC;
import pack.PackageTgstoCEkCTgs;
import pack.TicketTgs;
import pack.TicketV;
import struct.JavaStruct;
import struct.StructException;
import log.Log;

public class TGS implements Runnable {
        private Socket socket2;
        private static byte[] packageReceived;
        static String KEY = "12345678";
        private String IdTgs;
        private String KeyClientServer;
        private String IdServer;
        private String Lifetime4 = "1000s";
        private String TimeStamp4;
        private String TimeStamp;

        public TGS(Socket socket) {
                this.socket2 = socket;
                this.IdTgs = "192.168.43.205"; // TGS服务器IP地址
                this.KeyClientServer = "12345678"; // Client和Server共享的会话密钥
                this.IdServer = "192.168.43.38"; // 服务器IP地址
        }

        @Override
        public void run() {
                int len = 0, TmpArraySize = 0;
                byte[] buffer = new byte[1024];
                ArrayList<Byte> TmpArray = new ArrayList<Byte>();

                try {
                        while ((len = socket2.getInputStream().read(buffer)) != -1) { // 将套接字中的内容读到buffer中
                                System.out.println("len = " + len);
                                for (int i = 0; i < len; i++) {
                                        TmpArray.add(buffer[i]); // ArrayList类的add方法将buffer中的数据添加到ArrayList
                                }
                        }

                        TmpArraySize = TmpArray.size();
                        packageReceived = new byte[TmpArraySize];
                        for (int i = 0; i < TmpArraySize; i++) {
                                packageReceived[i] = TmpArray.get(i);
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }

                PackageCtoTgs packagectotgs = new PackageCtoTgs("", new byte[] { (byte) 0 }, new byte[] { (byte) 0 });
                try {
                        byte[] TGS_ticket = null;
                        byte[] TGS_authenticator = null;
                        TicketTgs tickettgs = new TicketTgs("", "", "", "", "", "");
                        Authenticator authenticator = new Authenticator("", "", "");

                        // 解整个包
                        JavaStruct.unpack(packagectotgs, packageReceived); // 将packageReceived字节流解包成packageCtoTgs结构体形式

                        // 提取packagectotgs中的TicketTgs部分并解密到字符数组TGS_ticket中
                        TGS_ticket = new MainBody(packagectotgs.TicketTgs, KEY, 0).mainBody(); // TGS解密从Client发送过来的包的票据部分
                        // 解字符数组TGS_ticket到结构体tickettgs中
                        JavaStruct.unpack(tickettgs, TGS_ticket); // 将解密后的票据部分的字节流形式解包成TicketTgs结构体形式

                        // TGS解密从Client发送过来的包的Authenticator部分
                        TGS_authenticator = new MainBody(packagectotgs.Authenticator,
                                        new String(tickettgs.KeyClientTgs), 0)
                                        .mainBody();
                        // 解字符数组TGS_authenticator到结构体authenticator中
                        JavaStruct.unpack(authenticator, TGS_authenticator);

                        // Log.PrintPackageCToTgs(packagectotgs.IdServer, tickettgs, authenticator);
                        System.out.println("服务器的标识(服务器的网络地址)：" + String.valueOf(IdServer) + "\n"
                                        + "PackageCToTgs --> Ticket:\n" +
                                        "AS生成的C与Tgs的会话密钥：" + String.valueOf(tickettgs.KeyClientTgs) + "\n"
                                        + "Client的标识(账号)："
                                        + String.valueOf(tickettgs.IdClient) + "\n" +
                                        "Client的网络地址：" + String.valueOf(tickettgs.AddressClient) + "\n"
                                        + "Tgs的标识(Tgs的网络地址)："
                                        + String.valueOf(tickettgs.IdTgs) + "\n" + "时间戳(票据签发时间)："
                                        + String.valueOf(tickettgs.TimeStamp) + "\n"
                                        + "票据的有效期："
                                        + String.valueOf(tickettgs.Lifetime) + "\n"
                                        + "PackageCToTgs --> Authenticator:\n"
                                        + "Client的标识(账号)：" + String.valueOf(authenticator.IdClient) + "\n"
                                        + "Client的网络地址："
                                        + String.valueOf(authenticator.AddressClient) +
                                        "\n" + "Client生成的时间戳：" + String.valueOf(authenticator.TimeStamp) + "\n");

                        if (Arrays.equals(tickettgs.IdClient, authenticator.IdClient)
                                        && Arrays.equals(tickettgs.AddressClient, authenticator.AddressClient))// 比对票据和认证中的信息是否核对成功，成功则发送包
                        {
                                //Log.AuthLog(IdTgs, 0, String.valueOf(tickettgs.IdClient));
                                System.out.println("客户端：" + String.valueOf(tickettgs.IdClient) + " 与 TGS：" + IdTgs);

                                System.out.println("用户认证成功!");

                                Date date1 = new Date(); // 获取当前时间(返回自1970年1月1日零时以来的毫秒数)
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                TimeStamp = sdf.format(date1); // 格式化时间

                                // 打包票据并加密
                                TicketV ticketv = new TicketV(KeyClientServer, String.valueOf(tickettgs.IdClient),
                                                String.valueOf(tickettgs.AddressClient), IdServer, TimeStamp,
                                                Lifetime4);
                                byte[] ticketv_pack = JavaStruct.pack(ticketv);
                                byte[] ticketv_encrypt = new MainBody(ticketv_pack, KEY, 1).mainBody();

                                Date date2 = new Date();
                                TimeStamp4 = sdf.format(date2);

                                // 打包(转换成字节流形式)整包并加密
                                PackageTgstoCEkCTgs package_tgs_to_c_encrypt_key_client_tgs = new PackageTgstoCEkCTgs(
                                                KeyClientServer, IdServer, TimeStamp4, ticketv_encrypt);

                                byte[] package_tgs_to_c_pack = JavaStruct.pack(package_tgs_to_c_encrypt_key_client_tgs);
                                byte[] data_encrypt = new MainBody(package_tgs_to_c_pack,
                                                String.valueOf(tickettgs.KeyClientTgs), 1)
                                                .mainBody();

                                // 加上状态位打包整包准备发送
                                PackageTgstoC package_send = new PackageTgstoC(data_encrypt);
                                // System.out.println("status =" + package_send.status + "EncryptKeyClientTgs"
                                // + String.valueOf(package_send.EncryptKeyClientTgs));
                                byte[] package_pack = JavaStruct.pack(package_send);

                                // System.out.println(Arrays.toString(package_pack));
                                // Log.PrintPackageTgsToC(ticketv, package_send);
                                System.out.println("PackageTgsToC --> Ticketv:\n" +
                                                "Tgs生成的Client与Server的会话密钥：" + String.valueOf(ticketv.KeyClientServer)
                                                + "\n" +
                                                "Server的标识(Server的网络地址)：" + String.valueOf(ticketv.IdServer) + "\n" +
                                                "Client的标识(账号)：" + String.valueOf(ticketv.IdClient) + "\n" +
                                                "时间戳(票据签发时间)：" + String.valueOf(ticketv.TimeStamp) + "\n" +
                                                "票据的有效期：" + String.valueOf(ticketv.Lifetime) + "\n" +
                                                "加密后的报文内容(二进制)：" + package_send.EncryptKeyClientTgs);

                                socket2.getOutputStream().write(package_pack);
                                socket2.shutdownOutput();
                        } else {
                                System.out.println("客户端：" + String.valueOf(tickettgs.IdClient) + " 与 TGS：" + IdTgs);
                                //log.Log.AuthLog(IdTgs, 1, String.valueOf(tickettgs.IdClient));
                                System.out.println("非法入侵！");
                        }
                } catch (IOException | StructException e) {
                        e.printStackTrace();
                }
        }

        public static void main(String[] args) throws Exception {
                ServerSocket serverSocket;
                try {
                        serverSocket = new ServerSocket(8888); // 绑定到端口8888的TGS服务器套接字
                        System.out.println("TGS服务器启动");
                        while (true) {
                                Socket socket = serverSocket.accept();
                                System.out.println("用户已连接");
                                new Thread(new TGS(socket)).start(); // start()会调用重写的run方法
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}