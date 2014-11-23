import java.util.ArrayList;

//# Given an arbitrarily-nested array, return a flattened version of it:
//[1,2,[3],[4,[5,6]],[[7]], 8] -> [1,2,3,4,5,6,7,8]
//
//================================================================================
//Test cases:
//
//[1, 2, [3], [4, [5, 6], 5, 6], [[7], [8, [9]]], 10]
//[1, 2, [3], [4, [5, 6], 5, 6], [[7], [8, [9]]], 10, [[[11], 12]]]

public class Flattenlist {

	static Object[] flatten(Object[] in) {
		ArrayList<Object> out = new ArrayList<Object>();
		for (Object obj : in) {
			if (obj instanceof Object[]) {
				// System.out.println((Object[]) in);
				for (Object b : flatten((Object[]) obj)) {
					if (b != null)
						out.add((Object) b);
				}
			} else {
				out.add(obj);
			}
		}
		return out.toArray();
	}

	public static void main(String[] args) {
		// [1,2,[3],[4,[5,6]],[[7]], 8] -> [1,2,3,4,5,6,7,8]

		Object[] input = new Object[6];
		input[0] = new Integer(1);
		input[1] = new Integer(2);

		input[2] = new Object[1];
		((Object[]) (input[2]))[0] = new Integer(3);

		input[3] = new Object[3];
		((Object[]) (input[3]))[0] = new Integer(4);

		((Object[]) (input[3]))[1] = new Object[2];

		((Object[]) ((Object[]) (input[3]))[1])[0] = new Integer(5);
		((Object[]) ((Object[]) (input[3]))[1])[1] = new Integer(6);

		input[4] = new Object[1];
		((Object[]) (input[4]))[0] = new Object[1];
		((Object[]) ((Object[]) (input[4]))[0])[0] = new Integer(7);

		input[5] = new Integer(8);

		// input[0] = new Object[1];
		// ((Object[]) (input[0]))[0] = new Integer(1);
		// System.out.println(((Object[]) (input[0]))[0]);
		Object[] out = flatten(input);
		System.out.println("Length is " + out.length);
		for (Object b : out)
			System.out.print((Integer) b + " ");

	}

}
