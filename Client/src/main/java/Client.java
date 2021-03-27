// Client
// 		Thread for connecting a client to server 
// 		Uses SerializableWord for communication
// Angel Cardenas		651018873		acarde36
// Kartik Maheshwari	665023848		kmahes5
//

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread {
	Socket clientSocket;	// socket for client
	ObjectOutputStream output;	// for writing objects that could be read by inputStream
	ObjectInputStream input;	// for reading objects that could be written by the outputStream 
	private Consumer<Serializable> callback;	// implemented for functional programming
	int port;	// holds user input port
	SerializableWord guessData = new SerializableWord();	// initialization of the serializable object
	
	/*Constructor for setting the fields to default*/
	Client(Consumer<Serializable> call, int startPort) {
		this.callback = call;
		this.port = startPort;
	}
	
	/*Sets up the connection between the client and server*/
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
			SerializableWord message = new SerializableWord();
			message.isConnectionFail = true;
			callback.accept(message);
			System.out.println("Client Connection failed");
		}
		
	}
	
	/*function that sends a serializable object to the server from the client to communicate*/
	public void send(SerializableWord data) {
		try {
			output.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
