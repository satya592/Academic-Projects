import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Anagarams {

	public static HashMap<String, String> dictionay;
	public static ArrayList<String> anagrams;

	Anagarams() {
		String str;
		dictionay = new HashMap<String, String>();
		anagrams = new ArrayList<String>();

		try (Scanner scanner = new Scanner(new File("wl.txt"))) {
			while (scanner.hasNextLine()) {
				str = scanner.nextLine();
				// System.out.println(str);
				dictionay.put(str, str);
			}
		} catch (Exception n) {
		}

	}

	private static void permutation(String prefix, String str) {
		int n = str.length();
		if (n == 0) {
			// System.out.println(prefix);
			if (Anagarams.dictionay.get(prefix) != null)
				Anagarams.anagrams.add(prefix);
		} else {
			for (int i = 0; i < n; i++)
				permutation(prefix + str.charAt(i),
						str.substring(0, i) + str.substring(i + 1, n));
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Anagarams A = new Anagarams();
		Scanner input = new Scanner(System.in);
		System.out.println("Enter input:");
		String s = input.next();
		input.close();
		permutation("", s);
		for (Object str : Anagarams.anagrams.toArray()) {
			System.out.println(str + " ");
		}
	}
}
