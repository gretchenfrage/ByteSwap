package com.phoenixkahlo.arbitraryzombie;

import java.io.IOException;
import java.io.InputStream;

public class PrintingInputStream extends InputStream {

	private InputStream in;
	
	public PrintingInputStream(InputStream in) {
		this.in = in;
	}
	
	@Override
	public int read() throws IOException {
		int read = in.read();
		//System.out.println("READ:" + read);
		return read;
	}

}
