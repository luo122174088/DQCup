package dqcup.repair.impl;

import java.io.BufferedReader;
import java.io.FileReader;

import dqcup.repair.attr.composite.impl.SalaryTaxValidator;
import dqcup.repair.attr.impl.StateValidator;
import dqcup.repair.comp.DQTuple;
import dqcup.repair.comp.impl.SalaryTaxRepairer;

public class TestSalary {
	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("input/DB-normal-comb.txt"));
		String line = null;
		StateValidator stateValidator = new StateValidator();
		SalaryTaxValidator stValidator = new SalaryTaxValidator();
		boolean column = true;
		SalaryTaxRepairer repair = new SalaryTaxRepairer();
		while ((line = reader.readLine()) != null) {
			if (column) {
				column = false;
				continue;
			}
			String[] tuple = line.split(":");
			String state = tuple[DQTuple.STATE_INDEX + DQTuple.Offset];
			String salary = tuple[DQTuple.SALARY_INDEX + DQTuple.Offset];
			String tax = tuple[DQTuple.TAX_INDEX + DQTuple.Offset];
			String cuid = tuple[1];
			if (stateValidator.validate(state)
					&& stValidator.strictValidate(salary, tax, null) == 0) {
				repair.addIndex(state, salary, tax, cuid);
			}
		}
		reader.close();

		repair.print();
	}
}
