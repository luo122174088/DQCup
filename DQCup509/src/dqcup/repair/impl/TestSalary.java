package dqcup.repair.impl;

import java.io.BufferedReader;
import java.io.FileReader;

import dqcup.repair.attr.composite.impl.SalaryTaxValidator;
import dqcup.repair.attr.impl.SalaryValidator;
import dqcup.repair.attr.impl.StateValidator;
import dqcup.repair.attr.impl.TaxValidator;
import dqcup.repair.comp.DQTuple;

public class TestSalary {
	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("input/DB-normal.txt"));
		String line = null;
		StateValidator stateValidator = new StateValidator();
		SalaryValidator salaryValidator = new SalaryValidator();
		TaxValidator taxValidator = new TaxValidator();
		boolean column = true;
		SalaryTaxValidator stValidator = new SalaryTaxValidator();
		while ((line = reader.readLine()) != null) {
			if (column) {
				column = false;
				continue;
			}
			String[] tuple = line.split(":");
			String state = tuple[DQTuple.STATE_INDEX + DQTuple.Offset];
			String salary = tuple[DQTuple.SALARY_INDEX + DQTuple.Offset];
			String tax = tuple[DQTuple.TAX_INDEX + DQTuple.Offset];
			if (stateValidator.validate(state) && salaryValidator.validate(salary)
					&& taxValidator.validate(tax)) {
				if (!stValidator.validate(salary, tax)) {
					System.out.println(tuple[0] + "\t" + salary + "\t" + tax);
				}
			}
		}
		reader.close();
	}
}
