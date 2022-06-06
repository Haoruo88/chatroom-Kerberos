import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.sql.*;
import struct.JavaStruct;
import struct.StructException;

import des.MainBody;
import pack.*;
import log.*;

public class AS implements Runnable{
    private Socket socket;// 连接客户端返回的socket对象
    private byte[] receivePackage;// 接收到的数据包
    private final static String keyCAS = "12345678";// C和AS会话的des密钥
    private final static String keyCTGS = "12345678";// C和TGS会话的des密钥
    private final static String keyASTGS = "12345678";// AS和TGS会话的des密钥
    private final static String ipTGS = "192.168.43.38";// tgs的ip地址
    private final static String ipAS = "192.168.43.248";// ASIP地址

    public AS(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8889);
            System.out.println("Authentication Server is running...");
            while (true) {
                Socket socket = serverSocket.accept();// 等待连接
                new Thread(new AS(socket)).start();// 每有一个连接新建一个线程处理
            }
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        ArrayList<Byte> receivePackageTmp = new ArrayList<Byte>();// 临时保存接收到的数据包
        int len = 0;
        try {
            InputStream inputStream = socket.getInputStream();
            while ((len = inputStream.read(buffer)) != -1) {// 获得所有接收到的数据临时保存在动态数组中
                for (int i = 0; i < len; i++) {
                    receivePackageTmp.add(buffer[i]);
                }
            }
            receivePackage = new byte[receivePackageTmp.size()];
            for (int i = 0; i < receivePackageTmp.size(); i++) {// 动态数组转化为静态数组
                receivePackage[i] = receivePackageTmp.get(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte status = receivePackage[0];// 根据状态位进入相应的处理函数
        switch (status) {
            case 0:
                handleRegister();
                break;
            case 1:
                handleLogin();
                break;
            default:
                break;
        }
    }


    //注册
    private void handleRegister() {
        try {
            PackageCtoAsRegister packageCtoAsRegister = new PackageCtoAsRegister(new byte[1]);// 解第一层包
            JavaStruct.unpack(packageCtoAsRegister, receivePackage, ByteOrder.BIG_ENDIAN);
            byte[] decryptedData = new MainBody(packageCtoAsRegister.EKc, keyCAS, 0).mainBody();// 解密
            PackageCtoAsRegisterEkc packageCtoAsRegisterEkc = new PackageCtoAsRegisterEkc("", "", "",new byte[1]);// 解第二层包
            JavaStruct.unpack(packageCtoAsRegisterEkc, decryptedData, ByteOrder.BIG_ENDIAN);

            String id = String.valueOf(packageCtoAsRegisterEkc.IDc);
            String name = String.valueOf(packageCtoAsRegisterEkc.name);
            String ts1 = String.valueOf(packageCtoAsRegisterEkc.TS1);
            byte[] pwd = packageCtoAsRegisterEkc.content;
            Log.receivePackageLog("Registration package", socket.getInetAddress().getHostAddress(), ipAS,id,ts1,name,pwd);// 记录收包日志

            PreparedStatement preSta = operateDataBase("select * from Table_1 where id=?");
            preSta.setString(1, id);
            ResultSet res = preSta.executeQuery();// 执行查询，返回结果集对象
            while (res.next()) {// 注册失败，返回注册失败包
                PackageAstoCRegister packageAstoCRegister = new PackageAstoCRegister((byte) 0);// 封包
                byte[] returnData = JavaStruct.pack(packageAstoCRegister, ByteOrder.BIG_ENDIAN);
                socket.getOutputStream().write(returnData);// 发包
                socket.shutdownOutput();
                Log.sendPackageLog("AS Reg_fail package", ipAS, socket.getInetAddress().getHostAddress());// 记录发包日志
                return;
            }

            PackageAstoCRegister packageAstoCRegister = new PackageAstoCRegister((byte) 1);// 封包
            byte[] returnData = JavaStruct.pack(packageAstoCRegister, ByteOrder.BIG_ENDIAN);
            socket.getOutputStream().write(returnData);// 发包
            socket.shutdownOutput();
            preSta = operateDataBase("insert into Table_1(id,pwd,name) values(?,?,?)");
            preSta.setString(1, id);// 预处理添加数据
            preSta.setBytes(2, pwd);
            preSta.setString(3, name);
            preSta.executeUpdate();// 数据库添加用户注册的账号、密码

            Log.sendPackageLog("AS Reg_suc package", ipAS, socket.getInetAddress().getHostAddress());// 记录发包日志
        } catch (StructException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 登录
    private void handleLogin() {
        try {
            PackageCtoAsLogin packageCtoAsLogin = new PackageCtoAsLogin(new byte[1]);// 解第一层包
            JavaStruct.unpack(packageCtoAsLogin, receivePackage, ByteOrder.BIG_ENDIAN);
            byte[] decryptedData = new MainBody(packageCtoAsLogin.Ekc, keyCAS, 0).mainBody();// 解密
            PackageCtoAsLoginEkc packageCtoAsLoginEkc = new PackageCtoAsLoginEkc("", "", "", new byte[1]);// 解第二层包
            JavaStruct.unpack(packageCtoAsLoginEkc, decryptedData, ByteOrder.BIG_ENDIAN);

            String idc = String.valueOf(packageCtoAsLoginEkc.IDc);
            String iptgs = String.valueOf(packageCtoAsLoginEkc.IDtgs);
            String TS = String.valueOf(packageCtoAsLoginEkc.TS1);
            byte[] pwd = packageCtoAsLoginEkc.content;
            Log.receivePackageLog("AS login authentication package", socket.getInetAddress().getHostAddress(), ipAS,idc,iptgs,pwd,TS);

            PreparedStatement preSta = operateDataBase("select * from Table_1 where id=? and pwd=?");
            preSta.setString(1, idc);
            preSta.setBytes(2, pwd);
            ResultSet res = preSta.executeQuery();// 执行查询，返回结果集对象

            while (res.next()) {// 登录成功，返回登录成功包
                String ipc = socket.getInetAddress().getHostAddress();
                String idTgs = String.valueOf(packageCtoAsLoginEkc.IDtgs);
                String ts = System.currentTimeMillis() + "";
                String name = res.getString("name");


                TicketTgs ticketTgs = new TicketTgs(keyCTGS, idc, ipc, idTgs, ts, ts);// 封第一层包，访问tgs的票据
                byte[] ticketPack = JavaStruct.pack(ticketTgs, ByteOrder.BIG_ENDIAN);
                byte[] ticketEe = new MainBody(ticketPack, keyASTGS, 1).mainBody();// 加密tgs票据
                PackageAstoCAuthEkc packageAstoCAuthEkc = new PackageAstoCAuthEkc(keyCTGS, ipTGS, ts, ts, ticketEe, name);// 封第二层包
                byte[] authAkcPack = JavaStruct.pack(packageAstoCAuthEkc, ByteOrder.BIG_ENDIAN);
                byte[] authAkcPackEn = new MainBody(authAkcPack, keyCAS, 1).mainBody();// 加密第二层包
                PackageAstoCAuth packageAstoCAuth = new PackageAstoCAuth(authAkcPackEn);// 封第三层包
                byte[] returnData = JavaStruct.pack(packageAstoCAuth, ByteOrder.BIG_ENDIAN);
                socket.getOutputStream().write(returnData);
                socket.shutdownOutput();
                Log.sendPackageLog("log_succ", ipAS, socket.getInetAddress().getHostAddress(), packageAstoCAuthEkc);
                Log.sendPackageLog("log_succ", ipAS, socket.getInetAddress().getHostAddress(), packageAstoCAuth);
                return;
            }

            PackageAstoCLogin packageAstoCLogin = new PackageAstoCLogin((byte) 0);// 封包。登录失败，返回登录失败包
            byte[] returnData = JavaStruct.pack(packageAstoCLogin, ByteOrder.BIG_ENDIAN);
            socket.getOutputStream().write(returnData);
            socket.shutdownOutput();
            Log.sendPackageLog("log_fail", ipAS, socket.getInetAddress().getHostAddress());
        } catch (StructException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private PreparedStatement operateDataBase(String sql) {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://localhost:1433;DatabaseName=AS";
            String user = "sa";
            String password = "12345678";
            Connection con = DriverManager.getConnection(url, user, password);// 连接数据库
            PreparedStatement preSta = con.prepareStatement(sql);
            return preSta;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
