package com.cvberry.berrypim;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/18/2016.
 */
public class ChartingUtil {

    public static PieDataset createPieDataset(List<String> keys, List<Double> values) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < keys.size(); i++) {
            dataset.setValue(keys.get(i), values.get(i));
        }
        return dataset;
    }

    public static PieDataset createPieDatasetFromArrays(String[] keys, Double[] values) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < keys.length; i++) {
            dataset.setValue(keys[i], values[i]);
        }
        return dataset;
    }

    public static CategoryDataset createCategoryDataset(List<? extends Number> values, List<String> category, List<String> topcat) {
        if (values == null || values.size() == 0) {
            throw new RuntimeException("non valid size for x!");
        }
        //otherwise fill with defaults if nonstandard
        if(category == null || category.size()==0) {
           category = createListFilledWithString("",values.size());
        } else if (values.size() != category.size()) {
            throw new RuntimeException("non valid size for categogory!");
        }
        if(topcat == null || topcat.size()==0) {
           topcat = createListFilledWithString("",values.size());
        } else if (values.size() != topcat.size()) {
            throw new RuntimeException("non valid size for topcat!");
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < values.size(); i++) {
            dataset.addValue(values.get(i), topcat.get(i), category.get(i));
        }
        return dataset;
    }

    public static List<Double> createListFilledWithNumber(Double o, int length) {
        List out = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            out.add(o);
        }
        return out;
    }

    public static List<String> createListFilledWithString(String o, int length) {
        List out = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            out.add(o);
        }
        return out;
    }

    public static XYZDataset createXYZDataset(List<? extends Number> x, List<? extends Number> y,
                                              List<? extends Number> z, List<String> categories) {

        DefaultXYZDataset dataset = new DefaultXYZDataset();
        Map<String,double[][]> xyzData = convertDataToXYZForm(x,y,z,categories);
        for (String key : xyzData.keySet()) {
            dataset.addSeries(key,xyzData.get(key));
        }

        return dataset;
    }

    public static Map<String,double[][]> convertDataToXYZForm(List<? extends Number> x, List<? extends Number> y,
    List<? extends Number> z, List<String> categories) {
        if (x == null || x.size() == 0) {
            throw new RuntimeException("non valid size for x!");
        }
        //otherwise fill with defaults if nonstandard
        if(y == null || y.size()==0) {
           y = createListFilledWithNumber(0.0,x.size());
        } else if (y.size() != x.size()) {
            throw new RuntimeException("non valid size for y!");
        }
        if(z == null || z.size()==0) {
           z = createListFilledWithNumber(1.0,x.size());
        } else if (z.size() != x.size()) {
            throw new RuntimeException("non valid size for z!");
        }
        if(categories == null || categories.size()==0) {
           categories = createListFilledWithString("",x.size());
        } else if (z.size() != x.size()) {
            throw new RuntimeException("non valid size for categories!");
        }

        Map<String,Integer> uniqueCats = new HashMap<>();
        for (String s : categories) {
            if(uniqueCats.containsKey(s)) {
                uniqueCats.put(s,uniqueCats.get(s)+1);
            } else {
                uniqueCats.put(s,1);
            }
        }

        Map<String,double[][]> out = new HashMap<>();
        for (String key : uniqueCats.keySet()) {
            out.put(key,new double[3][uniqueCats.get(key)]);
            uniqueCats.put(key,0);
        }

        for (int i = 0; i < x.size(); i++) {
            int currIndex = uniqueCats.get(categories.get(i));
            uniqueCats.put(categories.get(i),currIndex+1);
            out.get(categories.get(i))[0][currIndex] = (Double) x.get(i);
            out.get(categories.get(i))[1][currIndex] = (Double) y.get(i);
            out.get(categories.get(i))[2][currIndex] = (Double) z.get(i);
        }
        return out;
    }

    public static String createAndEnqueueLineChart(CategoryDataset dataset, String title, String rootPathStr,
                                                   boolean legend, boolean tooltips, int width, int height) {
        JFreeChart chart = ChartFactory.createLineChart(title, "x",
                "y", dataset,PlotOrientation.VERTICAL,legend,tooltips,false);
//     chart.addSubtitle(new TextTitle("Time to generate 1000 charts in SVG "
//                + "format (lower bars = better performance)"));
        chart.setBackgroundPaint(Color.white);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chart.getLegend().setFrame(BlockBorder.NONE);
        return enqueImageGetLink(chart,rootPathStr,width,height);
    }

       private static XYDataset createSampleDataset() {

        final XYSeries series1 = new XYSeries("First");
        series1.add(1.0, 1.0);
        series1.add(2.0, 4.0);
        series1.add(3.0, 3.0);
        series1.add(4.0, 5.0);
        series1.add(5.0, 5.0);
        series1.add(6.0, 7.0);
        series1.add(7.0, 7.0);
        series1.add(8.0, 8.0);

        final XYSeries series2 = new XYSeries("Second");
        series2.add(1.0, 5.0);
        series2.add(2.0, 7.0);
        series2.add(3.0, 6.0);
        series2.add(4.0, 8.0);
        series2.add(5.0, 4.0);
        series2.add(6.0, 4.0);
        series2.add(7.0, 2.0);
        series2.add(8.0, 1.0);

        final XYSeries series3 = new XYSeries("Third");
        series3.add(3.0, 4.0);
        series3.add(4.0, 3.0);
        series3.add(5.0, 2.0);
        series3.add(6.0, 3.0);
        series3.add(7.0, 6.0);
        series3.add(8.0, 3.0);
        series3.add(9.0, 4.0);
        series3.add(10.0, 3.0);

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);

        return dataset;
    }

    private static XYDataset createSampleDataset2() {

        DefaultXYZDataset ohboi = new DefaultXYZDataset();
        ohboi.addSeries("one",new double[][]{{1,2,3},{4,7,3},{1,1,1}});
        ohboi.addSeries("two",new double[][]{{1,2,3},{1,4,8},{1,1,1}});
        return ohboi;
    }

    public static String createAndEnqueueXYLineChart(XYDataset dataset, String title, String rootPathStr,
                                                   boolean legend, boolean tooltips, int width, int height) {
        JFreeChart chart = ChartFactory.createXYLineChart(title, "x", "y", dataset,PlotOrientation.VERTICAL,legend,
                tooltips,false);
        //chart.setBackgroundPaint(Color.white);
        return enqueImageGetLink(chart,rootPathStr,width,height);
    }

    public static String createAndEnqueueXYZScatterPlot(XYZDataset dataset, String title, String rootPathStr,
                                                   boolean legend, boolean tooltips, int width, int height) {
        JFreeChart chart = ChartFactory.createScatterPlot(title,"x","y",dataset, PlotOrientation.VERTICAL,
                legend,tooltips,false);
        chart.setBackgroundPaint(Color.white);
        return enqueImageGetLink(chart,rootPathStr,width,height);
    }

    public static String enqueImageGetLink(JFreeChart chart, String rootPathStr, int width, int height) {
        BufferedImage image = chart.createBufferedImage(width, height);
        String fileName = Anchor.getInstance().getImageStreamer().enqueueImage(image);
        String out = "<img src='" + rootPathStr + fileName + "'/>";
        return out;
    }

    public static String createAndEnqueueBarChart(CategoryDataset dataset, String title, String rootPathStr,
                                                  boolean legend, boolean tooltips, int width, int height) {
        JFreeChart chart = ChartFactory.createBarChart(title, "category",
                "y", dataset);
//     chart.addSubtitle(new TextTitle("Time to generate 1000 charts in SVG "
//                + "format (lower bars = better performance)"));
        chart.setBackgroundPaint(Color.white);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        chart.getLegend().setFrame(BlockBorder.NONE);

        return enqueImageGetLink(chart,rootPathStr,width,height);
    }


    public static String createAndEnqueuePieChart(PieDataset dataset, String title, String rootPathStr,
                                                  boolean legend, boolean tooltips, int width, int height) {
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, legend, tooltips, false);
        PiePlot piePlot = (PiePlot) chart.getPlot();
        piePlot.setShadowXOffset(0);
        piePlot.setShadowYOffset(0);
        piePlot.setLabelGenerator(new PieSectionLabelGenerator() {
            @Override
            public String generateSectionLabel(PieDataset dataset, Comparable key) {
                return String.format("%.3f", dataset.getValue(key));
            }

            @Override
            public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
                String out = String.format("%.3f", dataset.getValue(key));
                AttributedString attributedString = new AttributedString(out);
                return attributedString;
            }
        });
        piePlot.setSimpleLabels(true);
        return enqueImageGetLink(chart,rootPathStr,width,height);
    }
}
