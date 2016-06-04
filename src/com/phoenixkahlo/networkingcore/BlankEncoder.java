package com.phoenixkahlo.networkingcore;

import java.io.OutputStream;

public class BlankEncoder implements ObjectEncoder {

	@Override
	public boolean canEncode(Object obj) {
		return false;
	}

	@Override
	public void encode(Object obj, OutputStream out) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}

}
