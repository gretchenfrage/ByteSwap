package com.phoenixkahlo.arbitraryzombie;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.phoenixkahlo.networkingcore.SerializationUtils;

public class ArbitraryZombie {
		
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		System.out.println("initializing zombie!");
		Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
		System.out.println("socket connected!");
		
		InputStream in = socket.getInputStream();
		in = new PrintingInputStream(in);
		while (true) {
			String name = SerializationUtils.readString(in);
			String src = SerializationUtils.readString(in);
			execute(name, src);
		}
	}
	
	public static void execute(String name, String src) {
		//System.out.println("name=" + name + ",src=" + src);
		String srcPath = name + ".java";
		ArbitraryZombieUtils.writeFileUTF8(srcPath, src);
		String binPath = ArbitraryZombieUtils.compileSrc(srcPath);
		byte[] compiled = ArbitraryZombieUtils.readFile(binPath);
		Class<?> clazz = ArbitraryZombieUtils.loadClass(name, compiled);
		Thread other = new Thread(new Runnable() {
			@Override
			public void run() {
				ArbitraryZombieUtils.invokeStaticInvokable(clazz);
			}
		});
		other.start();
		try {
			other.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
