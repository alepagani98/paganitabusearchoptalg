package tabusrc.tabumanager;

import java.util.ArrayList;
import java.util.List;

import tabusrc.changes.Changes;
import tabusrc.changes.Solution;

public class TabuManager {
	public List<Changes> tabu_list;
	public int tabuListSize;
	public TabuManager(int tabuListSize) {
		this.tabu_list = new ArrayList<Changes>();
		this.tabuListSize = tabuListSize;
	}
	
	public void add_tabu(Changes ch) {
		this.tabu_list.add(ch);
		if(this.tabu_list.size() > this.tabuListSize) {
			this.tabu_list.remove(0); // remove first (oldest)
		}
	}
	
	public boolean allow(Solution current, Solution neighboor) throws Exception {
		// Retrieve new changes from current solution to another one
		// and check if are not taboo
		Changes new_changes = current.getChangesFromNew(neighboor);
		for(Changes ch : tabu_list) {
			if(ch.equals(new_changes)) {
				return false;
			}
		}
		return true;
	}
}
