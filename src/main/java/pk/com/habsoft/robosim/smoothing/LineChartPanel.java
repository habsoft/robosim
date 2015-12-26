package pk.com.habsoft.robosim.smoothing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChartPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private String xLabel, yLabel;
	ChartPanel chartPanel = null;
	List<ChartData> dataList = new ArrayList<ChartData>();

	private class ChartData {
		String lable;
		double[][] data;

		public ChartData(String lable, double[][] data) {
			this.data = data;
			this.lable = lable;
		}

		public String getLable() {
			return lable;
		}

		public double[][] getData() {
			return data;
		}

	}

	public LineChartPanel(String xLabel, String yLabel) {
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		chartPanel = new ChartPanel(null);
		add(chartPanel);
	}

	public LineChartPanel(int width, int height, String xLabel, String yLabel) {
		this.xLabel = xLabel;
		this.yLabel = yLabel;

		super.setSize(width, height);
		chartPanel = new ChartPanel(null);
		add(chartPanel);
	}

	public void clearData() {
		dataList.clear();
		updateChart();
	}

	public void addData(String lable, double[][] data) {
		dataList.add(new ChartData(lable, data));
		updateChart();
	}

	private void updateChart() {
		XYDataset dataset = createPositionDataset();
		JFreeChart chart = createChart(dataset);
		chartPanel.setChart(chart);
	}

	@Override
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		chartPanel.setLocation(x, y);
	}

	@Override
	public void setSize(int width, int height) {
		// super.setPreferredSize(new java.awt.Dimension(width - 10, height -
		// 10));
		chartPanel.setPreferredSize(new java.awt.Dimension(width - 10, height - 10));
	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return a sample dataset.
	 */
	private XYDataset createPositionDataset() {

		final XYSeriesCollection dataset = new XYSeriesCollection();

		for (int k = 0; k < dataList.size(); k++) {
			ChartData data = dataList.get(k);
			final XYSeries series1 = new XYSeries(data.getLable());
			for (int i = 0; i < data.getData().length; i++) {
				series1.add(data.getData()[i][0], data.getData()[i][1]);
			}
			dataset.addSeries(series1);
		}

		return dataset;

	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            the data for the chart.
	 * 
	 * @return a chart.
	 */
	private JFreeChart createChart(final XYDataset dataset) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createXYLineChart("", // chart
																	// title
				xLabel, // x axis label
				yLabel, // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
		);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		plot.setRenderer(renderer);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;

	}

}
