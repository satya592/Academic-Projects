import java.util.Scanner;

public class FindAllPrimes {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner s = new Scanner(System.in);
		int N = s.nextInt();// , t = size;
		int count = 1;
		out: for (int i = 2; i <= N; i++) {
			int sq = (int) Math.sqrt(i);
			for (int j = 2; j <= sq; j++) {
				if (i % j == 0)
					break out;
			}
			count++;
		}
		System.out.println(count);
		s.close();
	}

}
