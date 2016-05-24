package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RegisteredObjectDecoder implements ObjectDecoder {

	private Map<Short, ObjectDecoder> types = new HashMap<Short, ObjectDecoder>();
	
	public void registerType(ObjectDecoder decoder, short header) {
		types.put(header, decoder);
	}
	
	@Override
	public Object decode(InputStream in) throws IOException, BadDataException {
		ObjectDecoder decoder = types.get(SerializationUtils.readShort(in));
		if (decoder == null) throw new BadDataException("Invalid header");
		return decoder.decode(in);
	}
	
}
