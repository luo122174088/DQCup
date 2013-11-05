package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.impl.MinitValidator;

public class MinitValidatorTest {

	@Test
	public void testValid() {
		AttributeValidator repair = new MinitValidator();
		assertTrue(repair.validate(""));
		assertTrue(repair.validate(null));
		assertTrue(repair.validate("L"));
		assertFalse(repair.validate("l"));
		assertFalse(repair.validate("ll"));
	}

}
