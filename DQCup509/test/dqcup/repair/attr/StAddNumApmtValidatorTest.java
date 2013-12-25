package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.composite.impl.StAddNumApmtValidator;

public class StAddNumApmtValidatorTest {

	@Test
	public void testValidate() {
		StAddNumApmtValidator validator = new StAddNumApmtValidator();
		assertTrue(validator.strictValidate("Jodoinst.jean Lane", "1", "4c8") == 0);
		assertTrue(validator.strictValidate("Jodoinst.jean Lane", "11111", "4c8") == StAddNumApmtValidator.Invalid_StNum);
		assertTrue(validator.strictValidate("Jodoinst.jean Lane", "a", "4c8") == StAddNumApmtValidator.Invalid_StNum);
		assertTrue(validator.strictValidate("Jodoinst.jean Lane", "", "4c8") == StAddNumApmtValidator.Invalid_StNum);
		assertTrue(validator.strictValidate("Jodoinst.jean Lane", "1", "") == StAddNumApmtValidator.Invalid_Apmt);
		assertTrue(validator.strictValidate("Jodoinst.jean Lane", "", "") == (StAddNumApmtValidator.Invalid_Apmt | StAddNumApmtValidator.Invalid_StNum));
		assertTrue(validator.strictValidate("PO Bo", "1", "4c8") == 0);

		assertTrue(validator.strictValidate("PO Box", "", "") == StAddNumApmtValidator.Invalid_StAdd);
		assertTrue(validator.strictValidate("PO Bo", "", "") == (StAddNumApmtValidator.Invalid_StNum | StAddNumApmtValidator.Invalid_Apmt));
		assertTrue(validator.strictValidate("PO Box", "4", "4c8") == (StAddNumApmtValidator.Invalid_StAdd
				| StAddNumApmtValidator.Invalid_Apmt | StAddNumApmtValidator.Invalid_StNum));

	}

}
