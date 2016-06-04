package com.phoenixkahlo.byteswap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.phoenixkahlo.networkingcore.InstanceMethod;
import com.phoenixkahlo.networkingcore.NetworkedMethodReceiver;
import com.phoenixkahlo.networkingcore.NetworkedMethodReceiverThread;
import com.phoenixkahlo.networkingcore.ObjectCoderFactory;
import com.phoenixkahlo.networkingcore.RegisteredObjectDecoder;

public class Receiver {

	/**
	 * @param args {port, path}
	 */
	public static void main(String[] args) throws IOException {
		args = new String[] {"47953", "/Users/Phoenix/Desktop/splinesclone.png"};
		
		// Parse args
		int port = Integer.parseInt(args[0]);
		String path = args[1];
		
		// Create socket
		System.out.println("awaiting socket");
		ServerSocket ss = new ServerSocket(port);
		Socket socket = ss.accept();
		//ss.close();
		
		// Create network
		Receiver receiver = new Receiver(path);
		RegisteredObjectDecoder decoder = new RegisteredObjectDecoder();
		decoder.registerType(ObjectCoderFactory.byteArrayDecoder(), 1);
		NetworkedMethodReceiver network =
				new NetworkedMethodReceiver(socket.getInputStream(), decoder);
		network.registerType(receiver.APPEND, 1);
		network.registerType(receiver.END, 2);
		NetworkedMethodReceiverThread thread =
				new NetworkedMethodReceiverThread(network);
		
		// Begin
		System.out.println("beginning receiver");
		thread.start();
	}
	
	private File file;
	
	public Receiver(String path) throws IOException {
		file = new File(path);
		file.createNewFile();
	}
	
	public final InstanceMethod APPEND = new InstanceMethod(this, "append", byte[].class);
	public void append(byte[] data) {
		System.out.println("append");
		try {
			FileOutputStream out = new FileOutputStream(file, true);
			out.write(data);
			out.close();
			System.out.println("wrote " + data + " to " + file);
			System.out.println("length = " + data.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final InstanceMethod END = new InstanceMethod(this, "end");
	public void end() {
		System.out.println("end");
		System.out.println("done");
	}
	
}
