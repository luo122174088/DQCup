package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.impl.FNameValidator;

public class FNameValidatorTest {

	@Test
	public void testValid() {
		AttributeValidator repair = new FNameValidator();
		assertTrue(repair.valid("Luochen"));
		assertFalse(repair.valid("luochen"));
		assertFalse(repair.valid("lu0chen"));
		assertFalse(repair.valid(null));
		assertFalse(repair.valid(""));

	}

}
