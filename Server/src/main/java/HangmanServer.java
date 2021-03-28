// HangmanServer
// 		Main content of the server goes here
// Angel Cardenas		651018873		acarde36
// Kartik Maheshwari	665023848		kmahes5
//

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class HangmanServer {
	int count = 1;		// counter that keeps track of number of client connected to the server
	int port;			// port number that the server is listening to
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();		// list of all the client threads
	ServerSocket socket;													// server socket to connect
	private Consumer<Serializable> callback;								// implemented for functional programming
	TheServer server;														// thread object 
	
	/*Constructor that sets the fields to default*/
	public HangmanServer(Consumer<Serializable> call, int p) {
		callback = call;
		port = p;
		server = new TheServer();
		server.start();		// calls the run from TheServer
	}
	
	/*Nested server thread*/
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

	/*Nested client thread*/
	/*Created for each client that connects*/
	public class ClientThread extends Thread {
		Socket connection;			// socket from which to connect to client
		int count;					// unique client number
		ClientGameData clientData;	// data stored for current game state
		ObjectInputStream input;	// incoming SerializableWord
		ObjectOutputStream output;	// outgoing SerializableWord
		
		/*creates a client thread on the specified socket with specified count*/
		public ClientThread (Socket s, int c) {
			this.connection = s;
			this.count = c;
			this.clientData = new ClientGameData();
		}
		
		/*copies data from current game state to a SerializableWord for updating client*/
		public SerializableWord updateData(ClientGameData gameData, SerializableWord oldWord) {
			SerializableWord updatedWord = oldWord;
			updatedWord.catChoice = gameData.currentCat;
			
			updatedWord.animalAttempts = gameData.listMap.get("animals").totalAttempts;
			updatedWord.citiesAttempts = gameData.listMap.get("cities").totalAttempts;
			updatedWord.foodAttempts = gameData.listMap.get("food").totalAttempts;
			
			updatedWord.isAnimalsDone = gameData.listMap.get("animals").isComplete;
			updatedWord.isCitiesDone = gameData.listMap.get("cities").isComplete;
			updatedWord.isFoodDone = gameData.listMap.get("food").isComplete;
			
			// only copies category data if a category was picked
			if (gameData.currentCat != "") {
				updatedWord.isLetterCorrect = gameData.listMap.get(gameData.currentCat).isGuessCharCorrect;
				updatedWord.letterPositions = gameData.listMap.get(gameData.currentCat).charPositions;
				updatedWord.letterGuessesLeft = gameData.listMap.get(gameData.currentCat).charAttempts;
				
				updatedWord.isWordCorrect = gameData.listMap.get(gameData.currentCat).isGuessWordCorrect;
				updatedWord.wordGuessesLeft = gameData.listMap.get(gameData.currentCat).wordAttempts;
				
				updatedWord.wordSize = gameData.listMap.get(gameData.currentCat).completeWord.length();
			}
			
			return updatedWord;
		}
		
		/*sends client an updated SerializableWord*/
		public void updateClient(SerializableWord data) {
			try {
				this.output.writeObject(data);
			} catch (Exception e) {
				System.out.println("Fail to update the client!");
			}
		}
		
		/*called when the client thread is started*/
		public void run() {
			try {
				input = new ObjectInputStream(connection.getInputStream());
				output = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			} catch (Exception e) {
				callback.accept("Stream not open");
			}
			
			// runs until client disconnects
			while (true) {
				try {
					// blocking call waits for incoming data from client
					SerializableWord curData = (SerializableWord) input.readObject();
					if (curData.isReplay) {
						callback.accept("Client #" + this.count + ": Reset the game");
						clientData = new ClientGameData();
						curData = updateData(clientData, curData);
					}
					// performs game logic based on client action flags
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
					
					// updates client data
					curData = updateData(clientData, curData);
					
					// logs category success
					if (!curData.catChoice.equals("")) {
						// logs client progress to the server
						callback.accept("Client #" + this.count + ": Progress on word = " + clientData.listMap.get(curData.catChoice).partialWord);
						
						if (clientData.listMap.get(curData.catChoice).isComplete) {
							callback.accept("Client #" + this.count + ": (" + curData.catChoice + ") category is complete!!!");
						} else if (curData.wordGuessesLeft == 0 && curData.letterGuessesLeft == 0) {
							callback.accept("Client #" + this.count + ": has failed category (" + curData.catChoice + ")");
						}
					}
					
					// logs game success
					if (curData.isAnimalsDone && curData.isCitiesDone && curData.isFoodDone) {
						callback.accept("Client #" + this.count + ": Has won the game :)");
					} else if ( ((curData.animalAttempts == 0) && (!(curData.isAnimalsDone))) || 
							    ((curData.citiesAttempts == 0) && (!(curData.isCitiesDone))) || 
							    ((curData.foodAttempts == 0) && (!(curData.isFoodDone))) ) {
						if ((curData.letterGuessesLeft == 0) && (curData.wordGuessesLeft == 0)) {
							callback.accept("Client #" + this.count + ": failed the game on category (" + curData.catChoice + ")");
						}
					}
					
					// send updated data to client
					updateClient(curData);
				} catch (Exception e) {
					callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
			    	clients.remove(this);
			    	break;
				}
			}
			
		}  // end of run
	} // end of ClientThread
} // end of HangmanServer
