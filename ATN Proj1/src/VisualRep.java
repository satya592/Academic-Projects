import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class VisualRep extends JPanel {
	int[] data = { 21, 50, 18, 0, 86, 40, 74, 87, 30, 77, 20, 55, 80, 60, 10,
			36, 58, 27, 80, 18, 25, 55, 35, 73, 82, 49, 72, 89, 34, 74, 23, 50,
			82, 67, 12, 34, 51, 25, 8, 65, }; // 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,
												// 21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};
												// // this decides the number of
												// nodes on the screen
	int[] xAxis = new int[Proj1.No_Nodes];
	int[] yAxis = new int[Proj1.No_Nodes];
	int[][] fin_cost = new int[Proj1.No_Nodes][Proj1.No_Nodes];
	final int PAD = 40;

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int w = getWidth();
		int h = getHeight();
		double xInc = (double) (w - 2 * PAD) / (data.length - 1);
		double scale = (double) (h - 2 * PAD) / getMax();
		g2.setPaint(Color.BLACK);
		for (int i = 0; i < data.length; i++) {
			double x = PAD + i * xInc;
			double y = h - PAD - scale * data[i];
			g2.fill(new Ellipse2D.Double(x - 2, y - 2, 10, 10));
			String str = Integer.toString(i);
			g.drawString(str, (int) x - 4, (int) y - 4);
			xAxis[i] = (int) x;
			yAxis[i] = (int) y;
		}
		g2.setPaint(Color.BLUE);
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data.length; j++) {
				if (fin_cost[i][j] != 0)
					g.drawLine(xAxis[i], yAxis[i], xAxis[j], yAxis[j]);
			}
		}
	}

	private int getMax() {
		int max = -Integer.MAX_VALUE;
		for (int i = 0; i < data.length; i++) {
			if (data[i] > max)
				max = data[i];
		}
		return max;
	}

	VisualRep(int[][] fin_cost) {
		this.fin_cost = fin_cost;
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(this);
		f.setSize(1000, 1000);
		f.setLocation(20, 20);
		f.setVisible(true);
	}
}