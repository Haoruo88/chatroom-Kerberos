package PersonalManageWindow;
//Tony
import Utils.*;
import pack.*;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import pack.PackageCtoAsModify;
import pack.PackageCtoAsModifyEkc;
import struct.JavaStruct;
import struct.StructException;

public class ModifyPasswd {
	private String IDc;
	private String pwdOrigin;
	private String IDas="172.20.10.8";
	private Socket socket1;
	private String TS1;
	private String pwdnow;
	private String KC="12345678";
	private static byte[] packageReceived;
	private byte zhuangtai;
	public ModifyPasswd(String ID,String passwdO,String pwdnow)
	{
		this.IDc=ID;
		this.pwdOrigin=passwdO;
		this.pwdnow=pwdnow;
	}
	//客户端建立连接
	public void packCtoASModify() throws Exception {
		try {
			socket1= new Socket(IDas,8888);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Date date=new Date(0);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TS1=df.format(date);
		MessageDigest md5=MessageDigest.getInstance("MD5");
		PackageCtoAsModifyEkc a1=new PackageCtoAsModifyEkc(IDc, TS1, md5.digest(pwdOrigin.getBytes(StandardCharsets.UTF_8)),md5.digest(pwdnow.getBytes(StandardCharsets.UTF_8)));
		try {
			byte[] a1fb=JavaStruct.pack(a1);
			Log.PackageContentLog("PackageCtoAsModifyEkc",a1fb,0,"发送");
		    byte[] a1jiami=new MainBody(a1fb,KC,1).mainBody();
			Log.PackageContentLog("PackageCtoAsModifyEkc",a1jiami,1,"发送");
			PackageCtoAsModify b1=new PackageCtoAsModify(a1jiami);
		    byte[] b1fb=JavaStruct.pack(b1);
			Log.PackageContentLog("PackageCtoAsModify",b1fb,2,"发送");
			socket1.getOutputStream().write(b1fb);
		    socket1.shutdownOutput();
		} catch (StructException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void unpackAStoCmodify()
	{
		byte[] buffer=new byte[1024];	
		ArrayList<Byte> receivePackageTmp = new ArrayList<Byte>();
		int len = 0;
		try {
			while((len=socket1.getInputStream().read(buffer))!=-1)
			{
				for(int i=0;i<len;i++)
				{
					receivePackageTmp.add(buffer[i]);
				}
			}
			packageReceived=new byte[receivePackageTmp.size()];
			for(int i=0;i<receivePackageTmp.size();i++)
			{
				packageReceived[i]=receivePackageTmp.get(i);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PackageAstoCModify a1=new PackageAstoCModify(IDc,zhuangtai,TS1 );
	    try {
			JavaStruct.unpack(a1,packageReceived);
			Log.PackageContentLog("PackageAstoCModify",packageReceived,2,"接收");
			if(a1.modifyStatus==0)
			{
				Util.alertInformation("修改密码失败！");
			}
			if(a1.modifyStatus==1)
			{
				Util.alertInformation("修改密码成功！");
			}
		} catch (StructException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

