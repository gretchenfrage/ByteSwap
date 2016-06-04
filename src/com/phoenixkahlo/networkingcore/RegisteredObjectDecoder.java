package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Combines multiple types of ObjectDecoder, differentiating them with int headers.
 */
public class RegisteredObjectDecoder implements ObjectDecoder {

	private Map<Integer, ObjectDecoder> types = new HashMap<Integer, ObjectDecoder>();
	
	public void registerType(ObjectDecoder decoder, int header) {
		types.put(header, decoder);
	}
	
	@Override
	public Object decode(InputStream in) throws IOException, BadDataException {
		ObjectDecoder decoder = types.get(SerializationUtils.readInt(in));
		if (decoder == null) throw new BadDataException("Invalid header");
		return decoder.decode(in);
	}
	
}
