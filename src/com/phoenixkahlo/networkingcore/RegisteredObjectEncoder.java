package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class RegisteredObjectEncoder implements ObjectEncoder {

	private Map<ObjectEncoder, Short> types = new HashMap<ObjectEncoder, Short>();
	
	public void registerType(ObjectEncoder encoder, short header) {
		for (short n : types.values()) {
			if (n == header) throw new IllegalArgumentException("Duplicate headers");
		}
		types.put(encoder, header);
	}
	
	@Override
	public boolean canEncode(Object obj) {
		for (ObjectEncoder encoder : types.keySet()) {
			if (encoder.canEncode(obj)) return true;
		}
		return false;
	}

	@Override
	public void encode(Object obj, OutputStream out) throws IOException, IllegalArgumentException {
		for (ObjectEncoder encoder : types.keySet()) {
			if (encoder.canEncode(obj)) {
				SerializationUtils.writeShort(types.get(encoder), out);
				encoder.encode(obj, out);
				return;
			}
		}
		throw new IllegalArgumentException("Unencodable object: " + obj);
	}
	
}
