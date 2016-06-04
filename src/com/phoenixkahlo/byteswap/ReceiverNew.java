package com.phoenixkahlo.byteswap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.phoenixkahlo.networkingcore.InstanceMethod;
import com.phoenixkahlo.networkingcore.NetworkedMethodReceiver;
import com.phoenixkahlo.networkingcore.NetworkedMethodReceiverThread;
import com.phoenixkahlo.networkingcore.ObjectCoderFactory;
import com.phoenixkahlo.networkingcore.RegisteredObjectDecoder;

public class ReceiverNew {

	/**
	 * @param args {port, path}
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		args = new String[] {"23456", "/Users/Phoenix/Desktop/slrings.png"};

		
		int port = Integer.parseInt(args[0]);
		String path = args[1];
		
		ReceiverNew receiver = new ReceiverNew(port, path);
		receiver.start();
	}
	
	private Thread thread;
	private File file;
	
	public ReceiverNew(int port, String path) throws IOException {
		ServerSocket ss = new ServerSocket(port);
		Socket socket = ss.accept();
		ss.close();
		
		RegisteredObjectDecoder decoder = new RegisteredObjectDecoder();
		decoder.registerType(ObjectCoderFactory.byteArrayDecoder(), 1);
		
		NetworkedMethodReceiver receiver = new NetworkedMethodReceiver(socket.getInputStream(), decoder);
		receiver.registerType(APPEND, 1);
		
		thread = new NetworkedMethodReceiverThread(receiver);
		
		file = new File(path);
		file.createNewFile();
	}
	
	public void start() throws IOException {
		thread.start();
	}
	
	private final InstanceMethod APPEND = new InstanceMethod(this, "append", byte[].class);
	public void append(byte[] data) throws IOException {
		OutputStream out = new FileOutputStream(file, true);
		out.write(data);
		out.close();
	}
	
}
