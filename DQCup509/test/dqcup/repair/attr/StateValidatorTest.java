package dqcup.repair.attr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dqcup.repair.attr.impl.StateValidator;

public class StateValidatorTest {

	@Test
	public void testValid() {
		AttributeValidator repair = new StateValidator();
		assertTrue(repair.validate("CA"));
		assertFalse(repair.validate("Luo"));
		assertFalse(repair.validate(null));

	}

}
