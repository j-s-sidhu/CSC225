public class huffNode{
	
	public huffNode left;
	public huffNode right;
	
	public int bit;
	public HuffFileSymbol sym;
	
	public huffNode(int x, HuffFileSymbol y){
		bit = x;
		sym = y;
		
		left = null;
		right = null;
	}
}