package byteswapsimple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.phoenixkahlo.networkingcore.NetworkedMethodReceiver;
import com.phoenixkahlo.networkingcore.ObjectCoderFactory;

public class Receiver extends NetworkedMethodReceiver {

	/**
	 * @param args {port, path}
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Starting receiver");
		args = new String[] {"58764", "/Users/Phoenix/Desktop/splendaCLONE.pdf"};
		int port = Integer.parseInt(args[0]);
		File file = new File(args[1]);
		file.createNewFile();
		ServerSocket ss = new ServerSocket(port);
		Socket socket = ss.accept();
		ss.close();
		Receiver receiver = new Receiver(socket.getInputStream(), file);
		receiver.start();
		System.out.println("Receiver finished");
	}
	
	private OutputStream out;
	
	public Receiver(InputStream in, File file) throws Exception {
		super(in, Sender.PROTOCOL);
		registerType(ObjectCoderFactory.byteArrayDecoder(), (short) 1);
		
		out = new FileOutputStream(file);
	}
	
	@NetworkedMethod
	public void append(byte[] data) {
		try {
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@NetworkedMethod
	public void finish() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		kill();
	}
	
}
