package com.phoenixkahlo.networkingcore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InstanceMethod implements GeneralFunction {

	private Method method;
	private Object object;
	
	public InstanceMethod(Object object, String name, Class<?> ... argTypes) {
		this.object = object;
		try {
			method = object.getClass().getMethod(name, argTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Object invoke(Object ... args) throws Exception {
		try {
			return method.invoke(object, args);
		} catch (InvocationTargetException e) {
			throw (Exception) e.getTargetException();
		}
	}
	
	public Class<?>[] getArgTypes() {
		return method.getParameterTypes();
	}
	
	public boolean equals(InstanceMethod other) {
		return method == other.method && object == other.object;
	}
	
}
