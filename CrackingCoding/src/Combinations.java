import java.util.Scanner;

public class Combinations {

	public static int count;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner input = new Scanner(System.in);
		int n = input.nextInt();
		int[] A = new int[n];
		while (n-- != 0)
			A[A.length - n - 1] = input.nextInt();
		int k = input.nextInt();
		if (k > A.length)
			System.out.println("K cant be greater than length");
		else {
			System.out.println("Comnbinations");
			combinations(A, 0, k, new int[k]);
		}
		input.close();
		System.out.println("\ncount is " + count);
	}

	public static void combinations(int[] A, int n, int k, int[] B) {
		count++;
		if (k != 0) {
			for (int i = n; i < A.length; i++) {
				if (k <= A.length - i) {
					B[B.length - k] = A[i];
					// Each combination is selecting one element out of the
					// (A.length-i)
					combinations(A, i + 1, k - 1, B);
				}
			}
		} else {
			for (int e : B)
				System.out.print(e + ",");
			System.out.println();
		}
	}

}
