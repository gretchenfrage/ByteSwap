package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Uses reflection to automagically provide methods of encoding and decoding subclasses' 
 * fields, which may use an ObjectEncoder or ObjectDecoder.
 */
public abstract class RobustReflectionCodable {
	
	public RobustReflectionCodable() {}
	
	public RobustReflectionCodable(ObjectDecoder decoder, InputStream in) throws IOException, BadDataException {
		try {
			for (Field field : ReflectionUtils.getSerializableFields(getClass())) {
				field.setAccessible(true);
				Class<?> type = field.getType();
				field.set(this, SerializationUtils.readType(type, decoder, in));
			}
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public void encode(ObjectEncoder encoder, OutputStream out) throws IOException {
		try {
			for (Field field : ReflectionUtils.getSerializableFields(getClass())) {
				field.setAccessible(true);
				SerializationUtils.writeAny(field.get(this), encoder, out);
			}
		} catch (Exception e) {}
	}
	
}
