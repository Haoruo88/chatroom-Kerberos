package LogRegWindow;

import ClientWindow.ClientWindow;
import ClientWindow.CWindow;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import Utils.*;
import pack.*;
import struct.JavaStruct;

public class LogRegWindow extends Application {
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField accountTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Label accountAlertLabel;
    @FXML
    private Label passwordAlertLabel;
    @FXML
    private TextArea textArea;

    private final String IpAS = "192.168.43.248";    //AS服务器的IP地址
    private final String IDtgs= "192.168.43.205";   //TGS服务器标识(选用ip地址)
    private final String IDv = "192.168.43.38";     //V服务器标识(选用ip地址)
    private final String KeyCAS = "12345678";   //C-AS的对话密钥
    private String KeyCTgs;             //C-TGS的对话密钥
    private String KeyCV;               //C-V的对话密钥
    private byte[] ticketTgs;           //TGS票据
    private byte[] ticketV;             //V票据
//    private final String ADc = "192.168.43.38"; //Client的IP地址(防止非法冒用)
    private String ADc;  //客户端ip地址

    private String name;   //昵称

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LogRegWindow.fxml")));
        primaryStage.setTitle("公共聊天室");
        primaryStage.getIcons().add(new Image(getClass().getResource("信息.png").toExternalForm()));
        primaryStage.setScene(new Scene(root,1500,800));
//        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    /*
     *登录
     */
    public void login() throws Exception {
        String account = accountTextField.getText();
        String password = passwordField.getText();
        if(account.length() != 10){
            accountAlertLabel.setText("账号长度需为10位！");
        }
        if(account.length() == 10){
            accountAlertLabel.setText("");
        }
        if(password.length()<6 || password.length() > 16){
            passwordAlertLabel.setText("请输入6-16位密码！");
        }
        if((password.length() >= 6) && (password.length() <= 16)){
            passwordAlertLabel.setText("");
        }
        if((account.length() == 10) && (password.length() >= 6)&&(password.length() <= 16)){
            for(;;) {
                if(auth(account, password)) {
                    Log.loginLog(1,account);
                    enterMainWindow(account,password);
                    break;
                } else {
                    Log.loginLog(0,account);
                    JOptionPane.showMessageDialog(null,"登录失败");
                    break;
                }
            }
        }
    }
    /*
     *注册->打开注册窗口
     */
    public void register(ActionEvent actionEvent) throws Exception {
        RegisterWindow registerWindow = new RegisterWindow();
        registerWindow.start(new Stage());
    }
    /*
     *登录成功后打开主页面
     */
    public void enterMainWindow(String account,String pwd) throws Exception {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText("登录成功，欢迎用户 ID：" + account);
        alert.showAndWait();
        //打开客户端页面
        CWindow cWindow = new CWindow(name, account, pwd, ticketV);
        cWindow.start(new Stage());
        //不关闭登录页面
//        Stage primaryStage = (Stage)loginButton.getScene().getWindow();
//        primaryStage.close();
    }
    /*
     *认证：C ——> AS -> TGS -> V
     */
    public Boolean auth(String account, String passwd) throws Exception {
        ADc = InetAddress.getLocalHost().getHostAddress();
        Long TS1 = System.currentTimeMillis();    //TS时间戳
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        PackageCtoAsLoginEkc pcale = new PackageCtoAsLoginEkc(account, IDtgs, TS1.toString(), md5.digest(passwd.getBytes(StandardCharsets.UTF_8)));
        byte[] pcalePack = JavaStruct.pack(pcale);
        byte[] pacalePackE = new MainBody(pcalePack,KeyCAS,1).mainBody();
        PackageCtoAsLogin pcal = new PackageCtoAsLogin(pacalePackE);
        byte[] pcalPack = JavaStruct.pack(pcal);
        //明文显示——CtoAS
        textArea.appendText("PackageCtoAsLogin明文内容：\n   IDc(账号)：" + String.valueOf(pcale.IDc) + " IDtgs" + String.valueOf(pcale.IDtgs)
                + " TS(时间戳)：" + String.valueOf(pcale.TS1) + " password(MD5加密)：" + Arrays.toString(Arrays.toString(pcale.content).getBytes(StandardCharsets.UTF_8)));
        textArea.appendText("\nPackageCtoAsLogin密文内容：\n   " + Arrays.toString(pacalePackE));
        //打印日志
        Log.PackageCtoAsLoginContent(pcale, pacalePackE, 0);//明文
        Log.PackageCtoAsLoginContent(pcale, pacalePackE, 1);//密文
        byte[] paclPack = Connect("PackageCtoAsLogin","PackageAstoCAuth",pcalPack,IpAS,8889);

        if(paclPack[0] == 0) {   //接收到AS的包且状态位为0代表和AS认证成功
            Log.AuthLog("AS",1,account);
            PackageAstoCAuth paca = new PackageAstoCAuth(new byte[]{0});
            JavaStruct.unpack(paca,paclPack);
            byte[] paclEkcPack = new MainBody(paca.Ekc,KeyCAS,0).mainBody();  //解密
            PackageAstoCAuthEkc pacaEkc = new PackageAstoCAuthEkc("","","","",new byte[]{0},"");
            //得到Ekc:KeyCTgs(KcTgs-c与TGS的key)，IDtgs(tgs标识)，时间戳，票据有效期，票据TicketTgs ,冗余(昵称)
            JavaStruct.unpack(pacaEkc,paclEkcPack);
            KeyCTgs = String.valueOf(pacaEkc.KcTgs);   //C-TGS密钥
            ticketTgs = pacaEkc.TicketTgs;    //票据
            name = String.valueOf(pacaEkc.name);    //昵称
            //明文显示——AStoC
            textArea.appendText("\nPackageAstoCAuth明文内容：\n   KcTgs：" + String.valueOf(pacaEkc.KcTgs) + " IDtgs" + String.valueOf(pacaEkc.IDtgs)
                    + " TS(时间戳)：" + String.valueOf(pacaEkc.TS) + " Lifetime：" + String.valueOf(pacaEkc.Lifetime)
                    + "\n   TicketTgs：\n      " + Arrays.toString(pacaEkc.TicketTgs));
            textArea.appendText("\nPackageAstoCAuth密文内容：\n   " + Arrays.toString(paclPack));
            textArea.appendText("\nAS阶段认证成功！\n");
            //打印日志
            Log.PackageAStoCAuthContent(pacaEkc,paclPack,1);//密文
            Log.PackageAStoCAuthContent(pacaEkc,paclPack,0);//明文

            /*--------与TGS进行认证--------*/
            TS1 = System.currentTimeMillis();
            Authenticator auth = new Authenticator(account,ADc,TS1.toString());//IDc、ADc、TS
            byte[] authPack = JavaStruct.pack(auth);
            byte[] authE = new MainBody(authPack,KeyCTgs,1).mainBody();
            //PackageCtoTgs = IDv || TicketTgs || Authenticator
            PackageCtoTgs pct = new PackageCtoTgs(IDv,ticketTgs,authE);
            byte[] pctPack = JavaStruct.pack(pct);
            //明文展示
            textArea.appendText("\nPackageCtoTGSAuth明文内容：\n   IDv：" + String.valueOf(pct.IDv)
                    + "\n   TicketTgs：\n      " + Arrays.toString(pct.TicketTgs)
                    + "\n   Authenticator：\n      IDc：" + String.valueOf(auth.IDc) + " ADc：" + String.valueOf(auth.ADc) + " TS(时间戳):" + String.valueOf(auth.TS));
            textArea.appendText("\nPackageCtoTGSAuth密文内容(Authenticator)：\n   " + Arrays.toString(authE));
            //打印日志
            Log.PackageCtoTGSAuthContent(pct, auth, authE, 0);//明文
            Log.PackageCtoTGSAuthContent(pct, auth, authE, 1);//密文
            byte[] ptcPack = Connect("PackageCtoTgs","PackageTgstoC",pctPack,IDtgs,8888);

            if(ptcPack[0] == 4) {    //接收到TGS的包，状态位为4代表和TGS认证成功
                Log.AuthLog("TGS",1,account);
                PackageTgstoC ptc = new PackageTgstoC(new byte[]{0});
                JavaStruct.unpack(ptc,ptcPack);
                byte[] ptcEkcPack = new MainBody(ptc.EkCTgs,KeyCTgs,0).mainBody();  //解密
                //PackageTgstoCEkCTgs = EKc,tgs[Kc,v || IDV || TS4 || Ticketv]
                PackageTgstoCEkCTgs ptcEkc = new PackageTgstoCEkCTgs("","","",new byte[]{0});
                JavaStruct.unpack(ptcEkc,ptcEkcPack);
                //得到C-V密钥 和 V票据
                KeyCV = String.valueOf(ptcEkc.Kcv);
                ticketV = ptcEkc.TicketV;
                //明文显示TGS-C
                textArea.appendText("\nPackageTgstoC明文内容：\n   Kcv：" + String.valueOf(ptcEkc.Kcv) + " IDv：" + String.valueOf(ptcEkc.IDv) + " TS(时间戳)" + String.valueOf(ptcEkc.TS)
                        + "\n   TicketV：\n      " + Arrays.toString(ptcEkc.TicketV));
                textArea.appendText("\nPackageTgstoC密文内容(Authenticator)：\n   " + Arrays.toString(ptcPack));
                textArea.appendText("\nTGS阶段认证成功！\n");
                //打印日志
                Log.PackageTgstoCContent(ptcEkc, ptcPack,1);//密文
                Log.PackageTgstoCContent(ptcEkc, ptcPack,0);//明文

                /*--------和 V 进行认证---------*/
                //C → V : Ticketv || Authenticatorc
                TS1 = System.currentTimeMillis();
                auth = new Authenticator(account,ADc,TS1.toString());
                authPack = JavaStruct.pack(auth);
                authE = new MainBody(authPack,KeyCV,1).mainBody();
                PackageCtoVAuth pcva = new PackageCtoVAuth(ticketV,authE);
                byte[] pcvaPack = JavaStruct.pack(pcva);
                //明文展示C-V
                textArea.appendText("\nPackageCtoVAuth明文内容：\n   TicketV：\n      " + Arrays.toString(pcva.TicketV)
                        + "\n   Authenticator：\n      IDc：" + String.valueOf(auth.IDc) + " ADc：" + String.valueOf(auth.ADc) + " TS(时间戳):" + String.valueOf(auth.TS));
                textArea.appendText("\nPackageCtoVAuth密文内容(Authenticator)：\n   " + Arrays.toString(authE));
                //打印日志
                Log.PackageCtoVAuthContent(pcva,auth,authE,0);//明文
                Log.PackageCtoVAuthContent(pcva,auth,authE,1);//密文
                byte[] pvcaPack = Connect("PackageCtoVAuth","PackageVtoCAuth",pcvaPack,IDv,9998);

                if(pvcaPack[0] == 7) {     //接收到V的包，状态位为7代表和 V认证成功
                    textArea.appendText("\nC和V阶段认证成功！\n\n");
                    Log.AuthLog("服务器V",1,account);
                    return true;
                } else {
                    Log.AuthLog("服务器V",0,account);
                    return false;
                }
            } else {      //和TGS认证失败
                Log.AuthLog("服务器TGS",0,account);
                return false;
            }
        } else {   //和AS认证失败
            Log.AuthLog("服务器AS",0,account);
            return false;
        }
    }
    /*
     * 用于C与各服务器 发送/接收数据 短连接
     */
    public byte[] Connect(String sendPackageName,String receivePackageName,byte[] send,String ip,int port) throws IOException {
        Socket client = new Socket(ip,port);
        OutputStream os = client.getOutputStream();
        os.write(send);
        client.shutdownOutput();
        Log.sendPackageLog(sendPackageName,accountTextField.getText(),ip);
        byte[] receive;
        byte[] buffer = new byte[1024]; //缓冲区
        ArrayList<Byte> receivePackageTmp = new ArrayList<Byte>();// 临时保存接收到的数据包
        int len = 0;
        while ((len = client.getInputStream().read(buffer)) != -1) {    // 获得所有接收到的数据读取到缓冲区
            for (int i = 0; i < len; i++) {
                receivePackageTmp.add(buffer[i]);
            }
        }
        receive = new byte[receivePackageTmp.size()];
        for (int i = 0; i < receivePackageTmp.size(); i++) {// 动态数组转化为静态数组
            receive[i] = receivePackageTmp.get(i);
        }
        Log.receivePackageLog(receivePackageName,accountTextField.getText(),ip);

        client.close();
        return receive;
    }
}

