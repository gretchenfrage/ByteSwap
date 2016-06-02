package com.phoenixkahlo.networkingcorenew;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Broadcasting side of coupled method network.
 */
public class NetworkedMethodBroadcaster {

	private Map<InstanceMethod, Integer> methods = new HashMap<InstanceMethod, Integer>();
	private ObjectEncoder encoder;
	private OutputStream out;
	private Random random = new Random();
	
	public NetworkedMethodBroadcaster(OutputStream out, ObjectEncoder encoder) {
		this.out = out;
		this.encoder = encoder;
	}
	
	public void broadcast(InstanceMethod caller, Object ... args) throws IOException {
		SerializationUtils.writeInt(methods.get(caller), out);
		for (Object arg : args) {
			SerializationUtils.writeAny(arg, encoder, out);
		}
	}
	
	public void register(InstanceMethod method, int header) {
		methods.put(method, header);
	}
	
	public int register(InstanceMethod method) {
		int header = random.nextInt();
		register(method, header);
		return header;
	}
	
}
