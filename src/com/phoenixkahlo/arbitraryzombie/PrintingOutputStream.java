package com.phoenixkahlo.arbitraryzombie;

import java.io.IOException;
import java.io.OutputStream;

public class PrintingOutputStream extends OutputStream {

	private OutputStream out;
	
	public PrintingOutputStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		//System.out.println("WRITING:" + b);
		out.write(b);
	}
	
}
