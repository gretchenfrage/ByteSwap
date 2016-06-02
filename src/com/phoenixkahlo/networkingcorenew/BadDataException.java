package com.phoenixkahlo.networkingcorenew;

/**
 * For tfw you receive data from an InputStream that doesn't follow your data protocol.
 */
public class BadDataException extends Exception {

	private static final long serialVersionUID = -4831493768408458104L;

	public BadDataException() {}
	
	public BadDataException(String message) {
		super(message);
	}
	
}
