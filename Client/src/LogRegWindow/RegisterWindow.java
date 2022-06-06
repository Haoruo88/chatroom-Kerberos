package LogRegWindow;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import Utils.*;
import pack.PackageAstoCRegister;
import pack.PackageCtoAsRegister;
import pack.PackageCtoAsRegisterEkc;
import struct.JavaStruct;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;

public class RegisterWindow extends Application {
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField accountTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField passwordConfirmTextField;
    @FXML
    private Button registerButton;

    private String KeyCAS = "12345678";     //C-AS对话密钥
    private String IpAS = "192.168.43.248";     //AS服务器的IP地址

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("RegisterWindow.fxml"));
        primaryStage.setTitle("账号注册");
        primaryStage.getIcons().add(new Image(getClass().getResource("信息.png").toExternalForm()));
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void register(ActionEvent actionEvent) throws Exception {
        String name = nameTextField.getText();
        String account = accountTextField.getText();
        String password = passwordTextField.getText();
        String passwordConfirm = passwordConfirmTextField.getText();
        if (name.length() == 0){
            registerAlert("昵称不能为空！");
        }
        else if (account.length() != 10) {
            registerAlert("账号需为10位！");
        } else if (password.length() < 6 || password.length() > 16) {
            registerAlert("请输入6-16位密码！");
        } else if (!passwordConfirm.equals(password)) {
            registerAlert("两次输入密码不一样！");
        } else {
            if (register(account, password, name)) {  //注册成功
                registerAlert("注册成功！");
                Log.registerLog(1, account);
                Stage primaryStage = (Stage) registerButton.getScene().getWindow();
                primaryStage.close();
            } else {
                Util.alertInformation("请重新输入账号、密码");
                Log.registerLog(0, account);
            }
        }
    }
    public void registerAlert(String registerAlert) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(registerAlert);
        alert.showAndWait();
    }
    /*
     *判断注册是否成功
     */
    public Boolean register(String account, String passwd,String name) throws Exception {
        Long TS1 = System.currentTimeMillis();//当前时间戳
        MessageDigest md5 = MessageDigest.getInstance("MD5");//密码使用MD5算法加密
        PackageCtoAsRegisterEkc pcarEkc = new PackageCtoAsRegisterEkc(account, TS1.toString(),name, md5.digest(passwd.getBytes(StandardCharsets.UTF_8)));
        byte[] pcarEkcPack = JavaStruct.pack(pcarEkc);
        byte[] pcarEkcPackE = new MainBody(pcarEkcPack, KeyCAS, 1).mainBody();//DES加密包Ekc
        PackageCtoAsRegister pcar = new PackageCtoAsRegister(pcarEkcPackE);
        byte[] pcarPack = JavaStruct.pack(pcar);
        //打印日志
        Log.PackageCtoAsRegisterContent(pcarEkc, pcarEkcPackE,0);//明文
        Log.PackageCtoAsRegisterContent(pcarEkc, pcarEkcPackE,1);//密文
        byte[] pacrPack = CConnectAS("PackageCtoAsRegister","PackageAstoCRegister",pcarPack, IpAS,8889);
        if(pacrPack[0] == 1) { //收到注册状态返回包
            PackageAstoCRegister pacr = new PackageAstoCRegister((byte)10);
            JavaStruct.unpack(pacr,pacrPack);
            if(pacr.registerStatus == 1) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
   }
   /*
    *连接AS服务器 发送/接收数据包 短连接
    */
    public byte[] CConnectAS(String sendPackageName, String receivePackageName, byte[] sendPack, String ip, int port) throws IOException {
        Log.PackageContentLog(sendPackageName,sendPack,2,"发送");     //打印日志
        //创建套接字并连接
        Socket clientSocket = new Socket(ip,port);
        OutputStream os= clientSocket.getOutputStream();
        os.write(sendPack);
        clientSocket.shutdownOutput();
        Log.sendPackageLog(sendPackageName, accountTextField.getText(), ip);      //打印日志
        byte[] receive;
        byte[] buffer = new byte[1024];
        ArrayList<Byte> receivePackageTmp = new ArrayList<Byte>();// 临时保存接收到的数据包
        int len = 0;
        while ((len = clientSocket.getInputStream().read(buffer)) != -1) {// 获得所有接收到的数据临时保存在动态数组中
            for (int i = 0; i < len; i++) {
                receivePackageTmp.add(buffer[i]);
            }
        }
        receive = new byte[receivePackageTmp.size()];
        for (int i = 0; i < receivePackageTmp.size(); i++) {// 动态数组转化为静态数组
            receive[i] = receivePackageTmp.get(i);
        }
        Log.receivePackageLog(receivePackageName, ip, "客户端IDc：" + accountTextField.getText());

        clientSocket.close();
        return receive;
    }
}

