import java.util.Random;

public class Project1 {

	static int N = 40;
	static int a[][] = new int[N][N];
	static int b[][] = new int[N][N];
	static int d[][] = new int[N][N];
	static int intialAValue = 30;

	static int k = 15;
	static int maxLinks = N * (N - 1);

	public static void initialization() {
		a = new int[N][N];
		b = new int[N][N];
		d = new int[N][N];

		Random ran;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (i == j) {
					b[i][j] = 0;
					a[i][j] = 0;
				} else {
					ran = new Random();
					b[i][j] = ran.nextInt(4);
					a[i][j] = intialAValue;
				}
			}
		}

		// Changing the value of a for k elements for every i
		int randNum;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < k;) {
				ran = new Random();
				randNum = ran.nextInt(N);
				if (i != randNum) {
					a[i][randNum] = 1;
					j++;
				}
			}
		}
		// printAB();
	}

	public static void printAB() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print("\t" + a[i][j]);
			}
			System.out.println("");
		}
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print("\t" + b[i][j]);
			}
			System.out.println("");
		}
	}

	public static void applyFloydWarshall() {
		// by default d is zero. Java array of int initialization

		// handled the condition of i==j -> d[i][j] = 0 in initialization

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				d[i][j] = a[i][j];
			}
		}

		for (int k = 0; k < N; k++) {
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					if (d[i][j] > d[i][k] + d[k][j]) {
						d[i][j] = d[i][k] + d[k][j];
					}
				}
			}
		}

		// System.out.println("done");
	}

	public static void networkDesignAlgo() {
		int sum = 0;
		int count = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				d[i][j] = d[i][j] * b[i][j];
				sum += d[i][j];
				System.out.print("\t" + d[i][j]);
				if (d[i][j] != 0) {
					count++;
				}

			}
			System.out.print("\n");
		}
		float density = (float) count / maxLinks;
		System.out.println("Total Cost of Network: " + sum);
		System.out.println("Total Number of Directed edges of Network: "
				+ count + " :: " + maxLinks);
		System.out.println("Density of Network : " + density);

	}

	public static void main(String args[]) {
		for (int v = 3; v <= k; v++) {
			System.out.println("For k =");
			initialization();
			applyFloydWarshall();
			networkDesignAlgo();
		}
	}
}