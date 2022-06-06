package log;
import org.apache.log4j.Logger;
import pack.PackageAstoCAuth;
import pack.PackageAstoCAuthEkc;
import pack.TicketTgs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
public class Log {
    public static void sendPackageLog(String packageName,String senderID,String receiverID)
    {
        String className=new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        LOGGER.info(senderID+" send "+packageName+" to "+receiverID+"\n\n");
    }

    public static void sendPackageLog(String packageName, String senderID, String receiverID, PackageAstoCAuthEkc pack)
    {
        String className=new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        String tTGS = new String(pack.KcTgs);
        String tipTGS = new String(pack.IDtgs);
        String ttime = new String(pack.TS);
        String name = new String(pack.name);
        LOGGER.info(receiverID+" receive "+packageName+" from "+senderID+"\n发包内容(登录包):\nTGS会话密匙:"+tTGS+"\nTGS ip地址："+tipTGS+"\n发包时间："+ttime+"\n用户昵称："+name+"\n\n");
    }


    public static void sendPackageLog(String packageName, String senderID, String receiverID, PackageAstoCAuth pack)
    {
        String className=new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        String spack = Arrays.toString(pack.Ekc);
        LOGGER.info(receiverID+" receive "+packageName+" from "+senderID+"\n发包内容(登录包):\n"+spack+"\n\n");
    }


    public static void receivePackageLog(String packageName,String senderID,String receiverID,String IDc,String iptgs,byte[] content,String TS)
    {
        String className=new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        LOGGER.info(receiverID+" receive "+packageName+" from "+senderID+"\n收包内容(登录包):\n用户ID:"+IDc+"\nTGS ip地址"+iptgs+"\n时间戳:"+timeStamp(TS)+"\n密码:"+content+"\n\n");
    }

    public static void receivePackageLog(String packageName,String senderID,String receiverID,String IDc,String TS,String name,byte[] content)
    {
        String className=new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        LOGGER.info(receiverID+" receive "+packageName+" from "+senderID+"\n收包内容(注册包):\n用户ID:"+IDc+"\n时间戳:"+timeStamp(TS)+"\n昵称:"+name+"\n密码:"+content+"\n\n");
    }

    public static String timeStamp(String time) {
        Long timeLong = Long.parseLong(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//要转换的时间格式
        Date date;
        try {
            date = sdf.parse(sdf.format(timeLong));
            return sdf.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
