import java.util.Scanner;

public class RearrangePosNeginArray {
	// 6
	// -5 6 3 -2 1 -4
	// 8
	// 1 2 3 4 -1 -2 -3 -4
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner input = new Scanner(System.in);
		int size = input.nextInt();
		int i = size;
		int A[] = new int[size];
		while (i-- != 0) {
			A[size - 1 - i] = input.nextInt();
		}
		// rearrangePosNeg(A);
		rearrange2PosNeg(A, 0, A.length - 1);
		System.out.print("Result:");
		print(A);
		input.close();
	}

	// O(nlgn) time O(lgn) space
	public static void rearrange2PosNeg(int A[], int i, int j) {
		if (i == j)
			return;
		else {
			int mid = (i + j) / 2;
			rearrange2PosNeg(A, i, mid);
			rearrange2PosNeg(A, mid + 1, j);
			int si = i, sj = mid + 1;
			while (si < sj && A[si] < 0)
				si++;
			if (si > sj)
				si--;
			while (sj <= j && A[sj] < 0)
				sj++;
			// if (sj > j)
			sj--;
			reverseRange(A, si, mid);
			printRange(A, si, mid);
			reverseRange(A, mid + 1, sj);
			printRange(A, mid + 1, sj);
			reverseRange(A, si, sj);
			printRange(A, i, j);
		}
	}

	// O(n*n) time O(1) space
	public static void rearrangePosNeg(int A[]) {
		int i = 0, j = 0, size = A.length;
		while (i < size && j < size) {
			System.out.print("A:");
			print(A);
			while (i < size && A[i] < 0)
				// negatives
				i++;
			j = i;
			while (j < size && A[j] >= 0)
				// positivies
				j++;
			if (i < size && j < size) {
				reverseRange(A, i, j - 1);
				reverseRange(A, i, j);
			}
		}
	}

	public static void print(int A[]) {
		for (int i : A) {
			System.out.print(i);
			System.out.print(",");
		}
		System.out.println();
	}

	public static void printRange(int A[], int i, int j) {
		for (int k = 0; k < A.length; k++) {
			if (k == i)
				System.out.print("{");
			System.out.print(A[k]);
			if (k == j)
				System.out.print("}");

			System.out.print(",");
		}
		System.out.println();
	}

	public static void reverseRange(int[] A, int i, int j) {
		if (A == null || i >= A.length || j >= A.length || (i >= j))
			return;
		else {
			int tmp;
			for (; i < j; i++, j--) {
				tmp = A[i];
				A[i] = A[j];
				A[j] = tmp;
			}
		}
	}

}
