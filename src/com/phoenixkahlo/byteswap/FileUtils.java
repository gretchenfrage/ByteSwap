package com.phoenixkahlo.byteswap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {

	public static Map<String, File> cacheDir(File file) {
		Map<String, File> map = new HashMap<String, File>();
		cacheDir(file, map);
		return map;
	}
	
	private static void cacheDir(File file, Map<String, File> map) {
		if (file.isDirectory()) {
			for (File sub : file.listFiles()) {
				cacheDir(sub, map);
			}
		} else {
			map.put(file.getName(), file);
		}
	}
	
}
