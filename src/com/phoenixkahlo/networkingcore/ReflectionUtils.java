package com.phoenixkahlo.networkingcore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
	 * @return all fields of clazz, including invisible and inherited fields, except for
	 * fields marked as transient.
	 */
	public static List<Field> getSerializableFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		addSerializableFields(fields, clazz);
		return fields;
	}
	
	private static void addSerializableFields(List<Field> fields, Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			if (!Modifier.isTransient(field.getModifiers()))
				fields.add(field);
		}
		if (clazz != Object.class)
			addSerializableFields(fields, clazz.getSuperclass());
	}
	
}
