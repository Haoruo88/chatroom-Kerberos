package ClientWindow;

import Utils.Log;
import Utils.MainBody;
import Utils.RSA;
import Utils.Util;
import entity.Msg;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import pack.PackageCtoVPublicMsg;
import pack.PackageCtoVPublicMsgEkCV;
import pack.PackageVtoCPublicMsg;
import pack.PackageVtoCPublicMsgEkCV;
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

public class CWindow extends Application {

    Socket socket;

//    private String IDv = "192.168.43.75";
    private String IDv = "192.168.43.38";
    private String Kcv = "12345678";
    private String IDAS = "192.168.43.248";
    private String KeyCAS = "12345678";
    //UI控件
    private Label welComeLabel = new Label();
    private Label showLabel =  new Label();
    private TextArea showTextArea = new TextArea();
    private TextArea showSignatureArea = new TextArea();
    private TextField msgTextField = new TextField();
    private Button sendButton = new Button("发送");

    private String account = "1111111111";
    private String password = "";
    private String name = "";
    private byte[] ticketV = new byte[]{};

    //两个大质数的乘积n，公钥e，私钥d
    private BigInteger e = new BigInteger("65537");
    private static BigInteger[] ne = new BigInteger[2];
    private static BigInteger[] nd = new BigInteger[2];

