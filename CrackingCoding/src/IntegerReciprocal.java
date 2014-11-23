import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class IntegerReciprocal {
	/*
	 * Complete the function below.
	 */

	public static void main(String argp[]) {
		reciprocal(10);
		reciprocal(3);
		reciprocal(6);
		reciprocal(7);
		reciprocal(11);
	}

	static void reciprocal(int N) {

		BigDecimal one = new BigDecimal(1);

		BigDecimal thirtyThree = new BigDecimal(N);

		// Fix the decimals you want, i.e. 21
		MathContext context = new MathContext(50, RoundingMode.DOWN);

		BigDecimal result = one.divide(thirtyThree, context);

		String s = result.toString(), reg, sub1;
		System.out.println(s);
		// s.matches(regex)
		// System.out.println(lrs(s));
		char[] num = s.toCharArray();
		int p = 0;
		for (p = 0; p < num.length; p++)
			if (num[p] == '.')
				break;
		p++;
		for (int c = p; c < num.length; c++) {
			for (int i = p + 1; i <= num.length - c; i++) {
				sub1 = s.substring(c, c + i);
				reg = ".*\\.[0-9]*(" + sub1 + ")+";
				if (s.matches(reg)) {
					System.out.println(sub1);
					return;
				}
			}
		}
		// System.out.println(result);
	}
}