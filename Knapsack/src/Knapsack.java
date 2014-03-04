import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Queue;
import java.util.StringTokenizer;

public class Knapsack {

	int weights[] = null;
	int size = -1;
	AVLTree<Integer, Integer> uniqueWeights;

	// ArrayList<Integer> Results = new

	public static void main(String[] args) throws Exception {
		Knapsack k = new Knapsack();
		k.display();
	}

	//
	Knapsack() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("input.txt"));
		System.out.println("Started executing");
		this.uniqueWeights = new AVLTree<Integer, Integer>();
		int n = 0;
		int i = 0;
		while (true) {
			i = 0;
			String line = br.readLine();
			n = Integer.parseInt(line);
			uniqueWeights = new AVLTree<Integer, Integer>();
			if (n == 0)
				break;
			else {
				weights = new int[n]; //No of weights given
				line = br.readLine();
				StringTokenizer stkn = new StringTokenizer(line);
				while (stkn.hasMoreTokens()) {
					String str = stkn.nextToken();
					int wt = Integer.parseInt(str);
					weights[i] = wt;
					i++;
				}
			}
			R(n); // Find the Unique weights
			print(); // Print them into the file
		}//Read until zero encountered
		br.close(); // close the input file
	}

	void R(int n) {

		if (n == 0) {
			return;
		} else {// R(n-1) + this.weights[n-1]  R(n-1) - this.weights[n-1];
			
			R(n - 1);
			
			int Temp;
			AVLTree<Integer, Integer> avl = new AVLTree<Integer, Integer>();
			Queue<AVLNode<Integer, Integer>> queue = this.uniqueWeights
					.preOrder(this.uniqueWeights.root);
			
			while (!queue.isEmpty()) {
				AVLNode<Integer, Integer> current = queue.poll();

				Temp = current.value + weights[n - 1];
				avl.Insert(Temp, Temp);

				Temp = current.value - weights[n - 1];
				if (Temp == 0) continue;
				if (Temp < 0) 	Temp *= -1;
				avl.Insert(Temp, Temp);
			}
			// insert avl into uniqueweights and A[n-1]
			Queue<AVLNode<Integer, Integer>> que = avl.preOrder(avl.root);

			for (AVLNode<Integer, Integer> current : que) {
				uniqueWeights.Insert(current.key / 50, current.value);
			}
			uniqueWeights.Insert(this.weights[n - 1] / 50, this.weights[n - 1]);
		}
	}

	void print() {
		Queue<AVLNode<Integer, Integer>> q = this.uniqueWeights
				.preOrder(this.uniqueWeights.root);
		int count = q.size();
		System.out.println(count); // discard zero
		try {
			File file = new File("uniqueWeights.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("\n--------------------------------------------------------------------------------------------------------\n");
			bw.write("Total no of Unique Weights:" + count + "\n");
			while (!q.isEmpty()) {
				AVLNode<Integer, Integer> current = q.poll();
					// System.out.print(current.value+","+"    ");
					bw.write(current.value + ",");
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void display() {
		System.out.println("THE END");
	}
}
