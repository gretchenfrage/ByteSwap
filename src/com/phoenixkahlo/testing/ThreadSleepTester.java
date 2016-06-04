package com.phoenixkahlo.testing;

import java.io.IOException;
import java.io.InputStream;

import com.phoenixkahlo.networkingcore.BadDataException;
import com.phoenixkahlo.networkingcore.NetworkedMethodReceiver;
import com.phoenixkahlo.networkingcore.NetworkedMethodReceiverThread;
import com.phoenixkahlo.networkingcore.ObjectDecoder;

public class ThreadSleepTester {

	public static void main(String[] args) throws IOException, BadDataException, InterruptedException {
		InputStream in = new InputStream() {
			
			@Override
			public int read() throws IOException {
				try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return 0;
			}
			
		};
		ObjectDecoder decoder = new ObjectDecoder() {
			
			@Override
			public Object decode(InputStream in) throws IOException, BadDataException {
				return null;
			}
		};
		NetworkedMethodReceiver receiver = new NetworkedMethodReceiver(in, decoder);
		NetworkedMethodReceiverThread thread = new NetworkedMethodReceiverThread(receiver);
		thread.start();
		System.out.println("its");
		Thread.sleep(5000);
		thread.kill();
		System.out.println("dat boi!");
	}
	
}
