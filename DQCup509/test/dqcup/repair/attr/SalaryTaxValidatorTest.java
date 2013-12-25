package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.composite.impl.SalaryTaxValidator;

public class SalaryTaxValidatorTest {

	@Test
	public void testStrictValidate() {
		SalaryTaxValidator validator = new SalaryTaxValidator();
		assertTrue(validator.strictValidate("0", "0", null) == 0);
		assertTrue(validator.strictValidate("1500", "0", null) == 0);
		assertTrue(validator.strictValidate("1600", "0", null) == SalaryTaxValidator.Invalid_Tax);
		assertTrue(validator.strictValidate("a", "54", null) == SalaryTaxValidator.Invalid_Salary);
		assertTrue(validator.strictValidate("1600", "b", null) == SalaryTaxValidator.Invalid_Tax);
		assertTrue(validator.strictValidate("1600", "60", null) == 0);
		assertTrue(validator.strictValidate("26000", "60", null) == SalaryTaxValidator.Invalid_Salary);

	}

}
