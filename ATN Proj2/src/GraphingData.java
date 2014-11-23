import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GraphingData extends JPanel {
	static float[] data;
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
		// Draw labels.
		Font font = g2.getFont();
		FontRenderContext frc = g2.getFontRenderContext();
		LineMetrics lm = font.getLineMetrics("0", frc);
		float sh = lm.getAscent() + lm.getDescent();
		// Ordinate label.
		String s = "Network Reliability";
		float sy = PAD + ((h - 2 * PAD) - s.length() * sh) / 2 + lm.getAscent();
		for (int i = 0; i < s.length(); i++) {
			String letter = String.valueOf(s.charAt(i));
			float sw = (float) font.getStringBounds(letter, frc).getWidth();
			float sx = (PAD - sw) / 2;
			g2.drawString(letter, sx, sy);
			sy += sh;
		}
		// Abcissa label.
		s = "P=0.95  and K- 0 to 99 increments by 1";
		sy = h - PAD + (PAD - sh) / 2 + lm.getAscent();
		float sw = (float) font.getStringBounds(s, frc).getWidth();
		float sx = (w - sw) / 2;
		g2.drawString(s, sx, sy);
		// Draw lines.
		double xInc = (double) (w - 2 * PAD) / (data.length - 1);
		double scale = (double) (h - 2 * PAD) / getMax();
		g2.setPaint(Color.blue.darker());
		for (int i = 0; i < data.length - 1; i++) {
			double x1 = PAD + i * xInc;
			double y1 = h - PAD - scale * data[i];
			double x2 = PAD + (i + 1) * xInc;
			double y2 = h - PAD - scale * data[i + 1];
			g2.draw(new Line2D.Double(x1, y1, x2, y2));
		}
		// Mark data points.
		g2.setPaint(Color.blue.darker());
		for (int i = 0; i < data.length; i++) {
			double x = PAD + i * xInc;
			double y = h - PAD - scale * data[i];
			g2.fill(new Ellipse2D.Double(x - 2, y - 2, 4, 4));
		}
	}

	private float getMax() {
		float max = -Integer.MAX_VALUE;
		for (int i = 0; i < data.length; i++) {
			if (data[i] > max)
				max = data[i];
		}
		return max;
	}

	// public static void graphing(float[] args) {
	// JFrame f = new JFrame();
	// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// f.add(new GraphingData());
	// f.setSize(600, 600);
	// f.setLocation(200, 200);
	// f.setVisible(true);
	// }

	public static void graphing(float ar[]) {
		data = ar;
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new GraphingData());
		f.setSize(600, 600);
		f.setLocation(200, 200);
		f.setVisible(true);
	}
}