package pk.com.habsoft.robosim.filters.kalman;

import java.awt.Color;
import java.awt.GradientPaint;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChartPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private double[] measurement_error;
	private double[] kalman_error;
	private String xLabel, yLabel;
	private String mLabel, kLabel;
	ChartPanel chartPanel = null;

	public BarChartPanel(String xLabel, String yLabel, String mLabel, String kLabel) {
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.mLabel = mLabel;
		this.kLabel = kLabel;

		chartPanel = new ChartPanel(null);
		add(chartPanel);
	}

	public void setData(double[] measurement_error, double[] kalman_error) {
		this.measurement_error = measurement_error;
		this.kalman_error = kalman_error;
		updateChart();
	}

	private void updateChart() {
		// First Panel
		CategoryDataset dataset = createPositionDataset();
		JFreeChart chart = createChart(dataset);
		chartPanel.setChart(chart);
	}

	@Override
	public void setSize(int width, int height) {
		// setPreferredSize(new java.awt.Dimension(width, height));
		chartPanel.setPreferredSize(new java.awt.Dimension(width - 10, height - 10));
	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return a sample dataset.
	 */
	private CategoryDataset createPositionDataset() {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (int i = 0; i < measurement_error.length; i++) {
			dataset.addValue(measurement_error[i], mLabel, String.valueOf(i));
		}

		for (int i = 0; i < kalman_error.length; i++) {
			dataset.addValue(kalman_error[i], kLabel, String.valueOf(i));
		}

		return dataset;

	}

	/**
	 * Creates a sample chart.
	 * 
	 * @param dataset
	 *            the dataset.
	 * 
	 * @return The chart.
	 */
	private JFreeChart createChart(CategoryDataset dataset) {

		// create the chart...
		JFreeChart chart = ChartFactory.createBarChart("", // chart
															// title
				xLabel, // domain axis label
				yLabel, // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips?
				false // URLs?
		);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

		// set the background color for the chart...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);

		// set the range axis to display integers only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// disable bar outlines...
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);

		// set up gradient paints for series...
		GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f, 0.0f, new Color(64, 0, 0));
		GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f, 0.0f, new Color(0, 64, 0));
		renderer.setSeriesPaint(0, gp0);
		renderer.setSeriesPaint(1, gp1);

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2.0));
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;

	}
}
