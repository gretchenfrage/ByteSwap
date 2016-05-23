package com.phoenixkahlo.testing;

import java.io.InputStream;
import java.util.Properties;

import com.phoenixkahlo.networkingcore.NetworkedMethodReceiver;

public class TestingReceiver extends NetworkedMethodReceiver {

	public TestingReceiver(InputStream in, Properties methodConfig, Properties objectConfig) {
		super(in, methodConfig);
		registerDecodableTypes(objectConfig);
	}

	@NetworkedMethod
	public void print(String str, int times) {
		for (int i = 0; i < times; i++) {
			System.out.println(str);
		}
	}
	
	@NetworkedMethod
	public void printStringBox(StringBox box) {
		System.out.println(box);
	}
	
}
