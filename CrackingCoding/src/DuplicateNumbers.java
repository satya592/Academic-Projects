import java.util.Hashtable;
import java.util.Scanner;

public class DuplicateNumbers {
	public static void main(String args[]) throws Exception {
		/* Enter your code here. Read input from STDIN. Print output to STDOUT */
		Hashtable<Integer, Integer> elements = new Hashtable<Integer, Integer>();
		Scanner s = new Scanner(System.in);
		int size = s.nextInt();
		Integer arr[] = new Integer[size];
		// StringBuilder st= new StringBuilder();
		for (int i = 0; i < size; i++) {
			arr[i] = s.nextInt();
		}
		for (int i = 0; i < size; i++) {

			if (elements.contains(arr[i])) {
				System.out.print(1);
			} else {
				elements.put(arr[i], arr[i]);
				System.out.print(0);
			}

		}
		System.out.println();
		elements.clear();
		StringBuilder str = new StringBuilder("");

		for (int i = size - 1; i >= 0; i--) {

			if (elements.contains(arr[i])) {
				// System.out.print(1);
				str.insert(0, 1);

			} else {
				elements.put(arr[i], arr[i]);
				// System.out.print(0);
				str.insert(0, 0);

			}

		}
		System.out.println(str.toString());
		s.close();
	}

}
