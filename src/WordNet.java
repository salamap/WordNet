import java.util.HashMap;


public class WordNet {
	private final HashMap<Bag<String>, Integer> synSetMap;   // data structure of synset nouns read from file
	private final Digraph G;                                 // DAG of WordNet words
	private final SET<String> nounSet;                       // set of WordNet nouns
	private int largestID = 0;                               // largest id tracks the largest id read so far
	
	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) {
		In syns = new In(synsets);                           // synsets file
		
		In nyms = new In(hypernyms);                         // hypernyms file 
		
		this.synSetMap = new HashMap<Bag<String>, Integer>();// data structure to store collection of strings with associated id
		
		this.nounSet = new SET<String>();                    // set of all the nouns read from synsets file
		
		buildSynSetDS(syns);
		
		this.G = new Digraph(largestID + 1);
		
		buildGraph(nyms);
		
		// if the input did not correspond to a rooted DAG throw exception
		if (new DirectedCycle(this.G).hasCycle()) throw new IllegalArgumentException();
	}

	
	// the set of nouns (no duplicates), returned as an Iterable
	public Iterable<String> nouns() {
		return this.nounSet;
	}

	
	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		return this.nounSet.contains(word);
	}

	
	// distance between nounA and nounB (defined below)
	public int distance(String nounA, String nounB) {
		return 0;
	}

	
	// a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	// in a shortest ancestral path (defined below)
	public String sap(String nounA, String nounB) {
		return null;
	}

	
	private void buildSynSetDS(In synSet) {
		while(synSet.hasNextLine()) {                          // while the file has content to read
			String[] fields = synSet.readLine().split(",");    // split the line on commas
			
			Bag<String> nouns = new Bag<String>();            
			
			nouns.add(fields[1]);                            // save the sysnset noun to the collection
			
			this.nounSet.add(fields[1]);                     // add the synset noun to the noun set
			
			for (String s : fields[1].split(" ")) {          // split the noun into separate nouns and add to the collection
				nouns.add(s);
				
				this.nounSet.add(s);                         // add the separate nouns to the noun set
			}
			
			int id = Integer.parseInt(fields[0]);            // save the id which is the first field in a line
			
			this.synSetMap.put(nouns, id);                   // collection serves as key and id is the value
			
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
	
	// for unit testing of this class
	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
		for(Bag<String> key : wordnet.synSetMap.keySet()) {
			for (String item : key) {
			    StdOut.println(item +": ");
			    StdOut.println(wordnet.synSetMap.get(key));
			}
		}
		StdOut.println(wordnet.G);
	}
}
