import java.util.Scanner;

public class GCDProduct {

	public static int GCD(int m, int n) {
		if (n == 0)
			return m;
		else
			return GCD(n, m % n);
	}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		int m = input.nextInt();
		int n = input.nextInt();
		long[][] GCD = new long[m][n];
		long product = 1;
		long prime = 1000000007;
		for (int i = 2; i <= m; i++)
			for (int j = 2; j <= n; j++) {
				if (GCD[i][j] == 0 && GCD[j][i] == 0) {
					GCD[i][j] = GCD[j][i] = GCD(i, j);
					product = (product * (GCD[i][j] % prime)) % prime;
				} else {
					if (GCD[i][j] != 0)
						product = (product * (GCD[i][j] % prime)) % prime;
					else
						product = (product * (GCD[j][i] % prime)) % prime;
				}
			}
		input.close();
		System.out.println(product);
	}
}