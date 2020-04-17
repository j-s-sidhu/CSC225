/* HuffEncoder.java

   Starter code for compressed file encoder. You do not have to use this
   program as a starting point if you don't want to, but your implementation
   must have the same command line interface. Do not modify the HuffFileReader
   or HuffFileWriter classes (provided separately).
   
   B. Bird - 03/19/2019
   (Add your name/studentID/date here)
*/

import java.io.*;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Stack;

public class HuffEncoder{

    private BufferedInputStream inputFile;
    private HuffFileWriter outputWriter;

    public HuffEncoder(String inputFilename, String outputFilename) throws FileNotFoundException {
        inputFile = new BufferedInputStream(new FileInputStream(inputFilename));
        outputWriter = new HuffFileWriter(outputFilename);
    }


    public void encode() throws IOException{

        
        //You may want to start by reading the entire file into a list to make it easier
        //to navigate.
        LinkedList<Byte> input_bytes = new LinkedList<Byte>();
        for(int nextByte = inputFile.read(); nextByte != -1; nextByte = inputFile.read()){
            input_bytes.add((byte)nextByte);
        }
		
		TreeMap<Byte, huffEncodeNode> freqMap = new TreeMap<Byte, huffEncodeNode>();
		ListIterator<Byte> i = input_bytes.listIterator();
		
		//find frequency of symbols and put them in tree with key being symbol and value being the frequency (couldnt find a way to do with just frequency so made an object that holds the symbol and the frequency)
		while(i.hasNext()){
			byte b = i.next();
			
			try{
				huffEncodeNode n = freqMap.get(b);
				n.freq = n.freq +1;
				freqMap.put(b, n);
			}
			catch(NullPointerException e){
				
				huffEncodeNode n = new huffEncodeNode(b, 1);
				freqMap.put(b, n);
			}
			
		}
		
		
		
		PriorityQueue<huffEncodeNode> q = new PriorityQueue<huffEncodeNode>(freqMap.values());
		
		// keep removing and adding sub-trees until theres only one node which means theres no others left to merge as they are all part of one tree
		while(q.size() > 1){
			huffEncodeNode minNode = q.poll();
			huffEncodeNode secondMinNode = q.poll();
			
			int freq1 = minNode.freq;
			int freq2 = secondMinNode.freq;
			
			huffEncodeNode emptyRoot = new huffEncodeNode((byte)0, freq1+freq2);
			
			emptyRoot.left = minNode;
			emptyRoot.right = secondMinNode;
			
			q.add(emptyRoot);
		}
		
		Stack<Integer> stack = new Stack<Integer>();
		TreeMap<Byte, ArrayList<Integer>> symTable = new TreeMap<Byte, ArrayList<Integer>>();
		//create the symbol table
		traverse(q.peek(), stack, symTable);
		outputWriter.finalizeSymbols();
		
		i = input_bytes.listIterator();
		
		//encode each symbol
		while(i.hasNext()){
			byte by = i.next();
			ArrayList<Integer> al = symTable.get(by);
			Iterator<Integer> it = al.iterator();
			while(it.hasNext()){
				int z = it.next();
				outputWriter.writeStreamBit(z);
			}
		}
        //Suggested algorithm:

        //Compute the frequency of each input symbol. Since symbols are one character long,
        //you can simply iterate through input_bytes to see each symbol.
        
        //Build a prefix code for the encoding scheme (if using Huffman Coding, build a 
        //Huffman tree).
        
        //Write the symbol table to the output file

        //Call outputWriter.finalizeSymbols() to end the symbol table

        //Iterate through each input byte and determine its encode bitstring representation,
        //then write that to the output file with outputWriter.writeStreamBit()

        //Call outputWriter.close() to end the output file
		outputWriter.close();
		inputFile.close();
		

    }
	
	public void traverse(huffEncodeNode n, Stack<Integer> s, TreeMap<Byte, ArrayList<Integer>> tm){
		if(n.left == null && n.right == null){
			Stack<Integer> backwards = new Stack<Integer>();
			ArrayList<Integer> symBits = new ArrayList<Integer>(s.size());
			byte sy = n.symbol;
			int[] array = new int[s.size()];
			
			while(!(s.empty())){
				backwards.push(s.pop());
			}
			
			int x =0;
			while(!(backwards.empty())){;
				symBits.add(backwards.peek());
				s.push(backwards.pop());
				array[x] = symBits.get(symBits.size()-1);
				
				x++;
			}
			
			
			HuffFileSymbol sym = new HuffFileSymbol(sy, array);
			tm.put(sy, symBits);
			// System.out.println(sy);
			// System.out.println(s.size());
			outputWriter.writeSymbol(sym);
		}
		else{
			s.push(0);
			traverse(n.left, s, tm);
			s.pop();
			s.push(1);
			traverse(n.right, s, tm);
			s.pop();
		}
				
	
	}


    public static void main(String[] args) throws IOException{
        if (args.length != 2){
            System.err.println("Usage: java HuffEncoder <input file> <output file>");
            return;
        }
        String inputFilename = args[0];
        String outputFilename = args[1];

        try{
            HuffEncoder encoder = new HuffEncoder(inputFilename, outputFilename);
            encoder.encode();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException: "+e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: "+e.getMessage());
        }

    }
}

