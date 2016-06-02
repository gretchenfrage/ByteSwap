package com.phoenixkahlo.networkingcorenew;

import java.io.IOException;

public class NetworkedMethodReceiverThread extends Thread {

	private NetworkedMethodReceiver receiver;
	
	public NetworkedMethodReceiverThread(NetworkedMethodReceiver receiver) {
		super("NetworkedMethodReceiverThread for " + receiver);
		this.receiver = receiver;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				receiver.receive();
			}
		} catch (IOException | BadDataException e) {
			disconnected();
		}
	}
	
	/**
	 * Called at the end of this thread's life.
	 */
	protected void disconnected() {}
	
	public void kill() {
		interrupt();
		disconnected();
	}
	
}
