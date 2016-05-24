package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.phoenixkahlo.networkingcore.RegisteredObjectEncoderOld.Encodable;


public class SerializationUtils {

	private SerializationUtils() {}
	
	public static void writeShort(short n, OutputStream out) throws IOException {
		out.write((n >> 8) & 0xFF);
		out.write(n & 0xFF);
	}
	
	public static short readShort(InputStream in) throws IOException {
		byte[] buffer = new byte[2];
		in.read(buffer);
		return (short) ((buffer[0] << 8) | buffer[1]);
	}
	
	public static void writeInt(int n, OutputStream out) throws IOException {
		out.write((n >> 24) & 0xFF);
		out.write((n >> 16) & 0xFF);
		out.write((n >> 8) & 0xFF);
		out.write(n & 0xFF);
	}
	
	public static int readInt(InputStream in) throws IOException {
		byte[] buffer = new byte[4];
		in.read(buffer);
		return (buffer[0] << 24) | (buffer[1] << 16) | (buffer[2] << 8) | buffer[3];
	}
	
	public static void writeLong(long n, OutputStream out) throws IOException {
		out.write((int) ((n >> 56) & 0xFF));
		out.write((int) ((n >> 48) & 0xFF));
		out.write((int) ((n >> 40) & 0xFF));
		out.write((int) ((n >> 32) & 0xFF));
		out.write((int) ((n >> 24) & 0xFF));
		out.write((int) ((n >> 8) & 0xFF));
		out.write((int) (n & 0xFF));
	}
	
	public static long readLong(InputStream in) throws IOException {
		long[] buffer = new long[8];
		for (int i = 0; i < 8; i++) {
			buffer[i] = in.read();
		}
		return (buffer[0] << 56) | (buffer[1] << 48) | (buffer[2] << 40) | (buffer[3] << 32) |
				(buffer[4] << 24) | (buffer[5] << 16) | (buffer[6] << 8) | buffer[7];
	}
	
	public static void writeChar(char n, OutputStream out) throws IOException {
		writeShort((short) n, out);
	}
	
	public static char readChar(InputStream in) throws IOException {
		return (char) readShort(in);
	}
	
	public static void writeFloat(float n, OutputStream out) throws IOException {
		writeInt(Float.floatToIntBits(n), out);
	}
	
	public static float readFloat(InputStream in) throws IOException {
		return Float.intBitsToFloat(readInt(in));
	}
	
	public static void writeDouble(double n, OutputStream out) throws IOException {
		writeLong(Double.doubleToLongBits(n), out);
	}
	
	public static double readDouble(InputStream in) throws IOException {
		return Double.longBitsToDouble(readLong(in));
	}
	
	public static void writeBoolean(boolean b, OutputStream out) throws IOException {
		out.write(b ? 1 : 0);
	}
	
	public static boolean readBoolean(InputStream in) throws IOException {
		return in.read() != 0;
	}
	
	public static void writeByteArray(byte[] arr, OutputStream out) throws IOException {
		writeInt(arr.length, out);
		out.write(arr);
	}
	
	public static byte[] readByteArray(InputStream in) throws IOException {
		byte[] out = new byte[readInt(in)];
		in.read(out);
		return out;
	}
	
	public static void writeString(String str, OutputStream out) throws IOException {
		writeByteArray(str.getBytes(StandardCharsets.UTF_8), out);
	}
	
	public static String readString(InputStream in) throws IOException {
		return new String(readByteArray(in), StandardCharsets.UTF_8);
	}
	
	public static void writeAny(Object obj, OutputStream out) throws IOException, RuntimeException {
		if (obj instanceof Short)
			writeShort((Short) obj, out);
		else if (obj instanceof Integer)
			writeInt((Integer) obj, out);
		else if (obj instanceof Long)
			writeDouble((Long) obj, out);
		else if (obj instanceof Character)
			writeChar((Character) obj, out);
		else if (obj instanceof Float)
			writeFloat((Float) obj, out);
		else if (obj instanceof Double)
			writeDouble((Double) obj, out);
		else if (obj instanceof Boolean)
			writeBoolean((Boolean) obj, out);
		else if (obj instanceof String)
			writeString((String) obj, out);
		else
			throw new RuntimeException("Directed to write object of unknown type");
	}
	
	public static void writeAny(Object obj, OutputStream out, RegisteredObjectEncoderOld encoder)
			throws IOException {
		try {
			writeAny(obj, out);
		} catch (RuntimeException e) {
			encoder.encode((Encodable) obj, out);
		}
	}
	
	public static Object readType(Class<?> type, InputStream in) throws IOException, RuntimeException {
		if (type == short.class)
			return readShort(in);
		else if (type == int.class)
			return readInt(in);
		else if (type == long.class)
			return readLong(in);
		else if (type == char.class)
			return readChar(in);
		else if (type == float.class)
			return readFloat(in);
		else if (type == double.class)
			return readDouble(in);
		else if (type == boolean.class)
			return readBoolean(in);
		else if (type == String.class)
			return readString(in);
		else
			throw new RuntimeException("Invalid read type");
	}
	
}
