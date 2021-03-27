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
	}
	
	public class TheServer extends Thread{
		
		public void run() {
			try(ServerSocket socket = new ServerSocket(port);){
				callback.accept("Server is waiting for clients on Port: " + port);
		  
		    	while(true) {
					ClientThread c = new ClientThread(socket.accept(), count);
					callback.accept("client has connected to server: " + "client #" + count);
					clients.add(c);
					c.start();
					count++;
			    }
			} catch(Exception e) {
				callback.accept("Server socket did not launch");
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
		
		public SerializableWord updateData(ClientGameData gameData, SerializableWord oldWord) {
			SerializableWord updatedWord = oldWord;
			updatedWord.catChoice = gameData.currentCat;
			
			updatedWord.animalAttempts = gameData.listMap.get("animals").totalAttempts;
			updatedWord.citiesAttempts = gameData.listMap.get("cities").totalAttempts;
			updatedWord.foodAttempts = gameData.listMap.get("food").totalAttempts;
			
			updatedWord.isAnimalsDone = gameData.listMap.get("animals").isComplete;
			updatedWord.isCitiesDone = gameData.listMap.get("cities").isComplete;
			updatedWord.isFoodDone = gameData.listMap.get("food").isComplete;
			
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
				System.out.println("Fail to update the client!");
			}
		}
		
		public void run() {
			try {
				input = new ObjectInputStream(connection.getInputStream());
				output = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			} catch (Exception e) {
				callback.accept("Stream not open");
			}
			
			while (true) {
				try {
					SerializableWord curData = (SerializableWord) input.readObject();
					if (curData.isCatChoice) {
						clientData.pickCategory(curData.catChoice);
						callback.accept("Client #" + this.count + ": Category Choice = " + curData.catChoice);
						callback.accept("Client #" + this.count + ": Currrent word to guess = " + clientData.listMap.get(curData.catChoice).completeWord);
					}
					else if (curData.isGuessLetter) {
						clientData.checkCharacter(curData.guessLetter);
						callback.accept("Client #" + this.count + ": Guess Letter = " + curData.guessLetter);
					}
					else if (curData.isGuessWord) {
						clientData.checkWord(curData.guessWord);
						callback.accept("Client #" + this.count + ": Guess Word = " + curData.guessWord);
					}
					
					curData = updateData(clientData, curData);
					callback.accept("Client #" + this.count + ": Progress on word = " + curData.serverWord);
					
					if (!curData.catChoice.equals("")) {
						if (clientData.listMap.get(curData.catChoice).isComplete) {
							callback.accept("Client #" + this.count + ": (" + curData.catChoice + ") category is complete!!!");
						} else if (curData.wordGuessesLeft == 0 && curData.letterGuessesLeft == 0) {
							callback.accept("Client #" + this.count + ": has failed category (" + curData.catChoice + ")");
						}
					}
					
					if (curData.isAnimalsDone && curData.isCitiesDone && curData.isFoodDone) {
						callback.accept("Client #" + this.count + ": Has won the game :)");
					} else if (curData.animalAttempts == 0 && curData.citiesAttempts == 0 && curData.foodAttempts == 0) {
						callback.accept("Client #" + this.count + ": failed the game on category (" + curData.catChoice + ")");
					}
					
					updateClient(curData);
				} catch (Exception e) {
					callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
			    	clients.remove(this);
			    	break;
				}
			}
		}// end of run
		
	}
}
