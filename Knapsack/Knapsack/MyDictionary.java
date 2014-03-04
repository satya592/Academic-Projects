
public interface MyDictionary<K, V> {

	/*
	 * insert a new entry with key k, value v. If a key with key k already
	 * exists, its value is replaced by v.
	 */
	public void Insert(Integer key, Integer value);

	/*
	 * return the value associated with key k. If there is no element with key
	 * k, it returns null (or 0).
	 */
	public Integer Find(Integer key);

	/* return (k,v) corresponding to the current smallest key */
	public Integer FindMin();

	/* return (k,v) corresponding to the current largest key */
	public Integer FindMax();

	/*
	 * remove element with key k. Returns value of deleted element (null or 0 if
	 * such a key does not exist).
	 */
	public Integer Remove(Integer key);

	/* remove all elements whose value is v. Returns number of elements deleted. */
	public int RemoveValue(Integer value);

	/* return the number of elements currently stored. */
	public int Size();

	/* boolean indicating whether the current store is empty. */
	public boolean IsEmpty();

}
