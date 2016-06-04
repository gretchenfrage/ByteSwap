package com.phoenixkahlo.byteswap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;

import com.phoenixkahlo.networkingcore.InstanceMethod;
import com.phoenixkahlo.networkingcore.NetworkedMethodBroadcaster;
import com.phoenixkahlo.networkingcore.ObjectCoderFactory;
import com.phoenixkahlo.networkingcore.RegisteredObjectEncoder;

public class BroadcasterNew {

	/**
	 * @param args {ip, port, path}
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		args = new String[] {"localhost", "23456", "/Users/Phoenix/Desktop/splines.png"};
		
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		String path = args[2];
		
		System.out.println("gonna start");
		BroadcasterNew broadcaster = new BroadcasterNew(ip, port, path);
		System.out.println("created broadcaster");
		broadcaster.send();
		System.out.println("done");
	}
	
	private NetworkedMethodBroadcaster broadcaster;
	private File file;
	
	public BroadcasterNew(String ip, int port, String path) throws IOException {
		Socket socket = new Socket(ip, port);
		
		RegisteredObjectEncoder encoder = new RegisteredObjectEncoder();
		encoder.registerType(ObjectCoderFactory.byteArrayEncoder(), 1);
		
		broadcaster = new NetworkedMethodBroadcaster(socket.getOutputStream(), encoder);
		broadcaster.registerType(APPEND, 1);
		
		file = new File(path);
	}
	
	public void send() throws IOException {
		FileInputStream in = new FileInputStream(file);
		if (file.length() > Integer.MAX_VALUE)
			System.out.println(":/");
		byte[] buffer = new byte[(int) file.length()];
		in.read(buffer);
		in.close();
		append(buffer);
	}
	
	private final InstanceMethod APPEND = new InstanceMethod(this, "append", byte[].class);
	public void append(byte[] data) throws IOException {
		broadcaster.broadcast(APPEND, new Object[] {data});
	}
	
}
