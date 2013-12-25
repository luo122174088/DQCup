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
		assertTrue(repair.validate("Luo Chen"));
		assertTrue(repair.validate("L'-/. "));
	}

}
