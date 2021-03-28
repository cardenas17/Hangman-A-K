// ClientGameData
// 		An Object where all the aspects of the game are
//		being kept for the client and server to communicate
//		All the game logic goes here
// Angel Cardenas		651018873		acarde36
// Kartik Maheshwari	665023848		kmahes5
//

import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;


public class ClientGameData {
	
	public class Category {
		public boolean isComplete;		// flag if a category is completed
		public String name;				// name of the category
		public ArrayList<String> list;	// three word list, one for each possible try
		public int totalAttempts;		// total attempts left for the category
		public int charAttempts;		// total letter attempts for a word
		public int wordAttempts;		// total word attempts for a word
		public String completeWord;		// the word that the client have to guess
		public String partialWord;		// Partially guessed word by the client
		
		public ArrayList<Integer> charPositions;	// array holding the correct letters position in the word
		public boolean isGuessCharCorrect;			// flag for if letter guessed was correct
		public boolean isGuessWordCorrect;			// flag for if word guessed was correct
		
		/*Constructor that sets all the fields to default*/
		Category(String n, ArrayList<String> l){
			isComplete = false;
			name = n;
			list = l;
			totalAttempts = 3;
			charAttempts = 6;
			wordAttempts = 3;
			completeWord = "";
			partialWord = "";
			
			charPositions = new ArrayList<Integer>();
			isGuessCharCorrect = isGuessWordCorrect = false;
		}
	}
	
	public String currentCat;						// category that the user picks
	public HashMap<String, Category> listMap;		// Hashmap with all the categories and their list of words.
	// List of all the possible words that a client could get in game
	public static final ArrayList<String> foodList = new ArrayList<>(Arrays.asList("tacos", "noodles", "burger", "curry", "fries", "apple", "bread", 
			"popcorn", "poutine", "pho", "fajitas", "lasagna", "croissant", "kebab", "donut", "sushi", "chocolate", "cornbread", "meatloaf", "waffles"));
	public static final ArrayList<String> citiesList = new ArrayList<>(Arrays.asList("chicago", "mumbai", "guadalajara", "paris", "moscow", "tokyo", "vancouver",
			"london", "istanbul", "sydney", "berlin", "shanghai", "beijing", "barcelona", "amsterdam", "osaka", "miami", "bangkok", "dubai", "boston"));
	public static final ArrayList<String> animalsList = new ArrayList<>(Arrays.asList("lion", "deer", "tiger", "monkey", "turtle", "parrot", "dolphin",
			"gorilla", "elephant", "whale", "panda", "frog", "bear", "giraffe", "horse", "rabbit", "buffalo", "penguins", "otter", "sloth"));
	
	/*Helper function that picks three words at random from the list above and return the list with three words*/
	private ArrayList<String> pickWords(ArrayList<String> typeList) {
		ArrayList<String> returnList = new ArrayList<String>();
		Set<Integer> usedInts = new HashSet<Integer>();
		Random rand = new Random();
		while(usedInts.size() != 3) {
			int rand_int = rand.nextInt(foodList.size()); 
			if(!usedInts.contains(rand_int)) {
				usedInts.add(rand_int);
				returnList.add(typeList.get(rand_int));
			}
		}
		return returnList;
	}
	
	/*Default constructor that sets the default values and populate hashmap*/
	ClientGameData() {
		currentCat = "";
		Category food = new Category("food", pickWords(foodList));
		Category cities = new Category("cities", pickWords(citiesList));
		Category animals = new Category("animals", pickWords(animalsList));
		
		listMap = new HashMap<String, Category>();
		listMap.put("food", food);
		listMap.put("cities", cities);
		listMap.put("animals", animals);
	}
	
	/*Function that returns the empty word that needs to be guessed with all "-"*/
	public String returnEmptyWord(int size) {
		String temp = "";
		for (int i = 0; i < size; i++) {
			temp += "-";
		}
		return temp;
	}
	
	/*Function that gets called when a category is been picked by the client*/
	public boolean pickCategory(String catName) {
		currentCat = catName;
		if(listMap.get(currentCat).list.size() == 0) {
			return false;
		}
		listMap.get(currentCat).completeWord = listMap.get(currentCat).list.get(0);
		listMap.get(currentCat).list.remove(0);
		listMap.get(currentCat).partialWord = returnEmptyWord(listMap.get(currentCat).completeWord.length());
		listMap.get(currentCat).totalAttempts--;
		listMap.get(currentCat).wordAttempts = 3;
		listMap.get(currentCat).charAttempts = 6;
		return true;
	}
	
	/*Function that compares the client guessed word with the actual current word and determines if they are the same*/
	public boolean checkWord(String checkWord) {
		// setting up the flags accordingly
		if(checkWord.equals(listMap.get(currentCat).completeWord)) {
			listMap.get(currentCat).isComplete = true;
			listMap.get(currentCat).partialWord = listMap.get(currentCat).completeWord;
			listMap.get(currentCat).isGuessWordCorrect = true;
			return true;
		} else {
			listMap.get(currentCat).isGuessWordCorrect = false;
			listMap.get(currentCat).wordAttempts--;
			return false;
		}
	}
	
	/*Function that finds the character that the client guessed and checks if exists in the current word that is to be guessed*/
	public boolean checkCharacter(char letter) {
		String temp = "";
		ArrayList<Integer> pos = new ArrayList<Integer>();
		
		for (int i = 0; i < listMap.get(currentCat).completeWord.length(); i++){
		    char c = listMap.get(currentCat).completeWord.charAt(i);
		    if (c == letter) {
		    	temp += c;
		    	pos.add(i);
		    } else {
		    	temp += listMap.get(currentCat).partialWord.charAt(i);
		    }
		}
		// setting up the flags accordingly
		if (temp.equals(listMap.get(currentCat).partialWord)) {
			listMap.get(currentCat).charAttempts--;
			listMap.get(currentCat).isGuessCharCorrect = false;
			listMap.get(currentCat).charPositions = pos;
			return false;
		}
		if (temp.equals(listMap.get(currentCat).completeWord)) {
			listMap.get(currentCat).isComplete = true;
		}
		listMap.get(currentCat).partialWord = temp;
		listMap.get(currentCat).isGuessCharCorrect = true;
		listMap.get(currentCat).charPositions = pos;
		return true;
	}
}
