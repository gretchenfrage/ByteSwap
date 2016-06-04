package com.phoenixkahlo.testing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.phoenixkahlo.networkingcore.SerializationUtils;

public class WhatTheFuckening {

	public static void main(String[] args) throws IOException {
		String hello = "Hello World!";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		SerializationUtils.writeString(hello, out);
		
		byte[] data = out.toByteArray();
		for (int i = 0; i < data.length; i++) {
			System.out.println(data[i]);
		}
		
		InputStream in = new ByteArrayInputStream(data);
		String neu = SerializationUtils.readString(in);
		System.out.println(neu);
	}
	
}
