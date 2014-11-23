//SXR142730

import java.util.Scanner;

public class SXR_142730_Merge_Sort3 {
	public static int MergeSort(int[] A, int[] B, int p, int r, int counter) {
		counter++;
		if (p < r) {
			int q = (p + r) / 2;
			int counter1 = MergeSort(A, B, p, q, counter);
			MergeSort(A, B, q + 1, r, counter);
			if ((counter1 % 2) != 0) {
				Merge(B, A, p, q, r);
			} else {
				Merge(A, B, p, q, r);
			}
		}
		return counter;
	}

	public static void Merge(int[] src, int[] dest, int p, int q, int r) {
		int ls = q - p + 1;
		int rs = r - q;
		int i = 0;
		int j = 0;
		for (int k = p; k <= r; k++) {
			if ((j >= rs) || (i < ls) && (src[p + i] <= src[q + 1 + j])) {
				dest[k] = src[p + i++];
			} else {
				dest[k] = src[q + 1 + j++];
			}
		}
		return;
	}

	public static void main(String[] args) {

		System.out.println("Enter the value of n");
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		sc.close();
		int[] A = new int[n];
		int[] B = new int[n];
		for (int i = 0; i < n; i++) {
			A[i] = n - i;
			B[i] = n - i;
		}
		int h0 = MergeSort(A, B, 0, n - 1, 0);
		if (h0 % 2 != 0) {
			for (int i = 0; i < n; i++) {
				A[i] = B[i];
			}
		}

		for (int j = 0; j < A.length - 1; j++) {
			if (A[j] > A[j + 1]) {
				System.out.println("Sorting failed");
				return;
			}
		}
		System.out.println("Sorting Suceeded");
	}
}
