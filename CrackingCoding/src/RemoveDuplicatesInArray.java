import java.util.Scanner;

public class RemoveDuplicatesInArray {
	/*
	 * 12 1 2 3 3 3 4 4 10 13 15 15 17 //ouput 1,2,3,4,10,13,15,17,
	 * 1,2,3,4,10,13,15,17,-1,-1,-1,-1,
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner input = new Scanner(System.in);
		System.out.println("Enter x: ");
		int n = input.nextInt();
		int[] A = new int[n];
		while (n-- != 0) {
			A[A.length - n - 1] = input.nextInt();
		}
		int[] B = removeDuplicates(A);
		System.out.println("Length is " + B.length);
		for (int b : B)
			System.out.print(b + ",");

		System.out.println();
		removeDuplicates2(A);
		for (int a : A)
			System.out.print(a + ",");

		input.close();
	}

	// O(n) space
	private static int[] removeDuplicates(int[] A) {
		int count = 0;
		for (int i = 1; i < A.length; i++) {
			if (A[i] == A[i - 1]) {
				count++;
			}
		}
		// Create B whose size is A.lenth - duplicates
		int B[] = new int[A.length - count];
		B[0] = A[0];
		for (int i = 1, j = 1; i < A.length && j < B.length; i++) {
			if (A[i] != A[i - 1]) {
				B[j] = A[i];
				j++;
			}
		}
		return B;
	}

	// Constant space
	private static void removeDuplicates2(int[] A) {
		int j = 1;
		for (int i = 1; i < A.length; i++) {
			if (A[i] != A[i - 1]) {
				A[j] = A[i];
				j++;
			}
		}
		while (j < A.length)
			A[j++] = -1;
	}

}
