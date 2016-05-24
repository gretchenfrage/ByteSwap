package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class RegisteredObjectDecoderOld<E> {

	public static interface Decoder<E> {
		
		E decode(InputStream in) throws IOException;
		
	}
	
	private Map<Short, Decoder<? extends E>> decoders = new HashMap<Short, Decoder<? extends E>>();
	
	public void registerType(Decoder<E> decoder, short header) {
		decoders.put(header, decoder);
	}
	
	public E decode(InputStream in) throws IOException {
		short header = SerializationUtils.readShort(in);
		if (!decoders.containsKey(header))
			throw new RuntimeException("Decodable type not registered.");
		return decoders.get(header).decode(in);
	}
	
}