    public CWindow(){}
    public CWindow(String name,String account, String password, byte[] ticketV) {
        this.name = name;
        this.account = account;
        this.password = password;
        this.ticketV = ticketV;
    }
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
        //欢迎标签
        welComeLabel.setLayoutX(14.0);
        welComeLabel.setLayoutY(14.0);
        welComeLabel.setPrefHeight(20.0);
        welComeLabel.setPrefWidth(227.0);
        welComeLabel.setText("欢迎你，" + name + "(" + account + ")");
        //数字签名展示标签
        showLabel.setLayoutX(592.0);
        showLabel.setLayoutY(14.0);
        showLabel.setPrefHeight(20.0);
        showLabel.setPrefWidth(353.0);
        showLabel.setText("数字签名展示：ID + Content + Digital Signature");
        //消息展示框
        showTextArea.setLayoutX(14.0);
        showTextArea.setLayoutY(46.0);
        showTextArea.setPrefHeight(337.0);
        showTextArea.setPrefWidth(546.0);
        showTextArea.setEditable(false);
        //数字签名展示
        showSignatureArea.setLayoutX(592.0);
        showSignatureArea.setLayoutY(46.0);
        showSignatureArea.setPrefHeight(451.0);
        showSignatureArea.setPrefWidth(507.0);
        showSignatureArea.setEditable(false);
        //消息输入框
        msgTextField.setLayoutX(14.0);
        msgTextField.setLayoutY(402.0);
        msgTextField.setPrefHeight(93.0);
        msgTextField.setPrefWidth(546.0);
        msgTextField.setPromptText("在此输入文本消息");
        msgTextField.setAlignment(Pos.TOP_LEFT);
        //发送按钮
        sendButton.setLayoutX(510.0);
        sendButton.setLayoutY(503.0);

        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);

        //发送消息按钮事件
        sendButton.setOnAction(event -> {
            try {
                long TS1 = System.currentTimeMillis();
                String content = msgTextField.getText();

                byte[] nameByte = name.getBytes(StandardCharsets.UTF_8);
                byte[] accountByte = account.getBytes(StandardCharsets.UTF_8);
                byte[] contentByte = content.getBytes(StandardCharsets.UTF_8);
                byte[] msgByte = Util.byteMerger(nameByte, accountByte, contentByte);
                //对消息内容使用md5算法得到哈希值并生成私钥签名
                MessageDigest messageDigest = null;
                messageDigest = MessageDigest.getInstance("MD5");
                byte[] md = messageDigest.digest(msgByte);

                BigInteger[] privateKey = new BigInteger[2];
                Util.readPrivateKey(privateKey,account);    //获取私钥
                byte[] EpkcMD5 = RSA.encryption(md, privateKey[0], privateKey[1]);  //生成私钥签名
                PackageCtoVPublicMsgEkCV msgEkCV = new PackageCtoVPublicMsgEkCV(name, account, Long.toString(TS1), content, EpkcMD5);
                MainBody des = new MainBody(JavaStruct.pack(msgEkCV), Kcv, 1) ;    //DES加密
                byte[] EkCV = des.mainBody();
                int EkCVLen = EkCV.length;
                int totalLen = EkCVLen + ticketV.length;
                PackageCtoVPublicMsg packageCtoVPublicMsg = new PackageCtoVPublicMsg(totalLen, EkCV, ticketV);
                byte[] msg = JavaStruct.pack(packageCtoVPublicMsg);
                //打印日志
                Log.PackageCtoVMsgContent(msgEkCV,ticketV,EkCV,0);//明文
                Log.PackageCtoVMsgContent(msgEkCV,ticketV,EkCV,1);//密文
                //封包完成，发送
                if (socket != null) {
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(msg);
                    outputStream.flush();   //强制把缓冲区内容输出
                    //打印日志
                    Log.sendPackageLog("PackageCtoVPublicMsg",account,IDv);
                } else {
                    System.out.println("socket == null");
                }
                System.out.println("msg的长度为" + msg.length);
                msgTextField.setText("");
            } catch (NoSuchAlgorithmException | RSA.pqException | IOException | StructException noSuchAlgorithmException) {
                noSuchAlgorithmException.printStackTrace();
            }
        });
        //Enter快捷键发送
        scene.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode() == KeyCode.ENTER) {
                ke.consume();
            }
        });

        execute();

        pane.getChildren().addAll(welComeLabel,showTextArea,msgTextField,sendButton,showSignatureArea,showLabel);
        pane.setPrefSize(1114.0,582.0);
        primaryStage.setTitle("聊天室客户端");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("信息.png")).toExternalForm()));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void stop(){
        try {
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    public void execute() {
        try {
            socket = new Socket(IDv,9999);
            new ReceiveMsg().start();   //开启接收消息线程
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    //接收服务器消息
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
                        Log.receivePackageLog("PackageVtoCPublicMsg",IDv,"客户端IDc：" + account);
                        showMsg(receive);	//对收到的字节流进行处理
                    }
                    sleep(50);
                } catch (IOException | InterruptedException e) {
                    //e.printStackTrace();
                } catch (StructException | NoSuchAlgorithmException structException) {
                    structException.printStackTrace();
                }
            }
        }
        //将收到的字节流转换...显示在showTextArea上
        //如果有多条消息则进行分割
        public void showMsg(byte[] received) throws StructException, UnsupportedEncodingException, NoSuchAlgorithmException {
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
                //提示音
                Media media = new Media(new File("src/ClientWindow/notification.mp3").toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
                //展示
                showTextArea.appendText(msg + "\n");
                showSignatureArea.appendText(Objects.requireNonNull(msg).getSenderId() + ": " + msg.getContent() + Arrays.toString(msg.getDigitalSignature()) + "\n");

                cur = new byte[pre.length - 9 - totalLen];
                System.arraycopy(pre, 9 + totalLen, cur,0, pre.length - 9 - totalLen);
                pre = new byte[pre.length - 9 - totalLen];
                pre = cur;
            }
        }
        //验证数字签名 并将消息转化为Msg实例对象
        private Msg PackToEntity(PackageVtoCPublicMsg packageVtoCPublicMsg) throws UnsupportedEncodingException, StructException, NoSuchAlgorithmException {
            byte[] EkCV = new MainBody(packageVtoCPublicMsg.EkCV, Kcv,0).mainBody();//解密
            PackageVtoCPublicMsgEkCV msgEkCV = new PackageVtoCPublicMsgEkCV("","","","",new byte[1]);
            JavaStruct.unpack(msgEkCV,EkCV);
            String name = String.valueOf(msgEkCV.SenderName);
            String id = String.valueOf(msgEkCV.SenderID);
            String content = String.valueOf(msgEkCV.Content);
            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdFormatter.format(Long.parseLong(String.valueOf(msgEkCV.TS)));
            byte[] mdEncrypted = msgEkCV.EscMd5;//数字签名
            //打印日志
            Log.PackageVtoCMsgContent(msgEkCV,packageVtoCPublicMsg.EkCV,0);//明文
            //验证数字签名
            byte[] nameByte = name.getBytes(StandardCharsets.UTF_8);
            byte[] accountByte = id.getBytes(StandardCharsets.UTF_8);
            byte[] contentByte = content.getBytes(StandardCharsets.UTF_8);
            byte[] msgByte = Util.byteMerger(nameByte, accountByte, contentByte);
            MessageDigest messageDigest = null;
            messageDigest = MessageDigest.getInstance("MD5");
            byte[] md = messageDigest.digest(msgByte);//得到哈希值
            BigInteger[] publicKey = new BigInteger[2];
            Util.readPublicKey(publicKey,id);   //得到公钥
            byte[] mdDecrypted = RSA.decryption(mdEncrypted, publicKey[0], publicKey[1]);//对数字签名进行解密
            if (Arrays.equals(md, mdDecrypted)) {//验证哈希值
                return new Msg(name, id, content, time, mdEncrypted);
            }
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
