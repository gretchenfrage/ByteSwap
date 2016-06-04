package com.phoenixkahlo.arbitraryzombie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class ArbitraryLoading {
	
	public static void main(String[] args) throws Exception {
		// Create source code
		String arbitrary =
				  "for (int i = 0; i < 5; i++) {"
				+ "    System.out.println(\"Hello World!\");"
				+ "}";
		String name = "Arbitrary";
		String src =
				  "public class " + name + " {"
				+ "    public static void invoke() {"
				+ arbitrary
				+ "    }"
				+ "}";
		
		// Write to file
		File srcFile = new File(name + ".java");
		srcFile.createNewFile();
		OutputStreamWriter out =
				new OutputStreamWriter(new FileOutputStream(srcFile), StandardCharsets.UTF_8);
		out.write(src);
		out.close();
		
		// Compile
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, srcFile.getPath());
		
		// Serialize
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		File compiled = new File(name + ".class");
		byte[] buffer = new byte[(int) compiled.length()];
		InputStream in = new FileInputStream(compiled);
		try {
			if (in.read(buffer) < compiled.length())
				throw new RuntimeException("didn't read whole file");
		} finally {
			in.close();
		}
		map.put(name, buffer);
		
		URLClassLoader loader = new ByteClassLoader(
				new URL[0],
				ArbitraryLoading.class.getClassLoader(),
				map);
		Class<?> clazz = loader.loadClass("Arbitrary");
		loader.close();
		Method method = clazz.getMethod("invoke");
		
		// Invoke
		method.invoke(null);
		/*
		// Load
		URLClassLoader loader = URLClassLoader.newInstance(new URL[] {root.toURI().toURL()});
		Class<?> clazz = loader.loadClass("Arbitrary");
		Method method = clazz.getMethod("invoke");
		
		// Invoke
		method.invoke(null);
		*/
	}
	
	/**
	 * @return a dedicated application directory path, dependent on the operating system
	 */
	public static String getAppDirPath(String folderName) {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win"))
			return System.getenv("APPDATA") + "\\" + folderName;
		else if (os.contains("mac"))
			return System.getProperty("user.home") + "/Library/Application Support/" + folderName;
		else if (os.contains("nux"))
			return System.getProperty("user.home") + "/." + folderName;
		else
			return System.getProperty("user.dir" + File.separator + folderName);
	}

}