import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

//JScrolPane
public class SampleJob extends JScrollPane {
	int[] data = { 21, 14, 18, 03, 86, 88, 74, 87, 54, 77, 61, 55, 48, 60, 49,
			36, 38, 27, 20, 18 };
	final int PAD = 20;

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int w = getWidth();
		int h = getHeight();
		// Draw ordinate.
		g2.draw(new Line2D.Double(PAD, PAD, PAD, h - PAD));
		// Draw abcissa.
		g2.draw(new Line2D.Double(PAD, h - PAD, w - PAD, h - PAD));
		double xInc = (double) (w - 2 * PAD) / (data.length - 1);
		double scale = (double) (h - 2 * PAD) / getMax();
		// Mark data points.
		g2.setPaint(Color.red);
		String job = "T1,1";
		char[] chr = job.toCharArray();
		g2.setColor(Color.BLACK);
		g2.drawChars(chr, 0, chr.length, 0, h - PAD);
		int height = 5;
		double x = PAD + xInc * 5;
		double y = h - PAD;
		g2.setColor(Color.BLUE);
		g2.drawRect(0 + PAD, (int) y, 10 + PAD, height);
		g2.setColor(Color.YELLOW);
		g2.fillRect(0 + PAD, (int) y, 10 + PAD, height);

		g2.setColor(Color.YELLOW);
		g2.drawLine(0 + PAD, (int) y, 0 + PAD, height);
		g2.setColor(Color.BLUE);
		g2.drawLine(0 + PAD, height, 10 + PAD, height);
		g2.setColor(Color.GREEN);
		g2.drawLine(10 + PAD, (int) y, 10 + PAD, height);

	}

	private int getMax() {
		int max = -Integer.MAX_VALUE;
		for (int i = 0; i < data.length; i++) {
			if (data[i] > max)
				max = data[i];
		}
		return max;
	}

	public static void main(String args[]) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel test = new JPanel();
		test.setSize(800, 800);
		test.add(new SampleJob());
		f.add(test);
		f.setSize(800, 800);

		f.setLocation(200, 200);
		f.setVisible(true);

	}
}