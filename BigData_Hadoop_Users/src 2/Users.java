import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Users {

	public static class Map extends
			Mapper<LongWritable, Text, Text, IntWritable> {

		private static Text zipcode;

		@Override
		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			String param = conf.get("zipcode");
			zipcode = new Text(param);
		}

		public void map(LongWritable ikey, Text ivalue, Context context)
				throws IOException, InterruptedException {
			String userdata[] = ivalue.toString().split("::");
			if (userdata.length != 5) {
				System.out.println("Format error in data");
				System.exit(0);
			} else {
				// System.out.println("userdata is:" + userdata[0] + "->"
				// + userdata[4]);
				Text key = new Text(userdata[4]);
				if (key.equals(zipcode))
					context.write(key,
							new IntWritable(Integer.valueOf(userdata[0])));
			}
		}	
	}

	public static class Reduce extends
			Reducer<Text, IntWritable, Text, IntArrayWritable> {

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			// IntWritable[] a = (IntWritable[]) IteratorUtils.toArray(values
			// .iterator());
			ArrayList<IntWritable> res = new ArrayList<IntWritable>();
			Iterator<IntWritable> it = values.iterator();
			IntWritable ele = null;

			while (it.hasNext()) {
				// System.out.println(ele);
				ele = new IntWritable();
				ele.set(it.next().get());
				res.add(ele);
			}
			IntArrayWritable allValues = new IntArrayWritable();

			// Arraylist to array converstion
			IntWritable arrayInt[] = new IntWritable[res.size()];
			for (int i = 0; i < res.size(); i++) {
				// System.out.println(i + "->" + res.get(i));
				arrayInt[i] = res.get(i);

			}

			allValues.set(arrayInt);
			context.write(key, allValues);
		}
	}

	// Driver program
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs(); // get all args
		if (otherArgs.length != 3) {
			System.err.println("Usage: Users <input> <output> <zipcode>");
			System.exit(2);
		}
		conf.set("zipcode", otherArgs[2]);
		// create a job with name "usersjob"
		Job job = Job.getInstance(conf, "usersjob");
		job.setJarByClass(Users.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		// uncomment the following line to add the Combiner
		// job.setCombinerClass(Reduce.class);

		// set output key type
		job.setOutputKeyClass(Text.class);
		// set output value type
		job.setOutputValueClass(IntWritable.class);

		// set the HDFS path of the input data
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		// set the HDFS path for the output
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

		// Wait till job completion
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}