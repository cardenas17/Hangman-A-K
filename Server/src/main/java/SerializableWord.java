import java.io.Serializable;

public class SerializableWord implements Serializable {
	private static final long serialVersionUID = 4013155587837704558L;

	public transient String wordToGuess; 	// guess word is transient so it will not be sent
	public String catChoice;
	
	public String serverWord;	// partial word that server sends back
	public String clientWord;	// client guess word			
	public char guessChar;		// client guess char
	public boolean isCharGuess;
	public boolean isWordGuess;
	public boolean isCatChoice;
	
}
