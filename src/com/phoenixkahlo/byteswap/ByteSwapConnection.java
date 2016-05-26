package com.phoenixkahlo.byteswap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.phoenixkahlo.networkingcore.NetworkedMethodBroadcaster;
import com.phoenixkahlo.networkingcore.NetworkedMethodReceiver;

/**
 * Connects to other ByteSwapConnections through Sockets. Holds files that is
 * receiving, and files that is sending. Handles both tasks in own threads.
 */
public class ByteSwapConnection extends Thread {

	private static final Properties METHOD_HEADER_PROTOCOL;
	static {
		METHOD_HEADER_PROTOCOL = new Properties();
		METHOD_HEADER_PROTOCOL.setProperty("createFileReceiver(java.lang.String)", "1");
	}
	
	private class ByteSwapConnectionBroadcaster extends NetworkedMethodBroadcaster {

		public ByteSwapConnectionBroadcaster(OutputStream out) {
			super(out, METHOD_HEADER_PROTOCOL);
		}
		
		@Override
		public void disconnect() {
			super.disconnect();
			terminate();
		}
		
		@NetworkedMethod
		public void createFileReceiver(String id) {
			broadcast("createFileReceiver(java.lang.String)", id);
		}
		
	}
	
	private class ByteSwapConnectionReceiver extends NetworkedMethodReceiver {

		public ByteSwapConnectionReceiver(InputStream in) {
			super(in, METHOD_HEADER_PROTOCOL);
		}
		
		@Override
		public void disconnect() {
			super.disconnect();
			terminate();
		}
		
		@NetworkedMethod
		public void createFileReceiver(String id) {
			receiving.put(id, new FileReceiver());
		}
		
	}
	
	private ByteSwapConnectionBroadcaster broadcaster;
	private ByteSwapConnectionReceiver receiver;
	private List<FileSender> sending = new ArrayList<FileSender>();
	private Map<String, FileReceiver> receiving = new HashMap<String, FileReceiver>();
	
	/**
	 * Creates the connection and starts receiving.
	 */
	public ByteSwapConnection(Socket socket) {
		try {
			broadcaster = new ByteSwapConnectionBroadcaster(socket.getOutputStream());
			receiver = new ByteSwapConnectionReceiver(socket.getInputStream());
		} catch (IOException e) {
			terminate();
		}
		receiver.start();
		start();
	}
	
	@Override
	public void run() {
		while (true) {
			for (int i = sending.size() - 1; i >= 0; i--) {
				sending.get(i).tick();
			}
		}
	}
	
	/**
	 * Close streams and remove from Swing.
	 */
	public void terminate() {
		if (broadcaster.isAlive()) broadcaster.disconnect();
		if (receiver.isAlive()) receiver.disconnect();
		interrupt();
	}
	
}
