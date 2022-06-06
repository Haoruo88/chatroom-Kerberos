package Utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
//import java.util.ArrayList;
import java.util.Optional;

public class Util {
    public static void alertInformation(String alertString){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("温馨提示");
        alert.setHeaderText(null);
        alert.setContentText(alertString);
        alert.showAndWait();
    }

    public static int alertConfirmation(String alertString){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("温馨提示");
        alert.setHeaderText(null);
        alert.setContentText(alertString);
        Optional<ButtonType> buttonType=alert.showAndWait();
        if(buttonType.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)){
            return 1;
        }
        if(buttonType.get().getButtonData().equals(ButtonBar.ButtonData.CANCEL_CLOSE)){
            return 0;
        }
        return -1;
    }

    public static byte[] getBytesByFile(String pathStr) {
        File file = new File(pathStr);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] b = new byte[(int)file.length()];
            fis.read(b);
            fis.close();
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2,byte[] bt3){
        byte[] bt4 = new byte[bt1.length+bt2.length+bt3.length];
        System.arraycopy(bt1, 0, bt4, 0, bt1.length);
        System.arraycopy(bt2, 0, bt4, bt1.length, bt2.length);
        System.arraycopy(bt3,0,bt4,bt1.length+bt2.length,bt3.length);
        return bt4;
    }

    public static void sendPackage(byte[] send,String ip,int port) throws IOException {
        Socket client=new Socket(ip,port);
        OutputStream os= client.getOutputStream();
        DataOutputStream dos=new DataOutputStream(os);
        dos.write(send,0, send.length);
        client.shutdownOutput();
        client.close();
    }

    public static void readPrivateKey(BigInteger[] privateKey, String account) {
        String fileName = "src/privateKey.txt";
        File file = new File(fileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                String[] str = tempString.split("@");
                if (str[0].equals(account)) {
                    privateKey[0] = new BigInteger(str[1]);
                    privateKey[1] = new BigInteger(str[2]);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readPublicKey(BigInteger[] publicKey, String account) {
        String fileName = "src/publicKey.txt";
        File file = new File(fileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                String[] str = tempString.split("@");
                if (str[0].equals(account)) {
                    publicKey[0] = new BigInteger(str[1]);
                    publicKey[1] = new BigInteger(str[2]);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}