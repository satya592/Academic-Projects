import java.util.Scanner;

public class SquareRoot {
	public final static double delta = 1e-6;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner input = new Scanner(System.in);
		System.out.println("Enter x: ");
		double x = input.nextDouble();
		input.close();

		System.out.println("Square root of " + sqrt(x, delta));

	}

	private static double sqrt(double x, double delta) {
		double result = 0;
		if (x == 1 || x == 0)
			result = x;
		else {
			int i;
			for (i = 2; i * i < x; i *= 2)
				;
			if (i * i == x)
				result = i;
			else {
				int min = i / 2;
				int max = i;
				result = sqrtHelper(min, max, x, delta);
			}
		}
		return result;
	}

	private static double sqrtHelper(double min, double max, double x,
			double delta) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(min + ";" + max + ":" + x);
		if (Math.abs(max - min) <= delta)
			return max;
		double avg = (min + max) / 2;
		double sqr = avg * avg;
		if (sqr == x)
			return avg;
		else {
			if (sqr < x)
				return sqrtHelper(avg, max, x, delta);
			else
				return sqrtHelper(min, avg, x, delta);
		}
	}

}
