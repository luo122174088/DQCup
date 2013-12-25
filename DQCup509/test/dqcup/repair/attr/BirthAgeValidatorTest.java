package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.composite.impl.BirthAgeValidator;

public class BirthAgeValidatorTest {

	@Test
	public void testValidate() {
		BirthAgeValidator validator = new BirthAgeValidator();
		assertTrue(validator.strictValidate("2-14-1959", "54") == 0);
		assertTrue(validator.strictValidate("2-14-195a", "54") == BirthAgeValidator.Invalid_Birth);
		assertTrue(validator.strictValidate("2-14-1959", "55") == BirthAgeValidator.Invalid_Age);
		assertTrue(validator.strictValidate("2-14-1959", "5a") == BirthAgeValidator.Invalid_Age);

	}

}
