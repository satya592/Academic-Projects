import java.util.ArrayList;
import java.util.Scanner;

public class BaseNeg2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner input = new Scanner(System.in);
		int M = input.nextInt();
		int[] sq = bitSeq(M);
		System.out.println(-1 / -2 + "is it 1?");
		for (int i : sq)
			System.out.print(i + ",");
		input.close();
	}

	static int[] bitSeq(int M) {
		// return null;
		ArrayList<Integer> res = new ArrayList<Integer>();
		int base = -2;
		int car = 0;
		while (M != 0) {
			System.out.println(M + " is " + M % base + ":" + M / base);
			if (M % base == 0)
				res.add(0, 0);
			else {
				if (M % base == -1)
					car = 1;
				res.add(0, 1);
			}
			M = (M / base + car);
			car = 0;
		}
		int[] A = new int[res.size()];
		for (int i = 0; i < A.length; i++)
			A[i] = res.get(i);
		int i = Integer.MAX_VALUE;
		return A;
	}

}
