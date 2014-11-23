/**
 * String to Long conversion, if string is invalid or out of bounds return -1
 * Bounds are -2^63 and 2^63-1 i.e. 2^63 = 9,223,372,036,854,775,808
 * 
 * @author satyam kotikalapudi
 *
 */

public class Q1_StringtoLong {
	static long stringToLong(String s) {
		/* code goes here to convert a string to a long */
		if (s == null || s.equals("") || s.length() > 20)
			return -1;
		char str[] = s.toCharArray();

		long value = 0;
		int sign = 0;

		if (str[0] == '-')
			sign = -1;
		else if (str[0] == '+')
			sign = 1;

		for (int i = 0 + Math.abs(sign); i < str.length; i++) {
			if (!(str[i] <= '9' && str[i] >= '0'))
				return -1;
			else {
				if (Math.abs(value) > Long.MAX_VALUE / 10)
					return -1;
				else if (Math.abs(value) == Long.MAX_VALUE / 10) {
					if (str[i] == '9' || (str[i] == '8' && sign >= 0))
						return -1;
				}
				value *= 10;
				value += (str[i] - '0');
			}
		}
		if (sign == 0)
			sign++;
		return value * sign;
	}

	static void test() {
		assert stringToLong("123") == 123;
		assert stringToLong("-123") == -123;
		assert stringToLong("+123") == 123;
		System.out.println(stringToLong("9223372036854775807"));
		System.out.println(stringToLong("9223372036854775808"));
		System.out.println(stringToLong("-9223372036854775808"));
		System.out.println(stringToLong("-9223372036854775809"));
		assert stringToLong("asdf") == -1;
		assert stringToLong("") == -1;
		assert stringToLong(null) == -1;
	}

	public static void main(String[] args) {
		test();
	}

}
