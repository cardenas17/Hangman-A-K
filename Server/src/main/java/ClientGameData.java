import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;


public class ClientGameData {
	
	public class Category {
		public boolean isComplete;
		public String name;
		public ArrayList<String> list;
		public int totalAttempts;
		public int charAttempts;
		public int wordAttempts;
		public String completeWord;
		public String partialWord;
		
		Category(String n, ArrayList<String> l){
			isComplete = false;
			name = n;
			list = l;
			totalAttempts = 3;
			charAttempts = 6;
			wordAttempts = 3;
			completeWord = "";
			partialWord = "";
		}
	}
	
	public String currentCat;
	public HashMap<String, Category> listMap;
	public static final ArrayList<String> foodList = new ArrayList<>(Arrays.asList("tacos", "noodles", "burger", "curry", "fries", "apple", "bread"));
	public static final ArrayList<String> citiesList = new ArrayList<>(Arrays.asList("chicago", "mumbai", "guadalajara", "paris", "moscow", "tokyo", "vancouver"));
	public static final ArrayList<String> animalsList = new ArrayList<>(Arrays.asList("lion", "deer", "tiger", "monkey", "turtle", "parrot", "dolphin"));
	
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
	
	private String returnEmptyWord(int size) {
		String temp = "";
		for (int i = 0; i < size; i++) {
			temp += "-";
		}
		return temp;
	}
	
	public boolean pickCategory(String catName) {
		currentCat = catName;
		if(listMap.get(currentCat).list.size() == 0) {
			return false;
		}
		listMap.get(currentCat).completeWord = listMap.get(currentCat).list.get(0);
		listMap.get(currentCat).list.remove(0);
		listMap.get(currentCat).partialWord = returnEmptyWord(listMap.get(currentCat).completeWord.length());
		listMap.get(currentCat).totalAttempts--;
		return true;
	}
	
	public boolean checkWord(String checkWord) {
		if(checkWord.equals(listMap.get(currentCat).completeWord)) {
			listMap.get(currentCat).isComplete = true;
			listMap.get(currentCat).partialWord = listMap.get(currentCat).completeWord;
			return true;
		} else {
			listMap.get(currentCat).wordAttempts--;
			return false;
		}
	}
	
	public boolean checkCharacter(char letter) {
		String temp = "";
		for (int i = 0; i < listMap.get(currentCat).completeWord.length(); i++){
		    char c = listMap.get(currentCat).completeWord.charAt(i);
		    if (c == letter) {
		    	temp += c;
		    } else {
		    	temp += listMap.get(currentCat).partialWord.charAt(i);
		    }
		}
		if (temp.equals(listMap.get(currentCat).partialWord)) {
			listMap.get(currentCat).charAttempts--;
			return false;
		}
		if (temp.equals(listMap.get(currentCat).completeWord)) {
			listMap.get(currentCat).isComplete = true;
		}
		listMap.get(currentCat).partialWord = temp;
		return true;
	}
}
