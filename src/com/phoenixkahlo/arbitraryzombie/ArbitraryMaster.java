package com.phoenixkahlo.arbitraryzombie;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.phoenixkahlo.networkingcore.SerializationUtils;

public class ArbitraryMaster {

	private static OutputStream out;
	
	public static void main(String[] args) throws IOException {		
		System.out.println("initializing master!");
		ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
		Socket socket = ss.accept();
		ss.close();
		out = socket.getOutputStream();
		out = new ChoppedOutputStream(out);
		//out = new BufferedOutputStream(out, 1);
		System.out.println("socket accepted!");
		
		System.out.println("accepting input:");
		out = new PrintingOutputStream(out);
		Scanner scanner = new Scanner(System.in);
		StringBuilder builder = new StringBuilder();
		String line;
		while (true) {
			line = scanner.nextLine();
			if (line.equals("$END")) {
				break;
			} else if (line.equals("$INVOKE")) {
				invoke(builder.toString());
				builder = new StringBuilder();
				out.flush();
			} else if (line.equals("$LOAD")) {
				load(builder.toString());
				builder = new StringBuilder();
				out.flush();
			} else {
				builder.append(line);
			}
		}
		scanner.close();
		System.out.println("goodbye!");
	}
	
	private static void invoke(String code) throws IOException {
		String name = "";
		for (int i = 0; i < 20; i++) {
			name += (char) ((int) (Math.random() * 25 + 65));
		}
		SerializationUtils.writeString(name, out);
		SerializationUtils.writeString(ArbitraryZombieUtils.codeToClassSrc(name, code), out);
		out.flush();
	}
	
	private static void load(String code) throws IOException {
		SerializationUtils.writeString(code.split("\\s+")[2], out);
		SerializationUtils.writeString(code, out);
		out.flush();
	}
	
}
