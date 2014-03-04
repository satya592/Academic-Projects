public class Clock implements Runnable {
	double HyperPeriod;

	private static Clock instance = null;

	protected Clock(double HyperPeriod) {
		this.HyperPeriod = HyperPeriod;
	}

	public static Clock getInstance(double HyperPeriod) {
		if (instance == null) {
			instance = new Clock(HyperPeriod);
		}
		return instance;
	}

	void updateClock() {
		try {
			Thread.sleep(5); // sleep for 1 mill second
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Processor.processorClock++;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (Processor.processorClock < HyperPeriod)
			updateClock();
	}
}