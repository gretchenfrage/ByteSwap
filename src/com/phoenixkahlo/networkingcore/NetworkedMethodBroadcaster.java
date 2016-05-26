package com.phoenixkahlo.networkingcore;

import static com.phoenixkahlo.networkingcore.SerializationUtils.writeShort;

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
	private RegisteredObjectEncoder encoder = new RegisteredObjectEncoder();
	private boolean isAlive = true;

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
	 * Delegate method for the encapsulated RegisteredObjectEncoder.
	 */
	public void registerType(ObjectEncoder type, short header) {
		encoder.registerType(type, header);
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
				SerializationUtils.writeAny(obj, encoder, out);
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
		isAlive = false;
	}
	
	/**
	 * @return whether has disconnected.
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	private static boolean isNetworkedMethod(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation instanceof NetworkedMethod) return true;
		}
		return false;
	}
	
}
