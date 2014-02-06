package evogrn;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.swing.CellEditor;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import evogrn.alg.Algorithm;
import evogrn.alg.Individual;
import evogrn.alg.Termination;
import evogrn.alg.coev.CoevAlgorithm;
import evogrn.alg.coev.CoevIndividual;
import evogrn.alg.coev.problem.CSPGrnInference;
import evogrn.alg.coev.problem.CSPSimple;
import evogrn.alg.coev.problem.CoevSubProblem;
import evogrn.alg.de.DeAlgorithm;
import evogrn.alg.de.Vector;
import evogrn.alg.de.cross.ExponentialCross;
import evogrn.alg.de.cross.ICross;
import evogrn.alg.de.cross.UniformCross;
import evogrn.alg.de.diff.DeBest1;
import evogrn.alg.de.diff.DeRand1;
import evogrn.alg.de.diff.DeRand2;
import evogrn.alg.de.diff.IDiff;
import evogrn.alg.ga.GenerationalGA;
import evogrn.alg.ga.GeneticAlgorithm;
import evogrn.alg.ga.gahooke.GAHookeJeeves;
import evogrn.alg.ga.population.RealInd;
import evogrn.alg.pso.PSOAlgorithm;
import evogrn.dataset.DataSetManager;
import evogrn.dataset.MicroArrayData;
import evogrn.model.GRNModel;
import evogrn.model.LTMParams;
import evogrn.model.LinearTVModel;
import evogrn.problem.GRNInferenceProblem;
import evogrn.test.Test;

public class Program {
	
	public static void main(String[] args) {
		
		//generate("param-test-5-20.txt", 5, 20, 0.01);
		
		try {
			//printReport("../../Testiranje/Vrednovanje/IS-coli");
			//printWessa("../../Testiranje/Vrednovanje/IS-yeast6");
			//printMatrix("../../Testiranje/Vrednovanje/IS-t5/results-hib-IS-t5-1.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (args.length < 2) {
			System.err.println("Parameters missing");
			return;
		}
		
		Algorithm<?> alg = null;
		File dset = new File(args[0]);
				
		CoevSubProblem csp = new CSPGrnInference(new LTMParams());
		int nGenes = 8;
		try {
			nGenes = MicroArrayData.readFromFile(dset.listFiles()[0].getPath()).getGeneCount();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 1; i <= 1; i++) {
			
			if (args[1].equals("de")) alg = getDe(i);
			if (args[1].equals("ga")) alg = getGA(i);
			if (args[1].equals("pso")) alg = getPso(i);
			if (args[1].equals("hib")) { 
				alg = getGAHooke(i);
				alg.setLogInterval(10);
			}
			if (args[1].equals("coev-de")) alg = getDeCoev(nGenes, csp);
			if (args[1].equals("coev-ga")) alg = getGACoev(nGenes, csp);
			if (args[1].equals("coev-pso")) alg = getPsoCoev(nGenes, csp);
			if (args[1].equals("coev-hib")) { 
				alg = getHookeCoev(nGenes, csp);
				alg.setLogInterval(10);
			}
			//alg.setLogInterval(10);
			if (alg == null){
				System.err.println("Unknown algorithm");
				return;
			}
			alg.setLogInterval(10);
			String outFile = "results-"+args[1]+"-"+dset.getName().split("\\.")[0] +".xml";
			runTest(i,4, dset.getName() +" - evaluation" ,dset.getPath(), outFile, alg, true);
		}
	}

	public static void printMatrix(String path) throws Exception {
		
		SAXBuilder builder = new SAXBuilder();
		Document document = (Document) builder.build(path);
		Element rootNode = document.getRootElement();
		Element ind = rootNode.getChild("results").getChild("individual");
		
		int n = Integer.parseInt(rootNode.getChild("problem").getChild("LTV-model").getAttributeValue("gene_count"));
		
		String strParam[] = ind.getText().split("\n");
		double param[] = new double[strParam.length];
		for (int i = 0; i < param.length; ++i){
			param[i] = Double.parseDouble(strParam[i]);
		}
		GRNModel model = new LinearTVModel(n, new LTMParams());
		model.setParams(param);
		double w[][] = model.getTransitionMatrix();
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				System.out.print(w[i][j] +"\t");
				
			}
			System.out.println("");
		}
		
	}
	
