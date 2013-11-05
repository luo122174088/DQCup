package dqcup.repair.attr;

import static org.junit.Assert.*;

import org.junit.Test;

import dqcup.repair.attr.impl.FNameValidator;

public class FNameValidatorTest {

	@Test
	public void testValid() {
		AttributeValidator repair = new FNameValidator();
		assertTrue(repair.validate("Luochen"));
		assertTrue(repair.validate("Dept."));
		assertTrue(repair.validate("Jodoinst.jean"));
		assertTrue(repair.validate("LUOCHEN"));
		assertFalse(repair.validate("luochen"));
		assertFalse(repair.validate("lu0chen"));
		assertFalse(repair.validate("Lu0chen"));
		assertFalse(repair.validate(null));
		assertFalse(repair.validate(""));

	}

}
