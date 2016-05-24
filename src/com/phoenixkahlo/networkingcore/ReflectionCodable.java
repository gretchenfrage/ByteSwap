package com.phoenixkahlo.networkingcore;

import static com.phoenixkahlo.networkingcore.SerializationUtils.readBoolean;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readChar;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readDouble;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readFloat;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readInt;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readLong;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readShort;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readString;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeBoolean;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeChar;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeDouble;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeFloat;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeInt;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeLong;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeShort;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

import com.phoenixkahlo.networkingcore.RegisteredObjectEncoderOld.Encodable;

/**
 * Encodable and decodable that uses reflection to automagically code all primitive fields.
 */
public abstract class ReflectionCodable implements Encodable {

	public ReflectionCodable() {}
	
	public ReflectionCodable(InputStream in) throws IOException {
		try {
			for (Field field : ReflectionUtils.getAllFields(getClass())) {
				field.setAccessible(true);
				Class<?> type = field.getType();
				if (type == short.class)
					field.set(this, readShort(in));
				else if (type == int.class)
					field.set(this, readInt(in));
				else if (type == long.class)
					field.set(this, readLong(in));
				else if (type == char.class)
					field.set(this, readChar(in));
				else if (type == float.class)
					field.set(this, readFloat(in));
				else if (type == double.class)
					field.set(this, readDouble(in));
				else if (type == boolean.class)
					field.set(this, readBoolean(in));
				else if (type == String.class)
					field.set(this, readString(in));
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void encode(OutputStream out) throws IOException {
		try {
			for (Field field : ReflectionUtils.getAllFields(getClass())) {
				field.setAccessible(true);
				Class<?> type = field.getType();
				if (type == short.class)
					writeShort((Short) field.get(this), out);
				else if (type == int.class)
					writeInt((Integer) field.get(this), out);
				else if (type == long.class)
					writeLong((Long) field.get(this), out);
				else if (type == char.class)
					writeChar((Character) field.get(this), out);
				else if (type == float.class)
					writeFloat((Float) field.get(this), out);
				else if (type == double.class)
					writeDouble((Double) field.get(this), out);
				else if (type == boolean.class)
					writeBoolean((Boolean) field.get(this), out);
				else if (type == String.class)
					writeString((String) field.get(this), out);
			}
		} catch (Exception e) {}
	}

	
	
}
