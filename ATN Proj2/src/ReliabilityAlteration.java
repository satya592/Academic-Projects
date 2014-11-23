import java.util.ArrayList;

public class ReliabilityAlteration {
	public float newRel[] = new float[100];

	public void alteration() {

		float p = (float) 0.95;
		Proj2 graphFull = new Proj2();
		float actualRel = graphFull.networkReliablity(p);

		float total = 0;
		float[] avg = new float[1000];
		for (int k = 0; k < 100; k++) {
			for (int i = 0; i < 1000; i++) {
				total = actualRel;
				ArrayList<Integer> numbers = RandomNumber.rNum();
				for (int c = 0; c < numbers.size() && c <= k; c++) {
					Graph grh = graphFull.graph[numbers.get(c)];
					float graphRel = graphFull.graphReliability(grh, p);
					if (grh.isConnected()) {
						total = total - (graphRel); // subtracting 2 times is
													// wrong. Because in the
													// actual you just add once
													// so you delete once. You
													// need not delete it again
													// because we will not
													// subtract if the nw is
													// disconnected.
					} else if (!grh.isConnected()) {
						total = total + (graphRel);
					} else {
						System.out.println("You shouldnt reach here.");
					}
				}
				avg[i] = total;
			}
			float sum = 0;
			// calculate average of values
			for (int i = 0; i < avg.length; i++)
				sum = sum + avg[i];
			float average = sum / avg.length;

			newRel[k] = average;
		}
		System.out.println();
		System.out.println("The new reliability values are:");
		for (int i = 0; i < newRel.length; i++) {
			System.out.println("Average network reliability if k = " + i
					+ " : " + newRel[i]);
		}
	}
}