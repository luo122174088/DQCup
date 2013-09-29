package dqcup.repair.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import dqcup.repair.DatabaseRepair;
import dqcup.repair.DbFileReader;
import dqcup.repair.RepairedCell;
import dqcup.repair.Tuple;

public class DatabaseRepairImplExample implements DatabaseRepair {

	@Override
	public Set<RepairedCell> repair(String fileRoute) {
		//返回的结果集合,每找到一个要修复的Cell就会添加到这个集合中
		HashSet<RepairedCell> result = new HashSet<RepairedCell>();
		
		//读入Input文件，
		LinkedList<Tuple> tuples = DbFileReader.readFile(fileRoute);

		//对Tuple进行遍历
		for(Tuple tuple : tuples){
			//有两种方式可以获取Tuple中某一列的值，用列的index(起始为第0列)或是用列名均可
			System.out.println("SSN: "+tuple.getValue(1)+" ZIP: "+tuple.getValue("ZIP"));
		}
		
		//这里我们通过肉眼观测能发现两个Cell有错误，第2行(起始为第0行)的Tuple中的ZIP多写了一位，第6行的Tuple的ZIP出现了字母
		//将更正后的Cell添加到result集合中(即第2行的ZIP应改为"97420",第6行的ZIP应改为"88114")
		result.add(new RepairedCell(2, "ZIP", "97420"));
		result.add(new RepairedCell(6, "ZIP", "88114"));
		
		//返回result集合
		return result;
	}

}
