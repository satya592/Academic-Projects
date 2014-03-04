//package proj7;

import java.util.ArrayList;

class Node {
	int incoming = -1;
	boolean matched = false;
	ArrayList<Edge> nextNode = new ArrayList<Edge>();

	Node() {
		this.matched = false;
		this.incoming = -1;
	}
}