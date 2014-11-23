public class CommonPoint {

	public static void main(String[] args) {
		LinkedList lList1 = new LinkedList();
		LinkedList lList2 = new LinkedList();
		Node n1 = new Node("1");
		Node n2 = new Node("2");
		Node n3 = new Node("3");
		Node n4 = new Node("4");
		Node n5 = new Node("5");
		Node n6 = new Node("6");
		Node n7 = new Node("7");
		Node n8 = new Node("8");
		Node n9 = new Node("9");
		Node n10 = new Node("10");
		Node n11 = new Node("10.1");

		// add elements to LinkedList
		lList1.addNode(n1);
		lList1.addNode(n2);
		lList1.addNode(n3);
		lList1.addNode(n4);
		lList1.addNode(n5);
		lList1.addNode(n6);
		lList1.addNode(n7);
		lList1.addNode(n8);
		lList1.addNode(n9);
		lList1.addNode(n10);
		lList1.addNode(n11);

		lList2.add("11");
		lList2.add("22");
		lList2.add("23");
		lList2.add("24");
		// lList2.add("3");
		lList2.addNode(n5);

		System.out.println("lList.size() - print linkedlist size: "
				+ lList1.size());
		System.out.println("lList - print linkedlist: " + lList1);
		System.out.println("lList.size() - print linkedlist size: "
				+ lList2.size());
		System.out.println("lList - print linkedlist: " + lList2);

		lList1.CommonPoint(lList2);
	}

}
