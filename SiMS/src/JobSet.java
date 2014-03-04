import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

public class JobSet {

	JobTuple[] Jobset = null;
	int TotalNoOfJobs = 0;
	int[] JobsPerTask = null;
	double HyperPeriod = 0;
	private static JobSet instance = null;

	public static JobSet getInstance(double HyperPeriod) {
		if (instance == null) {
			instance = new JobSet(HyperPeriod);
		}
		return instance;
	}

	public static JobSet getInstance() {
		if (instance == null) {
			return null;
		}
		return instance;
	}

	public JobTuple getJob(int jobNo) {
		for (JobTuple job : Jobset) {
			if (job.jobNo == jobNo) {
				return job;
			}
		}
		// System.out.println("Job not found for jobNo:" + jobNo);
		return null;
	}

	private JobSet(double HyperPeriod) {
		int NoOfJobs = 0, i = 0, jobNo = 0;
		double avail = 0;
		this.HyperPeriod = HyperPeriod;
		// ArrayList<JobTuple> Jobs = new ArrayList<JobTuple>();
		JobsPerTask = new int[TaskSet.getInstance().Tasks.length];

		for (TaskTuple task : TaskSet.getInstance().Tasks) {
			JobsPerTask[task.taskNo - 1] = NoOfJobs = (int) Math
					.ceil((HyperPeriod - task.phase) / task.period);
			TotalNoOfJobs += NoOfJobs;
			// System.out.println("NoOfJobs of task" + task.taskNo + "="
			// + NoOfJobs);
		}

		Jobset = new JobTuple[this.TotalNoOfJobs];

		for (TaskTuple task : TaskSet.getInstance().Tasks) {
			NoOfJobs = (int) Math
					.ceil((HyperPeriod - task.phase) / task.period);
			// TotalNoOfJobs += NoOfJobs;
			for (avail = task.phase, i = 0; i < NoOfJobs && avail < HyperPeriod; i++, avail += task.period) {
				Jobset[jobNo++] = new JobTuple(task.taskNo, i + 1, avail);
			}
		}
		System.out.println("Total Jobs:" + this.Jobset.length);
	}

	// public int[] getJobNo(JobTuple job, int dependence[]) {
	// for (JobTuple j : this.Jobset) {
	// if (job.taskNo == j.taskNo && job.jobIndex == j.jobIndex) {
	// return job.jobNo;
	// }
	// }
	// }

	public void getTotalNoOfJobs() {
		System.out
				.println("Total No of Jobs to schedule:" + this.TotalNoOfJobs);
	}

	public void Display() {

		for (JobTuple job : this.Jobset) {
			System.out.println(job.Display());
		}
	}

	public void Sort() {
		Arrays.sort(Jobset);
	}

	// public void SortByEndTime() {
	// for(JobTuple job:Jobset){
	//
	// }
	// }

	public LinkedList<Integer> queue() {
		LinkedList<Integer> q = new LinkedList<Integer>();
		for (JobTuple job : this.Jobset) {
			q.add(job.jobNo);
		}
		return q;
	}

