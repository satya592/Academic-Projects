/**
 * Q2- Trinary Tree of integers
 * 
 * @author satyam kotikalapudi
 *
 */
public class Q2_Trinary_Tree {

	private class Node {
		int value;
		int count;
		Node left;
		Node right;

		Node(int val) {
			value = val;
			count = 1;
			left = null;
			right = null;
		}

		/** Binary search **/
		void insertNode(int e) {
			if (this.value == e)
				this.count++;
			else if (this.value < e) {
				if (this.right != null) {
					this.right.insertNode(e);
				} else {
					this.right = new Node(e);
				}
			} else if (this.value > e) {
				if (this.left != null) {
					this.left.insertNode(e);
				} else {
					this.left = new Node(e);
				}
			}
		}

		/** Find minimum node in tree **/
		Node min(Node n) {
			while (n.left != null) {
				n = n.left;
			}
			return n;
		}

		/** Find delete node **/
		boolean deleteNode(int e) {
			Node found = null;

			if ((this.left != null && this.left.value == e)
					|| (this.right != null && this.right.value == e)) {
				if (this.left != null && this.left.value == e) {
					found = this.left;
				} else if (this.right != null && this.right.value == e) {
					found = this.right;
				}

				if (found.count > 1)
					found.count--;
				else {
					// leaf node
					if (found.left == null && found.right == null) {
						if (found == this.left)
							this.left = null;
						if (found == this.right)
							this.right = null;
					}
					// left is not null and right is null
					else if (found.left != null && found.right == null) {
						if (found == this.left)
							this.left = found.left;
						if (found == this.right)
							this.right = found.left;
					}
					// right is not null and left is null
					else if (found.left == null && found.right != null) {
						if (found == this.left)
							this.left = found.right;
						if (found == this.right)
							this.right = found.right;
					}
					// right is not null and left is not null
					else {
						Node x = min(found.right);
						x.left = found.left;
						if (found == this.left)
							this.left = found.right;
						if (found == this.right)
							this.right = found.right;
					}
				}
				return true;
			} else {
				return ((this.left != null && this.left.deleteNode(e)) || (this.right != null && this.right
						.deleteNode(e)));
			}
		}

		void display() {
			if (this.left != null)
				this.left.display();
			for (int i = 0; i < this.count; i++)
				System.out.print(this.value + " ");
			if (this.right != null)
				this.right.display();
		}

	}

	Node tree;

	/** Insert value in the Trinary tree **/
	void insert(int e) {
		if (tree == null)
			tree = new Node(e);
		else
			tree.insertNode(e);
		System.out.println("Inserted:" + e);
	}

	/** Delete value in the Trinary tree **/
	boolean delete(int e) {

		if (tree != null && tree.value == e) {
			if (tree.count > 1)
				tree.count--;
			else if (tree.left == null && tree.right == null)
				tree = null;
			else if (tree.left != null && tree.right == null)
				tree = tree.left;
			else if (tree.left == null && tree.right != null)
				tree = tree.right;
			else {
				Node x = tree.min(tree.right);
				x.left = tree.left;
				tree = tree.right;
			}
			System.out.println("Deleted:" + e);
			return true;
		} else if (tree != null) {
			if (tree.deleteNode(e)) {
				System.out.println("Deleted:" + e);
				return true;
			} else {
				System.out.println("Not found:" + e);
				return false;
			}
		} else {
			System.out.println("Tree is empty");
			return false;
		}
	}

	/** Display Trinary tree **/
	void display() {
		if (tree != null) {
			System.out.print("Tree:");
			tree.display();
		} else {
			System.out.print("Tree is empty");
		}
		System.out.println();
	}

	public static void main(String[] args) {
		// 5, 4, 9, 5, 7, 2, 2
		Q2_Trinary_Tree tt = new Q2_Trinary_Tree();

		tt.delete(5);
		tt.display();

		tt.insert(5);
		tt.insert(5);
		tt.display();
		tt.delete(5);
		tt.display();
		tt.delete(5);
		tt.display();

		tt.insert(5);
		tt.insert(4);
		tt.insert(9);
		tt.display();
		tt.delete(9);
		tt.display();
		tt.insert(9);
		tt.insert(5);
		tt.insert(7);
		tt.insert(2);
		tt.insert(2);
		tt.display();
		tt.delete(5);
		tt.display();
		tt.delete(5);
		tt.display();

		tt.delete(9);
		tt.display();

		tt.delete(2);
		tt.display();
		tt.delete(2);
		tt.display();

		tt.delete(4);
		tt.display();
		tt.delete(7);
		tt.display();
	}
}
