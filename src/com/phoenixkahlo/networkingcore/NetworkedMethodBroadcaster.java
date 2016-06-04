package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Broadcasting side of coupled method network.
 */
public class NetworkedMethodBroadcaster {

	private Map<Object, Integer> methods = new HashMap<Object, Integer>();
	private ObjectEncoder encoder;
	private OutputStream out;
	private Random random = new Random();
	
	public NetworkedMethodBroadcaster(OutputStream out, ObjectEncoder encoder) {
		this.out = out;
		this.encoder = encoder;
	}
	
	public synchronized void broadcast(Object caller, Object ... args) throws IOException {
		SerializationUtils.writeInt(methods.get(caller), out);
		for (Object arg : args) {
			SerializationUtils.writeAny(arg, encoder, out);
		}
	}
	
	public void registerType(Object method, int header) {
		methods.put(method, header);
	}
	
	public int registerType(Object method) {
		int header = random.nextInt();
		registerType(method, header);
		return header;
	}
	
}
