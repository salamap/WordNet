
public class WordNet {
	// hash table of synset nouns, key is a word and value are IDs
	private final SeparateChainingHashST<String, Bag<Integer>> WordNetTable;
	
	// balancedBST of synset IDs, key is an ID and value is the original noun
	private final RedBlackBST<Integer, String> WordNetMap;                    
	
	private final Digraph G;                             // DAG of WordNet words
	
	private final SET<String> NounSet;                   // set of WordNet nouns
	
	private int largestID = 0;                           // largest id tracks the largest id read so far
	
	private SAP mySAP;
	
	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) {
		In syns = new In(synsets);                       // synsets file
		
		In nyms = new In(hypernyms);                     // hypernyms file 
		
		this.WordNetTable = new SeparateChainingHashST<String, Bag<Integer>>();
		
		this.WordNetMap = new RedBlackBST<Integer, String>();
		
		this.NounSet = new SET<String>();  
		
		this.buildWordNetDS(syns);
		
		this.G = new Digraph(largestID + 1);
		
		this.buildGraph(nyms);
		
		// if the input did not correspond to a rooted DAG throw exception
		if (new DirectedCycle(this.G).hasCycle()) throw new IllegalArgumentException();
		
		// otherwise build a SAP object for this DAG
		this.mySAP = new SAP(this.G);
	}

	
	// the set of nouns (no duplicates), returned as an Iterable
	public Iterable<String> nouns() {
		return this.NounSet;
	}

	
	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		return this.NounSet.contains(word);
	}

	
	// distance(A, B) = distance is the minimum length of any ancestral path 
	// between any synset v of A and any synset w of B.
	public int distance(String nounA, String nounB) {
		if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
		
		Bag<Integer> v = this.WordNetTable.get(nounA);
		
		Bag<Integer> w = this.WordNetTable.get(nounB);
		
		return this.mySAP.length(v, w);
	}

	
	// a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	// in a shortest ancestral path
	public String sap(String nounA, String nounB) {
		if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
		
		Bag<Integer> v = this.WordNetTable.get(nounA);
		
		Bag<Integer> w = this.WordNetTable.get(nounB);
		
		int ancestor = this.mySAP.ancestor(v, w);
		
		return this.WordNetMap.get(ancestor);
	}

	
	private void buildWordNetDS(In synSet) {
		while(synSet.hasNextLine()) {                          // while the file has content to read
			String[] fields = synSet.readLine().split(",");    // split the line on commas
			
			this.NounSet.add(fields[1]);                       // add the synset noun to the noun set
			
			int id = Integer.parseInt(fields[0]);              // save the id which is the first field in a line
			
			this.WordNetMap.put(id, fields[1]);                // update map with id and associated sysnet noun
			
			this.updateTable(fields[1], id);                   // update table with synset noun
			
			String[] words = fields[1].split(" ");             // split the noun into separate words
			
			if (words.length > 1) {
				for (String s : words) {
					this.NounSet.add(s);                       // add the separate words to the noun set
					this.updateTable(s, id);                   // update table with separate words
				}
			}
			
			if (id > this.largestID) this.largestID = id;
		}
	}
	
	
	private void buildGraph(In hyperNyms) {
		while (hyperNyms.hasNextLine()) {
			String[] fields = hyperNyms.readLine().split(",");
			
			int N = fields.length;
			
			int nounID = Integer.parseInt(fields[0]);
			
			for (int i = 1; i < N; i++) {
				int hyperNymID = Integer.parseInt(fields[i]);
				this.G.addEdge(nounID, hyperNymID);
			}
		}
	}
	
	
	// update WordNetTable helper method
	private void updateTable(String word, int id) {
		if (this.WordNetTable.contains(word)) this.WordNetTable.get(word).add(id);
		else {
			this.WordNetTable.put(word, new Bag<Integer>());
			this.WordNetTable.get(word).add(id);
		}
	}
	
	
	// for unit testing of this class
	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
	    while (!StdIn.isEmpty()) {
	        String a = StdIn.readString();
	        String b = StdIn.readString();
	        int length   = wordnet.distance(a, b);
	        String ancestor = wordnet.sap(a, b);
	        StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
	    }	
	}
}