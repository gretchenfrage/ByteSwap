package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface ObjectDecoder {

	Object decode(InputStream in) throws IOException, BadDataException;
	
}
