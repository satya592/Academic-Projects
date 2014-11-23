import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PlotDemo extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	private XYSeries plotSeries1, plotSeries2;
	private XYPlot plot;

	public PlotDemo() {
		XYSeriesCollection data = new XYSeriesCollection();

		plotSeries1 = new XYSeries("series 1");
		plotSeries2 = new XYSeries("series 2");
		data.addSeries(plotSeries1);
		data.addSeries(plotSeries2);

		JFreeChart chart = ChartFactory.createXYLineChart("", "", "", data,
				PlotOrientation.VERTICAL, true, true, false);
		plot = chart.getXYPlot();
		refreshRenderer();

		ValueAxis domain = new NumberAxis(), range = new NumberAxis();
		plot.setDomainAxis(domain);
		plot.setRangeAxis(range);

		ChartPanel phnPlotPanel = new ChartPanel(chart);
		add(phnPlotPanel, BorderLayout.CENTER);

		setVisible(true);
		setSize(new Dimension(400, 400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void refreshRenderer() {
		final Locale loc = new Locale("Deutsch", "Schweiz");

		APXYLineAndShapeRenderer r = new APXYLineAndShapeRenderer(true, false);
		r.setSeriesStroke(0, new BasicStroke(1f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));
		r.setSeriesShape(0, new Rectangle(0, 0));
		r.setSeriesPaint(0, Color.red);
		r.setSeriesStroke(1, new BasicStroke(1f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));
		r.setSeriesShape(1, new Rectangle(0, 0));
		r.setSeriesPaint(1, Color.blue);

		XYToolTipGenerator tt1 = new XYToolTipGenerator() {
			public String generateToolTip(XYDataset dataset, int series,
					int item) {
				StringBuffer sb = new StringBuffer();
				Number x = dataset.getX(series, item);
				Number y = dataset.getY(series, item);
				String htmlStr = "<html><p style='color:#ff0000;'>Series 1:</p>"
						+ String.format(loc, "%.2fHz <br />", x.doubleValue())
						+ String.format(loc, "%.2fdBm</html>", y.doubleValue());
				sb.append(htmlStr);
				return sb.toString();
			}
		};
		XYToolTipGenerator tt2 = new XYToolTipGenerator() {
			public String generateToolTip(XYDataset dataset, int series,
					int item) {
				StringBuffer sb = new StringBuffer();
				Number x = dataset.getX(series, item);
				Number y = dataset.getY(series, item);
				String htmlStr = "<html><p style='color:#0000ff;'>Series 2:</p>"
						+ String.format(loc, "%.2fHz <br />", x.doubleValue())
						+ String.format(loc, "%.2fdBm</html>", y.doubleValue());
				sb.append(htmlStr);
				return sb.toString();
			}
		};
		r.setSeriesToolTipGenerator(0, tt1);
		r.setSeriesToolTipGenerator(1, tt2);
		plot.setRenderer(r);

		UIManager.put("ToolTip.background", new Color(0.9f, 0.9f, 0.9f));
		UIManager.put("ToolTip.font", null);
	}

	public static void main(String[] args) {
		PlotDemo main = new PlotDemo();
		new Thread(main).start();
	}

	@Override
	public void run() {
		for (int i = 0; i < 500; i++)
			plotSeries1.add(i, Math.sin(i / 70.0), false);
		plotSeries1.fireSeriesChanged();
		for (int i = 0; i < 500; i++)
			plotSeries2.add(i, Math.sin(i / 30.0), false);
		plotSeries2.fireSeriesChanged();
		refreshRenderer();
	}
}