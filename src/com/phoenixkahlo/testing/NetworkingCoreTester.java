package com.phoenixkahlo.testing;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class NetworkingCoreTester {

	public static void main(String[] args) throws Exception {
		Properties methodConfig = new Properties();
		methodConfig.setProperty("print(java.lang.String,int)", "1");
		methodConfig.setProperty("printStringBox(com.phoenixkahlo.testing.StringBox)", "2");
		
		class SocketBox {
			Socket socket;
		}
		SocketBox box = new SocketBox();
		ServerSocket serverSocket = new ServerSocket(34567);
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
					box.socket = new Socket("localhost", 34567);
				} catch (Exception e) {}
			}
		}).start();
		Socket socket1 = serverSocket.accept();
		serverSocket.close();
		Socket socket2 = box.socket;
		
		TestingBroadcaster broadcaster = new TestingBroadcaster(socket1.getOutputStream(), methodConfig);
		TestingReceiver receiver = new TestingReceiver(socket2.getInputStream(), methodConfig);
		
		receiver.start();
		
		broadcaster.print("hello", 5);
		broadcaster.printStringBox(new StringBox("bonjour"));
	}
	
}
