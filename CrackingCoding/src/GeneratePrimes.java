import java.util.ArrayList;
import java.util.Scanner;

public class GeneratePrimes {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		int n = input.nextInt();
		ArrayList<Integer> primes = new ArrayList<Integer>();
		primes.add(2);

		int i = 3;
		while (i < n) {
			if (isPrime(primes, i))
				primes.add(i);
			i++;
		}
		System.out.println("No of primes " + primes.size());
		System.out.println(primes);
		input.close();
	}

	private static boolean isPrime(ArrayList<Integer> primes, int n) {
		int sqrt = (int) Math.sqrt(n);
		int count = 0;
		for (Integer i : primes) {
			count++;
			if (i > sqrt)
				break;
			if (n % i == 0)
				return false;
		}
		// System.out.println("Counts:" + count);
		return true;
	}

}
