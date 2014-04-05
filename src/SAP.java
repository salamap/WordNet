public class SAP {
	private final Digraph myG;     // immutable copy of client's graph 
	private SAPCache myCache;     // private class for caching and tracking SAP
	
	private class SAPCache  {
		int v;
		int w;
		int ancestor = -1;
		int length = -1;
		Iterable<Integer> V = null;
		Iterable<Integer> W = null;
		
		public SAPCache() {
			this(-1, -1);
		}
		
		public SAPCache(int v, int w) {
			this.v = v;
			this.w = w;
		}

		public SAPCache(Iterable<Integer> V, Iterable<Integer> W) {
			this.V = V;
			this.W = W;
		}
	}
	
	
	public SAP(Digraph G) {
		if (G == null) throw new NullPointerException();
		
		if (new DirectedCycle(G).hasCycle()) throw new IllegalArgumentException();
		
		this.myCache = new SAPCache();
		
		// make defensive copy
		this.myG = new Digraph(G);
		
		// make defensive copy
		//this.copy(G);
	}
	
	
	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) {
		if(!isValid(v) || !isValid(w)) throw new IndexOutOfBoundsException();

		//retrieve cached values
		if (myCache.v == v && myCache.w == w) return myCache.length;
		
		// update cache otherwise
		this.processVertices(v, w);
		
		return myCache.length;
	}
	
	
	// length of shortest ancestral path between any vertex in v and any vertex in w; 
	// -1 if no such path
	public int length (Iterable<Integer> v, Iterable<Integer> w) {
		if (myCache.V != null && myCache.W != null && 
			myCache.V.equals(v) && myCache.W.equals(w)) 
					return myCache.ancestor;
			
		for (int x : v) {
			if(!isValid(x)) throw new IndexOutOfBoundsException();
		}
		for (int x : w) {
			if(!isValid(x)) throw new IndexOutOfBoundsException();
		}
			
		this.processVertices(v, w);
		
		return myCache.length;
	}
	
	
	// a common ancestor of v and w that participates in a shortest ancestral
	// path; -1 if no such path
	public int ancestor(int v, int w) {
		if(!isValid(v) || !isValid(w)) throw new IndexOutOfBoundsException();
		
		//retrieve cached values
		if (myCache.v == v && myCache.w == w) return myCache.ancestor;
		
		// update cache otherwise
		this.processVertices(v, w);

		return  myCache.ancestor;
	}
	
	
	// a common ancestor of v and w that participates in a shortest ancestral
	// path; -1 if no such path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {		
		if (myCache.V != null && myCache.W != null && 
			myCache.V.equals(v) && myCache.W.equals(w)) 
				return myCache.ancestor;
		
		for (int x : v) {
			if(!isValid(x)) throw new IndexOutOfBoundsException();
		}
		for (int x : w) {
			if(!isValid(x)) throw new IndexOutOfBoundsException();
		}	
		
		this.processVertices(v, w);
		
		return  myCache.ancestor;
	}
	
	
	// kick off the SAP process for single source vertices
	private void processVertices (int v, int w) {
		this.myCache = new SAPCache(v, w);
		
		// conduct BFS from vertices of interest
		DeluxBFS vBFDP = new DeluxBFS(myG, v);
		DeluxBFS wBFDP = new DeluxBFS(myG, w);
		
		setSAP(vBFDP, wBFDP);
	}
	
	
	// kick off the SAP process for a collection of source vertices
	private void processVertices (Iterable<Integer> v, Iterable<Integer> w) {
		this.myCache = new SAPCache(v, w);
		
		// conduct BFS from vertices of interest
		DeluxBFS vBFDP = new DeluxBFS(myG, v);
		DeluxBFS wBFDP = new DeluxBFS(myG, w);
		
		setSAP(vBFDP, wBFDP);
	}
	
	
	// set SAP information based on provided parameters
	private void setSAP(DeluxBFS aPaths, DeluxBFS bPaths) {
		
		Queue<Integer> ancestors = new Queue<Integer>();
        
		for (int i = 0; i < this.myG.V(); i++) {
            if (aPaths.hasPathTo(i) && bPaths.hasPathTo(i)) {
                
            	ancestors.enqueue(i);
            }
        }
        
        for (Integer x : ancestors) {
        	int currDist = aPaths.distTo(x) + bPaths.distTo(x);
		    if (myCache.length == -1) {
	    	    
		    	// update the length field
	    	    myCache.length = currDist;
	    	    
	    	    // update the ancestor field
	    	    myCache.ancestor = x;
	        }
	        else if (currDist < myCache.length) {
		        myCache.length = currDist;
		        
		        myCache.ancestor = x;
	        }
        }
	}
	
	private boolean isValid(int v) {
		if (v < 0 || v >= myG.V()) return false;
		return true;
	}
	
	// Defensive copy 
//	private void copy(Digraph G) {
//		for (int v = 0; v < G.V(); v++) {
//			for (Integer w : G.adj(v)) {
//				this.myG.addEdge(v, w);
//			}
//		}
//	}
	
	
	public static void main(String[] args) {
	    In in = new In(args[0]);
	    Digraph G = new Digraph(in);
	    SAP sap = new SAP(G);
	    while (!StdIn.isEmpty()) {
	        int v = StdIn.readInt();
	        int w = StdIn.readInt();
	        int length   = sap.length(v, w);
	        int ancestor = sap.ancestor(v, w);
	        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
	    }
	}

}