package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.impl.StAddNumApmtValidator;

public class StAddNumApmtValidatorTest {

	@Test
	public void testValidate() {
		StAddNumApmtValidator validator = new StAddNumApmtValidator();
		assertTrue(validator.validate("San Juan", "1", "4c8"));
		assertTrue(validator.validate("PO Box 123", "", ""));
		assertFalse(validator.validate("PO Box 123", "1", "4c8"));
		assertFalse(validator.validate("PO Box 123a", "1", "4c8"));
		assertFalse(validator.validate("PO Box 123a", "", ""));
		assertFalse(validator.validate("San Juan", "a", "4c8"));
		assertFalse(validator.validate("San Juan", "1", "448"));
		assertFalse(validator.validate("PO BOX 123", "", ""));

	}

}
