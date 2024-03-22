package tabusrc.changes;

import java.util.List;

import tabusrc.KeyValue;

public class Solution {
	public List<KeyValue> value;
	public Solution(List<KeyValue> value) {
		this.value = value;
	}
	
	/**
	 * Get changes from old solution (current instance) to new_solution
	 * @param new_solution
	 * @return list of changes
	 * @throws Exception
	 */
	public Changes getChangesFromNew(Solution new_solution) throws Exception {
		Changes changes = null;
		if(value != null && new_solution != null) {
			if(new_solution.value != null) {
				changes = new Changes();
				for(int i = 0; i < value.size(); i++) {
					//If elements are not equals
					int curr_key_i = value.get(i).key;
					if(curr_key_i != new_solution.value.get(i).key) {
						//Find the change
						boolean found = false;
						for(int j = 0; j < value.size(); j++) {
							if(j==i)continue;
							if(curr_key_i == new_solution.value.get(j).key) {
								//Change from old to new (i to j)
								Change ch = new Change(curr_key_i,i,j);
								changes.list.add(ch);
								found = true;
								break;
							}
						}
						if(!found)
							throw new Exception("Illegal change.");
					}
				}
			}
			else 
				throw new Exception("Solution is null. You can not compare to other.");
		}else
			throw new Exception("Solution is null. You can not compare to other.");
		return changes;
	}
}
