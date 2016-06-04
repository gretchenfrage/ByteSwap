package com.phoenixkahlo.arbitraryzombie;

import java.io.IOException;
import java.io.OutputStream;

public class ChoppedOutputStream extends OutputStream {

	private OutputStream out;
	
	public ChoppedOutputStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		System.out.println("WRITING:" + b);
		out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = len; i < len + off; i++) {
			out.write(b[i]);
		}
	}
	
}
