package tabusrc.changes;

public class Change {
	public int key;
	public int from;
	public int to;
	public Change(int key, int from, int to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Change))
			return false;
		Change other = (Change)obj;
		if(this.key == other.key
				&&
				this.from == other.from
				&&
				this.to == other.to){
			return true;
		}
		return false;
	}
}
