import java.util.HashMap;

public class FindDulicatesInArray {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int A[][] = { { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 },
				{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 2, 4, 8, 5 },
				{ 1, 3, 5, 6, 7, 8, 9, 0, 2, 4, 8, 5 },
				{ 1, 3, 15, 36, 27, 48, 9, 10, 90, 21, 43, 65 } };
		// duplicates are 2,4,8,5
		for (int i = 0; i < A.length; i++) {
			int sum = A[i][0];
			for (int j = 1; j < A[i].length; j++) {
				sum ^= A[i][j];
			}

			System.out.println("XOR of all the elements:" + sum);
		}
		HashMap<Integer, Integer> hmap = new HashMap<Integer, Integer>();
		// hmap.put(key, value)

		for (int i = 0; i < A.length; i++) {
			// int sum = A[i][0];
			for (int j : A[i]) {
				if (hmap.containsKey(j))
					System.out.print(j + ",");
				else
					hmap.put(j, j);
			}
			hmap.clear();
			System.out.println("");
		}

	}

}
