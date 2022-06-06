package Utils;
import org.apache.log4j.Logger;
import pack.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Log {
    public static void sendPackageLog(String packageName,String senderID,String receiverID)
    {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        LOGGER.debug("客户端IDc：" + senderID + " 发送 " + packageName + " 到 " + receiverID);
    }
    public static void receivePackageLog(String packageName,String senderID,String receiverID)
    {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        LOGGER.debug(receiverID + " 接收 "+ packageName + " 来自 " + senderID);
    }
    /**
     * 客户端注册日志
     * @param status=0/1,0代表注册失败，1代表注册成功
     */
    public static void registerLog(int status,String IDc)
    {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if(status == 1)
            LOGGER.debug("客户端IDc：" + IDc + " 注册成功！");
        else
            LOGGER.error("客户端IDc：" + IDc + " 注册失败！");
    }
    /**
     * 客户端登录日志
     * @param status=0/1,0代表登录失败，1代表登录成功
     */
    public static void loginLog(int status,String IDc)
    {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if(status==1)
            LOGGER.debug("客户端IDc" + IDc + "登录成功！");
        else
            LOGGER.error("客户端IDc"+ IDc + "登录失败！");
    }
    /**
     * @param status=0/1,0代表注册失败，1代表注册成功
     */
    public static void modifyPwdLog(int status,String IDc)
    {
        String className=new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if(status==1)
            LOGGER.debug("客户端IDc"+IDc+"修改密码成功");
        else
            LOGGER.error("客户端"+IDc+"修改密码失败");
    }
    /**
     * @param name=AS/TGS/V,C和谁认证
     * @param status=0/1，C和谁认证成功或失败，0失败，1成功
     */
    public static void AuthLog(String name, int status, String IDc)
    {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if(status == 1)
            LOGGER.debug("客户端IDc："+ IDc + " 与 " + name + "认证成功！");
        else
            LOGGER.error("客户端IDc：" + IDc + " 与 " + name + "认证失败！");
    }
    /**
     * 报文输出日志
     * @param packageName 包名
     * @param packageContent   包的内容
     * @param status=0/1/2,0代表原报文，1代表加密报文，2代表完整报文
     * @param rs=发送/收到
     */
    public static void PackageContentLog(String packageName,byte[] packageContent,int status,String rs)
    {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if(status == 0) {   //原报文
            LOGGER.info(rs + " 数据包" + packageName + " 原报文：" + new String(packageContent));
        }
        else if(status == 1)
            LOGGER.info(rs + " 数据包" + packageName + " 加密报文：" + new String(packageContent));
        else
            LOGGER.info(rs + " 数据包" + packageName + " 完整报文：" + new String(packageContent));
    }
    /**
     * CtoAS的注册日志
     * status=0/1 0代表原报文，1代表加密报文
     */
    public static void PackageCtoAsRegisterContent(PackageCtoAsRegisterEkc ekc, byte[] bytes, int status) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if (status == 0) {
            LOGGER.info("PackageCtoAsRegister明文内容：\n   IDc(账号)：" + String.valueOf(ekc.IDc) + " TS(时间戳)：" + String.valueOf(ekc.TS1)
                    + " name(昵称)：" + String.valueOf(ekc.name) + " password(MD5摘要)：" + Arrays.toString(Arrays.toString(ekc.content).getBytes(StandardCharsets.UTF_8)));
        } else {
            LOGGER.info("PackageCtoAsRegister密文内容：\n   " + Arrays.toString(bytes));
        }
    }
    /**
     * CtoAS的认证报文日志
     * status=0/1 0代表原报文，1代表加密报文
     */
    public static void PackageCtoAsLoginContent(PackageCtoAsLoginEkc ekc,  byte[] bytes, int status) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if (status == 0) {
            LOGGER.info("PackageCtoAsLogin明文内容：\n   IDc(账号)：" + String.valueOf(ekc.IDc) + " IDtgs" + String.valueOf(ekc.IDtgs)
                    + " TS(时间戳)：" + String.valueOf(ekc.TS1) + " password(MD5加密)：" + Arrays.toString(Arrays.toString(ekc.content).getBytes(StandardCharsets.UTF_8)));
        } else {
            LOGGER.info("PackageCtoAsLogin密文内容：\n   " + Arrays.toString(bytes));
        }
    }
    /**
     * AStoC的认证状态返回报文日志
     * status=0/1 0代表原报文，1代表加密报文
     */
    public static void PackageAStoCAuthContent(PackageAstoCAuthEkc ekc, byte[] bytes, int status) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if (status == 0) {
            LOGGER.info("PackageAstoCAuth明文内容：\n   KcTgs：" + String.valueOf(ekc.KcTgs) + " IDtgs" + String.valueOf(ekc.IDtgs)
                    + " TS(时间戳)：" + String.valueOf(ekc.TS) + " Lifetime：" + String.valueOf(ekc.Lifetime)
                    + "\n   TicketTgs：\n      " + Arrays.toString(ekc.TicketTgs));
        } else {
            LOGGER.info("PackageAstoCAuth密文内容：\n   " + Arrays.toString(bytes));
        }
    }
    /**
     * CtoTGS的认证报文日志
     * status=0/1 0代表原报文，1代表加密报文
     */
    public static void PackageCtoTGSAuthContent(PackageCtoTgs pack, Authenticator auth, byte[] bytes, int status) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if (status == 0) {
            LOGGER.info("PackageCtoTGSAuth明文内容：\n   IDv：" + String.valueOf(pack.IDv)
                    + "\n   TicketTgs：\n      " + Arrays.toString(pack.TicketTgs)
                    + "\n   Authenticator：\n      IDc：" + String.valueOf(auth.IDc) + " ADc：" + String.valueOf(auth.ADc) + " TS(时间戳):" + String.valueOf(auth.TS));
        } else {
            LOGGER.info("PackageCtoTGSAuth密文内容(Authenticator)：\n   " + Arrays.toString(bytes));
        }
    }
    /**
     * TGStoC的认证状态返回报文日志
     * status=0/1 0代表原报文，1代表加密报文
     */
    public static void PackageTgstoCContent(PackageTgstoCEkCTgs ekc, byte[] bytes, int status) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if (status == 0) {
            LOGGER.info("PackageTgstoC明文内容：\n   Kcv：" + String.valueOf(ekc.Kcv) + " IDv：" + String.valueOf(ekc.IDv) + " TS(时间戳)" + String.valueOf(ekc.TS)
                    + "\n   TicketV：\n      " + Arrays.toString(ekc.TicketV));

        } else {
            LOGGER.info("PackageTgstoC密文内容(Authenticator)：\n   " + Arrays.toString(bytes));
        }
    }
    /**
     * CtoV的认证报文日志
     * status=0/1 0代表原报文，1代表加密报文
     */
    public static void PackageCtoVAuthContent(PackageCtoVAuth pack, Authenticator auth, byte[] bytes, int status) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if (status == 0) {
            LOGGER.info("PackageCtoVAuth明文内容：\n   TicketV：\n      " + Arrays.toString(pack.TicketV)
                    + "\n   Authenticator：\n      IDc：" + String.valueOf(auth.IDc) + " ADc：" + String.valueOf(auth.ADc) + " TS(时间戳):" + String.valueOf(auth.TS));
        } else {
            LOGGER.info("PackageCtoVAuth密文内容(Authenticator)：\n   " + Arrays.toString(bytes));
        }
    }
    /**
     * CtoV的msg报文日志
     * status=0/1 0代表原报文，1代表加密报文
     */
    public static void PackageCtoVMsgContent(PackageCtoVPublicMsgEkCV ekcv, byte[] ticketV, byte[] bytes, int status) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if (status == 0) {
            LOGGER.info("PackageCtoVMsg明文内容：\n   SenderName：" + String.valueOf(ekcv.SenderName) + " SenderID：" + String.valueOf(ekcv.SenderID)
                    + " TS(时间戳)：" + String.valueOf(ekcv.TS) + " Content：" + String.valueOf(ekcv.Content) + " 数字签名：" + Arrays.toString(ekcv.EscMd5)
                    + "\n   TicketV：" + Arrays.toString(ticketV));
        } else {
            LOGGER.info("PackageCtoVMsg密文内容：\n   " + Arrays.toString(bytes));
        }
    }
    /**
     * VtoC的msg报文日志
     * status=0/1 0代表原报文，1代表加密报文
     */
    public static void PackageVtoCMsgContent(PackageVtoCPublicMsgEkCV ekcv, byte[] bytes, int status) {
        String className = new Exception().getStackTrace()[1].getClassName();
        Logger LOGGER = Logger.getLogger(className);
        if (status == 0) {
            LOGGER.info("PackageVtoCMsg明文内容：\n   SenderName：" + String.valueOf(ekcv.SenderName) + " SenderID：" + String.valueOf(ekcv.SenderID)
                    + " TS(时间戳)：" + String.valueOf(ekcv.TS) + " Content：" + String.valueOf(ekcv.Content) + " 数字签名：" + Arrays.toString(ekcv.EscMd5));
        } else {
            LOGGER.info("PackageVtoCMsg密文内容：\n   " + Arrays.toString(bytes));
        }
    }

}

