package com.cvberry.berrypim.widgets;

import com.cvberry.berrypim.Anchor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.List;

public class BarChart {

//    public static Dataset createDatasetFromArrays(String[] keys, Double[] values) {
//        DefaultPieDataset dataset = new DefaultPieDataset();
//        for (int i = 0; i < keys.length; i++) {
//            dataset.setValue(keys[i], values[i]);
//        }
//        return dataset;
//    }

    /**
     * Returns a sample dataset.
     *
     * @return The dataset.
     */
    public static CategoryDataset createDataset(List<? extends Number> values, List<String> cat1, List<String> cat2) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (cat2==null) {
            for (int i= 0; i < values.size(); i++) {
                dataset.addValue(values.get(i),cat1.get(i),"");
            }
        } else {
             for (int i= 0; i < values.size(); i++) {
                dataset.addValue(values.get(i),cat1.get(i),cat2.get(i));
             }
        }
        return dataset;
    }


    public static String createAndEnqueueChart(CategoryDataset dataset, String title,String rootPathStr,
                                               boolean legend, boolean tooltips,int width, int height) {
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

        BufferedImage image = chart.createBufferedImage(width, height);
        String fileName = Anchor.getInstance().getImageStreamer().enqueueImage(image);
        String out = "<img src='"+rootPathStr+fileName+"'/>";
        return out;
    }


/*   public static void main( String[ ] args )
   {
      PieChart_AWT demo = new PieChart_AWT( "Mobile Sales" );
      demo.setSize( 560 , 367 );
      RefineryUtilities.centerFrameOnScreen( demo );
      demo.setVisible( true );
   }*/
}
