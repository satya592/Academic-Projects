import java.util.ArrayList;
import java.util.List;

public class DijkstraAlgorithm {
	// Dijkstra's algorithm to find shortest path from source to all other nodes
	public static int[] dijkstra(Graph G, int s) {
		final int[] distance = new int[G.size()]; // shortest known distance from source
		final int[] pred = new int[G.size()]; // Preceding nodes in path
		final boolean[] visited = new boolean[G.size()]; // all false initially
		for (int i = 0; i < distance.length; i++) {
			distance[i] = Integer.MAX_VALUE;
		}
		distance[s] = 0;
		for (int i = 0; i < distance.length; i++) {
			final int next = minVertex(distance, visited);
			visited[next] = true;
			final int[] n = G.neighbors(next);
			for (int j = 0; j < n.length; j++) {
				final int v = n[j];
				final int d = distance[next] + G.getWeight(next, v);
				if (distance[v] > d) {
					distance[v] = d;
					pred[v] = next;
				}
			}
		}
		return pred; 
	}

	private static int minVertex(int[] distance, boolean[] v) {
		int x = Integer.MAX_VALUE;
		int y = -1; // graph not connected, or no unvisited vertices
		for (int i = 0; i < distance.length; i++) {
			if (!v[i] && distance[i] < x) {
				y = i;
				x = distance[i];
			}
		}
		return y;
	}

	public static Path printPath(Graph G, int[] pred, int s, int e) {
		final List<Integer> path = new ArrayList<Integer>();
		Path p = new Path();
		int x = e;
		while (x != s) {
			path.add(0, G.getLabel(x));
			x = pred[x];
		}
		path.add(0, G.getLabel(s));
		p.setShortestPath(path);
		return p;
	}
}