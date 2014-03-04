import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

enum JobStatus {
	Undefiened, Ready, Blocked, Executed, Running, Suspending;
}

public class JobTuple implements Comparator<JobTuple>, Comparable<JobTuple> {

	static int totalJobs = 0;
	int jobNo;
	int taskNo;
	int jobIndex;// starts from 1
	int jobPartNo;
	double availTime;
	double CurrAvailTime;
	double executionTime;// this is effective execution time - to get the actual
	double execBalance; // execution time get info from Taskset(taskNo)
	double absdeadline;
	JobStatus jobStatus = JobStatus.Undefiened;
	// start and stop history of a job
	TreeMap<Double, Double> StartnStop = new TreeMap<Double, Double>();
	// TreeMap<Double, Double> suspends = new TreeMap<Double, Double>();
	TreeMap<Double, Double> suspends = new TreeMap<Double, Double>();
	TreeMap<Integer, Boolean> ImDependOn = null;
	Integer[] OthersDependOnMe = null;
	TreeMap<Integer, Boolean> dependence = null;
	ArrayList<LinkedList<Resource>> ResoList = null;

	double suspension;

	double utilization;

	ArrayList<Integer> processorNo = null;

	// @SuppressWarnings("unchecked")
	@SuppressWarnings("unchecked")
	public JobTuple(int taskNo, int jobIndex, int jobPartNo, double availTime) {
		TaskTuple currentTask = TaskSet.getInstance().Tasks[taskNo - 1];
		this.jobNo = ++totalJobs;
		this.taskNo = taskNo;
		this.jobIndex = jobIndex;
		this.availTime = availTime;
		this.CurrAvailTime = this.availTime;
		this.jobPartNo = jobPartNo;
		this.executionTime = currentTask.execution;
		this.execBalance = this.executionTime;
		if (TaskSet.getInstance().getResources(currentTask.ResoList) != null)
			this.ResoList = (ArrayList<LinkedList<Resource>>) TaskSet
					.getInstance().getResources(currentTask.ResoList).clone();
		else
			this.ResoList = null;
		this.absdeadline = this.availTime + currentTask.deadline;
		if (currentTask.dependence != null)
			this.dependence = (TreeMap<Integer, Boolean>) currentTask.dependence
					.clone();
		else
			this.dependence = null;
		this.ImDependOn = this.dependence;
		// if (this.ImDependOn != null)
		// System.out.println(this.jobNo + ":" + this.ImDependOn.toString()
		// + ":" + this.isDependent());
		if (currentTask.OthersDependOnMe != null) {
			this.OthersDependOnMe = new Integer[currentTask.OthersDependOnMe
					.size()];
			currentTask.OthersDependOnMe.toArray(this.OthersDependOnMe);
			// System.out.print("OthersDependOnMe(" + taskNo + "):");
			// for (int i : this.OthersDependOnMe)
			// System.out.print(i);
			// System.out.println();
		}
		// System.out.println(this.Display());

		processorNo = new ArrayList<Integer>();

		this.suspension = currentTask.suspension;
		if (this.suspension != -1) {
			int rand = randInt();
			double suspen[], exec[], totEx = 0;
			suspen = randSum(rand, this.suspension);
			exec = randSum(rand + 1, this.executionTime);
			System.out.print("Divided into: " + (rand + 1) + "->");
			for (int i = 0; i < rand; i++) {// last exec don't need suspension
				totEx += exec[i];
				suspends.put(totEx, suspen[i]);
				System.out.print("e:" + exec[i] + " " + totEx + " " + "p"
						+ suspen[i] + ",");
			}
			totEx += exec[rand];
			System.out.println("e:" + exec[rand] + "=" + totEx);
		}

	}

	public boolean isDependent() {
		if (this.ImDependOn != null) {
			if (this.ImDependOn.toString().contains("false"))
				return false;
		}
		return true;
	}

	public JobTuple(int taskNo, int jobIndex, double availTime) {
		this(taskNo, jobIndex, -1, availTime);
	}

	public static double getExecutionTime(int taskNo) {
		return TaskSet.getInstance().Tasks[taskNo - 1].execution;
	}

	public String Display() {
		// Taskset.DisplayTaskResource(ResoList);
		// String jobStatus = "";
		// if (this.execBalance <= 0)
		// jobStatus = "Hit";
		// else
		// jobStatus = "Miss";
		return "("
				+ taskNo
				+ ";"
				+ jobIndex
				+ ";"
				+ availTime
				+ ";"
				+ (Math.floor(executionTime * 100) / 100)
				+ ";"
				+ (Math.floor(absdeadline * 100) / 100)
				+ ";"
				+ suspension
				+ ";"
				+ TaskSet.getInstance().Tasks[taskNo - 1]
						.getTaskDepend(this.dependence) + ";"
				+ TaskSet.getInstance().Tasks[taskNo - 1].ResoList + ")";

	}

