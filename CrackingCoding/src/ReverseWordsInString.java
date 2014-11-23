import java.util.Scanner;

public class ReverseWordsInString {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		String str = input.nextLine();
		System.out.println(str);
		char[] in = str.trim().toCharArray();
		int si = 0, i = 0, sj = str.length() - 1, j = str.length() - 1;
		while (i <= j) {
			if (!delimiter(in[i]) || (delimiter(in[i]) && delimiter(in[i + 1]))) {
				i++;
			}
			if (!delimiter(in[j]) || (delimiter(in[j]) && delimiter(in[j + 1]))) {
				j--;
			}

			if (delimiter(in[j]) && delimiter(in[i])) {
				reverseWord(in, si, i - 1);
				reverseWord(in, j + 1, sj);
				reverseWord(in, i, j);
				reverseWord(in, si, sj);
				si = str.length() - j;
				sj = str.length() - i - 1 - 1;
				i = si;
				j = sj;
			}
		}

		System.out.print("Reversed words:");
		System.out.println(in);
		input.close();
	}

	public static boolean delimiter(char ch) {
		switch (ch) {
		case ' ':
		case '.':
		case '\'':
			return true;
		default:
			return false;
		}
	}

	public static String reverseWord(String in) {
		if (in == null)
			return null;
		else {
			char charArray[] = in.toCharArray();
			char tmp;
			for (int i = 0; i < charArray.length / 2; i++) {
				tmp = charArray[i];
				charArray[i] = charArray[charArray.length - 1 - i];
				charArray[charArray.length - 1 - i] = tmp;
			}
			return charArray.toString();
		}
	}

	public static char[] reverseWord(char[] charArray) {
		if (charArray == null)
			return null;
		else {
			char tmp;
			for (int i = 0; i < charArray.length / 2; i++) {
				tmp = charArray[i];
				charArray[i] = charArray[charArray.length - 1 - i];
				charArray[charArray.length - 1 - i] = tmp;
			}
			return charArray;
		}
	}

	public static char[] reverseWord(char[] charArray, int i, int j) {
		if (charArray == null || i >= charArray.length || j >= charArray.length
				|| (i > j))
			return null;
		else {
			char tmp;
			for (; i < j; i++, j--) {
				tmp = charArray[i];
				charArray[i] = charArray[j];
				charArray[j] = tmp;
			}
			return charArray;
		}
	}

}
