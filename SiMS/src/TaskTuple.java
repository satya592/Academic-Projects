import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

public class TaskTuple {
	int taskNo;
	double phase;
	double period;
	double execution;
	double deadline;
	double suspension;
	TreeMap<Integer, Boolean> dependence = new TreeMap<Integer, Boolean>();
	ArrayList<Integer> OthersDependOnMe = new ArrayList<Integer>();
	// int[] OthersDependOnMe = null;
	// ArrayList<LinkedList<Resource>> ResoList = null;
	String ResoList = null;

	double utilization;

	public static int TotalTasks = 0;

	public TaskTuple(int taskNo, double phase, double period, double execution,
			double deadline, double suspension, int dependence[],
			ArrayList<Integer> OthersDependOnMe, String ResoList) {
		this.taskNo = taskNo;
		this.phase = phase;
		this.period = period;
		this.execution = execution;
		this.deadline = deadline;
		this.suspension = suspension;
		// if (this.suspension != -1) {
		// int rand = randInt();
		// double suspen[], exec[];
		// suspen = randSum(rand, this.suspension);
		// exec = randSum(rand + 1, this.execution);
		// for (int i = 0; i < rand; i++) {// last exec don't need suspension
		// suspends.put(exec[i], suspen[i]);
		// System.out.print("e:" + exec[i] + "p" + suspen[i] + ",");
		// }
		// System.out.println();
		// }
		if (dependence != null) {
			for (int d : dependence) {
				this.dependence.put(d, false);
			}
		} else
			this.dependence = null;
		this.OthersDependOnMe = OthersDependOnMe;
		this.ResoList = ResoList;
		this.utilization = execution / period;
	}

	public TaskTuple(double phase, double period, double execution,
			double deadline) {
		this(TotalTasks++, phase, period, execution, deadline, -1, null, null,
				null);
	}

	public TaskTuple(double phase, double period, double execution,
			double deadline, double suspension, int dependence[],
			ArrayList<Integer> OthersDependOnMe, String ResoList) {
		this(++TotalTasks, phase, period, execution, deadline, suspension,
				dependence, OthersDependOnMe, ResoList);
	}

	public String Display() {
		return "(" + taskNo + ";" + phase + ";" + period + ";" + execution
				+ ";" + deadline + ";" + suspension + ";"
				+ getTaskDepend(this.dependence) + ";" + this.ResoList // getTaskResource()
				+ ")";
	}

	// Display Dependences##########################################
	public String getTaskDepend(TreeMap<Integer, Boolean> depends) {
		if (depends != null) {
			String str = "[";
			Set<Integer> deps = depends.keySet();
			Iterator<Integer> it = deps.iterator();
			// System.out.print("[");
			while (it.hasNext())
				str += (it.next().toString() + ",");
			str += "]";
			return str;
		} else
			return "-1";
	}

	// Display Resources##########################################
	public String getTaskResource(ArrayList<LinkedList<Resource>> ResoList) {
		Resource r = null;
		int count = 0;
		String str = "";
		if (ResoList != null) {
			for (LinkedList<Resource> list : ResoList) {
				while ((r = list.poll()) != null) {
					str += "[R" + r.Rno + "," + r.RTime;
					count++;
				}
				str += "]";
				count--;
			}
			while (count-- > 0)
				str += "]";
		} else
			str = "-1";
		return str;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof TaskTuple)) {
			return false;
		}
		TaskTuple other_ = (TaskTuple) other;
		return other_.taskNo == this.taskNo;
	}

}