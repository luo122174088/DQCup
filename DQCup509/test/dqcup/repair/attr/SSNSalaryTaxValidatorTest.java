package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.composite.impl.SSNSalaryTaxValidator;

public class SSNSalaryTaxValidatorTest {

	@Test
	public void testValidate() {
		SSNSalaryTaxValidator validator = new SSNSalaryTaxValidator();
		assertTrue(validator.validate("123456789", "1000", "0"));
		assertTrue(validator.validate("123456789", "2000", "10"));
		assertTrue(validator.validate("000000000", "0", "0"));
		assertFalse(validator.validate("123456789", "499", "0"));
		assertFalse(validator.validate("123456789", "20501", "100"));
		assertFalse(validator.validate("123456789", "2000", "0"));

	}

}
