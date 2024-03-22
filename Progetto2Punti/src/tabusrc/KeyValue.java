package tabusrc;


public class KeyValue implements Comparable<KeyValue>{
	public int key;
	public double value;
	
	@Override
	public int compareTo(KeyValue o) {
		//Sono valori frazionari < 1. Quindi moltiplico almeno per 100.
		//Per questo dataset va bene 1000 anche ma per un altro dataset
		//con più dati serve una maggior precisione.
		return (int)((this.value - o.value)*1000.0);
	}
}
