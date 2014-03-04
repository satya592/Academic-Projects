import java.util.LinkedList;
import java.util.Queue;

public class AVLTree<K, V> implements MyDictionary<Integer, Integer>{

	public AVLNode<K, V> root;
	private int numKeys; 
	private int delCount = 0;
	private int delKeys = 0;

	/**
	 * Constructs AVLTree object.
	 */
	public AVLTree() {
		root = null;

		numKeys = 0;
	}

	public void Insert(Integer key, Integer value) {

		// input validation
		//if (key == null || value == null) {
		//	throw new IllegalArgumentException();
		//}
		numKeys++;
		root = insert(root, key, value);
		
	}

	private AVLNode<K, V> insert(AVLNode<K, V> r, Integer key, Integer value) {
		if (r == null) {
			r = new AVLNode<K,V>(key, value);
			//numKeys++;
		} else if (key.compareTo(r.key) < 0) {
			r.left = insert(r.left, key, value);
			if ((height(r.left) - height(r.right) == 2)
					&& (key.compareTo( r.left.key) < 0)) {
				r = rotateWithLeftChild(r);
			} else if ((height(r.left) - height(r.right) < 2)) {
				;
			} else {
				r = doubleWithLeftChild(r);
			}
		} else if (key.compareTo(r.key) > 0) {
			r.right = insert(r.right, key, value);
			if ((height(r.right) - height(r.left) == 2)
					&& (key.compareTo(r.right.key) > 0)) {
				r = rotateWithRightChild(r);
			} else if ((height(r.right) - height(r.left) < 2)) {
				;
			} else {
				r = doubleWithRightChild(r);
			}
		} else {
			r.value = value;
		}
		r.height = max(height(r.left), height(r.right)) + 1;
		
		return r;
	}

	/*
	 * return the value associated with key k. If there is no element with key
	 * k, it returns null (or 0).
	 */
	public Integer Find(Integer key) {
		// return 0 if there is no value;
		Integer r = valueAt(get(root, key));
		if (r != 0)
			return r;
		else
			return 0;
	}

	private AVLNode<K, V> get(AVLNode<K, V> r, Integer key) {
		if (r == null) {
			return null;
		}

		int compare = key.compareTo(r.key);
		if (compare == 0) {
			return r;
		} else if (compare < 0) {
			return get(r.left, key);
		} else { // compare > 0
			return get(r.right, key);
		}
	}

	private Integer valueAt(AVLNode<K, V> t) {
		return (t == null ? 0 : t.value);
	}

	/* return (k,v) corresponding to the current smallest key */
	public Integer FindMin() {
		AVLNode<K, V> min = FindMin(root);
		return min.value;
	}

	/* return (k,v) corresponding to the current largest key */
	public Integer FindMax() {
		AVLNode<K, V> max = FindMax(root);
		return max.value;
	}

	/*
	 * remove element with key k. Returns value of deleted element (null or 0 if
	 * such a key does not exist).
	 */
	public Integer Remove(Integer key) {
		// BigInteger zero = 0;
		delKeys++;
		AVLNode<K, V> r = remove(root, key);
		if (r != null){
			
			return  r.value;
			}
		else
			return 0;
	}

	private AVLNode<K, V> remove(AVLNode<K, V> r, Integer key) {
		AVLNode<K, V> node = new AVLNode<K,V>();
		if (r == null) {
			return null;
		}
		int compare = key.compareTo(r.key);
		if (compare < 0) {
			node  = remove(get(r.left, key), key);

		} else if (compare > 0) {
			node = remove(get(r.right, key), key);
			
		}
		// Else, we found it! Remove n.
		else {

			// 0 children
			if (r.left == null && r.right == null) {
				node = r;
				r = null;
				//delKeys--;
			} else if (r.right == null) {
				r = rotateWithLeftChild(r);
				node = r.right;
				r.right = null;
				//delKeys--;
			} else if (r.left == null) {
				r = rotateWithRightChild(r);
				node = r.left;
				r.left = null;
				//delKeys--;
			}

			// 2 children - deleting may have unbalanced tree.
			else {
				//BigInteger finalKey = r.key;
				//BigInteger finalValue = r.value;
				node.key = r.key; //finalKey;
				node.value = r.value; //finalValue;
				AVLNode<K, V> smallestNode = FindMin(r.right);
				r.key = smallestNode.key;
				r.value = smallestNode.value;
//				AVLNode<K, V> removedNode = remove(r.right, smallestNode.key);  
				// balance
				//numKeys++;
				if ((height(r.right) - height(r.left) == 2)
						&& (key.compareTo(r.right.key) > 0)) {
					r = rotateWithRightChild(r);
				} else if ((height(r.right) - height(r.left) < 2)) {
					;
				} else {
					r = doubleWithRightChild(r);
				}

				if ((height(r.left) - height(r.right) == 2)
						&& (key.compareTo(r.left.key) < 0)) {
					r = rotateWithLeftChild(r);
				} else if ((height(r.left) - height(r.right) < 2)) {
					;
				} else {
					r = doubleWithLeftChild(r);
				}

			}
			//delKeys++;
		}
		
		return node;

	}

