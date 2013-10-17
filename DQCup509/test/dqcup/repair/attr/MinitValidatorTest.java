package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.impl.MinitValidator;

public class MinitValidatorTest {

	@Test
	public void testValid() {
		AttributeValidator repair = new MinitValidator();
		assertTrue(repair.valid(""));
		assertTrue(repair.valid(null));
		assertTrue(repair.valid("L"));
		assertFalse(repair.valid("l"));
		assertFalse(repair.valid("ll"));
	}

}
