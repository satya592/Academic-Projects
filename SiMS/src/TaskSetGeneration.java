import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class TaskSetGeneration {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int n = Configuration.TasksInTestTaskSet;
		double util[] = new double[n];
		double exec[] = new double[n];
		double perd[] = new double[n];
		double susp[] = new double[n];
		util = JobTuple.randSum(n, Configuration.TaskSetUtilization);

		// Random Periods Min and Max Periods
		for (int i = 0; i < n; i++) {
			perd[i] = JobTuple.randInt(Configuration.MinPeriodInTestTaskSet,
					Configuration.MaxPeriodInTestTaskSet);
		}

		// Random Execution
		for (int i = 0; i < n; i++)
			exec[i] = util[i] * perd[i];

		// Random Suspension(lower and upper)
		for (int i = 0; i < n; i++) {
			susp[i] = JobTuple.randDouble(Configuration.LowerBoundOfSuspension,
					Math.min(perd[i] - exec[i],
							Configuration.UpperBoundOfSuspension));
		}

		try {
			File file = new File("Tasklist.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();

			}
			PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(
					file.getAbsoluteFile(), false)));
			for (int i = 0; i < n; i++) {
				// (0;3;1;3;1;-1;-1)
				pr.write("(0;" + perd[i] + ";" + exec[i] + ";" + perd[i] + ";"
						+ susp[i] + ";-1;-1)\n");
			}
			pr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
