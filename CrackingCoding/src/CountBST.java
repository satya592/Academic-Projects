import java.math.BigInteger;
import java.util.Scanner;

public class CountBST {

	static BigInteger fibN[];

	// Sum of (n+k)/k for all k (2 to n)
	static long countBST(int n) {
		long total = 1;
		for (int k = 2; k <= n; k++)
			total *= (n + k);
		for (int k = 2; k <= n; k++)
			total /= k;

		return total;
	}

	public static void main(String[] arg) {
		Scanner s = new Scanner(System.in);
		int n = s.nextInt();

		fibN = new BigInteger[1000];
		int input = 0, n2;
		int max = 1;
		fibN[0] = BigInteger.ONE;
		while ((n--) >= 1) {
			input = s.nextInt();
			System.out.println("Number of bsts by countbst " + countBST(input));

			n2 = input * 2;
			// System.out.println(fibN[1]);
			if (!((fibN[n2 - 1]) != null)) {
				for (int i = (max - 1); i < n2; i++) {
					fibN[i + 1] = fibN[i].multiply(BigInteger.valueOf(i + 1));
				}
				max = n2;
			}
			// 2n!/(n!(n+1)!)
			// System.out.println((fibN[n2 - 1]));
			// System.out.println((fibN[input]));
			// System.out.println((fibN[input - 1]));
			System.out.println((fibN[n2]).divide((fibN[input]
					.multiply(fibN[input + 1]))));
		}
		s.close();
	}
}
