public class Proj2 {

	int n = 5; //
	int paths = 1024;// 024; // 2 power 10
	int edges = 10;
	Graph graph[];

	Proj2() {
		graph = new Graph[paths];
		for (int i = 0; i < paths; i++) {
			// create the graph and set the edges to 1
			graph[i] = new Graph(n);
			// Generate permutations of paths
			intToGraph(graph[i], i);
			// check if it is connected or not
			graph[i].checkConnected();
		}
	}

	void intToGraph(Graph g, int value) {
		System.out.print(value + "=");
		if ((value & 1) == 1) {
			System.out.print(1);
			g.setWeight(0, 1, 1);
			g.setWeight(1, 0, 1);
		}
		if ((value & 2) == 2) {
			System.out.print(2);
			g.setWeight(0, 2, 1);
			g.setWeight(2, 0, 1);
		}
		if ((value & 4) == 4) {
			System.out.print(3);
			g.setWeight(0, 3, 1);
			g.setWeight(3, 0, 1);
		}
		if ((value & 8) == 8) {
			System.out.print(4);
			g.setWeight(0, 4, 1);
			g.setWeight(4, 0, 1);
		}
		if ((value & 16) == 16) {
			System.out.print(5);
			g.setWeight(1, 2, 1);
			g.setWeight(2, 1, 1);
		}
		if ((value & 32) == 32) {
			System.out.print(6);
			g.setWeight(1, 3, 1);
			g.setWeight(3, 1, 1);
		}
		if ((value & 64) == 64) {
			System.out.print(7);
			g.setWeight(1, 4, 1);
			g.setWeight(4, 1, 1);
		}
		if ((value & 128) == 128) {
			System.out.print(8);
			g.setWeight(2, 3, 1);
			g.setWeight(3, 2, 1);
		}
		if ((value & 256) == 256) {
			g.setWeight(2, 4, 1);
			g.setWeight(4, 2, 1);
			System.out.print(9);
		}
		if ((value & 512) == 512) {
			g.setWeight(3, 4, 1);
			g.setWeight(4, 3, 1);
			System.out.print(10);
		}

	}

	float networkReliablity(float p) {
		float rel = 0;
		for (int i = 0; i < paths; i++) {
			if (graph[i].isConnected()) {// if connected
				// System.out.println("Conneted.." + i);
				rel += graphReliability(graph[i], p);
			} else {
				// System.out.println("Not Conneted.." + i);
			}
		}
		return rel;
	}

	float graphReliability(Graph g, float p) {
		float rel = 1;
		for (int j = 0; j < n; j++) {
			for (int k = 0; k < n; k++) {
				if (j < k) // only lower matrix
				{
					if (g.getWeight(j, k) == 1)
						rel *= p;
					else
						rel *= (1 - p);
				}
			}
		}
		// System.out.print(rel + "+");
		return rel;
	}

	float[] generateNW() {
		// range from 0 - 1
		float g[] = new float[101];
		int i = 0;
		for (float p = 0; p <= 1; i++, p = (float) (i / 100.0)) {
			System.out.print(p + " = ");
			g[i] = networkReliablity(p);
			System.out.println(g[i]);
		}
		return g;
	}

	void count() {
		int count = 0;
		for (int i = 0; i < 1024; i++) {
			if (graph[i].connected)
				count++;
		}
		System.out.println("count: " + count);
	}

	public static void main(String args[]) {
		// UndirectedConnectivityBFS.permute("0101010101");
		Proj2 p = new Proj2();
		float out[] = p.generateNW();
		p.count();
		// GraphingData.graphing(out);
		ReliabilityAlteration r = new ReliabilityAlteration();
		r.alteration();
		GraphingData.graphing(r.newRel);

	}

}
