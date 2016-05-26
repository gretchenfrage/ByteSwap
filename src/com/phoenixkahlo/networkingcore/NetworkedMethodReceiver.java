package com.phoenixkahlo.networkingcore;

import static com.phoenixkahlo.networkingcore.SerializationUtils.readShort;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Reads method calls and their arguments from an InputStream, and activates the corresponding
 * methods in its implementor.
 * Symmetrical to NetworkedMethodBroadcaster. 
 * Encapsulates a RegisteredObjectDecoder for receiving non-primitive arguments. 
 */
public abstract class NetworkedMethodReceiver extends Thread {

	/**
	 * Methods of implementors of NetworkedMethodBroadcaster annotated with NetworkedMethod 
	 * will be automagically registered with reflection. 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	protected static @interface NetworkedMethod {}
	
	private InputStream in;
	private Map<Short, Method> headers = new HashMap<Short, Method>();
	private RegisteredObjectDecoder decoder = new RegisteredObjectDecoder();
	
	/**
	 * @param config the headers for different methods, in which the keys are the signature String 
	 * of the methods, specified by the result of ReflectionUtils.methodSignatureString, and the values 
	 * are shorts representing their headers. 
	 */
	public NetworkedMethodReceiver(InputStream in, Properties config) {
		this.in = in;
		
		Method[] methods = getClass().getMethods();
		for (Method method : methods) {
			if (isNetworkedMethod(method)) {
				if (!config.containsKey(ReflectionUtils.methodSignatureString(method)))
					throw new RuntimeException(
							"Config does not contain header for " + ReflectionUtils.methodSignatureString(method)
							);
				headers.put(
						Short.parseShort(config.getProperty(ReflectionUtils.methodSignatureString(method))),
						method
						);
			}
		}
	}
	
	/**
	 * Delegate method for the encapsulated RegisteredObjectDecoder.
	 */
	public void registerType(ObjectDecoder type, short header) {
		decoder.registerType(type, header);
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				short header = readShort(in);
				if (!headers.containsKey(header))
					throw new RuntimeException("Invalid header received: " + header);
				Method method = headers.get(header);
				List<Object> args = new ArrayList<Object>();
				for (Class<?> type : method.getParameterTypes()) {
					args.add(SerializationUtils.readType(type, decoder, in));
				}
				method.invoke(this, args.toArray());
			}
		} catch (IOException | BadDataException e) {
			disconnect();
		} catch (InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("AbstractNetworkedMethodReceiver.NetworkedMethod invalidly applied.");
		}
	}

	/**
	 * Closes the InputStream. May be called externally, or is called when IOException is thrown.
	 * Can be overridden for further shutdown procedures.
	 */
	public void disconnect() {
		try {
			in.close();
		} catch (IOException e) {}
	}
	
	private static boolean isNetworkedMethod(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation instanceof NetworkedMethod) return true;
		}
		return false;
	}
	
}
