public class Processor implements Runnable {
	int ProcessorNo;
	double Utilization;
	int jobsCount;
	int HitsCount;
	int MissCount;
	public double currentTime;
	static double processorClock;
	JobTuple currentJob = null;
	int jobNo;

	private static Processor instance[] = null;

	public static Processor[] getAllProcessors(int noOfProc) {
		if (instance == null) {
			instance = new Processor[noOfProc];
			// set processor
			for (int i = 1; i <= noOfProc; i++) {
				instance[i - 1] = new Processor();
				instance[i - 1].ProcessorNo = i;
			}
		}
		return instance;
	}

	public static Processor getProcessor(int procNo) {
		if (instance != null) {
			return instance[procNo - 1];
		} else {
			return null;
		}
	}

	private Processor() {
		Utilization = 0;
		HitsCount = 0;
		MissCount = 0;
		jobsCount = 0;
		Processor.processorClock = 0;
		this.currentTime = 0;
	}

	// Hit count and Miss count setters and getters
	void setMisscount(int MissCount) {
		this.MissCount = MissCount;
	}

	void setHitsCount(int HitsCount) {
		this.HitsCount = HitsCount;
	}

	void setHitMissCount(int HitsCount, int MissCount) {
		this.HitsCount = HitsCount;
		this.MissCount = MissCount;
	}

	int getMisscount() {
		return MissCount;
	}

	int getHitsCount() {
		return HitsCount;
	}

	// Clock setters and getters
	static void setProcessorClock(double ProcessorClock) {
		Processor.processorClock = ProcessorClock;
		Processor procsers[] = Processor
				.getAllProcessors(Configuration.NoOfProcessors);
		for (int i = 0; i < Configuration.NoOfProcessors; i++)
			procsers[i].currentTime = ProcessorClock;
	}

	static void updateClock() {
		// try {
		// Thread.sleep(1); // sleep for 1 mill second
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		Processor.processorClock += Configuration.TickSize;
		// Processor.processorClock =
		// Clock.getInstance(HyperPeriod).processorClock;
		Processor procsers[] = Processor
				.getAllProcessors(Configuration.NoOfProcessors);
		for (int i = 0; i < Configuration.NoOfProcessors; i++)
			procsers[i].currentTime = Processor.processorClock;
	}

	void setJob(int jobNo) {
		this.jobNo = jobNo;
		if (jobNo != 0) {
			// System.out.println("Processor " + this.ProcessorNo + " set job "
			// + jobNo);
			this.currentJob = JobSet.getInstance().getJob(jobNo);
			jobsCount++; // Not the Unique jobs
		} else {
			this.currentJob = null;
		}
	}

	void Display() {
		System.out.println("ProcessorNo:" + this.ProcessorNo);
		System.out.println("Hits:Misss=" + this.HitsCount + ":"
				+ this.MissCount);
		System.out.println("CurrentJob:" + this.currentJob);
		System.out.println("ClockTime:" + Processor.processorClock);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// new Scheduler(HyperPeriod).schedule(this.ProcessorNo);
	}
}
