package com.phoenixkahlo.networkingcore;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class RegisteredObjectEncoder {

	public static interface Encodable {

		void encode(OutputStream out) throws IOException;
		
	}
	
	private Map<Predicate<Encodable>, Short> headers = new HashMap<Predicate<Encodable>, Short>();
	
	public void registerType(Predicate<Encodable> tester, short header) {
		headers.put(tester, header);
	}
	
	public void encode(Encodable encodable, OutputStream out) throws IOException {
		for (Predicate<Encodable> tester : headers.keySet()) {
			if (tester.test(encodable)) {
				SerializationUtils.writeShort(headers.get(tester), out);
				encodable.encode(out);
				return;
			}
		}
		throw new RuntimeException("Encodable type not registered.");
	}
	
}
