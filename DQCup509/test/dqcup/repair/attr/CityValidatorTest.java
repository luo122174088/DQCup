package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.impl.CityValidator;

public class CityValidatorTest {

	@Test
	public void testValid() {
		AttributeValidator repair = new CityValidator();
		assertTrue(repair.validate("Luo"));
		assertTrue(repair.validate("Luo Chen"));
		assertTrue(repair.validate("Luo of Chen"));
		assertTrue(repair.validate("Lu'O of Chen"));
		assertFalse(repair.validate("Luo 1Chen"));
		assertFalse(repair.validate("luo Chen"));
		assertTrue(repair.validate("Luo Chen"));
		assertFalse(repair.validate("LUo Chen"));
		assertFalse(repair.validate("Luo chen"));
		assertFalse(repair.validate("LUO CHEN"));

	}

}
