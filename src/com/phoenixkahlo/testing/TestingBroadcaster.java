package com.phoenixkahlo.testing;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import com.phoenixkahlo.networkingcore.NetworkedMethodBroadcaster;
import com.phoenixkahlo.networkingcore.SerializationUtils;
import com.phoenixkahlo.networkingcore.TypeObjectEncoder;

public class TestingBroadcaster extends NetworkedMethodBroadcaster {

	public TestingBroadcaster(OutputStream out, Properties methodConfig) {
		super(out, methodConfig);
		registerType(new TypeObjectEncoder<StringBox>(StringBox.class,
				(StringBox box, OutputStream o) -> box.encode(o)
				), (short) 1);
		registerType(new TypeObjectEncoder<int[]>(int[].class,
				new TypeObjectEncoder.TypeEncoder<int[]>() {

					@Override
					public void encode(int[] obj, OutputStream out) throws IOException {
						SerializationUtils.writeInt(obj.length, out);
						for (int n : obj) {
							SerializationUtils.writeInt(n, out);
						}
					}
					
				}), (short) 2);
	}
	
	@NetworkedMethod
	public void print(String str, int times) {
		broadcast("print(java.lang.String,int)", str, times);
	}
	
	@NetworkedMethod
	public void printStringBox(StringBox box) {
		broadcast("printStringBox(com.phoenixkahlo.testing.StringBox)", box);
	}
	
}
