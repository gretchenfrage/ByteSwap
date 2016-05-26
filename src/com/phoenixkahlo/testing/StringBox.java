package com.phoenixkahlo.testing;

import java.io.IOException;
import java.io.InputStream;

import com.phoenixkahlo.networkingcore.PrimitiveReflectionCodable;

public class StringBox extends PrimitiveReflectionCodable {

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
