
class AVLNode<K, V> {

    public Integer key;

    public Integer value;

    public AVLNode<K, V> left;
    
    public AVLNode<K, V> right;

    public int height;
    
    public AVLNode(){
    	this.key = 0;
    	this.value = 0;
    	this.left = null;
    	this. right = null;
    }
    
    public AVLNode(Integer key, Integer value, AVLNode<K, V> left, AVLNode<K, V> right) {
      // bind to references
      this.key = key;
      this.value = value;
      this.left = left;
      this.right = right;

      height = 0;
    }

    public AVLNode(Integer key, Integer value) {
      // call three parameter constructor
      this(key, value, null, null);

      height = 0;
    }

  }