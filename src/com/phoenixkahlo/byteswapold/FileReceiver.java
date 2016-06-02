package com.phoenixkahlo.byteswapold;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps a SimplifiedFileStructure and receives directives from the ByteSwapConnection from
 * the FileSender to build the file structure.
 */
public class FileReceiver {

	private SimplifiedFileStructure files;
	private String cachedPath;
	private OutputStream cachedOut;
	
	/**
	 * Prompts the user for a location to save
	 */
	public FileReceiver() {
		//TODO: implement file selection properly
		File base = new File("nyah.txt");
		files = new SimplifiedFileStructure(base);
	}
	
	public void createFile(String simplePath) {
		files.createFile(simplePath);
	}
	
	public void createFolder(String simplePath) {
		files.createFolder(simplePath);
	}
	
	public void append(String simplePath, byte[] data) {
		if (cachedPath == null || !cachedPath.equals(simplePath)) {
			cachedPath = simplePath;
			try {
				cachedOut = new FileOutputStream(files.getFile(simplePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			cachedOut.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
