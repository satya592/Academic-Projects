import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class XYChartExample {
	public static void main(String[] args) {
		// Create a simple XY chart
		XYSeries series = new XYSeries("JobScheduled");
		XYSeries missed = new XYSeries("JobMissed");
		// XYSeries missed = new XYSeries("JobMissed");

		series.add(1, 1);
		series.add(1, 2);
		series.add(2, 2);
		series.add(2, 1);

		series.add(1, 1);

		missed.add(2, 2);
		missed.add(2, 3);
		// series.add(4, 10);
		// Add the series to your data set
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		dataset.addSeries(missed);
		// Generate the graph
		JFreeChart chart = ChartFactory.createXYLineChart("Jobs Scheduling", // Title
				"Time", // x-axis Label
				"Processors", // y-axis Label
				dataset, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
				);
		// chart.draw(g2, area);
		// ChartFactory.
		try {
			ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, 500,
					300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
			e.printStackTrace();
		}
	}
}