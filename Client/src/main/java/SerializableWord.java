import java.io.Serializable;

public class SerializableWord implements Serializable {
	private static final long serialVersionUID = 4013155587837704558L;

	public String catChoice;			
	
	public int animalAttempts;
	public int citiesAttempts;
	public int foodAttempts;
	
	public String serverWord;	// partial word that server sends back
	
	public char guessLetter;	// client guess letter
	public String guessWord;	// client guess word
	
	public int wordGuessesLeft;
	public int letterGuessesLeft;
	
	public boolean isCatChoice;
	public boolean isGuessLetter;
	public boolean isGuessWord;
	
	public boolean isReplay;
}
