package com.phoenixkahlo.testing;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Tester {

	public static void main(String[] args) throws Exception {
		Properties methodConfig = new Properties();
		methodConfig.setProperty("print(java.lang.String,int)", "1");
		methodConfig.setProperty("printStringBox(networkedobjectstesting.StringBox)", "2");
		
		Properties objectConfig = new Properties();
		objectConfig.setProperty("networkedobjectstesting.StringBox", "1");
		
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
		
		TestingBroadcaster broadcaster = new TestingBroadcaster(socket1.getOutputStream(), methodConfig, objectConfig);
		TestingReceiver receiver = new TestingReceiver(socket2.getInputStream(), methodConfig, objectConfig);
		
		receiver.start();
		
		broadcaster.print("hello", 5);
		broadcaster.printStringBox(new StringBox("bonjour"));
	}
	
}
