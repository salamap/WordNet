
public class SAP {
	private final Digraph myG;     // immutable copy of client's graph 
	private SAPInfo mySAPInfo;     // private class for caching and tracking SAP
	
	private class SAPInfo  {
		int v = -1;
		int w = -1;
		int ancestor = -1;
		int length = -1;
	}
	
	
	public SAP(Digraph G) {
		if (G == null) throw new NullPointerException();
		if (new DirectedCycle(G).hasCycle()) throw new IllegalArgumentException();
		
		this.mySAPInfo = new SAPInfo();
		this.myG = new Digraph(G.V());
		// make defensive copy
		this.copy(G);
	}
	
	
	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) {
		if (v < 0 || v >= myG.V()) throw new IndexOutOfBoundsException();
		if (w < 0 || w >= myG.V()) throw new IndexOutOfBoundsException();
		//retrieve cached values
		if (mySAPInfo.v == v && mySAPInfo.w == w) return mySAPInfo.length;
		
		// update cache otherwise
		processVertices(v, w);
		
		return mySAPInfo.length;
	}
	
	
	// length of shortest ancestral path between any vertex in v and any vertex in w; 
	// -1 if no such path
	public int length (Iterable<Integer> v, Iterable<Integer> w) {
		DeluxBFS vBFDP = new DeluxBFS(myG, v);
		DeluxBFS wBFDP = new DeluxBFS(myG, w);
		for (int a : v) {
			SET<Integer> aAncestors = vBFDP.getAncestors(a);
			for (int b : w) {
				SET<Integer> bAncestors = wBFDP.getAncestors(b);
				setSAP(a, aAncestors, vBFDP, b, bAncestors, wBFDP);
			}
		}
		return mySAPInfo.length;
	}
	
	
	// a common ancestor of v and w that participates in a shortest ancestral
	// path; -1 if no such path
	public int ancestor(int v, int w) {
		if (v < 0 || v >= myG.V()) throw new IndexOutOfBoundsException();
		if (w < 0 || w >= myG.V()) throw new IndexOutOfBoundsException();
		//retrieve cached values
		if (mySAPInfo.v == v && mySAPInfo.w == w) return mySAPInfo.ancestor;
		
		// update cache otherwise
		processVertices(v, w);

		return  mySAPInfo.ancestor;
	}
	
	
	// a common ancestor of v and w that participates in a shortest ancestral
	// path; -1 if no such path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		DeluxBFS vBFDP = new DeluxBFS(myG, v);
		DeluxBFS wBFDP = new DeluxBFS(myG, w);
		for (int a : v) {
			SET<Integer> aAncestors = vBFDP.getAncestors(a);
			for (int b : w) {
				SET<Integer> bAncestors = wBFDP.getAncestors(b);
				setSAP(a, aAncestors, vBFDP, b, bAncestors, wBFDP);
			}
		}
		return  mySAPInfo.ancestor;
	}
	
	
	// kick off the SAP process
	private void processVertices (int v, int w) {
		mySAPInfo.v = v;
		mySAPInfo.w = w;
		mySAPInfo.ancestor = -1;
		mySAPInfo.length = -1;
		// conduct BFS from vertices of interest
		DeluxBFS vBFDP = new DeluxBFS(myG, v);
		DeluxBFS wBFDP = new DeluxBFS(myG, w);	
		// save the ancestors of each vertex in a set
		SET<Integer> vAncestors = vBFDP.getAncestors();
		SET<Integer> wAncestors = wBFDP.getAncestors();
		
		setSAP(v, vAncestors, vBFDP, w, wAncestors, wBFDP);
	}
	
	
	// set SAP information based on provided parameters
	private void setSAP(int a, SET<Integer> aAncestors, DeluxBFS aPaths,
			            int b, SET<Integer> bAncestors, DeluxBFS bPaths) {
		
		if (aAncestors != null && bAncestors != null) {
			// intersection of both ancestors
			SET<Integer> intersection = aAncestors.intersects(bAncestors);
			// common ancestors found
			if (intersection.size() != 0) {
			    for (int x : intersection) {
				    int currDist = aPaths.distTo(x) + bPaths.distTo(x);
				    // first distance calculation to be performed for these parameters
				    if (mySAPInfo.length == -1) {
				    	// update the length field
				    	mySAPInfo.length = currDist;
				    	// update the ancestor field
				    	mySAPInfo.ancestor = x;
				    }
				    else if (currDist < mySAPInfo.length) {
					    mySAPInfo.length = currDist;
					    mySAPInfo.ancestor = x;
				    }
			    }
		    }
		}
		// a has no ancestors
		else if (aAncestors == null) {
			for (int x : bAncestors) {
				int currDist = bPaths.distTo(x);
			    if (mySAPInfo.length == -1) {
			    	mySAPInfo.length = currDist;
			    	mySAPInfo.ancestor = x;
			    }
			    else if (currDist < mySAPInfo.length) {
				    mySAPInfo.length = currDist;
				    mySAPInfo.ancestor = x;
			    }
			}
		}
		// b has no ancestors
		else {
			for (int x : aAncestors) {
				int currDist = aPaths.distTo(x);
			    if (mySAPInfo.length == -1) {
			    	mySAPInfo.length = currDist;
			    	mySAPInfo.ancestor = x;
			    }
			    else if (currDist < mySAPInfo.length) {
				    mySAPInfo.length = currDist;
				    mySAPInfo.ancestor = x;
			    }
			}
		}
	}
	
	
	// Defensive copy 
	private void copy(Digraph G) {
		for (int v = 0; v < G.V(); v++) {
			for (Integer w : G.adj(v)) {
				this.myG.addEdge(v, w);
			}
		}
	}
	
	
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
