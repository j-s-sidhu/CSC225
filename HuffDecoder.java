/* HuffDecoder.java

   Starter code for compressed file decoder. You do not have to use this
   program as a starting point if you don't want to, but your implementation
   must have the same command line interface. Do not modify the HuffFileReader
   or HuffFileWriter classes (provided separately).
   
   B. Bird - 03/19/2019
   (Add your name/studentID/date here)
*/

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;


public class HuffDecoder{

    private HuffFileReader inputReader;
    private BufferedOutputStream outputFile;

    /* Basic constructor to open input and output files. */
    public HuffDecoder(String inputFilename, String outputFilename) throws FileNotFoundException {
        inputReader = new HuffFileReader(inputFilename);
        outputFile = new BufferedOutputStream(new FileOutputStream(outputFilename));
    }


    public void decode() throws IOException{

        /* This is where actual decoding should happen. */
		ArrayList<HuffFileSymbol> l = new ArrayList<HuffFileSymbol>();
		HuffFileSymbol s = inputReader.readSymbol();
		huffNode root = new huffNode(0, null); // useless values since root is irrelevant
		
		while(s != null){
			//l.add(s);
			int[] h = s.symbolBits;
			huffNode current = root;
			for(int w = 0; w < h.length; w++){
				if(h[w] == 0){
					
					if(current.left == null){
						if(w == (h.length)-1){
							current.left = new huffNode(0, s);
							current = current.left;
						}
						else{
							current.left = new huffNode(0, null);
							current = current.left;
						}
					}
					else{
						current = current.left;
					}
				}
				else if(h[w] == 1){
					if(current.right == null){
						if(w == h.length-1){
							current.right = new huffNode(1, s);
							current = current.right;
						}
						else{
							current.right = new huffNode(1, null);
							current = current.right;
						}	
					}
					else{
						current = current.right;
					}
				}
			}
			// for (int x = 0; x < s.symbolBits.length; x++){
				// System.out.print(s.symbolBits[x]);
			// }
			// System.out.println();
			
			s = inputReader.readSymbol();	
		}
		
		int x = inputReader.readStreamBit();
		huffNode c = root;
		while(x != -1){
			if (x == 0 ){
				c = c.left;
			}
			else{
				c = c.right;
			}
			if(c.left == null && c.right == null){
				
				HuffFileSymbol ss = c.sym;
				outputFile.write(ss.symbol);
				c = root;
			}
			x = inputReader.readStreamBit();
		//	System.out.println(x);
		}
		inputReader.close();
		outputFile.close();
        /* The outputFile.write() method can be used to write individual bytes to the output file.*/
        
    }
	


    public static void main(String[] args) throws IOException{
        if (args.length != 2){
            System.err.println("Usage: java HuffDecoder <input file> <output file>");
            return;
        }
        String inputFilename = args[0];
        String outputFilename = args[1];

        try {
            HuffDecoder decoder = new HuffDecoder(inputFilename, outputFilename);
            decoder.decode();
        } catch (FileNotFoundException e) {
            System.err.println("Error: "+e.getMessage());
        }
    }
}
