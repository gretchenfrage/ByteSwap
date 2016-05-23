package com.phoenixkahlo.testing;

import java.io.IOException;
import java.io.InputStream;

import com.phoenixkahlo.networkingcore.ReflectionCodable;

public class StringBox extends ReflectionCodable {

	private String string;
	
	public StringBox(String string) {
		this.string = string;
	}
	
	public StringBox(InputStream in) throws IOException {
		super(in);
	}
	
	@Override
	public String toString() {
		return "StringBox: " + string;
	}

}
