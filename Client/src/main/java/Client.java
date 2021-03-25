import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {
	Socket clientSocket;
	ObjectOutputStream output;
	ObjectInputStream input;
	private Consumer<Serializable> callback;
	int port;
	SerializableWord guessData = new SerializableWord();
	
	Client(Consumer<Serializable> call, int startPort) {
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
			System.out.println("Client socket did not launch");
 		}
		
		try {
			while (true) {
				SerializableWord message = (SerializableWord) input.readObject();
				callback.accept(message);
			}
		} catch (Exception t) {
			System.out.println("Client Connection failed");
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
