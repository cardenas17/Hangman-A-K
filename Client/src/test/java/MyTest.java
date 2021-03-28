import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {
	
	// test containsDash() from ClientGUI
	@Test
	void containsDashTest() {
		ClientGUI noGUI = new ClientGUI();
		assertFalse(noGUI.containsDash("No Dash"));
		assertTrue(noGUI.containsDash("Yes-Dash"));
	}
	
	// test the serializableWord constructor
	@Test
	void serializableWordTest() {
		SerializableWord sw = new SerializableWord();
		assertEquals(sw.catChoice, "");
		
		assertEquals(sw.animalAttempts, sw.citiesAttempts);
		assertEquals(sw.citiesAttempts, sw.foodAttempts);
		assertEquals(sw.foodAttempts, 3);
		
		assertEquals(sw.guessWord, "");
		
		assertEquals(sw.guessLetter, '\0');
		
		assertEquals(sw.isCatChoice, sw.isGuessLetter);
		assertEquals(sw.isGuessLetter, sw.isGuessWord);
		assertEquals(sw.isGuessWord, sw.isConnectionFail);
		
		assertEquals(sw.isConnectionFail, sw.isAnimalsDone);
		assertEquals(sw.isAnimalsDone, sw.isCitiesDone);
		assertEquals(sw.isCitiesDone, sw.isFoodDone);
		assertFalse(sw.isFoodDone);
	}

}
