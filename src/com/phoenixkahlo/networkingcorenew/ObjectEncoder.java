package com.phoenixkahlo.networkingcorenew;

import java.io.IOException;
import java.io.OutputStream;

public interface ObjectEncoder {

	boolean canEncode(Object obj);
	
	void encode(Object obj, OutputStream out) throws IOException, IllegalArgumentException;
	
}
