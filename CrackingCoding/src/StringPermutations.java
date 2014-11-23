import java.util.Arrays;
import java.util.Scanner;

public class StringPermutations {

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);
		String str = input.next();
		char[] A = str.toCharArray();
		Arrays.sort(A);
		System.out.println("Input:" + str);
		printPermutations(A, A.length - 1, 0);
		input.close();
	}

	private static void printPermutations(char[] A, int i, int n) {
		if (i == 0)
			System.out.println(A);
		else {
			printPermutations(A, i - 1, n + 1);
		}
	}

}
