package com.phoenixkahlo.arbitraryzombie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class ArbitraryZombieUtils {

	private ArbitraryZombieUtils() {}
	
	public static Class<?> loadClass(String name, byte[] data) {
		class ByteLoader extends URLClassLoader {
			
			String name;
			byte[] data;
			
			ByteLoader(String name, byte[] data) {
				super(new URL[0]);
				this.name = name;
				this.data = data;
			}
			
			@Override
			protected Class<?> findClass(String target) throws ClassNotFoundException {
				if (target.equals(name))
					return defineClass(name, data, 0, data.length);
				else
					throw new RuntimeException("told to find class " + target +
							" but can only load " + name);
			}
			
		}
		
		URLClassLoader loader = new ByteLoader(name, data);
		try {
			Class<?> out = loader.loadClass(name);
			loader.close();
			return out;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	
	public static void writeFileUTF8(String path, String contents) {
		File file = new File(path);
		if (file.getParentFile() != null) file.getParentFile().mkdirs();
		try {
			file.createNewFile();
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file),
					StandardCharsets.UTF_8);
			out.write(contents);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static byte[] readFile(String path) {
		File file = new File(path);
		byte[] buffer = new byte[(int) file.length()];
		try {
			InputStream in = new FileInputStream(file);
			try {
				if (in.read(buffer) < file.length())
					throw new RuntimeException("class not read completely");
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
		return buffer;
	}
	
	public static String compileSrc(String path) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, path);
		return path.substring(0, path.length() - 4) + "class";
	}
	
	public static String codeToClassSrc(String name, String code) {
		StringBuilder builder = new StringBuilder();
		builder.append("public class " + name + " {");
		builder.append("    public static void invoke() {");
		builder.append(code);
		builder.append("    }");
		builder.append("}");
		return builder.toString();
	}
	
	public static void invokeStaticInvokable(Class<?> clazz) {
		try {
			clazz.getMethod("invoke").invoke(null);
		} catch (Exception e) {}
	}
	
}
