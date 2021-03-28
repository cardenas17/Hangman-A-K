// SerializableWord
// 		Serializable Object for communication between client and server 
// 		Contains data about current game state as well as boolean flags for communication
// Angel Cardenas		651018873		acarde36
// Kartik Maheshwari	665023848		kmahes5
//

import java.io.Serializable;
import java.util.ArrayList;

public class SerializableWord implements Serializable {
	private static final long serialVersionUID = 4013155587837704558L;	// unique serializable ID

	public String catChoice;	// current category in use
	public int wordSize;
	
	public int animalAttempts;	// attempts left for animals category
	public int citiesAttempts;	// attempts left for cities category
	public int foodAttempts;	// attempts left for food category
	
	public boolean isAnimalsDone;	// flag for completed animals category
	public boolean isCitiesDone;	// flag for completed cities category
	public boolean isFoodDone;		// flag for completed food category
	
	public char guessLetter;	// client guess letter
	public String guessWord;	// client guess word
	
	public int wordGuessesLeft;		// word guess attempts left for current category
	public int letterGuessesLeft;	// letter guess attempts left for current category
	
	public boolean isCatChoice;		// flag for picking a category
	public boolean isGuessLetter;	// flag for guessing a letter
	public boolean isGuessWord;		// flag for guessing a word
	
	public boolean isLetterCorrect;		// flag to tell the client if the letter guessed was correct
	public boolean isWordCorrect;		// flag to tell the client if the word guessed was correct
	
	public ArrayList<Integer> letterPositions;		// array that holds the index value of the correct characters guessed
	
	public boolean isReplay;			// flag that lets the server know to set everything to default on the client thread
	public boolean isConnectionFail;	// flag for a connection error on the client
	
	// default constructor for starting a game
	public SerializableWord() {
		catChoice = "";
		wordSize = 0;
		animalAttempts = citiesAttempts = foodAttempts = 3;
		
		guessWord = "";
		guessLetter = '\0';
		
		isCatChoice = isGuessLetter = isGuessWord = false;
		isReplay = isConnectionFail = false;
		isAnimalsDone = isCitiesDone = isFoodDone = false;
		
		isLetterCorrect = isWordCorrect = false;
		letterPositions = new ArrayList<Integer>();
	}
}
