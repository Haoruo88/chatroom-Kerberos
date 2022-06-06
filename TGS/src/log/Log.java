package log;

import org.apache.log4j.Logger;

import pack.PackageTgstoC;
import pack.TicketTgs;
import pack.TicketV;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    public static void sendPackageLog(String packageName, String senderID, String receiverID) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        LOGGER.info(senderID + " send " + packageName + " to " + receiverID);
    }

    public static void receivePackageLog(String packageName, String senderID, String receiverID) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        LOGGER.info(receiverID + " receive " + packageName + " from " + senderID);
    }

    /**
     *
     * @param name=AS/TGS/V,C和谁认证
     * @param status=0/1，C和谁认证成功或失败，0失败，1成功
     */
    public static void AuthLog(String name, int status, String IdClient) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if (status == 0)
            LOGGER.info("客户端" + IdClient + "与" + name + "认证成功");
        else
            LOGGER.error("客户端" + IdClient + "与" + name + "认证失败");
    }

    public static void PrintPackageCToTgs(char[] IdServer, TicketTgs ticket, pack.Authenticator auth) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);

        LOGGER.info("服务器的标识(服务器的网络地址)：" + String.valueOf(IdServer) + "\n" + "PackageCToTgs --> Ticket:\n" +
                "AS生成的C与Tgs的会话密钥：" + String.valueOf(ticket.KeyClientTgs) + "\n" + "Client的标识(账号)："
                + String.valueOf(ticket.IdClient) + "\n" +
                "Client的网络地址：" + String.valueOf(ticket.AddressClient) + "\n" + "Tgs的标识(Tgs的网络地址)："
                + String.valueOf(ticket.IdTgs) + "\n" + "时间戳(票据签发时间)："
                + TimeStamp2Date(String.valueOf(ticket.TimeStamp)) + "\n"
                + "票据的有效期："
                + TimeStamp2Date(String.valueOf(ticket.Lifetime)) + "\n"
                + "PackageCToTgs --> Authenticator:\n"
                + "Client的标识(账号)：" + String.valueOf(auth.IdClient) + "\n" + "Client的网络地址："
                + String.valueOf(auth.AddressClient) +
                "\n" + "Client生成的时间戳：" + TimeStamp2Date(String.valueOf(auth.TimeStamp)) + "\n");
    }
    public static void PrintPackageTgsToC1(TicketV ticket, PackageTgstoC packages) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);

        LOGGER.info("PackageTgsToC --> Ticketv:\n" +
                "Tgs生成的Client与Server的会话密钥：" + String.valueOf(ticket.KeyClientServer) + "\n" +
                "Server的标识(Server的网络地址)" + String.valueOf(ticket.IdServer) + "\n" +
                "Client的标识(账号)：" + String.valueOf(ticket.IdClient) + "\n" +
                "时间戳(票据签发时间)：" + TimeStamp2Date(String.valueOf(ticket.TimeStamp)) + "\n" +
                "票据的有效期：" + TimeStamp2Date(String.valueOf(ticket.Lifetime)) + "\n" +
                "加密后的报文内容(二进制)" + packages.EncryptKeyClientTgs);
        // Arrays.toString(packages.EncryptKeyClientTgs);
    }
    public static void PrintPackageTgsToC(TicketV ticket, PackageTgstoC packages) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);

        LOGGER.info("PackageTgsToC --> Ticketv:\n" +
                "Tgs生成的Client与Server的会话密钥：" + String.valueOf(ticket.KeyClientServer) + "\n" +
                "Server的标识(Server的网络地址)" + String.valueOf(ticket.IdServer) + "\n" +
                "Client的标识(账号)：" + String.valueOf(ticket.IdClient) + "\n" +
                "时间戳(票据签发时间)：" + String.valueOf(ticket.TimeStamp) + "\n" +
                "票据的有效期：" + String.valueOf(ticket.Lifetime) + "\n" +
                "加密后的报文内容(二进制)" + packages.EncryptKeyClientTgs);
        // Arrays.toString(packages.EncryptKeyClientTgs);
    }

    // 将时间戳转换为日期格式
    public static String TimeStamp2Date(String time) {
        Long timeLong = Long.parseLong(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 要转换的时间格式
        Date date;
        try {
            date = sdf.parse(sdf.format(timeLong));
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
