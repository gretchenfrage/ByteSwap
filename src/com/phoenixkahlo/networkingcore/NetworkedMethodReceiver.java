package com.phoenixkahlo.networkingcore;

import static com.phoenixkahlo.networkingcore.SerializationUtils.readBoolean;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readChar;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readDouble;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readFloat;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readInt;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readLong;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readShort;
import static com.phoenixkahlo.networkingcore.SerializationUtils.readString;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.phoenixkahlo.networkingcore.RegisteredObjectDecoderOld.Decoder;

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
	private RegisteredObjectDecoderOld<Object> decoder = new RegisteredObjectDecoderOld<Object>();
	
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
	 * @param config the headers for different codable types, in which the keys are the full names of classes, 
	 * and the values are shorts representing their headers.
	 */
	public void registerDecodableTypes(Properties config) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (Object name : config.keySet()) {
			try {
				Constructor<?> constructor = loader.loadClass((String) name).getConstructor(InputStream.class);
				decoder.registerType(new Decoder<Object>() {
					
					@Override
					public Object decode(InputStream in) throws IOException {
						try {
							return constructor.newInstance(in);
						} catch (InvocationTargetException e) {
							throw (IOException) e.getTargetException();
						} catch (InstantiationException
								| IllegalAccessException
								| IllegalArgumentException e) {
							e.printStackTrace();
							throw new RuntimeException("Constructor reflection failure.");
						}
					}
					
				}, Short.parseShort(config.getProperty((String) name)));
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				throw new RuntimeException("Constructor reflection failure.");
			}
		}
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
					if (type == short.class)
						args.add(readShort(in));
					else if (type == int.class)
						args.add(readInt(in));
					else if (type == long.class)
						args.add(readLong(in));
					else if (type == char.class)
						args.add(readChar(in));
					else if (type == float.class)
						args.add(readFloat(in));
					else if (type == double.class)
						args.add(readDouble(in));
					else if (type == boolean.class)
						args.add(readBoolean(in));
					else if (type == String.class)
						args.add(readString(in));
					else
						args.add(decoder.decode(in));
				}
				method.invoke(this, args.toArray());
			}
		} catch (IOException e) {
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
