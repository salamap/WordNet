
public class Outcast {
	private final WordNet wordNet;
	
	private class OutcastNoun implements Comparable<OutcastNoun> {
		int length = -1;
		String noun = null;
		
		public OutcastNoun(String noun) {
			this.noun = noun;
		}
		
		public int compareTo(OutcastNoun other) {
			if (this.length > other.length) return 1;
			else if (this.length < other.length) return -1;
			else return 0;
		}
	}
	
	// constructor takes a WordNet object
	public Outcast(WordNet wordnet) {
		this.wordNet = wordnet;
	}

	// given an array of WordNet nouns, return an outcast
	public String outcast(String[] nouns) {
		if (nouns == null) throw new NullPointerException();
		
		MaxPQ<OutcastNoun> q = new MaxPQ<OutcastNoun>();
		
		for (int i = 0; i < nouns.length; i++) {
			OutcastNoun x = new OutcastNoun(nouns[i]);	
			for (int j = 0; j < nouns.length; j++) {
				if (i == j ) continue;
				x.length += this.wordNet.distance(nouns[i], nouns[j]);	
			}
			q.insert(x);
		}
		return q.delMax().noun;
	}

	// for unit testing of this class
	public static void main(String[] args) {
	    WordNet wordnet = new WordNet(args[0], args[1]);
	    Outcast outcast = new Outcast(wordnet);
	    for (int t = 2; t < args.length; t++) {
	        In in = new In(args[t]);
	        String[] nouns = in.readAllStrings();
	        StdOut.println(args[t] + ": " + outcast.outcast(nouns));
	    }	
	}
}
