/*
 * 1) Find the number of "visible" nodes in a binary tree. A node is a "visible" node 
 * if the path from root to that node does not encounter any node of value higher than that node.
 */
public class VisibleNodes {

	public static int visibleNodes(Node root, int max) {
		if (root == null)
			return 0;
		else if ((int) root.data >= max)
			return 1 + visibleNodes(root.left, (int) root.data)
					+ visibleNodes(root.right, (int) root.data);
		else
			return visibleNodes(root.left, max) + visibleNodes(root.right, max);
	}

	public static void main(String[] args) {
		Integer[] in = { 1, 2, 3, 4, 5, 6, 7, 8 };
		BinaryTree bt = new BinaryTree(in);
		// System.out.println(TreeTraversal.BFS(bt.root, new Node(8)));
		int left = visibleNodes(bt.root.left, (int) bt.root.data);
		int right = visibleNodes(bt.root.right, (int) bt.root.data);
		System.out.println("Total visible nodes are:" + (left + right));
	}

}
