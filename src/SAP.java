
public class SAP {
	private final Digraph myG;     // immutable copy of client's graph 
	private SAPInfo mySAPInfo;
	
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
		this.copy(G);
	}
	
	
	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) {
		if (v < 0 || v >= myG.V()) throw new IndexOutOfBoundsException();
		if (w < 0 || w >= myG.V()) throw new IndexOutOfBoundsException();
		if (mySAPInfo.v == v && mySAPInfo.w == w) return mySAPInfo.length;
		
		mySAPInfo.v = v;
		mySAPInfo.w = w;
		mySAPInfo.ancestor = -1;
		mySAPInfo.length = -1;
		DeluxBFS vBFDP = new DeluxBFS(myG, v);
		DeluxBFS wBFDP = new DeluxBFS(myG, w);	
		SET<Integer> vAncestors = vBFDP.getAncestors();
		SET<Integer> wAncestors = wBFDP.getAncestors();
		setSAP(v, vAncestors, vBFDP, w, wAncestors, wBFDP);
		return mySAPInfo.length;
	}
	
	
	// length of shortest ancestral path between any vertex in v and any vertex in w; 
	// -1 if no such path
	public int length (Iterable<Integer> v, Iterable<Integer> w) {
		DeluxBFS vBFDP = new DeluxBFS(myG, v);
		DeluxBFS wBFDP = new DeluxBFS(myG, w);
		for (int a : v) {
			for (int b : w) {
				SET<Integer> aAncestors = vBFDP.getAncestors(a);
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
		if (mySAPInfo.v == v && mySAPInfo.w == w) return mySAPInfo.ancestor;
		
		mySAPInfo.v = v;
		mySAPInfo.w = w;
		mySAPInfo.ancestor = -1;
		mySAPInfo.length = -1;
		DeluxBFS vBFDP = new DeluxBFS(myG, v);
		DeluxBFS wBFDP = new DeluxBFS(myG, w);
		SET<Integer> vAncestors = vBFDP.getAncestors();
		SET<Integer> wAncestors = wBFDP.getAncestors();
		setSAP(v, vAncestors, vBFDP, w, wAncestors, wBFDP);
		return  mySAPInfo.ancestor;
	}
	
	
	// a common ancestor of v and w that participates in a shortest ancestral
	// path; -1 if no such path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		DeluxBFS vBFDP = new DeluxBFS(myG, v);
		DeluxBFS wBFDP = new DeluxBFS(myG, w);
		for (int a : v) {
			for (int b : w) {
				SET<Integer> aAncestors = vBFDP.getAncestors(a);
				SET<Integer> bAncestors = wBFDP.getAncestors(b);
				setSAP(a, aAncestors, vBFDP, b, bAncestors, wBFDP);
			}
		}
		return  mySAPInfo.ancestor;
	}
	
	
	private void setSAP(int a, SET<Integer> aAncestors, DeluxBFS aPaths,
			            int b, SET<Integer> bAncestors, DeluxBFS bPaths) {
		
		if (aAncestors != null && bAncestors != null) {
			// intersection of both ancestors
			SET<Integer> intersection = aAncestors.intersects(bAncestors);
			if (intersection.size() != 0) {
			    for (int x : intersection) {
				    int currDist = aPaths.distTo(x) + bPaths.distTo(x);
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
		else if (aAncestors == null) {
			// a has no ancestors
			for (int x : bAncestors) {
				int currDist = bPaths.distTo(x); // is this correct or special case?
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
		else {
			// b has no ancestors
			for (int x : aAncestors) {
				int currDist = aPaths.distTo(x); // is this correct or special case?
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
	
	
	private void copy(Digraph G) {
		// Defensive copy 
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
