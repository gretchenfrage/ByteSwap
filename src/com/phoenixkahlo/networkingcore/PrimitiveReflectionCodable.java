package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Uses reflection to automagically provide methods of encoding and decoding subclasses' 
 * primitive fields.
 */
public abstract class PrimitiveReflectionCodable {

	public PrimitiveReflectionCodable() {}
	
	public PrimitiveReflectionCodable(InputStream in) throws IOException {
		try {
			for (Field field : ReflectionUtils.getSerializableFields(getClass())) {
				field.setAccessible(true);
				Class<?> type = field.getType();
				field.set(this, SerializationUtils.readType(type, in));
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void encode(OutputStream out) throws IOException {
		try {
			for (Field field : ReflectionUtils.getSerializableFields(getClass())) {
				field.setAccessible(true);
				SerializationUtils.writeAny(field.get(this), out);
			}
		} catch (Exception e) {}
	}
	
}
