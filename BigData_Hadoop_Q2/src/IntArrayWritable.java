import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

public class IntArrayWritable extends ArrayWritable {

	public IntArrayWritable(Writable[] values) {
		super(IntWritable.class, values);
	}

	public IntArrayWritable() {
		super(IntWritable.class);
	}

	public IntArrayWritable(Class valueClass, Writable[] values) {
		super(IntWritable.class, values);
	}

	public IntArrayWritable(Class valueClass) {
		super(IntWritable.class);
	}

	public IntArrayWritable(String[] strings) {
		super(strings);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String s : super.toStrings()) {
			sb.append(s).append(" ");
		}
		return sb.toString();
	}

}
