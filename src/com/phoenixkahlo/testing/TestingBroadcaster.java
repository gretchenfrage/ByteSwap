package com.phoenixkahlo.testing;

import java.io.OutputStream;
import java.util.Properties;

import com.phoenixkahlo.networkingcore.NetworkedMethodBroadcaster;

public class TestingBroadcaster extends NetworkedMethodBroadcaster {

	public TestingBroadcaster(OutputStream out, Properties methodConfig, Properties objectConfig) {
		super(out, methodConfig);
		registerEncodableTypes(objectConfig);
	}
	
	@NetworkedMethod
	public void print(String str, int times) {
		broadcast("print(java.lang.String,int)", str, times);
	}
	
	@NetworkedMethod
	public void printStringBox(StringBox box) {
		broadcast("printStringBox(networkedobjectstesting.StringBox)", box);
	}
	
}
