import java.math.BigInteger;
import java.util.Scanner;

public class TwoOperations {

	public static void main(String args[]) throws Exception {
		/* Enter your code here. Read input from STDIN. Print output to STDOUT */
		Scanner s = new Scanner(System.in);
		int t = s.nextInt();
		int count;
		BigInteger n;
		BigInteger two = new BigInteger("2");
		while ((t--) >= 1) {
			n = s.nextBigInteger();
			// n= new BigInteger();
			count = 0;
			while (n.compareTo(BigInteger.ZERO) > 0) {
				if ((n.divide(two).compareTo(BigInteger.ZERO)) > 0) {
					count++;
					if (n.remainder(two).equals(BigInteger.ONE))
						count++;
				} else {
					count++;
				}
				n = n.divide(two);// /= 2;
			}
			System.out.println(count);
		}
		s.close();
	}
}
