package evogrn.dataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.Date;
import java.util.Random;

public class MicroArrayData {
	private double data[][];
	private double time[];
	private int nGenes;
	private int nSteps;
	
	private void init(int nGenes, int nSteps) {
		
		this.nGenes = nGenes;
		this.nSteps = nSteps;
		
		time = new double[nSteps];
		data = new double[nSteps][nGenes];
		
	}
	
	public int getGeneCount() {
		return nGenes;
	}
	
	public int getStepCount(){
		return nSteps;
	}
	
	public double[] getTime(){
		return time;
	}
	
	public double[] getDataForTime(int t){
		return data[t];
	}

	public MicroArrayData(MicroArrayData mad){
		init(mad.getGeneCount(), mad.getStepCount());
		for (int i = 0; i < nSteps; ++i) {
			time[i] = mad.time[i];
			for (int j = 0; j < nGenes; ++j) {
				data[i][j] = mad.getData(j, i);
			}
		}
	}
	
	public MicroArrayData(int nGenes, int nSteps, double timeStep) {
		
		init(nGenes, nSteps);
		double t = 0;
		for (int i = 0; i < nSteps; ++i) {
			time[i] = t;
			t += timeStep;
		}
		
	}
	
	public MicroArrayData(int nGenes, double[] time) {
		init(nGenes, time.length);
		this.time = time;
	}
	
	public double[][] getData() {
		return data;
	}
	
	public double getData(int g, int t){
		return data[t][g];
	}
	
	public void setData(int g, int t, double d) {
		data[t][g] = d;
	}
	
	public double[] getDataForGene(int g){
		double[] data = new double[nSteps];
		for (int t = 0; t < nSteps; ++t)
			data[t] = getData(g, t);
		
		return data;
	}
	
	public void setDataForGene(int g, double[] data){
		for (int t = 0; t < nSteps; ++t)
			setData(g, t, data[t]);
	}
	
	public double getTime(int t){
		return time[t];
	}
	
	public double getMean(int gene){
		double ret = 0;
		
		for (int i = 0; i < nSteps; ++i)
			ret += data[i][gene];
		
		return ret /= nSteps;
	}
	
	public double getVariance(int gene){
		double ret = 0;
		double mean = getMean(gene);
		
		for (int i = 0; i < nSteps; ++i)
			ret += Math.pow(mean - data[i][gene],2);
		
		return ret /= nSteps;
	}
	

	/**
	 * Dodaje Gaussov sum cija je varijanca = varijanca signala * percent 
	 * @param percent
	 */
	public void addNoise(double percent){
		Random rnd = new Random();
		for (int i = 0; i < nGenes; ++i){
			double var = Math.sqrt(percent*getVariance(i));
			for (int j = 0; j < nSteps; ++j){
				data[j][i] +=  var * rnd.nextGaussian();
			}
		}
	}
	
	public static MicroArrayData readFromFile(String path) throws Exception{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String line = br.readLine();
		while (line.startsWith("#") || line.isEmpty())
			line = br.readLine();
		
		MicroArrayData mad = null;
		String[] params = line.split(" ");
		int nGenes = Integer.parseInt(params[0]);
		int nSteps = Integer.parseInt(params[1]);
		
		if (params.length > 2) {
			double timeStep = Double.parseDouble(params[2]);
			mad = new MicroArrayData(nGenes, nSteps, timeStep);
		} else {
			
			
			do	line = br.readLine();
			while (line.startsWith("#") || line.isEmpty());
//			line = br.readLine();
			
			String[] spt = line.split(" ");
			double time[] = new double[spt.length];
			for (int i = 0; i < time.length; ++i)
				time[i] = Double.parseDouble(spt[i]);
			mad = new MicroArrayData(nGenes, time);
		}
		
		do	line = br.readLine();
		while (line.startsWith("#") || line.isEmpty());
		
		for (int t = 0; t < nSteps; ++t) {
			
			if (line.startsWith("#") || line.isEmpty()) continue;
			line = line.replace(",", ".");
			String data[] = line.split("\\s+");
			
			
			for (int g = 0; g < nGenes; ++g) { 
				double d = Double.parseDouble(data[g]);
				mad.setData(g, t, d);
			}
			
			do	line = br.readLine();
			while (line != null && (line.startsWith("#") || line.isEmpty()));
		}
		
		//ako maksimalna vrijednost podataka prelazi 1, radi se normalizacija
		double max = 0;
		for (int t = 0; t < nSteps; ++t) {
			for (int g = 0; g < nGenes; ++g) {
				if (max < mad.getData(g, t)) max = mad.getData(g, t); 
			}
		}
		
		if (max > 1) mad.normalize();
		
		//vrijednosti na 0 postavi na jako male vrijednosti
		for (int t = 0; t < nSteps; ++t) {
			for (int g = 0; g < nGenes; ++g) {
				if (mad.getData(g,t) == 0) mad.setData(g,t,0.0001);
			}
		}
		
		br.close();
		return mad;
	}
	
	public void normalize() {
		
		double max = 0;
		for (int i = 0; i < nSteps; ++i)
			for (int j = 0; j < nGenes; ++j)
				if (data[i][j] > max) max = data[i][j]; 
				
		for (int i = 0; i < nSteps; ++i)
			for (int j = 0; j < nGenes; ++j) {
				data[i][j] /= max;
			}

	} 
	
	public void writeToFile(String path) {

		try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));
		String nl = System.getProperty("line.separator");
		bw.write("# "+ (new Date()) + nl);
		bw.write(nGenes+ " "+nSteps+ nl);
		
		for (double t : time) bw.write(t + " ");
		bw.write(nl);
		for (int i = 0; i < nSteps; ++i){
			for (int j = 0; j < nGenes; ++j) {
				Double x = new Double(data[i][j]);
				
				bw.write(x.toString().replace(".", ",") + "\t");
			}
			bw.write(nl);
		}
		bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
