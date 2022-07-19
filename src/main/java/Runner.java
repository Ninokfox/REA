import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JPanel;

import mutation.MutEA;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import fitnessFunc.LeadingOnes;

import fitnessFunc.OneMax;


public class Runner {
	
    public static void main(String args[]) throws IOException {
    	
    	String[] mut = {"EA"};
    	String problem = "LeadingOnes";
    	String[] type = {"Five", "Three", "Ten"};//"Five"};
    	int rounds = 100;
    	int steps = 50000;
    	int[] dynamicBudget = {1000, 3000, 6000};//8000, 9000, 10000};//, 10000};
    	int[] gamma = {10};
    	int SIZE = 100;  
    	int[] size = {50, 100, 150, 200};
    	int[] kappa = {1};//, 3, 5, 10000000};
    	boolean recentered = false; 
    	boolean smooth = true;
    	
    	boolean histo = false;
    	boolean runtime = true;
    	boolean cumulative = false;
    	boolean comparation = true;
    	boolean gradient = false;
    	boolean indgrad = false;
    	//boolean[] plottype = {histo, runtime, cumulative, comparation}; 
    	boolean modefied = false;
    	double[] smoothSlope = {0.05, 0.1, 0.4, 0.8, 1.2, 1.6, 2.0};
    	double EAinREA = 0.0; //old - 50/50 and its smooth modification 50/50->0/100
    							// new - 100/0 and modification 100/0->0/100
    	
    	
    	
    	if (!gradient) { 		    	
	    	for (int j = 0; j < type.length; ++j) {
		    	for (int k = 0; k < dynamicBudget.length; ++k) {
			    	for (int i = 0; i < gamma.length; ++i) {    		
			    		NewCalcFormat.main(indgrad, gradient, comparation, cumulative, runtime, histo, EAinREA, modefied, smooth, smoothSlope, 
			    				problem, mut, steps, rounds, SIZE, type[j], kappa, gamma[i], dynamicBudget[k], recentered);
	
			    	}
				}  
	    	}
    	}
    	else {
    		for (int j = 0; j < type.length; ++j) {
		    	for (int i = 0; i < size.length; ++i) {    		
			    		NewCalcFormat.main(indgrad, gradient, comparation, cumulative, runtime, histo, EAinREA, modefied, smooth, smoothSlope, 
			    				problem, mut, steps, rounds, size[i], type[j], kappa, gamma[0], dynamicBudget[0], recentered);
	
			    	}
				  
	    	}
    		
    	}
    	
    }

}
