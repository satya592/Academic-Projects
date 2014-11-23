import java.io.IOException;
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
			Mapper<LongWritable, Text, LongWritable, IntWritable> {

		public void map(LongWritable ikey, Text ivalue, Context context)
				throws IOException, InterruptedException {
			context.write(ikey, new IntWritable(Integer.valueOf("1")));
		}
	}

	public static class Reduce extends
			Reducer<LongWritable, IntWritable, LongWritable, IntWritable> {

		public void reduce(LongWritable key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			// IntWritable[] a = (IntWritable[]) IteratorUtils.toArray(values
			// .iterator());
			Iterator<IntWritable> it = values.iterator();
			int count = 0;
			while (it.hasNext()) {

				System.out.println(count++);
				System.out.println(key);
			}

			context.write(key, new IntWritable(Integer.valueOf("1")));
		}
	}

	// Driver program
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs(); // get all args
		if (otherArgs.length != 2) {
			System.err.println("Usage: Users <input> <output> <zipcode>");
			System.exit(2);
		}
		// create a job with name "usersjob"
		Job job = Job.getInstance(conf, "usersjob");
		job.setJarByClass(Users.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		// uncomment the following line to add the Combiner
		// job.setCombinerClass(Reduce.class);

		// set output key type
		job.setOutputKeyClass(LongWritable.class);
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