	public void Print() {

		try {
			int HyperPeriod = (int) ((Configuration.HyperPeriod == -1) ? TaskSet
					.getInstance().HyperPeriod() : Configuration.HyperPeriod);
			File file = new File("jobslist.txt");
			// if file doesnt exists, then create it
			int noSnE, len;
			if (!file.exists()) {
				file.createNewFile();
			}
			PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(
					file.getAbsoluteFile(), false)));
			pr.write("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
			pr.write((char) 0xA9
					+ " Author: Satyam Kotikalapudi "
					+ "Email:Satyam.Kotikalapudi@UTDallas.edu"
					+ " Objective: SiMS ( Simulation of Multiprocessors Scheduling ) TimeStamp:"
					+ new Date().toString() + "\n");
			pr.write("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");

			pr.println("Total numbers Tasks in Task-Set: "
					+ TaskSet.getInstance().Tasks.length);
			pr.println("Total numbers Jobs scheduled: " + TotalNoOfJobs);
			pr.println("Total Utilization of the Task-Set: "
					+ Math.ceil(TaskSet.getInstance().Utilization() * 1000)
					/ 1000);
			pr.println("Hyper-Period: " + HyperPeriod);

			for (int i = 1; i <= Configuration.NoOfProcessors; i++) {
				pr.println("Utilization of Processor"
						+ i
						+ ": "
						+ Math.ceil(Processor.getProcessor(i).Utilization
								/ HyperPeriod * 100) / 100);
			}

			pr.write("-----------------------------------------------------------------------------------------------------------------------------------------------------------\n");

			pr.printf(
					"%-6s|%-13s|%-9s|%-9s|%-9s|%-9s|%-12s|%-12s|%-20s|%-6s|%-8s|%-10s|%-12s\n",
					"Job-ID", "Task:Instance", "Release", "Period",
					"Execution", "Deadline", "Suspension", "Dependence",
					"(Start,End)", "Status", "Response", "TurnAround",
					"Resource");
			pr.write("-----------------------------------------------------------------------------------------------------------------------------------------------------------\n");
			for (JobTuple job : this.Jobset) {
				// pr.write(job.Display() + "\n");
				String[] str = job.toArray();
				pr.printf(
						"%-6s|%-13s|%-9s|%-9s|%-9s|%-9s|%-12s|%-12s|%-20s|%-6s|%-8s|%-10s|%-12s\n",
						str[0], str[1], str[2], str[3], str[4], str[5], str[6],
						str[7], str[12], str[9], str[10], str[11], str[8]);

				len = str.length;
				noSnE = len - 13;
				for (int i = 0; noSnE != 0; i++, noSnE--) {
					pr.printf(
							"%-6s|%-13s|%-9s|%-9s|%-9s|%-9s|%-12s|%-12s|%-20s|%-6s|%-8s|%-10s|%-12s\n",
							"    ", "  ", "  ", "  ", "  ", "  ", "   ", "",
							str[13 + i], " ", "  ", "   ", "   ");
				}
			}
			pr.write("-----------------------------------------------------------------------------------------------------------------------------------------------------------\n");
			pr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void Print(JobTuple Jobset[]) {

		try {
			File file = new File("jobslist.txt");
			// if file doesnt exists, then create it
			int noSnE, len;
			if (!file.exists()) {
				file.createNewFile();
			}
			PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter(
					file.getAbsoluteFile(), true)));
			pr.write("-------------------------------------------------------------------------------------------------------------------------------------------\n");
			pr.write((char) 0xA9 + " Author: Satyam Kotikalapudi "
					+ "Email:Satyam.Kotikalapudi@utdallas.edu"
					+ " Objective: Scheduling RTS tasks TimeStamp:"
					+ new Date().toString() + "\n");
			pr.write("-------------------------------------------------------------------------------------------------------------------------------------------\n");
			pr.printf(
					"%-10s|%-9s|%-9s|%-9s|%-9s|%-12s|%-12s|%-20s|%-6s|%-8s|%-10s|%-12s\n",
					"Task:JobNo", "Instance", "Release", "Execution",
					"Deadline", "Suspension", "Dependence", "(Start,End)",
					"Status", "Response", "TurnAround", "Resource");
			pr.write("-----------------------------------------------------------------------------------------------------------------------------------------------------------\n");
			for (JobTuple job : Jobset) {
				// pr.write(job.Display() + "\n");
				String[] str = job.toArray();
				pr.printf(
						"%-10s|%-9s|%-9s|%-9s|%-9s|%-12s|%-12s|%-20s|%-6s|%-8s|%-10s|%-12s\n",
						str[0], str[1], str[2], str[3], str[4], str[5], str[6],
						str[11], str[8], str[9], str[10], str[7]);

				len = str.length;
				noSnE = len - 12;
				for (int i = 0; noSnE != 0; i++, noSnE--) {
					pr.printf(
							"%-10s|%-9s|%-9s|%-9s|%-9s|%-12s|%-12s|%-20s|%-6s|%-8s|%-10s|%-12s\n",
							"    ", "  ", "  ", "  ", "  ", "   ", "",
							str[12 + i], " ", "  ", "   ", "   ");
				}
			}
			pr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
