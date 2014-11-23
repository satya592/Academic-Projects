public class CrossingTheSamePath {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Scanner input = new Scanner(System.in);
		// int n = input.nextInt();
		// int A[] = new int[n];
		// while (n-- != 0)
		// A[A.length - n - 1] = input.nextInt();
		// int A[] = { 1, 3, 2, 5, 4, 4, 6, 3, 2 };
		int A[] = { 2, 3, 1, 5, 4, 4, 6, 3, 2 };
		System.out.println("{ 1, 3, 2, 5, 4, 4, 6, 3, 2 }");
		System.out.println(countCrossing(A));
	}

	private static int countCrossing(int[] A) {

		// 0-North 1-East 2-South 3-West
		int top = Integer.MAX_VALUE;
		int btm = Integer.MIN_VALUE;
		int left = Integer.MIN_VALUE;
		int right = Integer.MAX_VALUE;
		int t = Integer.MAX_VALUE;
		int b = Integer.MIN_VALUE;
		int l = Integer.MIN_VALUE;
		int r = Integer.MAX_VALUE;
		int x = 0, y = 0;
		int move = 0;
		for (int i = 0; i < A.length; i++) {
			move++;
			System.out.println("{" + x + "," + y + "}");
			System.out.println("top:" + top + " btm:" + btm + " left:" + left
					+ " right:" + right);

			if (y < top && y > btm && x < right && x > left) {
				switch (i % 4) {
				case 0:// North x axis
					y += A[i];
					t = y;
					btm = b;
					break;
				case 1:// East y-axis
					x += A[i];
					// if (i != 1)
					left = l;
					// top = y;
					r = x;
					break;
				case 2:// south x axis
					y -= A[i];
					top = t;
					b = y;
					break;
				case 3:// west y-axis
					x -= A[i];
					right = r;
					l = x;
					break;
				}
			} else {
				return move;
			}
		}

		return 0;
	}
}
