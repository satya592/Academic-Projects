/**
 * MagicNumber is sum of elements of left is equal to sum of elements of right
 * It may have +ve and -ve elements
 * Sum of two +ve numbers may overflow the memory
 */

/**
 * @author satyamkotikalapudi
 *
 */
public class MagicNumber {

	public static int magicNumberIndex(int[] a) {
		int lsum = 0, index = -1, totalSum = 0;
		for (int i = 0; i < a.length; i++)
			totalSum ^= a[i];
		System.out.println("total" + totalSum);
		for (int i = 0; i < a.length; i++) {
			System.out.println("index:" + i + "(l:r)" + (lsum) + "="
					+ (totalSum ^ a[i]));
			if (lsum == (totalSum ^ a[i]))
				System.out.println("index is" + i); // i;
			lsum ^= a[i];
			totalSum ^= a[i];
		}

		return index;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = { 1, 2147483647, 2147483647, -2147483647, -2147483647, 0,
				2147483647, -2147483647, 1073741823, 1073741824, -2147483647 };
		int[] b = { -1, 3, 4, -5 };// , 1, -6, 2, 1 };
		int[] c = { -2, 3, -4, 5, 1, -6, 2 };
		// System.out.println(0 ^ -1);
		// System.out.println((-4) ^ 5 ^ 1 ^ (-6) ^ 2 ^ 1);
		// System.out.println(-4 ^ 5 ^ 1 ^ -6 ^ 2 ^ 1);
		int magicIndex = magicNumberIndex(b);
		magicIndex = magicNumberIndex(c);
		// magicNumberIndex(c);
		// System.out.println(Integer.MAX_VALUE);
		System.out.println("magicIndex:" + magicIndex);
		if (magicIndex != -1)
			System.out.println("magicNumber:" + (b[magicIndex]));
	}

}
