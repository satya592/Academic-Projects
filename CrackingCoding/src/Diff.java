import java.util.Scanner;

public class Diff {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Scanner s = new Scanner(System.in);
		int size = s.nextInt(), t = size;
		// int count;
		int[] A = new int[t];
		while ((t--) >= 1) {
			A[t] = s.nextInt();
		}
		// for(int e:A) System.out.print(e+",");

		int pt = s.nextInt();// ,pt=size;
		int i, j;
		while ((pt--) >= 1) {
			j = size - s.nextInt() - 1;
			i = size - s.nextInt() - 1;

			int max, min, p, q, dif = 0;// = A[i] - A[i + 1], dif = 0;
			max = min = A[i];
			p = q = i;
			for (int k = i; k <= j; k++) {
				// System.out.println("max is"+max+"min is"+min);
				if (min > A[k]) {
					min = A[k];
					p = k;
				}
				// System.out.println("diff is"+dif);
				if (max < A[k]) {
					max = A[k];
					q = k;
				}
				if (q < p) {
					max = min;
					q = p;
				}

				if (p < q && dif < (A[q] - A[p]))
					dif = A[q] - A[p];
			}
			System.out.println(dif);
		}
		s.close();
	}

}