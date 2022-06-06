package ClientWindow;

import Utils.Log;
import Utils.MainBody;
import Utils.RSA;
import Utils.Util;
import entity.Msg;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import pack.*;
import struct.JavaStruct;
import struct.StructException;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Description：客户端界面
 * Date:2022/5/18 19:32
 **/
public class ClientWindow extends Application {

    public Label onlineNumberLabel;

    public ListView userListView;

    public TextArea showTextArea;

    public TextField msgTextField;

//    private String IDv="192.168.43.75";
    private String IDv = "127.0.0.1";
    private String Kcv = "12345678";
    private String IDAS = "192.168.43.248";
    private String KeyCAS = "12345678";

    private static Socket socket;

    private String account = "";
    private String password = "";
    private String name = "";
    private byte[] ticketV = new byte[]{};
    //两个大质数的乘积n，公钥e，私钥d
    private BigInteger e = new BigInteger("65537");
    private static BigInteger[] ne = new BigInteger[2];//公钥
    private static BigInteger[] nd = new BigInteger[2];//私钥

    public void getKey(BigInteger[] publicKey,BigInteger[] privateKey ){
        privateKey = RSA.generateKey(e);    //两个大质数的乘积n、私钥d
        publicKey[0] = privateKey[0];
        publicKey[1] = e;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTicketV(byte[] ticketV) {
        this.ticketV = ticketV;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ClientWindow.fxml")));
        primaryStage.setTitle("聊天室客户端");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("信息.png")).toExternalForm()));
        primaryStage.setScene(new Scene(root,700,500));
        primaryStage.setResizable(false); //不可调整大小
        primaryStage.show();
        execute();
    }
    public void execute() {
        try {
            socket = new Socket(IDv,9999);
            new ReceiveMsg().start();   //开启接收消息线程
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }

    //发送消息
    public void send() throws NoSuchAlgorithmException, RSA.pqException, IOException, StructException, NullPointerException {
        long TS1 = System.currentTimeMillis();
        String content = msgTextField.getText();

        byte[] nameByte = name.getBytes(StandardCharsets.UTF_8);
        byte[] accountByte = account.getBytes(StandardCharsets.UTF_8);
        byte[] contentByte = content.getBytes(StandardCharsets.UTF_8);
        byte[] msgByte = Util.byteMerger(nameByte, accountByte, contentByte);
        //对消息内容使用md5算法得到哈希值
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] md = messageDigest.digest(msgByte);
        getKey(ne, nd);
        byte[] EpkcMD5 = RSA.encryption(md, ne[0], ne[1]);  //私钥签名
        PackageCtoVPublicMsgEkCV msgEkCV = new PackageCtoVPublicMsgEkCV(name, account, Long.toString(TS1), content, EpkcMD5);
        MainBody des = new MainBody(JavaStruct.pack(msgEkCV), Kcv, 1) ;    //DES加密
        byte[] EkCV = des.mainBody();
        int EkCVLen = EkCV.length;
        int totalLen = EkCVLen + ticketV.length;
        PackageCtoVPublicMsg packageCtoVPublicMsg = new PackageCtoVPublicMsg(totalLen, EkCV, ticketV);
        byte[] msg = JavaStruct.pack(packageCtoVPublicMsg);
        //封包完成，发送
        if (socket != null) {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg);
            outputStream.flush();   //强制把缓冲区内容输出
        } else {
            System.out.println("socket == null");
        }
        System.out.println("msg的长度为" + msg.length);
        Log.sendPackageLog("PackageVtoCPublicMsg",account,IDv);
        msgTextField.setText("");
    }
    //接收服务器消息的线程
    class ReceiveMsg extends Thread {
        public void run() {
            System.out.println("客户端接收消息线程已运行...");
            InputStream inputStream = null;
            while (this.isAlive()){
                byte[] receive;
                byte[] buffer = new byte[1024]; //缓冲区
                ArrayList<Byte> receivePackageTmp = new ArrayList<Byte>();// 临时保存接收到的数据包
                int len = -1;
                try {
                    inputStream = socket.getInputStream();
                    assert inputStream != null;
                    len = inputStream.read(buffer,0,1000);
                    for (int i = 0; i < len; i++) {
                        receivePackageTmp.add(buffer[i]);
                    }
                    receive = new byte[len];
                    for (int i = 0; i < len; i++) {
                        receive[i] =receivePackageTmp.get(i);
                    }
                    if (!Arrays.equals(receive, new byte[]{})){
                        System.out.println("接收到了消息...");
                        System.out.println(Arrays.toString(receive));
                        showMsg(receive);	//对收到的字节流进行处理
                    }
                    sleep(50);
                } catch (IOException | InterruptedException e) {
                    //e.printStackTrace();
                } catch (StructException structException) {
                    structException.printStackTrace();
                }
            }
        }
        //将收到的字节流转换...显示在showTextArea上
        //如果有多条消息则进行分割
        public void showMsg(byte[] received) throws StructException, UnsupportedEncodingException {
            byte[] pre = received;
            byte[] cur = received;
            while(cur.length >= 9) {
                byte[] totalLen_byte = new byte[4];
                int totalLen = 0;
                System.arraycopy(cur, 1, totalLen_byte, 0, 4);
                totalLen = new BigInteger(totalLen_byte).intValue();
                if(cur.length - 9 < totalLen){
                    break;
                }
                byte[] packVtoCPublicMsg = new byte[totalLen + 9];
                System.arraycopy(cur, 0, packVtoCPublicMsg, 0, totalLen + 9);
                PackageVtoCPublicMsg packageVtoCPublicMsg = new PackageVtoCPublicMsg(0, new byte[1]);
                JavaStruct.unpack(packageVtoCPublicMsg, packVtoCPublicMsg);
                //解密后展示在textArea上
                Msg msg = PackToEntity(packageVtoCPublicMsg);
                System.out.println(msg);
                //提示音
                Media media = new Media(new File("src/ClientWindow/notification.mp3").toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
                showTextArea.appendText(msg + "\n");

                cur = new byte[pre.length - 9 - totalLen];
                System.arraycopy(pre, 9 + totalLen, cur,0, pre.length - 9 - totalLen);
                pre = new byte[pre.length-9-totalLen];
                pre = cur;
            }
        }
        private Msg PackToEntity(PackageVtoCPublicMsg packageVtoCPublicMsg) throws UnsupportedEncodingException, StructException {
            byte[] EkCV = new MainBody(packageVtoCPublicMsg.EkCV, Kcv,0).mainBody();//解密
            PackageVtoCPublicMsgEkCV msgEkCV = new PackageVtoCPublicMsgEkCV("","","","",new byte[1]);
            JavaStruct.unpack(msgEkCV,EkCV);
            String name = String.valueOf(msgEkCV.SenderName);
            String id = String.valueOf(msgEkCV.SenderID);
            String content = String.valueOf(msgEkCV.Content);
            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdFormatter.format(Long.parseLong(String.valueOf(msgEkCV.TS)));

            return new Msg(name, id, content, time, new byte[]{});
        }
    }
}
