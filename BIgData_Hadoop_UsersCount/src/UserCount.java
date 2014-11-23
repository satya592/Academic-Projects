import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

@SuppressWarnings("deprecation")
public class UserCount {

	public static class Map extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		// Load user.data into hashmap
		private HashMap<String, String> UserDetails = new HashMap<String, String>();
		private static Text movieID;

		public void setup(Context context) throws IOException,
				InterruptedException {
			Configuration conf = context.getConfiguration();
			String param = conf.get("movieID");
			movieID = new Text(param);

			// Path[] files = (Path[])
			// DistributedCache.getLocalCacheFiles(context
			// .getConfiguration());
			// Path[] files = context.getLocalCacheFiles();

			// context.getCacheArchives();
			// Path p = new Path(context.getCacheFiles()[0]);
			// FileSystem.getLocal(conf)

			// for (Path p : files)
			{
				// System.out.println("Files in Cache " + p.toString ());
				BufferedReader br = new BufferedReader(new FileReader(new File(
						"cache-file")));
				// p.toString())));
				String str = null;
				while ((str = br.readLine()) != null) {
					String[] line = str.split("::");
					if (line.length == 5) {
						// System.out.println(str);
						// store the content from the files in DistributedCache
						UserDetails.put(line[0].trim(), line[1].trim());
					}
				}
				br.close();
			}
		}

		public void map(LongWritable ikey, Text ivalue, Context context)
				throws IOException, InterruptedException {
			String data[] = ivalue.toString().split("::");
			if (data.length != 4) {
				System.out.println("Format error in Ratings data");
				System.exit(0);
			} else {
				Text key = new Text(data[1].trim());
				String value = (data[0].trim());

				// System.out.println("ivalue: key,value:" + ivalue + ":" + key
				// + "," + value);

				if (key.equals(movieID) && UserDetails.get(value).equals("M"))
					context.write(key, new IntWritable(1));
			}
		}
	}

	public static class Reduce extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;

			// Add all the ratings of the movie
			for (IntWritable e : values) {
				sum += e.get();
			}

			IntWritable result = new IntWritable(sum);
			context.write(key, result);

		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		// argument validation
		if (args.length != 4) {
			System.err
					.println("Usage: UserCount <input: users.dat> <input: ratings.dat>  <out> <movieId>");
			System.exit(2);
		}

		System.out.println("@@@@@@@@@@@@@DistributedCache@@@@@@@@@@@@@");

		// *** IMPORTANT :: taking the args[0] in cache :: IMPORTANT ******//

		DistributedCache.addCacheFile(new URI(args[0] + "#cache-file"), conf);
		DistributedCache.createSymlink(conf);

		// Path p = new Path(new URI(args[0]));
		// conf.addResource(p);

		// set movieID
		conf.set("movieID", args[3]);

		Job avgJob = Job.getInstance(conf, "UserCount");

		avgJob.setJarByClass(UserCount.class);

		// avgJob.addCacheFile(new URI(args[0]));

		avgJob.setMapperClass(Map.class);
		avgJob.setReducerClass(Reduce.class);

		avgJob.setOutputKeyClass(Text.class);
		avgJob.setOutputValueClass(IntWritable.class);

		// set the HDFS path of the input data
		FileInputFormat.addInputPath(avgJob, new Path(args[1]));
		// set the HDFS path for the output
		FileOutputFormat.setOutputPath(avgJob, new Path(args[2]));

		avgJob.waitForCompletion(true);

	}

}