package com.phoenixkahlo.testing;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.phoenixkahlo.byteswapold.SimplifiedFileStructure;

public class SimplifiedFileStructureTester {

	public static void main(String[] args) throws Throwable {
		SimplifiedFileStructure struct = new SimplifiedFileStructure(
				new File("/Users/Phoenix/Desktop/C"));
		
		System.out.println(struct.getAllFilePaths());
		System.out.println(struct.getAllFolderPaths());
		
		System.out.println("Test completed");
	}
	
	public static Object getField(Object obj, String name) throws Exception {
		Field field = obj.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return field.get(obj);
	}
	
	public static Object invoke(Object obj, String methodName, Object ... args) throws Throwable {
		Class<?>[] paramTypes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			paramTypes[i] = args[i].getClass();
		}
		Method method = obj.getClass().getDeclaredMethod(methodName, paramTypes);
		method.setAccessible(true);
		return method.invoke(obj, args);
	}

}
