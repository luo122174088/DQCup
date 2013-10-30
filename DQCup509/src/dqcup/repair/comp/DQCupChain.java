package dqcup.repair.comp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import dqcup.repair.RepairedCell;

public class DQCupChain {
	private LinkedList<DQCupProcessor> procs = null;
	private DQCupContext context = null;

	public DQCupChain(String filePath, HashSet<RepairedCell> repairs) {
		procs = new LinkedList<DQCupProcessor>();
		context = new DQCupContext(filePath, repairs);
	}

	public void add(DQCupProcessor proc) {
		procs.add(proc);
	}

	public void remove(DQCupProcessor proc) {
		procs.remove(proc);
	}

	public void execute() {
		Iterator<DQCupProcessor> it = procs.iterator();
		while (it.hasNext()) {
			try {
				DQCupProcessor proc = it.next();
				proc.process(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
