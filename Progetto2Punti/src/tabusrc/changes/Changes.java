package tabusrc.changes;

import java.util.ArrayList;
import java.util.List;

public class Changes {
	public List<Change> list;
	public Changes() {
		this.list = new ArrayList<Change>();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Changes))
			return false;
		Changes other = (Changes)obj;
		if(other.list.size() != this.list.size()) {
			return false;
		}else {
			int found = 0;
			for(int i = 0; i < list.size(); i++) {
				Change curr_elem = this.list.get(i);
				for(Change oth_ch : other.list) {
					if(oth_ch.equals(curr_elem)) {
						found++;
						break;
					}
				}
			}
			return found == this.list.size(); //true if all found
		}
	}
	
	
}
