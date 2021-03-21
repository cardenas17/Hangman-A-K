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
		public String currWord;
		public String partialWord;
		
		Category(String n, ArrayList<String> l){
			isComplete = false;
			name = n;
			list = l;
			totalAttempts = charAttempts = wordAttempts = 0;
			currWord = "";
			partialWord = "";
		}

	}
	
	public String currentCat;
	public HashMap<String, Category> listMap;
	public static final ArrayList<String> foodList = new ArrayList<>(Arrays.asList("tacos", "noodles", "burger", "curry", "fries", "apple", "bread"));
	public static final ArrayList<String> citiesList =new ArrayList<>(Arrays.asList("chicago", "mumbai", "guadalajara", "paris", "moscow", "tokyo", "vancouver"));
	public static final ArrayList<String> animalsList =new ArrayList<>(Arrays.asList("lion", "deer", "tiger", "monkey", "turtle", "parrot", "dolphin"));
	
	private ArrayList<String> pickWords(ArrayList<String> typeList) {
		ArrayList<String> returnList = new ArrayList<String>();
		Set<Integer> usedInts = new HashSet<Integer>();
		Random rand = new Random();
		while(usedInts.size() != 3) {
			int rand_int = rand.nextInt(7);
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
		while (size != 0) {
			temp += "-";
			size--;
		}
		return temp;
	}
	
	public boolean pickCategory(String catName) {
		currentCat = catName;
		if(listMap.get(currentCat).list.size() == 0) {
			return false;
		}
		listMap.get(currentCat).currWord = listMap.get(currentCat).list.get(0);
		listMap.get(currentCat).list.remove(0);
		listMap.get(currentCat).partialWord = returnEmptyWord(listMap.get(currentCat).currWord.length());
		return true;
	}
	
	public boolean checkWord(String checkWord) {
		if(checkWord == listMap.get(currentCat).currWord) {
			listMap.get(currentCat).isComplete = true;
			return true;
		}
		else {
			listMap.get(currentCat).wordAttempts++;
			
			if (listMap.get(currentCat).wordAttempts == 3) {
				listMap.get(currentCat).totalAttempts++;
			}
			return false;
		}
	}
	
	public boolean checkCharacter(char letter) {
		String temp = "";
		for (int i = 0; i < listMap.get(currentCat).currWord.length(); i++){
		    char c = listMap.get(currentCat).currWord.charAt(i);
		    if (c == letter) {
		    	temp += c;
		    }
		    else {
		    	temp += listMap.get(currentCat).partialWord.charAt(i);
		    }
		}
		
		if (temp == listMap.get(currentCat).partialWord) {
			listMap.get(currentCat).charAttempts++;
			return false;
		}
		
		listMap.get(currentCat).partialWord = temp;
		return true;
	}
	
	
	
	
}
