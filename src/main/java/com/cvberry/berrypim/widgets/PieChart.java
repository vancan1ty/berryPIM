package com.cvberry.berrypim.widgets;

import javax.swing.JPanel;

import com.cvberry.berrypim.Anchor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

public class PieChart {

    public static PieDataset createDatasetFromArrays(String[] keys, Double[] values) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < keys.length; i++) {
            dataset.setValue(keys[i], values[i]);
        }
        return dataset;
    }


    public static String createAndEnqueueChart(PieDataset dataset, String title,String rootPathStr,
                                               boolean legend, boolean tooltips,int width, int height) {
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, legend, tooltips, false);
        PiePlot piePlot = (PiePlot) chart.getPlot();
        piePlot.setShadowXOffset(0);
        piePlot.setShadowYOffset(0);
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
