enum SchedulingAlgorithm {
	EDF, RM, DM;
}

public class Configuration {
	// Parameters for Scheduling
	public static int NoOfProcessors = 2;
	public static int HyperPeriod = -1;
	public static SchedulingAlgorithm TypeOfScheduliing = SchedulingAlgorithm.EDF; // P-Edf,P-Rm,Edf,Rm
	public static double TickSize = 1;

	// Parameters for Task-Set Generations
	public static int TasksInTestTaskSet = 10;
	public static int MinPeriodInTestTaskSet = 5;
	public static int MaxPeriodInTestTaskSet = 10;
	public static int MaxSuspensions = 3;
	public static double LowerBoundOfSuspension = 1;
	public static double UpperBoundOfSuspension = 2;
	public static double TaskSetUtilization = 1.2;
}
