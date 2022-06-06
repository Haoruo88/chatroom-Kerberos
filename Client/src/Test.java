import ClientWindow.CWindow;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Description：
 * Date:2022/5/28 22:02
 **/
public class Test {
    public static void main(String[] args) throws Exception {
//        System.out.println("你好ddddddddd\n   nn" + "111");
//        byte[] bytes = new byte[]{11,3,23,21,12};
//        System.out.println(Arrays.toString(bytes));
//        System.out.println(InetAddress.getLocalHost().getHostAddress());
        //打开客户端页面
        CWindow cWindow = new CWindow("name", "account", "pwd", new byte[1]);
        cWindow.start(new Stage());
    }
}
