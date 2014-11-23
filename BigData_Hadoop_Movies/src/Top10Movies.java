import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Top10Movies {

	public static class Map extends
			Mapper<LongWritable, Text, IntWritable, IntWritable> {

		public void map(LongWritable ikey, Text ivalue, Context context)
				throws IOException, InterruptedException {
			String data[] = ivalue.toString().split("::");
			if (data.length != 4) {
				System.out.println("Format error in Ratings data");
				System.exit(0);
			} else {
				IntWritable key = new IntWritable(Integer.valueOf(data[1]));
				IntWritable value = new IntWritable(Integer.valueOf(data[2]));
				context.write(key, value);
			}
		}
	}

	private static class Pair {
		public Pair(int key2, float value2) {
			key = key2;
			value = value2;
		}

		int key;
		float value;
	}

	private static class PairComparator implements Comparator<Pair> {
		@Override
		public int compare(Pair x, Pair y) {
			if (x.value < y.value) {
				return -1;
			}
			if (x.value > y.value) {
				return 1;
			}
			return 0;
		}

	}

	private static class Top10 {
		static Comparator<Pair> comparator = new PairComparator();
		static PriorityQueue<Pair> top10 = new PriorityQueue<Pair>(10,
				comparator);

		static void update(Pair p) {
			if (top10.size() < 10)
				top10.add(p);
			else {
				if (top10.peek().value < p.value) {
					top10.poll();
					top10.add(p);
				}
			}
		}

	}

	public static class Reduce extends
			Reducer<IntWritable, IntWritable, IntWritable, FloatWritable> {

		public void reduce(IntWritable key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			float sum = 0;
			int count = 0;

			// Add all the ratings of the movie
			for (IntWritable e : values) {
				sum += e.get();
				count++;
			}
			// take average of the ratings
			sum /= count;

			FloatWritable result = new FloatWritable(sum);
			context.write(key, result);
		}
	}

	public static class Map2 extends
			Mapper<LongWritable, Text, IntWritable, FloatWritable> {

		public void map(LongWritable ikey, Text ivalue, Context context)
				throws IOException, InterruptedException {
			String data[] = ivalue.toString().split("	");
			if (data.length != 2) {
				System.out.println("Format error in intermediate data");
				System.exit(0);
			} else {
				float value = Float.valueOf(data[1]);
				int key = Integer.valueOf(data[0]);
				// Use Top10 class to capture the top10 movies with highest
				// averages
				Top10.update(new Pair(key, value));
			}
		}

		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			Pair[] top10 = new Pair[10];
			top10 = Top10.top10.toArray(top10);
			for (Pair p : top10)
				context.write(new IntWritable(p.key),
						new FloatWritable(p.value));

		}

	}

	public static class Reduce2 extends
			Reducer<IntWritable, FloatWritable, IntWritable, FloatWritable> {

		public void reduce(IntWritable key, Iterable<FloatWritable> values,
				Context context) throws IOException, InterruptedException {
			for (FloatWritable e : values) {
				context.write(key, e);
			}
		}
	}

	// Driver program
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs(); // get all args
		if (otherArgs.length != 2) {
			System.err.println("Usage: Input Output");
			System.exit(2);
		}

		// Delete the tmp directory
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path("/tmp"), true); // delete file, true for recursive

		// create a job with name "Top10Movies"
		Job job = Job.getInstance(conf, "Top10Movies");
		job.setJarByClass(Top10Movies.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		// uncomment the following line to add the Combiner
		// job.setCombinerClass(Reduce.class);

		// set output key type
		job.setOutputKeyClass(IntWritable.class);
		// set output value type
		job.setOutputValueClass(IntWritable.class);

		// set the HDFS path of the input data
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		// set the HDFS path for the output
		FileOutputFormat.setOutputPath(job, new Path("/tmp"));

		// Wait till job completion
		job.waitForCompletion(true);

		job = Job.getInstance(conf, "Top10Movies2");
		job.setJarByClass(Top10Movies.class);

		job.setMapperClass(Map2.class);
		job.setReducerClass(Reduce2.class);
		// uncomment the following line to add the Combiner
		// job.setCombinerClass(Reduce.class);

		// set output key type
		job.setOutputKeyClass(IntWritable.class);
		// set output value type
		job.setOutputValueClass(FloatWritable.class);

		// set the HDFS path of the input data
		FileInputFormat.addInputPath(job, new Path("/tmp"));
		// set the HDFS path for the output
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		job.waitForCompletion(true);

	}
}