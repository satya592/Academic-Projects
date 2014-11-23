import java.util.ArrayList;
import java.util.List;

public class Path {
	private int source;
	private int dest;
	private List<Integer> shortestPath = new ArrayList<Integer>();

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getDest() {
		return dest;
	}

	public void setDest(int dest) {
		this.dest = dest;
	}

	public List<Integer> getShortestPath() {
		return shortestPath;
	}

	public void setShortestPath(List<Integer> shortestPath) {
		this.shortestPath = shortestPath;
	}

	public int compare(int s, int d) {
		int size = this.shortestPath.size();
		for (int i = 0; i < size - 1; i++) {
			if (this.shortestPath.get(i) == s
					&& this.shortestPath.get(i + 1) == d)
				return 1;
		}
		return 0;
	}
}