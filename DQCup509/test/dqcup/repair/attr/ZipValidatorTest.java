package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.impl.ZipValidator;

public class ZipValidatorTest {

	@Test
	public void testValid() {
		AttributeValidator repair = new ZipValidator();
		assertTrue(repair.valid("12345"));
		assertFalse(repair.valid(null));
		assertFalse(repair.valid("1234"));
		assertFalse(repair.valid("1234a"));

	}

}
