import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

enum JobColor {
	Hit, Miss, Running;
}

/** @see http://stackoverflow.com/a/14459322/230513 */
public class Graph {

	private static Graph instance[] = null;

	int GraphNo;

	public static Graph[] getAllGraphs(int noOfProc) {
		if (instance == null) {
			instance = new Graph[noOfProc];
			// set Graph
			for (int i = 1; i <= noOfProc; i++) {
				instance[i - 1] = new Graph();
				instance[i - 1].GraphNo = i;
			}
		}
		return instance;
	}

	public static Graph getGraph(int procNo) {
		if (instance != null) {
			return instance[procNo - 1];
		} else {
			return null;
		}
	}

	private static JFreeChart createChart(final XYDataset dataset,
			JobColor jColor) {
		NumberAxis domain = new NumberAxis("Time");
		NumberAxis range = new NumberAxis("Processors");
		domain.setAutoRangeIncludesZero(false);
		APXYLineAndShapeRenderer renderer = new APXYLineAndShapeRenderer(true,
				false);
		String key = null;
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			key = (String) dataset.getSeriesKey(i);
			// System.out.println(key);

			if (key.startsWith("Release")) {
				// renderer.setSeriesPaint(i, Color.YELLOW.darker());
			} else if (key.startsWith("Started") || key.startsWith("Running")
					|| key.startsWith("Preempted")) {
				// renderer.setSeriesPaint(i, Color.BLUE.darker());
			} else if (key.startsWith("Stoped")) {
				// renderer.setSeriesPaint(i, Color.RED.darker());
			} else if (key.startsWith("Completed")) {
				// renderer.setSeriesPaint(i, Color.GREEN.darker());
			} else if (key.startsWith("Processor")) {
				// renderer.setSeriesPaint(i, Color.BLACK.darker());
			}
		}
		// renderer.setBaseItemLabelGenerator(new LabelGenerator());
		renderer.setBaseItemLabelPaint(Color.BLACK.darker());
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.TOP_LEFT));
		renderer.setBaseItemLabelFont(renderer.getBaseItemLabelFont()
				.deriveFont(14f));
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		XYPlot plot = new XYPlot(dataset, domain, range, renderer);
		JFreeChart chart = new JFreeChart("SiMS",
				JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		return chart;
	}

	// public void DrawGraph() {
	public static void Draw(JobSet allJobs) {
		System.out.println("Creating Graph ...");

		JFrame f = new JFrame();

		double starts = -1;
		double ends = -1;
		double procs = -1;

		JobSet alljobs = JobSet.getInstance();
		int totalJobs = alljobs.Jobset.length;

		double PAD = 1;
		double height = 0.5;
		XYSeries temp = null;
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		XYSeriesCollection datasets = new XYSeriesCollection();
		JobTuple job = null;
		int lineNo = 0;

		for (int i = 0; i < totalJobs; i++) {
			job = alljobs.Jobset[i];

			temp = new XYSeries("Relased:T" + job.taskNo + "," + job.jobIndex);
			temp.add(job.availTime, 1 * PAD);
			temp.add(job.availTime, 1 * PAD + height);
			temp.setDescription("release");
			datasets.addSeries(temp);

			Double key, value;

			int size = job.StartnStop.size();

			if (size == 0) {
				continue;
			} else {
				int index = 0;
				Integer p[] = new Integer[job.processorNo.size()];
				job.processorNo.toArray(p);
				for (Entry<Double, Double> entry : job.StartnStop.entrySet()) {
					lineNo++;
					key = entry.getKey();
					value = entry.getValue();
					starts = Math.floor(key * 100) / 100;
					ends = Math.floor(value * 100) / 100;
					procs = p[index] - 1;

					temp = new XYSeries("Started:T" + job.taskNo + ","
							+ job.jobIndex + "." + index);
					temp.add(starts, procs * PAD);
					temp.add(starts, procs * PAD + height);
					temp.setDescription("started");
					datasets.addSeries(temp);

					temp = new XYSeries("Running:T" + job.taskNo + ","
							+ job.jobIndex + "." + index);
					temp.add(starts, procs * PAD + height);
					temp.add(0.999999999 * ends, procs * PAD + height);
					temp.setDescription("running");
					datasets.addSeries(temp);

					if (job.execBalance <= 0) {
						if (job.StartnStop.lastEntry().getValue() == ends
								&& ends <= job.absdeadline) {

							temp = new XYSeries("Completed:T" + job.taskNo
									+ "," + job.jobIndex + "." + index);
						} else {
							temp = new XYSeries("Stoped:T" + job.taskNo + ","
									+ job.jobIndex + "." + index);
						}
					} else {
						temp = new XYSeries("Preempted:T" + job.taskNo + ","
								+ job.jobIndex + "." + index);
					}
					temp.add(0.999999999 * ends, procs * PAD + height);
					temp.add(0.999999999 * ends, procs * PAD);
					temp.setDescription("stopped");
					datasets.addSeries(temp);

					index++;
				}
			}

		}

		for (int i = 0; i < Configuration.NoOfProcessors; i++) {
			temp = new XYSeries("Processor " + (i + 1));
			temp.add(0, i * PAD);
			temp.add(alljobs.HyperPeriod, i * PAD);
			temp.setDescription("Processor " + (i + 1));
			datasets.addSeries(temp);
		}

		System.out.println("Total no of start and stops of jobs:" + lineNo / 3);

		JFreeChart chart = createChart(datasets, JobColor.Running);
		ChartPanel chartPanel = new ChartPanel(chart) {

			private static final long serialVersionUID = 1L;

			public Dimension getPreferredSize() {
				return new Dimension(640, 480);
			}
		};
		f.add(chartPanel);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setLocationByPlatform(true);

		try {
			JPanel panel = new JPanel();
			JLabel label = new JLabel(
					"Press SaveAs to save the Graph or Press View to View the Graph");
			panel.add(label);
			String[] options = new String[] { "SaveAs", "View" };
			int option = JOptionPane.showOptionDialog(null, panel,
					"Make your choice", JOptionPane.NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[1]);

			if (option == 1) {
				f.setVisible(true);
			} else if (option == 0) {
				chartPanel.setSize(chartPanel.getMaximumDrawWidth(),
						chartPanel.getMaximumDrawHeight());
				chartPanel.doSaveAs();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
}