import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class TreeBuilder {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String inorder = scanner.nextLine();
		String postorder = scanner.nextLine();
		String[] in = inorder.split(",");
		String[] post = postorder.split(",");
		Node n = printTree(in, post, 0, in.length - 1, 0, in.length - 1);
		n.print();
	}

	private static Node printTree(String[] in, String[] post, int s, int l,
			int o, int e) {
		if (e < 0 || l < 0)
			return null;
		// System.out.println("(" + s + "," + l + ")" + "(" + o + "," + e +
		// ")");
		int b = findIndex(in, post[e], s, l);
		Node n = new Node();
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException ex) {
		// TODO Auto-generated catch block
		// ex.printStackTrace();
		// }
		if (b != -1) {
			// System.out.println(in[b]);
			n.data = in[b];
			if (s <= b - 1)
				n.left = printTree(in, post, s, b - 1, o, o + (b - s) - 1);
			if (b + 1 <= l)
				n.right = printTree(in, post, b + 1, l, o + (b - s), e - 1);
		} else
			System.out.println("Invalid");
		return n;
	}

	static int findIndex(String[] in, String val, int s, int len) {

		for (int i = s; i <= len; i++) {
			// System.out.println(in[i] + "==" + val);
			if (in[i].equals(val))
				return i;
		}
		return -1;

	}

}

class Node {
	Node left;
	Node right;
	String data;

	void print() {
		Queue<Node> level = new LinkedList<>();
		level.add(this);
		boolean first = true;
		while (!level.isEmpty()) {
			Node node = level.poll();
			if (!first)
				System.out.print("," + node.data);
			else {
				System.out.print(node.data);
				first = false;
			}
			if (node.left != null)
				level.add(node.left);
			if (node.right != null)
				level.add(node.right);
		}

	}
}
