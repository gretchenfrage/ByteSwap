package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.phoenixkahlo.networkingcore.SerializationUtils.*;

public class ObjectCoderFactory {

	private ObjectCoderFactory() {}
	
	public static ObjectEncoder byteArrayEncoder() {
		return new TypeObjectEncoder<byte[]>(byte[].class,
				new TypeObjectEncoder.TypeEncoder<byte[]>() {

					@Override
					public void encode(byte[] obj, OutputStream out) throws IOException {
						writeInt(obj.length, out);
						out.write(obj);
					}
			
		});
	}
	
	public static ObjectDecoder byteArrayDecoder() {
		return new ObjectDecoder() {

			@Override
			public Object decode(InputStream in) throws IOException, BadDataException {
				byte[] obj = new byte[readInt(in)];
				in.read(obj);
				return obj;
			}
			
		};
	}
	
}