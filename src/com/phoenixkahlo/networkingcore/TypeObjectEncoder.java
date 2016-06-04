package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Encodes a single class of object. The encoder is designed for setup with anonymous/lambda/
 * pass-through classes.
 * @param <E> what type of object it's encoding
 */
public class TypeObjectEncoder<E> implements ObjectEncoder {

	@FunctionalInterface
	public static interface TypeEncoder<E> {
		
		void encode(E obj, OutputStream out) throws IOException;
		
	}
	
	private Class<E> type;
	private TypeEncoder<E> typeEncoder;
	
	public TypeObjectEncoder(Class<E> type, TypeEncoder<E> typeEncoder) {
		this.type = type;
		this.typeEncoder = typeEncoder;
	}
	
	@Override
	public boolean canEncode(Object obj) {
		return obj.getClass() == type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void encode(Object obj, OutputStream out) throws IOException, IllegalArgumentException {
		if (!canEncode(obj))
			throw new IllegalArgumentException(obj + " is not " + type);
		typeEncoder.encode((E) obj, out);
	}

}
