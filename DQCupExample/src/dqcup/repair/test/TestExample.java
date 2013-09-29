package dqcup.repair.test;

import java.util.Set;

import dqcup.repair.DatabaseRepair;
import dqcup.repair.RepairedCell;
import dqcup.repair.impl.DatabaseRepairImplExample;

public class TestExample {
	
	/**
	 * test your own repairing method
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception{
		//用你自己的类实现DatabaseRepair接口
		DatabaseRepair dr = new DatabaseRepairImplExample();
		//调用修复方法dr.repair
		Set<RepairedCell> result = dr.repair("input/DB-example.txt");
		System.out.println(result);
	}
}