	public String[] toArray() {
		String jobStatus = "";
		// String jobResponseTime = String.valueOf(this.getResposeTime());
		// String TurnAroundTime = String.valueOf(getTurnAroundTime());

		if (this.execBalance <= 0
				&& this.getRecentEndTime() <= this.absdeadline) {
			jobStatus = "Hit";

		} else {
			jobStatus = "Miss";
		}
		ArrayList<String> str = new ArrayList<String>();

		str.add(String.valueOf(this.jobNo));
		str.add("T" + String.valueOf(taskNo) + ":"
				+ String.valueOf(this.jobIndex));
		str.add(String.valueOf(this.availTime));
		str.add(String.valueOf(TaskSet.getInstance().Tasks[this.taskNo - 1].period));
		str.add(String.valueOf(Math.ceil(executionTime * 100) / 100));
		str.add(String.valueOf(Math.ceil(this.absdeadline * 100) / 100));
		str.add(String.valueOf(Math.ceil(this.suspension * 100) / 100));
		str.add(String.valueOf(TaskSet.getInstance().Tasks[taskNo - 1]
				.getTaskDepend(this.dependence)));
		str.add(String.valueOf(TaskSet.getInstance().Tasks[taskNo - 1].ResoList));
		str.add(jobStatus);

		str.add(String.valueOf(Math.ceil(getResposeTime() * 100) / 100));
		str.add(String.valueOf(Math.ceil(getTurnAroundTime() * 100) / 100));

		for (String srt : this.getStartnEnd()) {
			str.add(srt);
		}

		String astr[] = new String[str.size()];
		return str.toArray(astr);

	}

	public String Formated() {
		// Taskset.DisplayTaskResource(ResoList);
		String str = "";// "\"%9S,%9S,%9S,%9S,%9S,%9S,%9S,%9s\",";
		str += (String.valueOf(taskNo)) + ",";// 1
		str += (String.valueOf(this.jobIndex)) + ",";// 2
		str += (String.valueOf(this.availTime)) + ",";// 3
		str += (String.valueOf(this.executionTime)) + ",";// 4
		str += (String.valueOf(this.absdeadline)) + ",";// 5
		str += (String.valueOf(this.suspension)) + ",";// 6
		str += (String.valueOf(TaskSet.getInstance().Tasks[taskNo - 1]
				.getTaskDepend(this.dependence))) + ",";// 7
		str += (String
				.valueOf(TaskSet.getInstance().Tasks[taskNo - 1].ResoList));// 8

		return str;
	}

	// public synchronized void setStartTime() {
	// this.notifyAll();
	// }
	public synchronized void setStartTime(double startTime) {
		// Entry<Double, Double> last = this.StartnStop.lastEntry();
		this.StartnStop.put(startTime, (double) -1);
		// System.out.print("This has to be true:" + (last.getValue() == -1));
		// this.StartnStop.put(last.getKey(), startTime); // update the endtime
		this.notifyAll();
	}

	public synchronized void setEndTime(int procNo, Double endTime) {
		Entry<Double, Double> last = this.StartnStop.lastEntry();
		// System.out.print("This has to be true:" + (last.getValue() == -1));
		if (last.getKey() != endTime) {
			// System.out.println("settingEndTime for: " + last.getKey() + ":"
			// + endTime + " on P" + procNo);
			this.StartnStop.put(last.getKey(), endTime); // update the endtime
			Processor.getProcessor(procNo).Utilization += (this.StartnStop
					.lastEntry().getValue() - this.StartnStop.lastEntry()
					.getKey());

		} else {

			this.StartnStop.remove(last.getKey());
		}

		this.notifyAll();
	}

	public void displayStartnEnd() {
		Double key, value;
		for (Entry<Double, Double> entry : this.StartnStop.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			System.out.println(key + " => " + value);
		}
	}

	public double getRecentStartTime() {
		if (this.StartnStop != null)
			return this.StartnStop.lastEntry().getKey();
		else
			return -1;
	}

	public double getFirstStartTime() {
		if (StartnStop.size() != 0) {
			return this.StartnStop.firstKey();
			// System.out.println("RecentEndTime:" +
			// this.StartnStop.firstKey());
		} else
			return -1;
	}

	public double getResposeTime() {
		double responseTime = getFirstStartTime();
		if (responseTime != -1) {
			return responseTime - this.availTime;
		} else {
			return -1;
		}
	}

	public double getTurnAroundTime() {
		double EndTime = getRecentEndTime();
		if (EndTime != -1 && EndTime <= this.absdeadline) { // JobCompleted
			return EndTime - this.availTime;
		} else {
			return -1;
		}
	}

