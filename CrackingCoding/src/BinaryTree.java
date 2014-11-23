import java.util.LinkedList;
import java.util.Queue;

public class BinaryTree {
	Node root;

	BinaryTree() {
		root = null;
	}

	BinaryTree(Node root) {
		this.root = root;
	}

	BinaryTree(Object[] elements) {
		Node current = null;
		Queue<Node> Q = new LinkedList<>();
		boolean first = true;
		for (Object obj : elements) {
			if (first && root == null) {
				root = new Node(obj);
				Q.add(root);
				first = false;
				continue;
			}

			if (!Q.isEmpty()) {
				current = Q.peek();
				if (current.left == null) {
					current.left = new Node(obj);
					Q.add(current.left);
				} else if (current.right == null) {
					current.right = new Node(obj);
					Q.add(current.right);
				}
				if (current.left != null && current.right != null)
					Q.poll();
			} else {
				System.out.println("Q is empty!");
			}
		}

	}
}
