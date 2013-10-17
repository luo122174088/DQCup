package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.impl.CityValidator;

public class CityValidatorTest {

	@Test
	public void testValid() {
		AttributeValidator repair = new CityValidator();
		assertTrue(repair.valid("Luo"));
		assertTrue(repair.valid("Luo Chen"));
		assertFalse(repair.valid("Luo 1Chen"));
		assertFalse(repair.valid("luo Chen"));
		assertTrue(repair.valid("Luo Chen"));
		assertFalse(repair.valid("LUo Chen"));
		assertFalse(repair.valid("Luo chen"));

	}

}
