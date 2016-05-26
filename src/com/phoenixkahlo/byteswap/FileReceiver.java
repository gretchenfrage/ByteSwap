package com.phoenixkahlo.byteswap;

import java.io.File;
import java.util.Map;

/**
 * Represents a file being received accross a ByteSwapConnection
 */
public class FileReceiver {
	
	//private File file;
	private Map<String, File> contained;
	
	public FileReceiver() {
		contained = FileUtils.cacheDir("FileReceiver.txt");
	}
	
}
