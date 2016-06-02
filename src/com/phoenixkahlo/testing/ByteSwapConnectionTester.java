package com.phoenixkahlo.testing;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.phoenixkahlo.byteswapold.ByteSwapConnection;

public class ByteSwapConnectionTester {

	public static void main(String[] args) throws IOException {
		// Get two sockets connected to each other across the localhost in a muy legit way
		class SocketBox {
			Socket socket;
		}
		SocketBox box = new SocketBox();
		ServerSocket serverSocket = new ServerSocket(34567);
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(300);
					box.socket = new Socket("localhost", 34567);
				} catch (Exception e) {}
			}
		}).start();
		Socket socket1 = serverSocket.accept();
		serverSocket.close();
		Socket socket2 = box.socket;
		// Test stuff
		ByteSwapConnection connection1 = new ByteSwapConnection(socket1);
		ByteSwapConnection connection2 = new ByteSwapConnection(socket2);
		System.out.println("Connections set up!");
		connection1.sendFile(new File("/Users/Phoenix/Desktop/C/sudoku.c"));
		System.out.println("Goodby(t)e!");
	}
	
}
