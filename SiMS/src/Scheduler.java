import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Scheduler {

	// compare jobs on ReadyQueue(sorted on priority) of JobSet with jobs on
	// Processors
	// For all the jobs whose arrival is less than processors
	// if any of the jobs have more priority than jobs on processors. Preempt
	// the job on processors and put them back on ReadyQueue.

	public static LinkedList<Integer> Q = null;
	public static Handler shedulingLog;
	private static Logger MyLogger = Logger.getLogger("shedulingLog");
	double HyperPeriod = 0;

	Scheduler(double HyperPeriod) {
		this.HyperPeriod = HyperPeriod;
	}

	public static synchronized void add(int index, int jobNo) {
		Q.add(index, jobNo);
		MyLogger.log(Level.INFO, "AddQueue: Job " + jobNo
				+ " added to the Queue");
	}

	public static void addQueue(int jobNo) {

		JobTuple job = JobSet.getInstance().getJob(jobNo);
		int i = 0;
		for (Integer current : Q) {
			if (current == job.jobNo) {
				// Job already exists in the Queue
				return;
			}
			if (JobSet.getInstance().getJob(current).compareTo(job) > 0) {
				// this.absdeadline > o.absdeadline
				break;
			} else {
				i++;
			}
		}
		MyLogger.info("Adding at:" + i);
		Scheduler.add(i, jobNo);
	}

	public static void addQueue(JobTuple job) {

		Scheduler.addQueue(job.jobNo);
	}

	public static synchronized void remove(int index) {
		Q.remove(index);
	}

	public static void removeQueue(int jobNo) {

		int i = 0;
		boolean found = false;
		for (Integer current : Q) {
			if (jobNo == current.intValue()) {
				found = true;
				break;
			} else {
				i++;
			}
		}
		if (found) {
			Scheduler.remove(i);
			MyLogger.info("RemoveQueue: Job " + jobNo
					+ " removed from the Queue");
		} else {
			MyLogger.log(Level.WARNING, "RemoveQueue: Job Not found in Queue");
		}
	}

	public static void removeQueue(JobTuple job) {
		Scheduler.removeQueue(job.jobNo);
	}

	public void scheduler(int procs) {
		while (!Q.isEmpty() && Processor.processorClock < HyperPeriod) {
			for (int i = 1; i <= procs; i++) {
				MyLogger.info("call for Processor.." + i + "at time"
						+ Processor.getProcessor(i).currentTime + " VS "
						+ Processor.processorClock);
				schedule(i);

			}
			Processor.updateClock();// Clock update
		}

		// Unfinished jobs on the processors
		int count = procs;

		while (count != 0 && Processor.processorClock < HyperPeriod) {
			for (int i = 1; i <= procs; i++) {
				if (Q.isEmpty() && Processor.processorClock < HyperPeriod
						&& Processor.getProcessor(i).currentJob != null) {
					schedule(i);
				} else {
					count--;
				}
			}
			Processor.updateClock();// Clock update
		}

		JobTuple currentJob = null;

		for (int i = 1; i <= procs; i++) {
			currentJob = Processor.getProcessor(i).currentJob;
			if (currentJob != null) {
				currentJob.setEndTime(i, HyperPeriod);
			}
		}

	}

	public void schedule(int procNo) {// , double balance) {
		MyLogger.info("Got call for Processor.." + procNo + "at time"
				+ Processor.getProcessor(procNo).currentTime + " VS "
				+ Processor.processorClock);

		// MyLogger.info("assignJob called..." + procNo);
		// balance is negative and procNo
		int jobOnProc = -1;
		JobTuple jobsList[] = null, hPJob = null, currentJob = null;
		Double bal = (double) -1, newBal = (double) -1, currentJobEnd = (double) -1, newSusCurrentTime = (double) -1, newHPCurrentTime = (double) -1;

		JobSet Jobset = JobSet.getInstance();
		Processor processor = Processor.getProcessor(procNo);
		currentJob = Jobset.getJob(processor.jobNo);

		if (!Q.isEmpty()
				&& processor.currentTime <= Processor.processorClock + 1
				&& processor.currentTime <= this.HyperPeriod) {

			if (currentJob == null) {

				for (Integer jobNo : Q) {// Job is present of ReadyQueue
					if (isScheduleable(jobNo, processor.currentTime)) {
						// set job no to remove from the Queue
						jobOnProc = jobNo;
						break;
					}
				}
				MyLogger.info("\n");

				// No Job found whose currentAvailTime <= processor.currentTime
				if (jobOnProc == -1) {
					MyLogger.info("check till next tick time"
							+ Processor.processorClock + 1);
					MyLogger.info("getNextAvailableJobs:");
					jobsList = this
							.getNextAvailableJobs(Processor.processorClock + 1);

					MyLogger.info("\n");
					if (jobsList != null) {
						for (int i = 0; i < jobsList.length; i++) {
							if (isScheduleable(jobsList[i].jobNo,
									Processor.processorClock + 1)) {
								jobOnProc = jobsList[i].jobNo;
								break;
							}
						}
					}
				}
				MyLogger.info("\n");
				// No Job is available
				if (jobOnProc == -1)
					return;
				else {

					Scheduler.removeQueue(jobOnProc);

					currentJob = Jobset.getJob(jobOnProc);

					processor.currentTime = Math.max(processor.currentTime,
							currentJob.CurrAvailTime);
					processor.setJob(jobOnProc);

					currentJob.assignProc(procNo, processor.currentTime);

				}
			} else {
				jobOnProc = processor.jobNo;
			}

			// Check for Preemption from <currentTime to
			// Processor.processorClock+1>
			Map.Entry<Double, Double> suspension = this.isSuspended(jobOnProc,
					procNo);
			hPJob = this.CheckForPriorityJobs(jobOnProc, procNo);

			if (suspension != null) {// (K,V)=(newCurrentTime, susp)
				newSusCurrentTime = suspension.getKey();
			}
			if (hPJob != null) {
				newHPCurrentTime = hPJob.CurrAvailTime;
			}
			currentJobEnd = currentJob.getRecentStartTime()
					+ currentJob.executionTime
					- currentJob.getJobExecutedTime();

			// Add CODE HERE FOR RESOURCE BLOCKED TIME

			if (newSusCurrentTime != -1 || newHPCurrentTime != -1) {// true
				// Implement preemption and UpdateCurrentTime and call assign
				if (newSusCurrentTime != -1 && newHPCurrentTime != -1) {// true
					if (Math.min(newHPCurrentTime, newSusCurrentTime) == newSusCurrentTime) {
						// Suspend the job
						processor.currentTime = newSusCurrentTime;

						currentJob.CurrAvailTime = processor.currentTime
								+ suspension.getValue();
						RemoveSuspension(currentJob.jobNo);

					} else if (Math.min(newHPCurrentTime, currentJobEnd) == newHPCurrentTime) {
						// Just preempt the job, for HP
						processor.currentTime = newHPCurrentTime;
					}
				} else if (newSusCurrentTime != -1) {
					// Suspend the job
					processor.currentTime = newSusCurrentTime;
					currentJob.CurrAvailTime = processor.currentTime
							+ suspension.getValue();
					RemoveSuspension(currentJob.jobNo);

				} else if (newHPCurrentTime != -1
						&& Math.min(newHPCurrentTime, currentJobEnd) == newHPCurrentTime) {
					// Just preempt the job, for HP
					processor.currentTime = newHPCurrentTime;
				} else {
					processor.currentTime = currentJobEnd;
				}
				// MyLogger.info("Job Preempted1");
				this.jobPreempted(procNo);
				// return;
				int nextProc = (procNo) % Configuration.NoOfProcessors + 1;
				;
				while (nextProc != procNo) {
					this.schedule(nextProc);
					nextProc = (nextProc) % Configuration.NoOfProcessors + 1;
				}
				this.schedule(procNo);

			} else {// false- not preempted

				bal = Double.parseDouble(String.format("%.6f",
						currentJob.executionTime))
						- Double.parseDouble(String.format("%.6f",
								currentJob.getJobExecutedTime()));
				// - currentJob.getJobExecutedTime();
				// MyLogger.info("Exected:"
				// + Double.parseDouble(String.format("%.6f",
				// currentJob.getJobExecutedTime())));

				newBal = bal
						- (Processor.processorClock + 1 - currentJob
								.getRecentStartTime());
				// MyLogger.info("RecentStart:" +
				// currentJob.getRecentStartTime());
				newBal = Double.parseDouble(String.format("%.6f", newBal));
				if (newBal <= 0) {
					// MyLogger.info("bal:" + bal + "newbal:" + newBal);
					// Job is done

					processor.currentTime = Processor.processorClock + 1
							+ newBal;

					// MyLogger.info("Job Preempted2");

					this.jobPreempted(procNo);
					// return;
					int nextProc = (procNo) % Configuration.NoOfProcessors + 1;
					while (nextProc != procNo) {
						this.schedule(nextProc);
						nextProc = (nextProc) % Configuration.NoOfProcessors
								+ 1;
					}
					this.schedule(procNo);
				} else {
					MyLogger.info("processor is busy with " + currentJob.jobNo
							+ ",from" + currentJob.getRecentStartTime());
					return;
				}
			}
			// Processor is occupied
		} else if (Q.isEmpty()
				&& processor.currentTime <= Processor.processorClock + 1
				&& processor.currentTime <= HyperPeriod
				&& processor.currentJob != null) {

			currentJob = Jobset.getJob(processor.jobNo);

			bal = currentJob.executionTime
					- Double.parseDouble(String.format("%.6f",
							currentJob.getJobExecutedTime()));
			// - currentJob.getJobExecutedTime();
			// MyLogger.info("Exected:"
			// + Double.parseDouble(String.format("%.6f",
			// currentJob.getJobExecutedTime())));

			newBal = bal
					- (Processor.processorClock + 1 - currentJob
							.getRecentStartTime());

			if (newBal <= 0) {
				// MyLogger.info("bal:" + bal + "newbal:" + newBal);
				// Job is done

				processor.currentTime = Processor.processorClock + 1 + newBal;

				// MyLogger.info("Job Preempted3");

				this.jobPreempted(procNo);
				// return;
				int nextProc = (procNo) % Configuration.NoOfProcessors + 1;
				while (nextProc != procNo) {
					this.schedule(nextProc);
					nextProc = (nextProc) % Configuration.NoOfProcessors + 1;
				}
				this.schedule(procNo);

			} else {
				MyLogger.info("processor is busy with " + currentJob.jobNo
						+ ",from" + currentJob.getRecentStartTime());
				return;
			}
		}
	}

	public void jobPreempted(int procNo) {
		Processor processor = Processor.getProcessor(procNo);
		JobSet Jobset = JobSet.getInstance();
		JobTuple pJob = Jobset.getJob(processor.jobNo);

		if (pJob == null) {// processor is empty
			MyLogger.log(Level.SEVERE, "Processor is free");
		}

		setEndTime(processor.jobNo, procNo);

		// double excutedTime = Double.parseDouble(String.format("%.6f",
		// pJob.getJobExecutedTime()));

		MyLogger.info("EXCEBAL is "
				+ Double.parseDouble(String.format("%.6f", pJob.executionTime))
				+ "-"
				+ (Double.parseDouble(String.format("%.6f",
						pJob.getJobExecutedTime()))));

		pJob.execBalance = Double.parseDouble(String.format("%.6f",
				pJob.executionTime))
				- Double.parseDouble(String.format("%.6f",
						pJob.getJobExecutedTime()));

		MyLogger.info("EXCEBAL "
				+ (Double.parseDouble(String.format("%.6f", pJob.executionTime)) - Double
						.parseDouble(String.format("%.6f",
								pJob.getJobExecutedTime()))));
		if (pJob.execBalance <= 0
				&& pJob.getRecentEndTime() <= pJob.absdeadline) {
			MyLogger.log(Level.INFO, "jobPreempted:Job " + pJob.jobNo
					+ " is done");

			jobDone(processor.jobNo);
			processor.HitsCount++;

			// MyLogger.info("True");
		} else if (pJob.execBalance > 0) {
			MyLogger.log(Level.INFO, "jobPreempted:Job " + pJob.jobNo
					+ " back to Queue");

			Scheduler.addQueue(pJob.jobNo);
			try {
				// Thread.sleep(5000);
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (pJob.getRecentEndTime() > pJob.absdeadline) {
			MyLogger.log(Level.WARNING, "jobPreempted:Job " + pJob.jobNo
					+ " missed deadline ");

			processor.MissCount++;

		}
		processor.currentJob = null;
		processor.setJob(0);
	}

	public Entry<Double, Double> isSuspended(int jobNo, int procNo) {
		JobSet Jobset = JobSet.getInstance();
		Processor processor = Processor.getProcessor(procNo);
		JobTuple pJob = Jobset.getJob(jobNo);
		Double exec = (double) 0, susp = (double) 0, newCurrentTime = (double) 0;
		Double excutedTime = pJob.getJobExecutedTime();
		@SuppressWarnings("unused")
		Double currentExecTime = processor.currentTime
				- pJob.getRecentStartTime();
		Map.Entry<Double, Double> Suspension = null;
		// MyLogger.info("isSuspended:currentExec " + currentExecTime + "="
		// + Processor.processorClock + "-" + pJob.getRecentStartTime()
		// + "&excutedTime:" + pJob.getJobExecutedTime());

		if (pJob.suspends != null && pJob.suspends.isEmpty() == false) {
			Entry<Double, Double> entry = pJob.suspends.firstEntry();

			exec = entry.getKey();
			susp = entry.getValue();

			// MyLogger.info("isSuspended:" + entry.getKey() + ","
			// + entry.getValue() + "==>" + exec);

			if (excutedTime < exec) {

				if ((excutedTime + Processor.processorClock + 1 - pJob
						.getRecentStartTime()) >= exec) {
					newCurrentTime = pJob.getRecentStartTime() + exec
							- excutedTime;

					MyLogger.info("newCurrentTime:" + newCurrentTime + ",susp:"
							+ susp);

					Suspension = new AbstractMap.SimpleEntry<Double, Double>(
							newCurrentTime, susp);

					return Suspension;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	public void RemoveSuspension(int jobNo) {
		JobTuple pJob = JobSet.getInstance().getJob(jobNo);
		pJob.suspends.pollFirstEntry();
	}

	public void jobDone(int jobNo) {
		JobSet Jobset = JobSet.getInstance();
		JobTuple jDone = Jobset.getJob(jobNo);
		int jDoneIndex = jDone.jobIndex;
		if (jDone.OthersDependOnMe != null) {
			for (JobTuple job : Jobset.Jobset) {
				for (int jDoneNo : jDone.OthersDependOnMe)
					if (job.taskNo == jDoneNo && job.jobIndex == jDoneIndex) {
						// MyLogger.info("" + job.taskNo + "==" + jDoneNo
						// + "&&" + job.jobIndex + "==" + jDoneIndex);
						job.ImDependOn.put(jDone.taskNo, true);
						// MyLogger.info("Making true:" + job.jobNo + "("
						// + job.taskNo + "," + job.jobIndex + ")"
						// + " dependence " + jDone.taskNo + ","
						// + jDone.jobIndex);
						// displayJobDep();
					}
			}
		}
	}

	public void displayJobDep() {
		JobSet Jobset = JobSet.getInstance();

		for (JobTuple job : Jobset.Jobset) {
			if (job.ImDependOn != null) {
				MyLogger.info(job.ImDependOn.toString());
			}
		}
		MyLogger.info("\n");
	}

	public boolean isScheduleable(int jobNo, double maxAvailTime) {
		JobSet Jobset = JobSet.getInstance();
		// Processor processor = Processor.getProcessor(procNo);
		MyLogger.info(Jobset.getJob(jobNo).CurrAvailTime + "<=" + maxAvailTime
				+ ",");
		return (Jobset.getJob(jobNo).CurrAvailTime <= maxAvailTime && Jobset
				.getJob(jobNo).isDependent());

		// Add a check if it is blocked by unavailability of resource
	}

	public boolean isJobAvailable(int jobNo, int procNo) {
		JobSet Jobset = JobSet.getInstance();
		Processor processor = Processor.getProcessor(procNo);
		// MyLogger.info(Jobset.getJob(jobNo).CurrAvailTime + "<="
		// + (processor.currentTime));
		return (Jobset.getJob(jobNo).CurrAvailTime <= (processor.currentTime) && Jobset
				.getJob(jobNo).isDependent());

		// Add a check if it is blocked by unavailability of resource
	}

	public void setEndTime(int jobNo, int procNo) {
		JobSet Jobset = JobSet.getInstance();

		Jobset.getJob(jobNo).setEndTime(procNo,
				Processor.getProcessor(procNo).currentTime);
		MyLogger.info("Job " + jobNo + " SetEndTime "
				+ (Processor.getProcessor(procNo).currentTime));
	}

	public void setStartTime(int jobNo, double balTime) {
		JobSet Jobset = JobSet.getInstance();
		Jobset.getJob(jobNo).setStartTime(Processor.processorClock + balTime);
		MyLogger.info("Job " + jobNo + " started at "
				+ (Processor.processorClock + balTime));
	}

	JobTuple CheckForPriorityJobs(int sjobNo, int procNo) {
		// MyLogger.info("CheckForPriorityJobs called...");
		JobSet.getInstance().Sort();// sorting based on the scheduling algorithm
		JobSet Jobset = JobSet.getInstance();
		// Processor processor = Processor.getProcessor(procNo);
		JobTuple qJob = null, pJob = null;

		// pJob = Jobset.getJob(processor.jobNo);
		pJob = Jobset.getJob(sjobNo);
		for (Integer jobNo : Q) {// Job is present of ReadyQueue
			qJob = Jobset.getJob(jobNo.intValue());
			if (qJob.compareTo(pJob) < 0) {
				// if this is true for high priority
				if (isScheduleable(qJob.jobNo, Processor.processorClock + 1)) {

					return qJob;
				}
			} else { // No high priority jobs
				MyLogger.info("No high priority jobs to asign");
				return null;
			}
			MyLogger.info("\n");
		}
		return null;
	}

	/*
	 * // job arrived setEndTime(processor.jobNo, balTime); // add this
	 * Preempted job to back to Ready Queue jobAddIndex = processor.jobNo; //
	 * Remove assigned job from ReadyQueue jobRemoveIndex = jobNo; //
	 * JobSet.removeQueue(Q, jobNo); processor.setJob(jobNo); // Update start
	 * time and Processor no of the Job setStartTime(jobNo, balTime);
	 * Jobset.getJob(jobNo).processorNo.add(processor.ProcessorNo); break; } }
	 * }// Check the Q for high priority jobs if (jobRemoveIndex != -1) {
	 * Scheduler.removeQueue(jobRemoveIndex); jobRemoveIndex = -1; } if
	 * (jobAddIndex != -1) { Scheduler.addQueue(jobAddIndex); jobAddIndex = -1;
	 * } }
	 */
	public JobTuple[] getNextAvailableJobs(double MaxAvailTime) {

		JobSet Jobset = JobSet.getInstance();
		ArrayList<JobTuple> jobsBasedOnAvailTime = new ArrayList<JobTuple>();
		JobTuple JobList[] = null;
		JobTuple job = null;
		int index = 0;
		for (int jobNo : Q) {
			job = Jobset.getJob(jobNo);
			if (job.CurrAvailTime <= MaxAvailTime) {
				if (jobsBasedOnAvailTime.isEmpty() == false) {

					for (index = 0; index < jobsBasedOnAvailTime.size(); index++) {
						if (job.CurrAvailTime <= jobsBasedOnAvailTime
								.get(index).CurrAvailTime) {
							while (index < jobsBasedOnAvailTime.size()
									&& job.CurrAvailTime == jobsBasedOnAvailTime
											.get(index).CurrAvailTime) {
								// this.absdeadline > o.absdeadline
								if (job.compareTo(jobsBasedOnAvailTime
										.get(index)) == 1) {
									index++;
								} else {
									// this.absdeadline <= o.absdeadline
									break;
								}
							}
							jobsBasedOnAvailTime.add(index, job);
							break;
						} else {
							// MyLogger.info("skipping " + index);
						}
					}

				} else {
					jobsBasedOnAvailTime.add(job);
				}
			}
		}

		JobList = new JobTuple[jobsBasedOnAvailTime.size()];

		jobsBasedOnAvailTime.toArray(JobList);
		for (JobTuple ljob : JobList) {
			MyLogger.info("(" + ljob.CurrAvailTime + "," + ljob.absdeadline
					+ ")" + ",");
		}
		// Arrays.sort(JobList);
		// MyLogger.info(jobsBasedOnAvailTime.get(0).CurrAvailTime + ","
		// + jobsBasedOnAvailTime.get(0).absdeadline);
		return JobList;
	}

	public static void main(String args[]) throws Exception {
		// TaskSet.getInstance().Display();
		MyLogger.setUseParentHandlers(false);
		TaskSet.getInstance().SystemUtilization();
		double HyperPeriod = -1;
		if (Configuration.HyperPeriod == -1) {
			HyperPeriod = TaskSet.getInstance().HyperPeriod();
		} else {
			HyperPeriod = Configuration.HyperPeriod;
		}
		try {
			// shedulingLog = new FileHandler("sheduling.log");
		} catch (Exception e) {
			e.printStackTrace();
		}

		MyLogger.info("HyperPeriod: " + HyperPeriod);
		JobSet Jobset = JobSet.getInstance(HyperPeriod);

		Jobset.Sort();
		// for (JobTuple job : Jobset.Jobset) {
		// MyLogger.info(job.suspends.toString());
		// }

		// java.util.logging.ConsoleHandler.level = OFF;

		Scheduler.Q = JobSet.getInstance().queue();
		MyLogger.info("Jobs::" + Q.toString());
		Processor.getAllProcessors(Configuration.NoOfProcessors);
		Scheduler s = new Scheduler(HyperPeriod);
		Jobset.Print();
		// s.getNextAvailableJobs(10);
		s.scheduler(Configuration.NoOfProcessors);
		Jobset.Print();
		Graph.Draw(Jobset);
		Jobset.Print();
	}
}