public static void printWessa(String path) throws Exception {
		
		File dsetDir = new File(path);
		Element rootNode = null;
		
		ArrayList<ArrayList<String> > output = new ArrayList<ArrayList<String>>();
		int n = Integer.MAX_VALUE;
		for (File f : dsetDir.listFiles()){
			
			SAXBuilder builder = new SAXBuilder();
			
			Document document = (Document) builder.build(f);
			rootNode = document.getRootElement();
			System.out.println(f.getName());
			
			ArrayList<String> list = new ArrayList<String>();
			for (Object ob : rootNode.getChild("results").getChildren("run")){
				Element el = (Element) ob;
				list.add(round(el.getAttributeValue("fit_best")));
			}
			if (n > list.size()) n = list.size();
			output.add(list);
		}
		
		for (int i = 0; i < n; ++i){
			String line = "";
			int j = 0;
			for (ArrayList<String> l : output){
				//if (j++ >= 4) break;
				//if (j++ < 4) continue;
				line += (line.isEmpty() ? l.get(i) : "\t" + l.get(i));
			}
			System.out.println(line);
		}
		
	}
	
	public static void printReport(String path) throws Exception {
		
		File dsetDir = new File(path);
		
		Element rootNode = null;
		int i =-1;
		for (File f : dsetDir.listFiles()){
		
			if (++i % 4 == 0) System.out.println();
			
			SAXBuilder builder = new SAXBuilder();
			
			Document document = (Document) builder.build(f);
			rootNode = document.getRootElement();
	
			String best = round(rootNode.getChild("results").getAttribute("fit_best").getValue());
			String worst = round(rootNode.getChild("results").getAttribute("fit_worst").getValue());
			String avg = round(rootNode.getChild("results").getAttribute("fit_avg").getValue());
			String dev = round(rootNode.getChild("results").getAttribute("fit_dev").getValue());
			
			System.out.println(f.getName()+ " " + best + " & " + worst + " & " + avg + " & " + dev + "\\\\");
	
		}
		
	}
	
	static String round(String s){
		double d = Double.parseDouble(s);
		if (d < 0) d = -d;
		//if (d > 20) d = 20;
		
		return String.format("%.10g", d);//.replace(",", ".");
	}

	private static void runTest(int i, int n, String txt, String in, String out, Algorithm<?> alg, boolean printLog){
		
		System.out.println("Test description: "+ txt);
		System.out.println(getTime() + " - test " + i+"/"+n);
		System.out.println("\t - max remaining time to finish: " + (n - i +1)*30 +" min");
		
		File dir = new File(in);
		String[] dsets = new String[dir.listFiles().length];
		int k = 0;
		for (File f : dir.listFiles()) {
			dsets[k++] = f.getPath();
		}
		
		try {
			Test test = new Test(txt,dsets, alg, printLog);
			test.run(50, new Termination(-1, 3000000, 600, -1, false ), out);
			//test.run(1, new Termination(1, -1, -1, false ), out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(getTime() + " test succesfully finished (results file: " + out +")");
	}
	
	private static String getTime(){
		Calendar now = Calendar.getInstance();
		return "[" + (now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE)) + "]";
	}

	private static void generate(String file, int nGenes, int nSteps, double step) {
		LinearTVModel model = new LinearTVModel(nGenes, new LTMParams());
		GRNInferenceProblem problem = new GRNInferenceProblem(model, null);
		Vector v = new Vector();
		v.init(problem);
		
		Random rnd = new Random();
		double initData[] = new double[nGenes];
		double time[] = new double[nSteps];
		
		for (int i = 0; i < nGenes; ++i){
			initData[i] = rnd.nextDouble();
		}
		
		double t = 0;
		for (int i = 0; i < nSteps; ++i){
			time[i] = t;
			t += step;
		}
		
		model.setParams(v.getElements());
		
		System.out.println("parameters:");
		System.out.println(v.toXmlElement(problem).getText());
		
		MicroArrayData mad = model.simulate(initData, time);
		mad.writeToFile(file);
		
	}
	
	private static Algorithm<?> getDe(int i) {
		
		IDiff dif = new DeBest1();
//		switch (i) {
//			case 1: dif = new DeBest1(); break;
//			case 2: dif = new DeRand1(); break;
//			case 3: dif = new DeRand2(); break;
//		}
		
		ICross cross = new UniformCross();
		
//		switch (i) {
//			case 4: cross = new UniformCross(); break;
//			case 5: cross = new ExponentialCross(); break;
//		}
		
		return new DeAlgorithm(200, dif, cross, 0.5, 0.9);
		
	}
	
	private static Algorithm<?> getPso(int i){
		
		return new PSOAlgorithm(250, 8, 0.5, 1);

	}
	
	private static Algorithm<?> getGA(int i) {
						
		GeneticAlgorithm ga = new GenerationalGA (new RealInd(), 250, 2);
		
		ga.setParams(0.8, 0.06);
		return ga;
		
	}
	
	private static Algorithm<?> getGAHooke(int i) {
		 
		GeneticAlgorithm ga = new GAHookeJeeves(new RealInd(), 50,1,0.000001);
		ga.setParams(0.7, 0.1);

		return ga;
	}
	
	private static Algorithm<?> getHookeCoev(int nGenes, CoevSubProblem csp)  {
		
		ArrayList<Algorithm<?>> algs = new ArrayList<Algorithm<?>>(); 
		int[] iters = new int[nGenes];
		for (int i = 0; i < nGenes; ++i) {
			iters[i] = 2;
			
			algs.add(getGAHooke(1));
		}
		
		return new CoevAlgorithm(algs, iters, csp);

	}
	 
	private static Algorithm<?> getDeCoev(int nGenes, CoevSubProblem csp)  {
				
		ArrayList<Algorithm<?>> algs = new ArrayList<Algorithm<?>>(); 
		int[] iters = new int[nGenes];
		for (int i = 0; i < nGenes; ++i) {
			iters[i] = 2;
			algs.add(getDe(1));
		}
		
		return new CoevAlgorithm(algs, iters, csp);

	}
	private static Algorithm<?> getGACoev(int nGenes, CoevSubProblem csp) {
		
		ArrayList<Algorithm<?>> algs = new ArrayList<Algorithm<?>>(); 
		int[] iters = new int[nGenes];
		for (int i = 0; i < nGenes; ++i) {
			iters[i] = 2;
			algs.add(getGA(1));
		}
		
		return new CoevAlgorithm(algs, iters, csp);

	}
	
	private static Algorithm<?> getPsoCoev(int nGenes, CoevSubProblem csp)  {
		
		ArrayList<Algorithm<?>> algs = new ArrayList<Algorithm<?>>(); 
		int[] iters = new int[nGenes];
		for (int i = 0; i < nGenes; ++i) {
			iters[i] = 5;
			algs.add(getPso(1));
		}
		
		return new CoevAlgorithm(algs, iters, csp);
	}
}
