package com.phoenixkahlo.byteswapold;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.function.Consumer;

public class FileSender {

	private static final int PACKAGE_SIZE = 50_000; // Send data in 50kB packages
	
	private Consumer<? super FileSender> remover;
	private SimplifiedFileStructure files;
	private Stack<String> toSend = new Stack<String>();
	private long transferID;
	private boolean initialized = false;
	private String cachedPath;
	private InputStream cachedIn;
	
	public FileSender(File base, long transferID, Consumer<? super FileSender> remover) {
		this.remover = remover;
		this.transferID = transferID;
		files = new SimplifiedFileStructure(base);
		for (String str : files.getAllFilePaths()) {
			toSend.add(str);
		}
	}
	
	public void tick(ByteSwapConnection connection) throws IOException {
		ByteSwapConnection.ByteSwapConnectionBroadcaster broadcaster = connection.getBroadcaster();
		if (!initialized) {
			broadcaster.setupReceiver(
					transferID,
					files.getAllFilePaths(),
					files.getAllFolderPaths()
					);
			initialized = true;
		}
		if (cachedPath == null) {
			if (toSend.isEmpty()) {
				remover.accept(this);
				return;
			}
			cachedPath = toSend.pop();
			try {
				cachedIn = new FileInputStream(files.getFile(cachedPath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		byte[] data = new byte[Integer.min(cachedIn.available(), PACKAGE_SIZE)];
		cachedIn.read(data);
		broadcaster.appendToReceiver(transferID, cachedPath, data);
		if (cachedIn.available() == 0)
			cachedPath = null;
	}
	
}
