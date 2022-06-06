import com.sun.corba.se.impl.orb.ParserTable;
import des.MainBody;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import log.Log;
import pack.*;
import struct.JavaStruct;
import struct.StructException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class MessageServer extends Application {

	//UI控件
	private TextArea showTextArea = new TextArea();

	private static ServerSocket serverSocket;  //服务器端认证套接字
	private static ServerSocket serverSocket_1;	//转发消息的套接字
	//套接字容器
	private static ArrayList<Socket> sockets = new ArrayList<Socket>();
	// 聊天信息链表集合
	private static LinkedList<PackageVtoCPublicMsg> msgList = new LinkedList<PackageVtoCPublicMsg>();

	private final static String keyVTgs = "12345678";// C和TGS会话的des密钥
	private final static String serverIp = "192.168.43.38";// ServerIP地址
	private final static String keyCV = "12345678";// ServerIP地址

	@Override
	public void start(Stage primaryStage) throws Exception {
		//CV认证展示框
		showTextArea.setText("服务器已启动...");
		showTextArea.setPrefHeight(620.0);
		showTextArea.setPrefWidth(625.0);
		showTextArea.setEditable(false);

		excute();

		AnchorPane pane = new AnchorPane();
		Scene scene = new Scene(pane);
		pane.getChildren().addAll(showTextArea);
		pane.setPrefSize(620.0,585.0);
		primaryStage.setTitle("聊天室服务器");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
//	public static void main(String[] args) {
//		new MessageServer().excute();
//	}
	private void excute(){
		try {
			System.out.println("服务器运行中......");
			serverSocket = new ServerSocket(9998);//认证
			serverSocket_1 = new ServerSocket(9999);//消息
			new AcceptSocketThread1().start();  //处理认证
			new AcceptSocketThread().start();	//处理转发消息
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//接收客户端Socket套接字(处理认证)的线程
	class AcceptSocketThread1 extends Thread {
		public void run() {
			while (this.isAlive()) {
				try {
					Socket socket = serverSocket.accept();  //接收一个客户端Socket对象
					if (socket != null) {// 建立该客户端的通信管道
						new Thread(new Auth(socket)).start();//开启一个子线程处理
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	//处理客户端认证的线程
	class Auth implements Runnable {
		private Socket socket;// 连接客户端返回的socket对象
		private byte[] receivePackage;// 接收到的数据包

		public Auth(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
			byte[] buffer = new byte[1024];
			ArrayList<Byte> receivePackageTmp = new ArrayList<Byte>();// 临时保存的接收到的数据包
			int len = 0;
			try {
				while ((len = socket.getInputStream().read(buffer)) != -1) {// 获得所有接收到的数据临时保存在动态数组中
					for (int i = 0; i < len; i++) {
						receivePackageTmp.add(buffer[i]);
					}
				}
				receivePackage = new byte[receivePackageTmp.size()];
				for (int i = 0; i < receivePackageTmp.size(); i++) {// 动态数组转化为静态数组
					receivePackage[i] = receivePackageTmp.get(i);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			byte status = receivePackage[0];
			if (status == 0) {
				handleAuth();
			} else {
				System.out.println("客户端端口搞错了...");
			}
		}
		private void handleAuth() {
			try {
				Log.receivePackageLog("客户端认证包", socket.getInetAddress().getHostAddress(), serverIp);

				PackageCtoVAuth packageCtoVAuth = new PackageCtoVAuth(new byte[1], new byte[1]);// 解第一层包
				JavaStruct.unpack(packageCtoVAuth, receivePackage, ByteOrder.BIG_ENDIAN);
				byte[] ticketDecrypted = new MainBody(packageCtoVAuth.TicketV, keyVTgs, 0).mainBody();// 解密ticketv
				TicketV ticketv = new TicketV("", "", "", "", "", "");// 解第二层包，ticketv
				JavaStruct.unpack(ticketv, ticketDecrypted, ByteOrder.BIG_ENDIAN);

				byte[] authDecrypted = new  MainBody(packageCtoVAuth.Authenticator, keyCV, 0).mainBody();// 解密Authenticator
				Authenticator auth = new Authenticator("","","");
				JavaStruct.unpack(auth, authDecrypted, ByteOrder.BIG_ENDIAN);
				//明文展示
				showTextArea.appendText("\n" + String.valueOf(ticketv.IDc) + "("  + String.valueOf(ticketv.ADc) + ")" + "认证中：");
				showTextArea.appendText("\nPackageCtoVAuth明文内容：\nTicketV:\n" + "TGS生成的C与V的会话密钥：" + String.valueOf(ticketv.Kcv) + "\n" + "Client的标识(账号)："
						+ String.valueOf(ticketv.IDc) + "\n" + "Client的网络地址：" + String.valueOf(ticketv.ADc) + "\n" + "Server的IP地址):"
						+ String.valueOf(ticketv.IDv) + "\n" + "时间戳(票据签发时间)："
						+ String.valueOf(ticketv.TS) + "\n"
						+ "票据的有效期：" + String.valueOf(ticketv.Lifetime) + "\n"
						+ "Authenticator:\n"
						+ "Client的标识(账号)：" + String.valueOf(auth.IDc) + "\n" + "Client的网络地址：" + String.valueOf(auth.ADc)
						+ "\n" + "Client生成的时间戳：" + String.valueOf(auth.TS));
				//打印日志
				Log.PackageCtoVAuth(ticketv,auth);
				String idv = String.valueOf(ticketv.IDv);// 传过来的服务器ip地址
				if (!checkTicket(idv)) {// 票据验证
					showTextArea.appendText("\n票据验证失败！\n");
					System.out.println("票据验证失败！");
					return;
				}
				showTextArea.appendText("\n认证成功！\n");
				PackageVtoCAuth packageVtoCAuth = new PackageVtoCAuth(System.currentTimeMillis() + "");// 验证成功，返回包
				byte[] returnData = JavaStruct.pack(packageVtoCAuth, ByteOrder.BIG_ENDIAN);
				socket.getOutputStream().write(returnData);
				socket.shutdownOutput();
				Log.sendPackageLog("客户端登录认证成功包", serverIp, socket.getInetAddress().getHostAddress());
			} catch (IOException | StructException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//验证票据，对比票据中的和本机的IP地址
		private boolean checkTicket(String idv) {
			// 对比接收到的邮件服务器ip地址是否和本机获取的是否一致，以此来进行验证
			return idv.equals(serverIp);
		}
	}
	//接收客户端Socket套接字(处理消息)的线程
	class AcceptSocketThread extends Thread {
		public void run() {
			new SendMsgToClient().start();//开启发送消息的线程
			while (this.isAlive()) {
				try {
					Socket socket = serverSocket_1.accept();  //接收一个客户端Socket对象
					if (socket != null) {// 建立该客户端的通信管道
						System.out.println(socket.getInetAddress() + "已连接...");
						sockets.add(socket);
						new GetMsgFromClient(socket).start();	//开启一个线程接收该客户端的聊天信息
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/* 接收客户端消息的线程
	* inputStream：该客户端套接字的输入流
	 */
	class GetMsgFromClient extends Thread {
		Socket socket;
		public GetMsgFromClient(Socket socket) {
			this.socket = socket;
		}
		public void run() {
			InputStream inputStream = null;
			while (this.isAlive()){
				byte[] receive;
				byte[] buffer = new byte[2048]; //缓冲区
				ArrayList<Byte> receivePackageTmp = new ArrayList<Byte>();// 临时保存接收到的数据包
				int len = -1;
				try {
					inputStream = socket.getInputStream();
					assert inputStream != null;
					len = inputStream.read(buffer,0,2047);
					if (len == -1) {	//客户端主动断开
						sockets.remove(socket);
						showTextArea.appendText("\n" + socket.getInetAddress().getHostAddress() + "已断开");
						this.stop();
					}
					for (int i = 0; i < len; i++) {
						receivePackageTmp.add(buffer[i]);
					}
					receive = new byte[len];
					for (int i = 0; i < len; i++) {
						receive[i] =receivePackageTmp.get(i);
					}
					if (!Arrays.equals(receive, new byte[]{})){
						handlePublicMsg1(receive);	//对收到的字节流进行处理
					}
					sleep(50);
				} catch (IOException | InterruptedException e) {
					//e.printStackTrace();
				}
			}
		}
		//处理公共信息包
		private void handlePublicMsg1(byte[] msgBytes) {
			PackageCtoVPublicMsg packageCtoVPublicMsg = new PackageCtoVPublicMsg(0, new byte[1],new byte[1]);
			try {
				Log.receivePackageLog("PackageCtoVPublicMsg",socket.getInetAddress().getHostAddress(),serverIp);
				JavaStruct.unpack(packageCtoVPublicMsg, msgBytes);
				byte[] ticketDecrypted = new MainBody(packageCtoVPublicMsg.TicketV, keyVTgs, 0).mainBody();// 解密ticketv
//				TicketV ticketv = new TicketV("", "", "", "", "", "");// 解第二层包，ticketv
//				JavaStruct.unpack(ticketv, ticketDecrypted, ByteOrder.BIG_ENDIAN);
//				String idv = String.valueOf(ticketv.IDv);// 传过来的服务器ip地址
//			if (!checkTicket(idv)) {// 票据验证
//				return;
//			}
				//默认cv密钥均相同......进行处理-解包 再封包
				int len = packageCtoVPublicMsg.EkCV.length;
				PackageVtoCPublicMsg packageVtoCPublicMsg = new PackageVtoCPublicMsg(len, packageCtoVPublicMsg.EkCV);
				msgList.add(packageVtoCPublicMsg);  //添加到聊天信息集合
			} catch (StructException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		}
		//验证票据，对比票据中的和本机的IP地址
		private boolean checkTicket(String idv) {
			// 对比接收到的邮件服务器ip地址是否和本机获取的是否一致，以此来进行验证
			return idv.equals(serverIp);
		}
	}
	// 给所有客户发送聊天信息的线程
	class SendMsgToClient extends Thread {
		public void run() {
			while (this.isAlive()) {
				try {
					// 如果信息链表集合不空（还有聊天信息未发送）
					if (!msgList.isEmpty()) {
						// 取信息链表集合中的最后一条,并移除
						PackageVtoCPublicMsg msg = msgList.removeLast();
						// 对输出流列表集合进行遍历，循环发送信息给所有客户端
						for (Socket socket : sockets) {
							OutputStream outputStream = socket.getOutputStream();
							outputStream.write(JavaStruct.pack(msg));
							outputStream.flush();
							Log.sendPackageLog("PackageVtoCPublicMsg",serverIp,socket.getInetAddress().getHostAddress());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
