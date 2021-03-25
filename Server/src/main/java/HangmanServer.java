import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class HangmanServer {
	int count = 1;
	int port;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ServerSocket socket;
	private Consumer<Serializable> callback;
	TheServer server;
	
	public HangmanServer(Consumer<Serializable> call, int p) {
		callback = call;
		port = p;
		server = new TheServer();
		server.start();
		System.out.println("Port = " + port);
	}
	
	public class TheServer extends Thread{
		
		public void run() {
			try(ServerSocket socket = new ServerSocket(port);){
				System.out.println("Server is waiting for a client!");
		  
		    	while(true) {
		
					ClientThread c = new ClientThread(socket.accept(), count);
					callback.accept("client has connected to server: " + "client #" + count);
					System.out.println("client has connected to server: " + "client #" + count);
					clients.add(c);
					c.start();
				
					count++;
			    }
			} catch(Exception e) {
				callback.accept("Server socket did not launch");
				System.out.println("Server socket did not launch");
			}
		}
	}

	
	public class ClientThread extends Thread {
		Socket connection;
		int count;
		ClientGameData clientData;
		ObjectInputStream input;
		ObjectOutputStream output;
		
		public ClientThread (Socket s, int c) {
			this.connection = s;
			this.count = c;
			this.clientData = new ClientGameData();
		}
		
		public SerializableWord updateData(ClientGameData gameData) {
			SerializableWord updatedWord = new SerializableWord();
			updatedWord.catChoice = gameData.currentCat;
			
			updatedWord.animalAttempts = gameData.listMap.get("animals").totalAttempts;
			updatedWord.citiesAttempts = gameData.listMap.get("cities").totalAttempts;
			updatedWord.foodAttempts = gameData.listMap.get("food").totalAttempts;
			
			if (gameData.currentCat != "") {
				updatedWord.serverWord = gameData.listMap.get(gameData.currentCat).partialWord;
				
				updatedWord.wordGuessesLeft = gameData.listMap.get(gameData.currentCat).wordAttempts;
				updatedWord.letterGuessesLeft = gameData.listMap.get(gameData.currentCat).charAttempts;
			}
			
			return updatedWord;
		}
		
		public void updateClient(SerializableWord data) {
			try {
				this.output.writeObject(data);
			} catch (Exception e) {
				
			}
		}
		
		public void run() {
			try {
				input = new ObjectInputStream(connection.getInputStream());
				output = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			} catch (Exception e) {
				System.out.println("Stream not open");
			}
			
			while (true) {
				try {
					SerializableWord curData = (SerializableWord) input.readObject();
					System.out.println("animalAttempts = " + curData.animalAttempts);
					if (curData.isCatChoice) {
						clientData.pickCategory(curData.catChoice);
						curData.isCatChoice = false;
						callback.accept("Client #" + this.count + "Category Choice is " + curData.catChoice);
					}
					else if (curData.isGuessLetter) {
						clientData.checkCharacter(curData.guessLetter);
						curData.isGuessLetter = false;
						callback.accept("Client #" + this.count + "Guess Letter is " + curData.guessLetter);
					}
					else if (curData.isGuessWord) {
						clientData.checkWord(curData.guessWord);
						curData.isGuessWord = false;
						callback.accept("Client #" + this.count + "Guess Word is " + curData.guessWord);
					}
					
					curData = updateData(clientData);
					updateClient(curData);
				} catch (Exception e) {
					callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					System.out.println("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
			    	clients.remove(this);
			    	break;
				}
			}
		}
	}
}
