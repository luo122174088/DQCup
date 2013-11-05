package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.composite.impl.BirthAgeValidator;

public class BirthAgeValidatorTest {

	@Test
	public void testValidate() {
		BirthAgeValidator validator = new BirthAgeValidator();
		assertTrue(validator.validate("2-14-1959", "54"));
		assertFalse(validator.validate("2-14-1959", "55"));
		assertFalse(validator.validate("2-a-1959", "54"));
		assertFalse(validator.validate("2-14-1959", "5a"));

	}

}
