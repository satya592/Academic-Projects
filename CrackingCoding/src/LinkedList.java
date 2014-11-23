public class LinkedList {
	// reference to the head node.
	private Node head;
	private int listCount;

	// LinkedList constructor
	public LinkedList() {
		// this is an empty list, so the reference to the head node
		// is set to a new node with no data
		head = null;
		listCount = 0;
	}

	public Node CommonPoint(LinkedList l2) {
		Node l1ptr = this.head;
		Node l2ptr = l2.head;
		Node prev1 = null, prev2 = null;
		Node next1 = null, next2 = null;
		int x = 0, y = 0;
		if (l2.head == this.head) {
			return this.head;
		}

		while (prev1 != l2.head && prev2 != this.head) {
			if (l1ptr != null) {
				next1 = l1ptr.next;
				l1ptr.next = prev1;
				prev1 = l1ptr;
				l1ptr = next1;
				x++;
			} else {
				l1ptr = prev1;
			}
			if (l2ptr != null) {
				// System.out.println(this.print(l2ptr) + this.print(prev2));
				// System.out.print(l2ptr.data + ",");
				next2 = l2ptr.next;
				l2ptr.next = prev2;
				prev2 = l2ptr;
				l2ptr = next2;
				// System.out.print("=>" + prev2.data + ",");
				// if (next2 != null)
				// System.out.println(next2.data);

				y++;
			} else {
				// l2ptr = prev2;
			}
			// System.out.println("l2 after reversal=" + this.print(l2ptr));
		}
		if (l1ptr == null)
			l1ptr = prev1;
		if (l2ptr == null)
			l2ptr = prev2;

		System.out.println("l1=" + this.print(l1ptr) + "l2="
				+ this.print(l2ptr) + this.print(prev2));
		System.out.println("x=" + x + "y=" + y);
		int z = x;

		if (l2ptr == this.head) // x<y
		{
			System.out.println("l2 reached to the head of l1");
			while (z != 0) {
				System.out.print(l2ptr + ",");
				next2 = l2ptr.next;
				l2ptr.next = prev2;
				prev2 = l2ptr;
				l2ptr = next2;
				z--;
			}

			System.out.println("Z " + z);
			l2ptr = l2.head;
			while (prev1 != l2ptr) {
				prev1 = prev1.next;
				l2ptr = l2ptr.next;
			}
			System.out.println("Intersection point is " + prev1.data);

		} else if (l1ptr == l2.head) {
			prev1 = null;
			System.out.println("l1 reached to the head of l2");
			while (z != 0) {
				System.out.print(l1ptr + ",");
				next1 = l1ptr.next;
				l1ptr.next = prev1;
				prev1 = l1ptr;
				l1ptr = next1;
				z--;
			}

			System.out.println("Z " + z);
			System.out.println("l2=" + this.print(l2ptr) + this.print(prev2)
					+ "l1=" + this);
			l1ptr = this.head;
			while (prev2 != l1ptr) {
				prev2 = prev2.next;
				l1ptr = l1ptr.next;
			}
			System.out.println("Intersection point is " + prev2.data);
		}

		return null;
	}

	public void add(Object data)
	// appends the specified element to the end of this list.
	{
		Node crunchifyTemp = new Node(data);
		Node crunchifyCurrent = head;
		// starting at the head node, crawl to the end of the list
		if (crunchifyCurrent == null) {
			head = crunchifyTemp;
		} else {
			while (crunchifyCurrent.getNext() != null) {
				crunchifyCurrent = crunchifyCurrent.getNext();
			}
			// the last node's "next" reference set to our new node
			crunchifyCurrent.setNext(crunchifyTemp);
		}
		listCount++;// increment the number of elements variable
	}

	public void addNode(Node node)
	// appends the specified element to the end of this list.
	{
		// Node crunchifyTemp = new Node(data);
		Node crunchifyCurrent = head;
		// starting at the head node, crawl to the end of the list
		if (crunchifyCurrent == null) {
			head = node;
		} else {
			while (crunchifyCurrent.getNext() != null) {
				crunchifyCurrent = crunchifyCurrent.getNext();
			}
			// the last node's "next" reference set to our new node
			crunchifyCurrent.setNext(node);
		}
		listCount++;// increment the number of elements variable
	}

	public void add(Object data, int index)
	// inserts the specified element at the specified position in this list
	{
		Node crunchifyTemp = new Node(data);
		Node crunchifyCurrent = head;
		// crawl to the requested index or the last element in the list,
		// whichever comes first
		for (int i = 1; i < index && crunchifyCurrent.getNext() != null; i++) {
			crunchifyCurrent = crunchifyCurrent.getNext();
		}
		// set the new node's next-node reference to this node's next-node
		// reference
		crunchifyTemp.setNext(crunchifyCurrent.getNext());
		// now set this node's next-node reference to the new node
		crunchifyCurrent.setNext(crunchifyTemp);
		listCount++;// increment the number of elements variable
	}

	public Object get(int index)
	// returns the element at the specified position in this list.
	{
		// index must be 1 or higher
		if (index <= 0)
			return null;

		Node crunchifyCurrent = head.getNext();
		for (int i = 1; i < index; i++) {
			if (crunchifyCurrent.getNext() == null)
				return null;

			crunchifyCurrent = crunchifyCurrent.getNext();
		}
		return crunchifyCurrent.getData();
	}

	public boolean remove(int index)
	// removes the element at the specified position in this list.
	{
		// if the index is out of range, exit
		if (index < 1 || index > size())
			return false;

		Node crunchifyCurrent = head;
		for (int i = 1; i < index; i++) {
			if (crunchifyCurrent.getNext() == null)
				return false;

			crunchifyCurrent = crunchifyCurrent.getNext();
		}
		crunchifyCurrent.setNext(crunchifyCurrent.getNext().getNext());
		listCount--; // decrement the number of elements variable
		return true;
	}

	public int size()
	// returns the number of elements in this list.
	{
		return listCount;
	}

	public String toString() {
		Node crunchifyCurrent = head;
		String output = "";
		while (crunchifyCurrent != null) {
			output += "[" + crunchifyCurrent.getData().toString() + "]";
			crunchifyCurrent = crunchifyCurrent.getNext();
		}
		return output;
	}

	public String print(Node start) {
		Node crunchifyCurrent = start;
		String output = "";
		while (crunchifyCurrent != null) {
			output += "[" + crunchifyCurrent.getData().toString() + "]";
			crunchifyCurrent = crunchifyCurrent.getNext();
		}
		return output;
	}

}

class Node {
	// reference to the next node in the chain,
	// or null if there isn't one.
	Node next;
	Node left;
	Node right;

	// data carried by this node.
	// could be of any type you need.
	Object data;

	// Node constructor
	public Node(Object dataValue) {
		next = null;
		left = null;
		right = null;
		data = dataValue;
	}

	// another Node constructor if we want to
	// specify the node to point to.
	public Node(Object dataValue, Node nextValue) {
		next = nextValue;
		data = dataValue;
	}

	public String toString() {
		return data.toString();
	}

	// these methods should be self-explanatory
	public Object getData() {
		return data;
	}

	public void setData(Object dataValue) {
		data = dataValue;
	}

	public Node getNext() {
		return next;
	}

	public void setNext(Node nextValue) {
		next = nextValue;
	}
}