	private AVLNode<K, V> FindMin(AVLNode<K, V> r) {
		if (r == null)
			return null;
		else if (r.left == null)
			return r;
		return FindMin(r.left);
	}

	private AVLNode<K, V> FindMax(AVLNode<K, V> r) {
		if (r == null)
			return null;
		else
			while (r.right != null)
				r = r.right;

		return r;

	}

	/* remove all elements whose value is v. Returns number of elements deleted. */
	public int RemoveValue(Integer value) {
		delCount = 0;
		Queue<AVLNode<K, V>> li = new LinkedList<AVLNode<K, V>>();
		AVLNode<K, V> node = null;
		AVLNode<K, V> r = null;
		li = preOrder(root);
		while (!li.isEmpty()) {
			node = li.poll();
			if (node.value.equals(value)) {
				r = remove(root, node.key);
				
				if (r != null)
					delCount++;
				} else
				;
		}
		return delCount;
	}

	Queue<AVLNode<K, V>> que = new LinkedList<AVLNode<K, V>>();

	public Queue<AVLNode<K, V>> preOrder(AVLNode<K, V> node) {
		if (node != null) {
			que.offer(node);
			// do nothing
			preOrder(node.left);
			preOrder(node.right);
		}

		return que;

	}

	/* return the number of elements currently stored. */
	public int Size() {
		return (numKeys - delKeys - delCount);
	}

	/* boolean indicating whether the current store is empty. */
	public boolean IsEmpty() {
		return root == null;
	}

	private int height(AVLNode<K, V> t) {
		return t == null ? -1 : t.height;
	}

	// This helper method returns the greater of two integers.

	private int max(int n1, int n2) {
		if (n1 > n2) {
			return n1;
		} else { // left <= right
			return n2;
		}
	}

	/**
	 * Rotate binary tree node with left child. For AVL trees, this is a single
	 * rotation for case 1. Update heights, then return new root.
	 */
	private AVLNode<K, V> rotateWithLeftChild(AVLNode<K, V> root2) {
		AVLNode<K, V> root1 = root2.left;
		root2.left = root1.right;
		root1.right = root2;

		root2.height = max(height(root2.left), height(root2.right)) + 1;
		root1.height = max(height(root1.left), root2.height) + 1;

		return root1;
	}

	/**
	 * Rotate binary tree node with right child. For AVL trees, this is a single
	 * rotation for case 4. Update heights, then return new root.
	 */
	private AVLNode<K, V> rotateWithRightChild(AVLNode<K, V> root1) {
		AVLNode<K, V> root2 = root1.right;
		root1.right = root2.left;
		root2.left = root1;

		root1.height = max(height(root1.left), height(root1.right)) + 1;
		root2.height = max(height(root2.right), root1.height) + 1;

		return root2;
	}

	/**
	 * Double rotate binary tree node: first left child with its right child;
	 * then node k3 with new left child. For AVL trees, this is a double
	 * rotation for case 2. Update heights, then return new root.
	 */
	private AVLNode<K, V> doubleWithLeftChild(AVLNode<K, V> r) {
		r.left = rotateWithRightChild(r.left);

		return rotateWithLeftChild(r);
	}

	/**
	 * Double rotate binary tree node: first right child with its left child;
	 * then node r1 with new right child. For AVL trees, this is a double
	 * rotation for case 3. Update heights, then return new root.
	 */
	private AVLNode<K, V> doubleWithRightChild(AVLNode<K, V> r) {
		r.right = rotateWithLeftChild(r.right);

		return rotateWithRightChild(r);
	}

}