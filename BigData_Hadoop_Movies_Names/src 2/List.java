import java.util.ArrayList;

import org.apache.hadoop.io.IntWritable;

/*
 * @List for list of ArrayList
 */
public class List {
	ArrayList<IntWritable> list = null;

	List() {
		list = new ArrayList<IntWritable>();
		list.add(new IntWritable(592));
	}

	List(IntWritable value) {
		list = new ArrayList<IntWritable>();
		list.add(value);
	}

	List(ArrayList<IntWritable> valuelist) {
		list = valuelist;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		boolean first = true;
		for (IntWritable val : list) {
			if (!first) {
				str.append(",");
			}
			str.append(val.toString());
			if (first) {
				first = false;
			}
		}

		return str.toString();
	}
}
