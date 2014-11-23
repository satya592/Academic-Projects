import java.util.LinkedList;
import java.util.Queue;

public class TreeTraversal {
	public static Node BFS(Node root, Node element) {
		Queue<Node> Q = new LinkedList<Node>();
		Q.add(root);
		Node current = null;
		while (!Q.isEmpty()) {
			current = Q.poll();
			if (current.data.equals(element.data))
				return current;
			else {
				if (current.left != null)
					Q.add(current.left);
				if (current.right != null)
					Q.add(current.right);
			}
		}
		return null;
	}

	public static Node DFS(Node root, Node element) {
		if (root == null)
			return null;
		else if (root.data.equals(element.data))
			return root;
		else {
			Node current = DFS(root.left, element);
			if (current == null)
				current = DFS(root.right, element);
			return current;
		}
	}
}