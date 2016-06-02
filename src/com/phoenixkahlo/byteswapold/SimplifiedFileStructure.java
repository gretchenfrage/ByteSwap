package com.phoenixkahlo.byteswapold;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a structure of files with localized and platform independent paths.
 */
public class SimplifiedFileStructure {

	private static final String DELIMITER = "/";
	
	private String trailingPath;
	private Map<String, File> files = new HashMap<String, File>();
	private Map<String, File> folders = new HashMap<String, File>();
	
	public SimplifiedFileStructure(File base) {
		String[] pathComps = base.getPath().split(File.separator);
		trailingPath = String.join(
				File.separator, Arrays.copyOf(pathComps, pathComps.length - 1)) + File.separator;
		
		recursiveConstruct(base);
	}
	
	private void recursiveConstruct(File base) {
		if (base.isFile()) {
			files.put(realToSimplePath(base.getPath()), base);
		} else {
			folders.put(realToSimplePath(base.getPath()), base);
			for (File sub : base.listFiles()) {
				recursiveConstruct(sub);
			}
		}
	}
	
	private String realToSimplePath(String realPath) {
		return String.join(DELIMITER, realPath.substring(trailingPath.length()).split(File.separator));
	}
	
	private String simpleToRealPath(String simplePath) {
		return trailingPath + String.join(File.separator, simplePath.split(DELIMITER));
	}
	
	public void createFile(String simplePath) {
		File file = new File(simpleToRealPath(simplePath));
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		files.put(simplePath, file);
	}
	
	public void createFolder(String simplePath) {
		File folder = new File(simpleToRealPath(simplePath));
		folder.mkdirs();
		folders.put(simplePath, folder);
	}
	
	public File getFile(String simplePath) {
		return files.get(simplePath);
	}
	
	public String[] getAllFilePaths() {
		String[] out = new String[files.size()];
		files.keySet().toArray(out);
		return out;
		//return new ArrayList<String>(files.keySet());
	}
	
	public String[] getAllFolderPaths() {
		String[] out = new String[folders.size()];
		folders.keySet().toArray(out);
		return out;
		//return new ArrayList<String>(folders.keySet());
	}
	
}
