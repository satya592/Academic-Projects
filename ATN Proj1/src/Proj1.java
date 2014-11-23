import java.util.ArrayList;
import java.util.Random;

class Proj1 {
	public final static int No_Nodes = 40;
	static int[][] Tot_Cost = new int[No_Nodes][No_Nodes];

	// final static int K = 3;

	public static void main(String args[]) {
		int K;
		final Graph graph = new Graph(No_Nodes); // number of nodes as
													// parameter.
		int Cost[][] = new int[No_Nodes][No_Nodes];
		Path[] path = new Path[No_Nodes * No_Nodes];
		Random rand = new Random();

		// printing traffic demand adjacency matrix.
		System.out.println("Traffic demand adjacency matrix:.....");
		for (int i = 0; i < No_Nodes; i++) {
			for (int j = 0; j < No_Nodes; j++) {
				System.out.print(" " + Cost[i][j]);
			}
			System.out.println("");
		}

		for (K = 3; K <= 15; K++) {
			for (int i = 0; i < No_Nodes; i++) {
				for (int j = 0; j < No_Nodes; j++) {
					if (i == j)
						Cost[i][j] = 0; // no self loop
					else {
						Cost[i][j] = rand.nextInt(4); // traffic demand in
														// the range [0-3]
					}
				}
			}

			// printing traffic demand adjacency matrix.
			System.out.println("Demand Traffic:");
			for (int i = 0; i < No_Nodes; i++) {
				for (int j = 0; j < No_Nodes; j++) {
					System.out.print(" " + Cost[i][j]);
				}
				System.out.println("");
			}

			System.out.println("");
			System.out.println("");

			for (int i = 0; i < No_Nodes; i++)
				graph.setLabel(i, i);

			for (int k = 0; k < No_Nodes; k++) {
				ArrayList<Integer> numbers = RandomNumber.rNum();
				int temp = 0;
				for (int i = 0; i < numbers.size(); i++) {
					int pick = numbers.get(i);
					if (k != pick) {
						if ((graph.getWeight(k, pick) != 1 && temp < K)
								|| (graph.getWeight(k, pick) == 1)) {
							graph.addEdge(k, pick, 1);
						} else {
							graph.addEdge(k, pick, 300);
						}
						temp++;
					}
				}
			}

			// printing weight matrix
			System.out.println("Weighted :");
			for (int i = 0; i < No_Nodes; i++) {
				for (int j = 0; j < No_Nodes; j++) {
					System.out.print(" " + graph.getWeight(i, j));
				}
				System.out.println();
			}

			System.out.println("");
			System.out.println("");

			int l = 0;
			for (int start = 0; start < No_Nodes; start++) {
				final int[] pred = DijkstraAlgorithm.dijkstra(graph, start);
				for (int n = 0; n < No_Nodes; n++) {
					if (start != n) {
						path[l] = (Path) DijkstraAlgorithm.printPath(graph,
								pred, start, n);
						path[l].setSource(start);
						int size_path = path[l].getShortestPath().size();
						int dest = path[l].getShortestPath().get(size_path - 1);
						path[l].setDest(dest);
						l++;
					}
				}
			}

			// Tot_Cost matrix
			int ret, src, des, temp, fl;
			for (int i = 0; i < No_Nodes; i++) {
				for (int k = 0; k < No_Nodes; k++) {
					if (i != k) {
						temp = 0;
						for (int m = 0; m < l; m++) {
							ret = path[m].compare(i, k);
							if (ret == 1) {
								src = path[m].getSource();
								des = path[m].getDest();
								fl = Cost[src][des];
								temp = temp + fl;
							}
						}
						Tot_Cost[i][k] = temp;
					}
				}
			}

			// printing Tot_Cost matrix
			System.out.println("Cost:");
			for (int u = 0; u < No_Nodes; u++) {
				for (int v = 0; v < No_Nodes; v++) {
					System.out.print(" " + Tot_Cost[u][v]);
				}
				System.out.println(";");
			}

			int temp_cost = 0;
			for (int i = 0; i < No_Nodes; i++) {
				for (int j = i; j < No_Nodes; j++) {
					temp_cost = temp_cost + Tot_Cost[i][j] * graph.edges[i][j];
				}
			}
			System.out.println("For k = " + K
					+ " the total Cost of the Network:" + temp_cost);
			VisualRep x = new VisualRep(Tot_Cost);

		}
	}

}