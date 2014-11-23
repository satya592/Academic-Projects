//SXR142730

import java.util.Scanner;

public class SXR_142730_Merge_Sort2 {
	public static void MergeSort(int[] A, int[] B, int p, int r) {
		if (p < r) {
			if (r - p > 11) {
				int q = (p + r) / 2;
				MergeSort(A, B, p, q);
				MergeSort(A, B, q + 1, r);
				Merge(A, B, p, q, r);
			} else { // Insertion sort
				for (int i = p, j = i; i < r; j = ++i) {
					int ai = A[i + 1];
					while (ai < A[j]) {
						A[j + 1] = A[j];
						if (j-- == p) {
							break;
						}
					}
					A[j + 1] = ai;
				}
			}
		}
	}

	public static void Merge(int[] A, int[] B, int p, int q, int r) {
		int ls = q - p + 1;
		int rs = r - q;
		for (int i = p; i <= q; i++) {
			B[i] = A[i];
		}
		for (int i = q + 1; i <= r; i++) {
			B[i] = A[i];
		}
		int i = 0;
		int j = 0;
		for (int k = p; k <= r; k++) {
			if ((j >= rs) || (i < ls) && (B[p + i] <= B[q + 1 + j])) {
				A[k] = B[p + i++];
			} else {
				A[k] = B[q + 1 + j++];
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
		}
		MergeSort(A, B, 0, n - 1);
		for (int j = 0; j < A.length - 1; j++) {
			if (A[j] > A[j + 1]) {
				System.out.println("Sorting failed");
				return;
			}
		}
		System.out.println("Sorting Suceeded");
	}
}