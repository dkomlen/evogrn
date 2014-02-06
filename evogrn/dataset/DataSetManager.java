package evogrn.dataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DataSetManager {
	
	public static MicroArrayData parseUriAlonLab (String file) throws Exception {
		
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		
		String line = br.readLine();
		String[] spt = line.split("\t");
		int nTime = spt.length - 1;
		double[] time = new double[nTime];
		
		for (int i = 0; i < nTime; ++i) {
			time[i] = Double.parseDouble(spt[i+1]);
		} 
		
		int nGenes = 0;
		ArrayList<double[]> data = new ArrayList<double[]>();
		
		while ((line = br.readLine()) != null) {
			nGenes++;
			spt = line.split("\t");
			double[] datGene = new double[spt.length - 1];
			for (int j = 0; j < datGene.length; ++j) {
				double d =  Double.parseDouble(spt[j+1]);
				datGene[j] = d ;//== 0 ? 0.0001 : d;
			}
			data.add(datGene);
		}
		
		br.close();
		MicroArrayData mad = new MicroArrayData(nGenes, time);
		
		int g = 0;
		for (double[] dat : data) {
			for (int t = 0; t < dat.length; ++t){
				mad.setData(g, t, dat[t]);
			}
			g++;
		}
		
		return mad;
	}

	public static void exportMatlab(MicroArrayData mad, String file) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(file)));
			String nl = System.getProperty("line.separator");
			
			bw.write(mad.getGeneCount()+ " "+mad.getStepCount()+ nl);
			
			for (double t : mad.getTime()) bw.write(t + " ");
			bw.write(nl);
			for (int i = 0; i < mad.getStepCount(); ++i){
				for (int j = 0; j < mad.getGeneCount(); ++j) {
					Double x = new Double(mad.getData(j,i));
					
					bw.write(x.toString().replace(",", ".") + "\t");
				}
				bw.write(nl);
			}
			bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public static XYSeries getChartDataSet(MicroArrayData mad, int gene) {
		
		XYSeries series = new XYSeries("Gene "+(gene+1));
		for (int j = 0; j < mad.getStepCount(); ++j) {
			series.add(mad.getTime(j),mad.getData(gene, j));
		}
		return series;
	
	}
	
	public static XYDataset getChartDataSet(MicroArrayData mad) {
		XYSeriesCollection xyDataset = new XYSeriesCollection();
		for (int i = 0; i < mad.getGeneCount(); ++i) {		
			xyDataset.addSeries(getChartDataSet(mad,i));
		}
		return xyDataset;
	}
}
