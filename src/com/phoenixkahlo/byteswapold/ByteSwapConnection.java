package com.phoenixkahlo.byteswapold;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import com.phoenixkahlo.networkingcore.BadDataException;
import com.phoenixkahlo.networkingcore.NetworkedMethodBroadcaster;
import com.phoenixkahlo.networkingcore.NetworkedMethodReceiver;
import com.phoenixkahlo.networkingcore.ObjectDecoder;
import com.phoenixkahlo.networkingcore.SerializationUtils;
import com.phoenixkahlo.networkingcore.TypeObjectEncoder;

/**
 * Connects to other ByteSwapConnections through Sockets. Holds files that is
 * receiving, and files that is sending. Handles both tasks in own threads.
 */
public class ByteSwapConnection extends Thread {

	private static final Properties METHOD_HEADER_PROTOCOL;
	static {
		METHOD_HEADER_PROTOCOL = new Properties();
		METHOD_HEADER_PROTOCOL.setProperty(
				"setupReceiver(long,[Ljava.lang.String;,[Ljava.lang.String;)", "1");
		METHOD_HEADER_PROTOCOL.setProperty(
				"appendToReceiver(long,java.lang.String,[B)", "2");
		METHOD_HEADER_PROTOCOL.setProperty(
				"finishReceiver(long)", "3");
	}
	
	public class ByteSwapConnectionBroadcaster extends NetworkedMethodBroadcaster {

		public ByteSwapConnectionBroadcaster(OutputStream out) {
			super(out, METHOD_HEADER_PROTOCOL);
			registerType(new TypeObjectEncoder<byte[]>(byte[].class,
					new TypeObjectEncoder.TypeEncoder<byte[]>() {
						@Override
						public void encode(byte[] obj, OutputStream out) throws IOException {
							SerializationUtils.writeInt(obj.length, out);
							out.write(obj);
						}
					}), (short) 1);
			registerType(new TypeObjectEncoder<String[]>(String[].class,
					new TypeObjectEncoder.TypeEncoder<String[]>() {
						@Override
						public void encode(String[] obj, OutputStream out) throws IOException {
							SerializationUtils.writeInt(obj.length, out);
							for (String str : obj) {
								SerializationUtils.writeString(str, out);
							}
						}
					}), (short) 2);
		}
		
		@Override
		public void kill() {
			super.kill();
			terminate();
		}
		
		@NetworkedMethod
		public void setupReceiver(long id, String[] files, String[] folders) {
			broadcast("setupReceiver(long,[Ljava.lang.String;,[Ljava.lang.String;)",
					id, files, folders);
		}
		
		@NetworkedMethod
		public void appendToReceiver(long id, String simplePath, byte[] data) {
			broadcast("appendToReceiver(long,java.lang.String,[B)",
					id, simplePath, data);
		}
		
		@NetworkedMethod
		public void finishReceiver(long id) {
			broadcast("finishReceiver(long)", id);
		}
		
	}
	
	public class ByteSwapConnectionReceiver extends NetworkedMethodReceiver {

		public ByteSwapConnectionReceiver(InputStream in) {
			super(in, METHOD_HEADER_PROTOCOL);
			registerType(new ObjectDecoder() {
				@Override
				public Object decode(InputStream in) throws IOException, BadDataException {
					byte[] obj = new byte[SerializationUtils.readInt(in)];
					in.read(obj);
					return obj;
				}
			}, (short) 1);
			registerType(new ObjectDecoder() {
				@Override
				public Object decode(InputStream in) throws IOException, BadDataException {
					String[] obj = new String[SerializationUtils.readInt(in)];
					for (int i = 0; i < obj.length; i++) {
						obj[i] = SerializationUtils.readString(in);
					}
					return obj;
				}
			}, (short) 2);
		}
		
		@Override
		public void kill() {
			super.kill();
			terminate();
		}
		
		@NetworkedMethod
		public void setupReceiver(long id, String[] files, String[] folders) {
			FileReceiver receiver = new FileReceiver();
			receiving.put(id, receiver);
			for (String str : files) {
				receiver.createFile(str);
			}
			for (String str : folders) {
				receiver.createFolder(str);
			}
		}
		
		@NetworkedMethod
		public void appendToReceiver(long id, String simplePath, byte[] data) {
			receiving.get(id).append(simplePath, data);
		}
		
		@NetworkedMethod
		public void finishReceiver(long id) {
			//TODO: implement
			System.out.println(receiving.get(id) + " finished!");
		}
		
	}
	
	private ByteSwapConnectionBroadcaster broadcaster;
	private ByteSwapConnectionReceiver receiver;
	private Map<Long, FileReceiver> receiving = new HashMap<Long, FileReceiver>();
	private List<FileSender> sending = new ArrayList<FileSender>();
	private Random random = new Random();
	
	/**
	 * Creates the connection and starts receiving.
	 */
	public ByteSwapConnection(Socket socket) {
		super("ByteSwapConnection thread");
		try {
			broadcaster = new ByteSwapConnectionBroadcaster(socket.getOutputStream());
			receiver = new ByteSwapConnectionReceiver(socket.getInputStream());
		} catch (IOException e) {
			terminate();
		}
		receiver.start();
		start();
	}
	
	public ByteSwapConnectionBroadcaster getBroadcaster() {
		return broadcaster;
	}
	
	@Override
	public void run() {
		while (true) {
			if (sending.isEmpty()) {
				try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {}
			}
			for (int i = sending.size() - 1; i >= 0; i--) {
				try {
					sending.get(i).tick(this);
				} catch (IOException e) {
					e.printStackTrace();
					terminate();
				}
			}
		}
	}
	
	
	public void sendFile(File base) {
		sending.add(new FileSender(base, random.nextLong(), sending::remove));
		interrupt();
	}
	
	/**
	 * Close streams and remove from Swing.
	 */
	public void terminate() {
		if (broadcaster.isAlive()) broadcaster.kill();
		if (receiver.isAlive()) receiver.kill();
		interrupt();
	}
	
}
