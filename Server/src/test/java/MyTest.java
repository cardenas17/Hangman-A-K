import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {
	
	@Test
	void testCGD_Constructor() {
		ClientGameData temp = new ClientGameData();
		System.out.println(temp.listMap.get("food").list.toString());
		System.out.println(temp.listMap.get("food").completeWord.toString());
		System.out.println(temp.listMap.get("cities").list.toString());
		System.out.println(temp.listMap.get("cities").completeWord.toString());
		System.out.println(temp.listMap.get("animals").list.toString());
		System.out.println(temp.listMap.get("animals").completeWord.toString());
	}
	

}
