package com.phoenixkahlo.networkingcore;

import java.io.IOException;

public class NetworkedMethodReceiverThread extends Thread {

	private NetworkedMethodReceiver receiver;
	
	public NetworkedMethodReceiverThread(NetworkedMethodReceiver receiver) {
		super("NetworkedMethodReceiverThread for " + receiver);
		this.receiver = receiver;
	}
	
	@Override
	public void run() {
		System.out.println("beginning receiver thread");
		try {
			while (true) {
				receiver.receive();
			}
		} catch (IOException | BadDataException e) {
			e.printStackTrace();
			disconnected();
		}
	}
	
	/**
	 * Called at the end of this thread's life.
	 */
	protected void disconnected() {
		System.out.println("disconnected");
	}
	
	public void kill() {
		interrupt();
		disconnected();
	}
	
}
