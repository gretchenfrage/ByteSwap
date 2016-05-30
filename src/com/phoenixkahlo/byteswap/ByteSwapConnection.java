package com.phoenixkahlo.byteswap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
		
	}
	
	private ByteSwapConnectionBroadcaster broadcaster;
	private ByteSwapConnectionReceiver receiver;
	
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
