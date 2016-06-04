package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.InputStream;

public class BlankDecoder implements ObjectDecoder {

	@Override
	public Object decode(InputStream in) throws IOException, BadDataException {
		throw new BadDataException();
	}

}
