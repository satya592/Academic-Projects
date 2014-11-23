//given 2 strings, haystack and needle. Write a function to find the occurence of needle inside haystack
public class SubStr {

	static int findStr(String haystack, String needle) {

		if (haystack == null || needle == null)
			return -1;
		if (needle.equals(""))
			return 0;
		if (haystack.equals(""))
			return -1;

		char[] pool = haystack.toCharArray();
		char[] query = needle.toCharArray();

		if (pool.length < query.length)
			return -1;

		int i, j, index = -1;
		// loop to find the occurence
		for (int k = 0; k < pool.length; k++) {
			for (i = k, j = 0; i < pool.length && j < query.length; i++) {

				if (pool[i] == query[j]) {
					System.out.println(pool[i] + " == " + query[j]);
					if (j == 0)
						index = i;// first occurence
					j++;
				}
				// reset the j position to start of the query
				else
					j = 0;

			}// loop ends

			if (j == query.length)// found the match
			{
				return index;
			}
		}
		return -1;
	}

	// TimeComplex: O(length(haystack))
	//
	// findStr("abc", 'b') => 1
	// findStr("abc", "bd")
	// Worstcase: findStr("abcd", "s")
	// Bestcase: findStr("abcd","a"): O(length(needle))
	// Bestcase: findStr("abcd","abcdef") - O(1)

	public static void main(String[] args) {
		// System.out.println(findStr("abc", "b"));
		// System.out.println(findStr("abc", "bcd"));
		// System.out.println(findStr("abc", "s"));
		// System.out.println(findStr("abc", "abc"));
		// System.out.println(findStr("abc", "abcd"));
		System.out.println(findStr("bababbabe", "babe"));
	}

}
