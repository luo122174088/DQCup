package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.impl.ZipValidator;

public class ZipValidatorTest {

	@Test
	public void testValid() {
		AttributeValidator repair = new ZipValidator();
		assertTrue(repair.validate("12345"));
		assertFalse(repair.validate(null));
		assertFalse(repair.validate("1234"));
		assertFalse(repair.validate("1234a"));

	}

}
