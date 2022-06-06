package PersonalManageWindow;

import Utils.Util;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PersonalManageWindow extends Application implements Initializable {
    @FXML
    private Label accountLabel;
    @FXML
    private PasswordField oldPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField confirmNewPassword;
    @FXML
    private Button modifyButton;

    private static String id;
    private static String password;
    private StringProperty accountString=new SimpleStringProperty();

    public void setId(String id){ PersonalManageWindow.id=id;}
    public void setPassword(String password){ PersonalManageWindow.password=password;}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountLabel.textProperty().bind(accountString);
        accountString.set(id);
        System.out.println(id);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("PersonalManageWindow.fxml"));
        primaryStage.setTitle("个人中心");
        primaryStage.getIcons().add(new Image(getClass().getResource("icon3.png").toExternalForm()));
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void modify(ActionEvent actionEvent) throws Exception {
        String oldPwd=oldPassword.getText();
        String newPwd=newPassword.getText();
        String confirmPwd=confirmNewPassword.getText();
        if(!oldPwd.equals(password)){
            Util.alertInformation("旧密码错误");
        }
        else if((newPwd.length()<6)||(newPwd.length()>16)){
            Util.alertInformation("新密码长度需为6-16位");
        }
        else if(!confirmPwd.equals(newPwd)){
            Util.alertInformation("输入的两个新密码不同！");
        }
        else if(newPwd.equals(password)){
            Util.alertInformation("新旧密码相同！");
        }
        else{
            ModifyPasswd m=new ModifyPasswd(id,oldPwd,newPwd);
            m.packCtoASModify();
            m.unpackAStoCmodify();
            Stage primaryStage=(Stage)modifyButton.getScene().getWindow();
            primaryStage.close();
        }
    }
}
