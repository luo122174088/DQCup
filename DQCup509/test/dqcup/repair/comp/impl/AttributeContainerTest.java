package dqcup.repair.comp.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import dqcup.repair.attr.AttributeContainer;
import dqcup.repair.attr.AttributeContainer.AttributeEntry;

public class AttributeContainerTest {

	@Test
	public void test() {
		AttributeContainer container = new AttributeContainer();
		container.add("a");
		container.add("b");
		container.add("c");
		container.add("a");
		container.add("a");
		container.add("b");
		List<AttributeEntry> order = container.getOrderValues();
		assertTrue(order.get(0).count==3);
		assertTrue(order.get(0).value.equals("a"));

		assertTrue(order.get(2).count==1);
		assertTrue(order.get(2).value.equals("c"));
	}

}
