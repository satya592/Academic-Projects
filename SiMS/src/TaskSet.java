import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

public class TaskSet {

	TaskTuple[] Tasks = null;
	private static TaskSet instance = null;

	public static TaskSet getInstance() {
		if (instance == null) {
			instance = new TaskSet();
		}
		return instance;
	}

	protected TaskSet() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("tuples.txt"));
			System.out.println("Started Loading...");
			String currentLine = null;
			StringTokenizer stkn = null;

			String phase, period, exec, deadline, susp, depend, reso;
			ArrayList<TaskTuple> tTasks = new ArrayList<TaskTuple>();
			// ArrayList<Integer> dependList = new ArrayList<Integer>();
			// ArrayList<LinkedList<Resource>> ResoList = new
			// ArrayList<LinkedList<Resource>>();

			while ((currentLine = br.readLine()) != null
					&& !currentLine.trim().isEmpty()) {
				stkn = new StringTokenizer(currentLine, " (;)");
				phase = stkn.nextToken();
				// System.out.println(phase);
				period = stkn.nextToken();
				// System.out.println(period);
				exec = stkn.nextToken();
				// System.out.println(exec);
				deadline = stkn.nextToken();
				// System.out.println(deadline);
				susp = stkn.nextToken();
				// System.out.println(susp);
				depend = stkn.nextToken(); // String of dependences tasks
				// System.out.println(depend);
				reso = stkn.nextToken();
				// System.out.println(reso);
				int[] depends = getDependences(depend);
				// DisplayTaskDepend(depends);
				// ResoList = getResources(reso);
				// DisplayTaskResource(ResoList);

				TaskTuple temp = new TaskTuple(Double.parseDouble(phase),
						Double.parseDouble(period), Double.parseDouble(exec),
						Double.parseDouble(deadline), Double.parseDouble(susp),
						depends, null, reso);
				tTasks.add(temp);
			}
			br.close();
			Tasks = new TaskTuple[tTasks.size()];
			tTasks.toArray(Tasks);
			setOthersDependOnMe();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setOthersDependOnMe() {
		for (TaskTuple task : Tasks) {
			if (task.dependence == null)
				continue;
			else {
				Set<Integer> deps = task.dependence.keySet();
				for (int taskNo : deps) {
					if (Tasks[taskNo - 1].OthersDependOnMe == null)
						Tasks[taskNo - 1].OthersDependOnMe = new ArrayList<Integer>();
					Tasks[taskNo - 1].OthersDependOnMe.add(task.taskNo);
				}
			}
		}

	}

	// Generate dependences######################################
	public int[] getDependences(String depend) {
		StringTokenizer stkn = new StringTokenizer(depend, " [,]");
		int size = stkn.countTokens(), i = 0;
		if (size > 1) { // not -1
			size--;
			stkn.nextToken();
			// System.out.println(stkn.nextToken());// discard "d"
		} else
			return null;

		int depends[] = new int[size];

		while (stkn.hasMoreElements()) {
			depends[i++] = Integer.parseInt(stkn.nextToken());
		}

		return depends;
	}

	// Display Dependencee##########################################
	public void DisplayTaskDepend(int[] depends) {
		System.out.print("[");
		for (int i : depends)
			System.out.print(i + ",");
		System.out.println("]");
	}

	// Display Resources##########################################
	public void DisplayTaskResource(ArrayList<LinkedList<Resource>> ResoList) {
		Resource r = null;
		if (ResoList != null)
			for (LinkedList<Resource> list : ResoList) {
				System.out.println();
				while ((r = list.poll()) != null)
					r.Display();
			}
	}

	// Generate Resources##########################################
	public ArrayList<LinkedList<Resource>> getResources(String reso) {
		if (reso.equals("-1"))
			return null;
		ArrayList<LinkedList<Resource>> ResoList = new ArrayList<LinkedList<Resource>>();
		LinkedList<Resource> list = new LinkedList<Resource>();
		Resource element = new Resource();
		char[] resps = reso.toCharArray();
		String Rno = "";
		String Rtime = "";
		int i = 0;
		int size = reso.length();
		while (i < size) {
			Rno = "";
			Rtime = "";
			if (resps[i] == '[') {// [R1,14[R4,9[R5,4]]]
				i += 2; // skip "[R"
				while (resps[i] != ',') {
					Rno += resps[i++];
				}
				i++;
				while (resps[i] != '[' && resps[i] != ']') {
					Rtime += resps[i++];
				}
				element = new Resource(Integer.parseInt(Rno),
						Integer.parseInt(Rtime));
				list.add(element);
				if (resps[i] == ']') {
					ResoList.add(list);
					list = new LinkedList<Resource>();
					i++;
				}
			} else
				i++;
		}
		return ResoList;
	}

	// Display all the task set
	public void Display() {
		System.out.println("(taskNo,phase,period,exec,deadline,susp,depd)");
		for (TaskTuple T : this.Tasks) {
			System.out.println(T.Display());
		}
	}

	public void setOthersDependents() {
		for (TaskTuple task : Tasks) {
			if (task.dependence != null) {
				for (Entry<Integer, Boolean> entry : task.dependence.entrySet()) {
					this.Tasks[entry.getKey() - 1].OthersDependOnMe
							.add(task.taskNo);
				}
			}
		}
	}

	// Display all the system utilization
	public void SystemUtilization() {
		System.out.println("Utilization of the system:" + this.Utilization());
	}

	// Calculate system utilization
	public double Utilization() {
		double utilization = 0;
		for (TaskTuple temp : Tasks) {
			utilization += temp.utilization;
		}
		return utilization;
	}

	// Display all the system utilization
	public static double gcd(double a, double b) {
		while (b > 0) {
			double temp = b;
			b = a % b; // % is remainder
			a = temp;
		}
		return a;
	}

	public static double gcd(double[] input) {
		double result = input[0];
		for (int i = 1; i < input.length; i++)
			result = gcd(result, input[i]);
		return result;
	}

	public static double getLCM(double a, double b) {
		return a * b / gcd(a, b);
	}

	public static double getLCM(double[] element) {
		double lLCM = element[0];
		for (int k = 1; k < element.length; k++) {
			lLCM = getLCM(lLCM, element[k]);
			// cout<<lLCM<<endl;
		}
		return lLCM;
	}

	public double HyperPeriod() {
		double lLCM = Tasks[0].period;
		for (TaskTuple temp : Tasks) {
			lLCM = getLCM(temp.period, lLCM);
		}
		return lLCM;
		// return 32;
	}

	public double getExecutionTime(int taskNo) {
		for (TaskTuple t : this.Tasks) {
			if (t.taskNo == taskNo) {
				return t.execution;
			}
		}
		return -1;
	}

}
