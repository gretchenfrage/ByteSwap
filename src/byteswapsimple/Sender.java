package byteswapsimple;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import com.phoenixkahlo.networkingcore.NetworkedMethodBroadcaster;
import com.phoenixkahlo.networkingcore.ObjectCoderFactory;

public class Sender extends NetworkedMethodBroadcaster {

	public static final Properties PROTOCOL = new Properties();
	static {
		PROTOCOL.setProperty("append([B)", "1");
		PROTOCOL.setProperty("finish()", "2");
	}
	
	/**
	 * @param args {ip, port, path}
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Starting sender");
		args = new String[] {"localhost", "58764", "/Users/Phoenix/Desktop/splenda.pdf"};
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		String path = args[2];
		
		Socket socket = new Socket(ip, port);
		Sender sender = new Sender(socket.getOutputStream(), new File(path));
		sender.send();
		socket.close();
		System.out.println("Sender completed");
	}
	
	private File file;
	
	public Sender(OutputStream out, File file) {
		super(out, PROTOCOL);
		registerType(ObjectCoderFactory.byteArrayEncoder(), (short) 1);
		
		this.file = file;
	}
	
	public void send() throws IOException {
		InputStream in = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		while (in.available() > 0) {
			if (in.available() < 4096)
				buffer = new byte[in.available()];
			in.read(buffer);
			append(buffer);
		}
		finish();
		in.close();
	}
	
	@NetworkedMethod
	public void append(byte[] data) {
		broadcast("append([B)", new Object[] {data});
	}
	
	@NetworkedMethod
	public void finish() {
		broadcast("finish()");
	}
	
}
