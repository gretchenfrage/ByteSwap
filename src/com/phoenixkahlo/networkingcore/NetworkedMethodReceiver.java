package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Receiving side of coupled method network.
 */
public class NetworkedMethodReceiver {

	private Map<Integer, InstanceMethod> methods = new HashMap<Integer, InstanceMethod>();
	private InputStream in;
	private Random random = new Random();
	private ObjectDecoder decoder;
	
	public NetworkedMethodReceiver(InputStream in, ObjectDecoder decoder) {
		this.in = in;
		this.decoder = decoder;
	}
	
	public void receive() throws IOException, BadDataException {
		int header = SerializationUtils.readInt(in);
		InstanceMethod method = methods.get(header);
		if (method == null)
			throw new BadDataException("Invalid header received: " + header);
		Class<?>[] argTypes = method.getArgTypes();
		Object[] args = new Object[argTypes.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = SerializationUtils.readType(argTypes[i], decoder, in);
		}
		try {
			method.invoke(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerType(InstanceMethod method, int header) {
		methods.put(header, method);
	}
	
	public int register(InstanceMethod method) {
		int header = random.nextInt();
		registerType(method, header);
		return header;
	}
	
}
