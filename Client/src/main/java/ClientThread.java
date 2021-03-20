import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientThread extends Thread {
	Socket clientSocket;
	ObjectOutputStream output;
	ObjectInputStream input;
	private Consumer<Serializable> callback;
	int port;
	
	ClientThread(Consumer<Serializable> call, int startPort) {
		this.callback = call;
		this.port = startPort;
	}
	
	public void run() {
		try {
			clientSocket = new Socket("127.0.0.1", port);
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			input = new ObjectInputStream(clientSocket.getInputStream());
			clientSocket.setTcpNoDelay(true);
		} catch (Exception e) {
			
 		}
		
		while (true) {
			try {
				SerializableWord wordData = (SerializableWord) input.readObject();
				callback.accept(wordData);
			} catch (Exception t) {
				
			}
		}
		
	}
	
	public void send(SerializableWord data) {
		try {
			output.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
