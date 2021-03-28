import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {
	
	// test the ClientGameData constructor
	@Test
	void clientGameDataConstructor() {
		ClientGameData data = new ClientGameData();
		assertEquals(data.currentCat, "");
		assertEquals(data.listMap.size(), 3);
		assertTrue(data.listMap.containsKey("food"));
		assertTrue(data.listMap.containsKey("cities"));
		assertTrue(data.listMap.containsKey("animals"));
	}
	
	// further test the ClientGameData constructor
	@Test
	void categorySubclassConstructor1() {
		ClientGameData data = new ClientGameData();
		assertFalse(data.listMap.get("animals").isComplete);
		assertEquals(data.listMap.get("animals").name, "animals");
		assertEquals(data.listMap.get("animals").completeWord, "");
		assertEquals(data.listMap.get("animals").partialWord, "");
	}
	
	// test the inner class Category from ClientGameData class
	@Test
	void categorSubclassConstructor2() {
		ClientGameData data = new ClientGameData();
		assertEquals(data.listMap.get("cities").name, "cities");
		assertEquals(data.listMap.get("cities").totalAttempts, 3);
		assertEquals(data.listMap.get("cities").charAttempts, 6);
		assertEquals(data.listMap.get("cities").wordAttempts, 3);
		assertEquals(data.listMap.get("cities").list.size(), 3);
	}
	
	
	// test returnEmptyWord() from ClientGameData class
	@Test
	void returnEmptyWordTest() {
		ClientGameData data = new ClientGameData();
		assertEquals(data.returnEmptyWord(6), "------");
		assertEquals(data.returnEmptyWord(4), "----");
		assertEquals(data.returnEmptyWord(0), "");
		
		String s = "woah, woah";
		assertEquals(data.returnEmptyWord(s.length()), "----------");
	}
	
	// test pickCategory() from ClientGameData class
	@Test
	void pickCategoryTest() {
		ClientGameData data = new ClientGameData();
		assertTrue(data.pickCategory("animals"));
		assertNotEquals(data.listMap.get("animals").completeWord, "");
		assertNotEquals(data.listMap.get("animals").partialWord, "");
		assertEquals(data.listMap.get("animals").totalAttempts, 2);
		assertEquals(data.listMap.get("animals").list.size(), 2);
	}
	
	// further test pickCategory() from ClientGameData class
	@Test
	void pickCategoryTest2() {
		ClientGameData data = new ClientGameData();
		data.listMap.get("cities").list.clear();
		assertFalse(data.pickCategory("cities"));
		assertEquals(data.listMap.get("cities").completeWord, "");
		assertEquals(data.listMap.get("cities").partialWord, "");
		assertEquals(data.listMap.get("cities").list.size(), 0);
	}
	
	// test checkWord() from ClientGameData class
	@Test
	void checkWordTest() {
		ClientGameData data = new ClientGameData();
		assertTrue(data.pickCategory("cities"));
		assertFalse(data.checkWord("canada"));
		assertEquals(data.listMap.get("cities").wordAttempts, 2);
		assertFalse(data.checkWord("us"));
		assertEquals(data.listMap.get("cities").wordAttempts, 1);
		assertFalse(data.checkWord("mexico"));
		assertEquals(data.listMap.get("cities").wordAttempts, 0);
	}
	
	// test checkCharacter() from ClientGameData class
	@Test
	void checkCharacterTest() {
		ClientGameData data = new ClientGameData();
		assertTrue(data.pickCategory("food"));
		assertFalse(data.checkCharacter('z'));
		assertEquals(data.listMap.get("food").charAttempts, 5);
		assertFalse(data.checkCharacter('6'));
		assertEquals(data.listMap.get("food").charAttempts, 4);
		assertFalse(data.checkCharacter('9'));
		assertEquals(data.listMap.get("food").charAttempts, 3);
		assertFalse(data.checkCharacter('4'));
		assertEquals(data.listMap.get("food").charAttempts, 2);
		assertFalse(data.checkCharacter('2'));
		assertEquals(data.listMap.get("food").charAttempts, 1);
		assertFalse(data.checkCharacter('0'));
		assertEquals(data.listMap.get("food").charAttempts, 0);
	}
}
