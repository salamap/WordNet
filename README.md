WordNet
=======
WordNet is a semantic lexicon for the English language that is used extensively by computational linguists and cognitive scientists; for example, it was a key component in IBM's Watson. WordNet groups words into sets of synonyms called synsets and describes semantic relationships between them. One such relationship is the is-a relationship, which connects a hyponym (more specific synset) to a hypernym (more general synset). For example, locomotion is a hypernym of running and running is a hypernym of dash.

The WordNet digraph: each vertex v is an integer that represents a synset, and each directed edge v→w represents that w is a hypernym of v. The wordnet digraph is a rooted DAG: it is acyclic and has one vertex—the root—that is an ancestor of every other vertex. However, it is not necessarily a tree because a synset can have more than one hypernym. 

The WordNet input file formats. The files are in CSV format: each line contains a sequence of fields, separated by commas.

List of noun synsets. The file synsets.txt lists all the (noun) synsets in WordNet. The first field is the synset id (an integer), the second field is the synonym set (or synset), and the third field is its dictionary definition (or gloss). For example, the line
36,AND_circuit AND_gate,a circuit in a computer that fires only when all of its inputs fire  
means that the synset { AND_circuit, AND_gate } has an id number of 36 and it's gloss is a circuit in a computer that fires only when all of its inputs fire. The individual nouns that comprise a synset are separated by spaces (and a synset element is not permitted to contain a space). The S synset ids are numbered 0 through S − 1; the id numbers will appear consecutively in the synset file.
List of hypernyms. The file hypernyms.txt contains the hypernym relationships: The first field is a synset id; subsequent fields are the id numbers of the synset's hypernyms. For example, the following line
164,21012,56099
means that the the synset 164 ("Actifed") has two hypernyms: 21012 ("antihistamine") and 56099 ("nasal_decongestant"), representing that Actifed is both an antihistamine and a nasal decongestant. The synsets are obtained from the corresponding lines in the file synsets.txt.

164,Actifed,trade name for a drug containing an antihistamine and a decongestant...
21012,antihistamine,a medicine used to treat allergies...
56099,nasal_decongestant,a decongestant that provides temporary relief of nasal...

WordNet data type:

// constructor takes the name of the two input files
public WordNet(String synsets, String hypernyms)

// the set of nouns (no duplicates), returned as an Iterable
public Iterable<String> nouns()

// is the word a WordNet noun?
public boolean isNoun(String word)

// distance between nounA and nounB (defined below)
public int distance(String nounA, String nounB)

// a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
// in a shortest ancestral path (defined below)
public String sap(String nounA, String nounB)

// for unit testing of this class
public static void main(String[] args)

The constructor throws a java.lang.IllegalArgumentException if the input does not correspond to a rooted DAG. The distance() and sap() methods throws a java.lang.IllegalArgumentException unless both of the noun arguments are WordNet nouns.

Shortest ancestral path. An ancestral path between two vertices v and w in a digraph is a directed path from v to a common ancestor x, together with a directed path from w to the same ancestor x. A shortest ancestral path is an ancestral path of minimum total length.

SAP data type:

// constructor takes a digraph (not necessarily a DAG)
public SAP(Digraph G)

// length of shortest ancestral path between v and w; -1 if no such path
public int length(int v, int w)

// a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
public int ancestor(int v, int w)

// length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
public int length(Iterable<Integer> v, Iterable<Integer> w)

// a common ancestor that participates in shortest ancestral path; -1 if no such path
public int ancestor(Iterable<Integer> v, Iterable<Integer> w)

// for unit testing of this class (such as the one below)
public static void main(String[] args)

All methods thow a java.lang.IndexOutOfBoundsException if one (or more) of the input arguments is not between 0 and G.V() - 1.

Measuring the semantic relatedness of two nouns. Semantic relatedness refers to the degree to which two concepts are related. Measuring semantic relatedness is a challenging problem.

We define the semantic relatedness of two wordnet nouns A and B as follows:

distance(A, B) = distance is the minimum length of any ancestral path between any synset v of A and any synset w of B.
This is the notion of distance that you will use to implement the distance() and sap() methods in the WordNet data type.

Outcast detection. Given a list of wordnet nouns A1, A2, ..., An, which noun is the least related to the others? To identify an outcast, compute the sum of the distances between each noun and every other one:

di   =   dist(Ai, A1)   +   dist(Ai, A2)   +   ...   +   dist(Ai, An)
and return a noun At for which dt is maximum.

Ddata type Outcast:

// constructor takes a WordNet object
public Outcast(WordNet wordnet)

// given an array of WordNet nouns, return an outcast
public String outcast(String[] nouns)

// for unit testing of this class (such as the one below)
public static void main(String[] args)

Here is a sample execution:
% more outcast5.txt
horse zebra cat bear table

% more outcast8.txt
water soda bed orange_juice milk apple_juice tea coffee

% more outcast11.txt
apple pear peach banana lime lemon blueberry strawberry mango watermelon potato

% java Outcast synsets.txt hypernyms.txt outcast5.txt outcast8.txt outcast11.txt
outcast5.txt: table
outcast8.txt: bed
outcast11.txt: potato
