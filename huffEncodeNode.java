public class huffEncodeNode implements Comparable<huffEncodeNode>{
	
	public int freq;
	public byte symbol;
	
	public huffEncodeNode left;
	public huffEncodeNode right;
	
	public huffEncodeNode(byte s, int f){
		this.symbol = s;
		this.freq = f;
		this.left = null;
		this.right = null;
	}
	
	public int compareTo( huffEncodeNode n){
		if(this.freq < n.freq){
			return -1;
		}
		if(this.freq > n.freq){
			return 1;
		}
		else{
			return 0;
		}
	}
}