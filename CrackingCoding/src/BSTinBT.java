import java.util.LinkedList;

/**
 * 
 * @author Shagun Jhaver
 * @email-id sxj124330@utdallas.edu
 * 
 *           Given a binary tree, find the largest induced subtree that is a BST
 *           (Not all descendants necessarily taken)
 *
 */
public class BSTinBT {

	/**
	 * Prints the size of the largest induced subtree that is a BST
	 * 
	 * @param tree
	 */
	public static void largestInducedBST(BinaryTree tree) {
		findLargest(tree.root);
		System.out.println("Size of largest induced subtree that's a BST is "
				+ maxSize);
	}

	/**
	 * Returns a dequeue of the largest subtree with root = Node that is a BST
	 * 
	 * @param node
	 * @return
	 */
	private static LinkedList<Node> findLargest(Node node) {
		if (node == null) {
			return new LinkedList<Node>();
		} else if (node.left == null && node.right == null) {
			// Leaf node
			LinkedList<Node> queue = new LinkedList<Node>();
			queue.addLast(node);
			if (maxSize < 1) {
				maxSize = 1;
			}
			return queue;
		} else {
			// Dequeue for the left subtree
			LinkedList<Node> leftLinkedList = findLargest(node.left);
			// Dequeue for the right subtree
			LinkedList<Node> rightLinkedList = findLargest(node.right);

			// Process the left subtree dequeue
			while (!(leftLinkedList.isEmpty())) {// &&
													// leftLinkedList.getLast().data
													// > node.data) {
				if (leftLinkedList.getLast() == node.left) {
					leftLinkedList.clear();
					break;
				} else {
					Node corruptNode = leftLinkedList.getLast();
					deleteNodes(leftLinkedList, corruptNode);
				}

			}

			// Process the right subtree dequeue
			while (!(rightLinkedList.isEmpty())) {// &&
													// rightLinkedList.getFirst().data
													// < node.data) {
				if (rightLinkedList.getFirst() == node.right) {
					rightLinkedList.clear();
					break;
				} else {
					Node corruptNode = rightLinkedList.getFirst();
					deleteNodes(rightLinkedList, corruptNode);
				}

			}

			// Size for induced subtree at current node
			int inducedSize = leftLinkedList.size() + rightLinkedList.size()
					+ 1;
			if (inducedSize > maxSize) {
				maxSize = inducedSize;
			}

			/* Prepare the queue to be returned */
			leftLinkedList.addLast(node);
			while (!rightLinkedList.isEmpty()) {
				leftLinkedList.addLast(rightLinkedList.removeFirst());
			}
			return leftLinkedList;
		}

	}

	/**
	 * Deletes corruptNode and all its children present in the list
	 * 
	 * @param list
	 * @param corruptNode
	 */
	private static void deleteNodes(LinkedList<Node> list, Node corruptNode) {
		if (corruptNode != null && list.contains(corruptNode)) {
			deleteNodes(list, corruptNode.left);
			deleteNodes(list, corruptNode.right);
			list.remove(corruptNode);
		}
	}

	/**
	 * Testing on an example
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/* Tree of Fig. 12.2, Cormen Third Ed. */
		Node root = new Node(12);
		Node a = new Node(8);
		Node k1 = new Node(2);
		a.left = k1;
		a.right = new Node(4);
		Node b = new Node(13);
		Node k2 = new Node(9);
		b.left = k2;
		Node c = new Node(7);
		c.right = b;
		Node rootLeft = new Node(6);
		rootLeft.left = a;
		rootLeft.right = c;

		Node rootRight = new Node(18);
		rootRight.left = new Node(17);
		rootRight.right = new Node(20);

		root.left = rootLeft;
		root.right = rootRight;

		BinaryTree tree = new BinaryTree(root);
		largestInducedBST(tree);
	}

	/* Class Variable */
	static int maxSize = 0;

}