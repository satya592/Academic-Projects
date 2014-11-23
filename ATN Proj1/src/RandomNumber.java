import java.util.ArrayList;
import java.util.Collections;

public class RandomNumber {

	public static ArrayList<Integer> rNum() {

		// ArrayList to hold Integer objects
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for (int i = 0; i < Proj1.No_Nodes; i++) {
			numbers.add(i);
		}

		Collections.shuffle(numbers);
		return numbers;
	}
}