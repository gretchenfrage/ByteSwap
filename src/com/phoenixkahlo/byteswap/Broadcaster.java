package com.phoenixkahlo.byteswap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import com.phoenixkahlo.networkingcore.InstanceMethod;
import com.phoenixkahlo.networkingcore.NetworkedMethodBroadcaster;
import com.phoenixkahlo.networkingcore.ObjectCoderFactory;
import com.phoenixkahlo.networkingcore.RegisteredObjectEncoder;

public class Broadcaster {

	/**
	 * @param args {ip, port, path}
	 */
	public static void main(String[] args) throws IOException {
		args = new String[] {"localhost", "47953", "/Users/Phoenix/Desktop/splines.png"};
		
		// Parse args
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		String path = args[2];
		
		// Create socket
		Socket socket = new Socket(ip, port);
		
		// Create network
		Broadcaster broadcaster = new Broadcaster(path);
		RegisteredObjectEncoder encoder = new RegisteredObjectEncoder();
		encoder.registerType(ObjectCoderFactory.byteArrayEncoder(), 1);
		NetworkedMethodBroadcaster network = 
				new NetworkedMethodBroadcaster(socket.getOutputStream(), encoder);
		network.registerType(broadcaster.APPEND, 1);
		network.registerType(broadcaster.END, 2);
		broadcaster.broadcaster = network;
		
		// Start
		System.out.println("beginning broadcaster");
		broadcaster.start();
		System.out.println("done");
	}
	
	private File file;
	private NetworkedMethodBroadcaster broadcaster;
	
	public Broadcaster(String path) {
		file = new File(path);
	}
	
	public void start() throws IOException {
		byte[] buffer = new byte[(int) file.length()];
		FileInputStream in = new FileInputStream(file);
		in.read(buffer);
		in.close();
		append(buffer);
		end();
	}
	
	public final InstanceMethod APPEND = new InstanceMethod(this, "append", byte[].class);
	public void append(byte[] data) throws IOException {
		System.out.println("append");
		broadcaster.broadcast(APPEND, new Object[] {data});
	}
	
	public final InstanceMethod END = new InstanceMethod(this, "end");
	public void end() throws IOException {
		System.out.println("end");
		broadcaster.broadcast(END);
	}
	
}
