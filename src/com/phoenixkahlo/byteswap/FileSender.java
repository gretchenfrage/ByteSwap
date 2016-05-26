package com.phoenixkahlo.byteswap;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * Represents a file being sent across a ByteSwapConnection
 */
public class FileSender {

	private ByteSwapConnection connection;
	private Map<String, File> toSend;
	private InputStream currentIn;
	private String currentPath;
	private byte[] buffer = new byte[1024];
	
	public FileSender(ByteSwapConnection connection, File file) {
		this.connection = connection;
		toSend = FileUtils.cacheDir(file);
	}
	
	public void tick() {
		if (currentIn == null) {
			currentPath = connect
		}
	}
	
}
