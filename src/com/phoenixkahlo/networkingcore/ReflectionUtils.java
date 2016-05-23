package com.phoenixkahlo.networkingcore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

	private ReflectionUtils() {}
	
	public static String methodSignatureString(Method method) {
		StringBuilder builder = new StringBuilder();
		builder.append(method.getName());
		builder.append("(");
		Class<?>[] paramTypes = method.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			builder.append(paramTypes[i].getName());
			if (i < paramTypes.length - 1)
				builder.append(",");
		}
		builder.append(")");
		return builder.toString();
	}
	
	/**
	 * @return all fields of clazz, including invisible and inherited fields.
	 */
	public static List<Field> getAllFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		addAllFields(fields, clazz);
		return fields;
	}
	
	private static void addAllFields(List<Field> fields, Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			fields.add(field);
		}
		if (clazz != Object.class)
			addAllFields(fields, clazz.getSuperclass());
	}
	
}
