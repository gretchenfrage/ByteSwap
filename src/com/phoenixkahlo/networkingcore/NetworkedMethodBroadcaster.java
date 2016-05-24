package com.phoenixkahlo.networkingcore;

import static com.phoenixkahlo.networkingcore.SerializationUtils.writeBoolean;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeChar;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeDouble;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeFloat;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeInt;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeShort;
import static com.phoenixkahlo.networkingcore.SerializationUtils.writeString;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.phoenixkahlo.networkingcore.RegisteredObjectEncoderOld.Encodable;

/**
 * Sends method calls and their arguments across an OutputStream. 
 * Symmetrical to NetworkedMethodReceiver. 
 * Encapsulates a RegisteredObjectEncoder for sending non-primitive arguments. 
 */
public abstract class NetworkedMethodBroadcaster {

	/**
	 * Methods of implementors of NetworkedMethodBroadcaster annotated with NetworkedMethod 
	 * will be automagically registered with reflection. 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	protected static @interface NetworkedMethod {}
	
	private OutputStream out;
	private Map<String, Short> headers = new HashMap<String, Short>();
	private RegisteredObjectEncoderOld encoder = new RegisteredObjectEncoderOld();

	/**
	 * @param config the headers for different methods, in which the keys are the signature String 
	 * of the methods, specified by the result of ReflectionUtils.methodSignatureString, and the values 
	 * are shorts representing their headers. 
	 */
	public NetworkedMethodBroadcaster(OutputStream out, Properties config) {
		this.out = out;
		
		Method[] methods = getClass().getMethods();
		for (Method method : methods) {
			if (isNetworkedMethod(method)) {
				if (!config.containsKey(ReflectionUtils.methodSignatureString(method)))
					throw new RuntimeException(
							"Config does not contain header for " + ReflectionUtils.methodSignatureString(method)
							);
				headers.put(
						ReflectionUtils.methodSignatureString(method),
						Short.parseShort(config.getProperty(ReflectionUtils.methodSignatureString(method)))
						);
			}
		}
	}
	
	/**
	 * @param config the headers for different codable types, in which the keys are the full names of classes, 
	 * and the values are shorts representing their headers.
	 */
	public void registerEncodableTypes(Properties config) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (Object name : config.keySet()) {
			try {
				Class<?> clazz = loader.loadClass((String) name);
				encoder.registerType(
						(Encodable encodable) -> encodable.getClass() == clazz,
						Short.parseShort(config.getProperty((String) name))
						);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Config names unfound class.");
			}
		}
	}
	
	/**
	 * Should be called by implementing methods annotated by NetworkedMethod. 
	 * @param signature the signature of the networked method, specified by the result 
	 * of ReflectionUtils.methodSignatureString
	 * @param args all of the args passed to the networked method
	 */
	protected void broadcast(String signature, Object ... args) {
		try {
			writeShort(headers.get(signature), out);
			for (Object obj : args) {
				if (obj instanceof Short)
					writeShort((Short) obj, out);
				else if (obj instanceof Integer)
					writeInt((Integer) obj, out);
				else if (obj instanceof Long)
					writeDouble((Long) obj, out);
				else if (obj instanceof Character)
					writeChar((Character) obj, out);
				else if (obj instanceof Float)
					writeFloat((Float) obj, out);
				else if (obj instanceof Double)
					writeDouble((Double) obj, out);
				else if (obj instanceof Boolean)
					writeBoolean((Boolean) obj, out);
				else if (obj instanceof String)
					writeString((String) obj, out);
				else if (obj instanceof Encodable)
					encoder.encode((Encodable) obj, out);
			}
		} catch (IOException e) {
			disconnect();
		}
	}
	
	/**
	 * Closes the OutputStream. May be called externally, or is called when IOException is thrown.
	 * Can be overridden for further shutdown procedures.
	 */
	public void disconnect() {
		try {
			out.close();
		} catch (IOException e) {}
	}
	
	private static boolean isNetworkedMethod(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation instanceof NetworkedMethod) return true;
		}
		return false;
	}
	
}
