package com.phoenixkahlo.testing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.phoenixkahlo.networkingcorenew.BadDataException;
import com.phoenixkahlo.networkingcorenew.InstanceMethod;
import com.phoenixkahlo.networkingcorenew.NetworkedMethodBroadcaster;
import com.phoenixkahlo.networkingcorenew.NetworkedMethodReceiver;
import com.phoenixkahlo.networkingcorenew.NetworkedMethodReceiverThread;
import com.phoenixkahlo.networkingcorenew.ObjectDecoder;
import com.phoenixkahlo.networkingcorenew.ObjectEncoder;

public class NetworkingCoreTester2 {

	public static void main(String[] args) throws IOException {
		new NetworkingCoreTester2();
	}
	
	public NetworkingCoreTester2() throws IOException {
		ObjectEncoder encoder = new ObjectEncoder() {

			@Override
			public boolean canEncode(Object obj) {
				return false;
			}

			@Override
			public void encode(Object obj, OutputStream out) throws IOException, IllegalArgumentException {
				throw new IllegalArgumentException();
			}
			
		};
		ObjectDecoder decoder = new ObjectDecoder() {

			@Override
			public Object decode(InputStream in) throws IOException, BadDataException {
				throw new BadDataException();
			}
			
		};
		System.out.println("created blank encoder and decoder");
		
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
		System.out.println("created connected sockets");
		
		broadcaster =
				new NetworkedMethodBroadcaster(socket1.getOutputStream(), encoder);
		NetworkedMethodReceiver receiver = 
				new NetworkedMethodReceiver(socket2.getInputStream(), decoder);
		System.out.println("created broadcaster and receiver");
		
		NetworkedMethodReceiverThread thread = new NetworkedMethodReceiverThread(receiver);
		thread.start();
		System.out.println("created receiver thread");
		
		broadcaster.register(BROADCAST_SPEAK, 1);
		receiver.register(RECEIVE_SPEAK, 1);
		System.out.println("coupled methods");
		
		broadcastSpeak("hello world!", 5);
		
		System.out.println("about to kill!");
		thread.kill();
		System.out.println("finsihed!");
	}
	
	NetworkedMethodBroadcaster broadcaster;
	
	private final InstanceMethod BROADCAST_SPEAK =
			new InstanceMethod(this, "broadcastSpeak", String.class, int.class);
	public void broadcastSpeak(String str, int times) throws IOException {
		broadcaster.broadcast(BROADCAST_SPEAK, str, times);
	}
	
	private final InstanceMethod RECEIVE_SPEAK = 
			new InstanceMethod(this, "receiveSpeak", String.class, int.class);
	public void receiveSpeak(String str, int times) {
		for (int i = 0; i < times; i++) {
			System.out.println(str);
		}
	}

}