	public double getRecentEndTime() {
		if (StartnStop.size() != 0) {
			Entry<Double, Double> last = this.StartnStop.lastEntry();
			if (last.getValue() != -1)
				return this.StartnStop.lastEntry().getValue();
			else if (StartnStop.size() > 1) {
				last = this.StartnStop.pollLastEntry();
				double lastEndTime = this.StartnStop.lastEntry().getValue();
				this.StartnStop.put(last.getKey(), last.getValue());
				return lastEndTime;
			} else
				return -1;
			// System.out.println("RecentEndTime:"
			// + this.StartnStop.lastEntry().getValue());
		} else
			return -1;
	}

	public String[] getStartnEnd() {
		Double key, value;
		int size = this.StartnStop.size();
		boolean empty = false;
		if (size == 0) {
			size = 1;
			empty = true;
		}
		String str[] = new String[size];
		if (empty) {
			str[0] = "(----,----)";
			return str;
		} else {
			int i = 0;
			Integer p[] = new Integer[this.processorNo.size()];
			this.processorNo.toArray(p);
			for (Entry<Double, Double> entry : this.StartnStop.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();
				str[i] = ("(" + Math.floor(key * 100) / 100 + ","
						+ Math.floor(value * 100) / 100 + ")-p" + p[i]);
				i++;
			}
			return str;
		}
	}

	public double getJobExecutedTime() {
		Double key, value;
		double executedTime = 0;
		int size = this.StartnStop.size();
		if (size == 0) {
			return 0;
		} else {
			for (Entry<Double, Double> entry : this.StartnStop.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();
				if (value != -1) {
					executedTime += value - key;
				}
			}
			// System.out.println("JobexecutedTime:" + executedTime);
			return executedTime;
		}
	}

	public synchronized void assignProc(int processorNo, double startTime) {
		this.processorNo.add(processorNo);
		this.StartnStop.put(startTime, (double) -1);
		this.notifyAll();
	}

	private int myCompareTo(JobTuple o) {
		switch (Configuration.TypeOfScheduliing) {
		case EDF:
			if (this.absdeadline < o.absdeadline) {
				return -1;
			} else if (this.absdeadline > o.absdeadline) {
				return 1;
			} else {// (this.absdeadline == o.absdeadline)
				return 0;
			}

		case DM:
			if (TaskSet.getInstance().Tasks[this.taskNo - 1].deadline < TaskSet
					.getInstance().Tasks[o.taskNo - 1].deadline) {
				return -1;
			} else if (TaskSet.getInstance().Tasks[this.taskNo - 1].deadline > TaskSet
					.getInstance().Tasks[o.taskNo - 1].deadline) {
				return 1;
			} else {// (this.deadline == o.deadline)
				return 0;
			}

		case RM:
			if (TaskSet.getInstance().Tasks[this.taskNo - 1].period < TaskSet
					.getInstance().Tasks[o.taskNo - 1].period) {
				return -1;
			} else if (TaskSet.getInstance().Tasks[this.taskNo - 1].period > TaskSet
					.getInstance().Tasks[o.taskNo - 1].period) {
				return 1;
			} else {// (this.rate == o.rate)
				return 0;
			}

		default:

			return jobIndex;
		}
	}

	// private boolean isDependent(){
	// return false;
	// this.
	// Scheduler.Q.contains(o)
	// }

	@Override
	public int compareTo(JobTuple o) {
		// TODO Auto-generated method stub
		// this.processorNo;
		return myCompareTo(o);
	}

	@Override
	public int compare(JobTuple o1, JobTuple o2) {
		// TODO Auto-generated method stub
		return 0;
	}

	static double[] randSum(int n, double m) {
		Random rand = new Random();
		double randNums[] = new double[n], sum = 0;

		for (int i = 0; i < randNums.length; i++) {
			randNums[i] = rand.nextDouble();// 0 to 1
			sum += randNums[i];// .1+.2+.3 = .6
		}

		for (int i = 0; i < randNums.length; i++) {
			randNums[i] /= sum; // (.1/.6)
			randNums[i] *= m; // (1/6)*m
		}// ((1/6)+(2/6)+(3/6)) * m =(1)* m = m

		return randNums;
	}

	/**
	 * Returns a psuedo-random number between min and max, inclusive. The
	 * difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 * 
	 * @param min
	 *            Minimim value
	 * @param max
	 *            Maximim value. Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	static int randInt(int min, int max) {

		// Usually this can be a field rather than a method variable
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	static double randDouble(double min, double max) {

		// Usually this can be a field rather than a method variable
		Random rand = new Random();
		// nextDouble is normally exclusive of the top value, 0 - 0.999
		double randomNum = min + rand.nextInt(1000001) / 1000000.0
				* (max - min);

		return randomNum;
	}

	static int randInt() {

		// Usually this can be a field rather than a method variable
		Random rand = new Random();
		int num = Configuration.MaxSuspensions;
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt(num) + 1; // 1 - 3

		return randomNum;
	}

}
