package evogrn.test;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import evogrn.alg.Algorithm;
import evogrn.alg.Individual;
import evogrn.alg.Termination;
import evogrn.dataset.MicroArrayData;
import evogrn.model.GRNModel;
import evogrn.model.LTMParams;
import evogrn.model.LinearTVModel;
import evogrn.problem.GRNInferenceProblem;
import evogrn.problem.Problem;

public class Test {
	
	String description;
	Problem problem;
	GRNModel model;
	Algorithm<?> alg;
	String[] dataSets;
	Log log;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	public Test(String description, String[] dataSets, Algorithm<?> alg, LTMParams ltm, boolean printLog) throws Exception {
		init(description, dataSets,alg,ltm,printLog);
	}
	
	public Test(String description, String[] dataSets, Algorithm<?> alg, boolean printLog) throws Exception {
		init(description, dataSets,alg,new LTMParams(), printLog);
	}
	
	private void init(String description,String[] dataSets, Algorithm<?> alg, LTMParams ltm, boolean printLog) throws Exception {
		
		this.description = description;
		this.alg = alg;
		this.dataSets = dataSets;
		
		ArrayList<MicroArrayData> testData = new ArrayList<MicroArrayData>();
		MicroArrayData mad = null;
		int nGenes = -1;
		for (String file : dataSets) {
			mad = MicroArrayData.readFromFile(file);
			//mad.normalize(); //ubaceno u readFromFile
			testData.add(mad);
			
			if (nGenes < 0) {
				nGenes = mad.getGeneCount();
			} else if (mad.getGeneCount() != nGenes) {
				throw new Exception("Gene count in data files doesn't match!");
			}	
		}
		
		model = new LinearTVModel(nGenes, ltm);
		problem = new GRNInferenceProblem(model, testData.toArray(new MicroArrayData[] {mad}));
		log = new Log(printLog);
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public void run(int nIter,Termination term, String output) throws Exception {
		
		Individual best = null;

		Element xmlRes = getResElement(nIter);
		List<Double> fit = new ArrayList<Double>();
		double avgFitness = 0; 
		
		double mainStart = System.currentTimeMillis();
		
		for (int i = 0; i < nIter; ++i) {
			
			System.out.println(" test " + (i+1) +"/" +nIter);
			log.clear();
			alg.setLog(log);
			problem.init();
			alg.init(problem);
			Individual ia =  alg.getBestInd();
			
			double start = System.currentTimeMillis();
			Individual ind = alg.run(term, problem);
			double end = System.currentTimeMillis();
		
			if (Thread.currentThread().isInterrupted()) return;
			
			fit.add(ind.getFitness());
			avgFitness += ind.getFitness();
			
			double avg = alg.getFitnessAvg();
			double dev = alg.getFitnessDev();
			double worst = alg.getWorstInd().getFitness();
			
			xmlRes.addContent(getRunElement(term, (end-start)/1000,ind.getFitness(),worst, avg , dev));
			
			if (best == null || best.getFitness() < ind.getFitness()) {
				best = ind;
			}
			
			firePropertyChange("test", null, 100*(i+1)/nIter);
		}
		double mainEnd = System.currentTimeMillis();
		
		avgFitness /= nIter;
		double devFitness = 0;
		double worstFit = fit.get(0);
		for (Double d : fit){
			devFitness += (d - avgFitness) * (d-avgFitness);
			if (d < worstFit) worstFit = d;
		}
		
		devFitness = Math.sqrt(devFitness / (nIter-1));
		
		xmlRes.addContent(best.toXmlElement(problem));
		xmlRes.setAttribute("duration", (new Double((mainEnd-mainStart)/1000)).toString());
		xmlRes.setAttribute("fit_best", (new Double(best.getFitness())).toString());
		xmlRes.setAttribute("fit_worst", (new Double(worstFit)).toString());
		xmlRes.setAttribute("fit_avg", (new Double(avgFitness)).toString());
		xmlRes.setAttribute("fit_dev", (new Double(devFitness)).toString());
		
		Element xmlTest = new Element("test");
		Element xmlDesc = new Element("desc");
		xmlDesc.setText(description);
		
		xmlTest.addContent(xmlDesc);
		xmlTest.addContent(alg.toXmlElement());
		xmlTest.addContent(xmlRes);
		 
		xmlTest.addContent(getProbXmlElement());
		
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		Document doc = new Document(xmlTest);
		FileWriter writer = new FileWriter(output);
		outputter.output(doc, writer);
		writer.close();
		firePropertyChange("test-end", null, output);
	}
	
	private Element getResElement(int maxIter) {
		Element el = new Element("results");
		el.setAttribute("iter",(new Integer(maxIter)).toString());
		return el;
	}
	
	private Element getRunElement(Termination term, double time,double best, double worst, double avg, double dev){
		Element el = new Element("run");
		//el.setAttribute("iter",(new Integer(iter)).toString());
		el.setAttribute("duration",(new Double(time)).toString());
		el.setAttribute("fit_best",(new Double(best)).toString());
		el.setAttribute("fit_worst", new Double(worst).toString());
		el.setAttribute("fit_avg",(new Double(avg)).toString());
		el.setAttribute("fit_dev",(new Double(dev)).toString());
		el.addContent(term.toXmlElement());
		el.addContent(log.toXmlElement());
		return el;
	}
	
	private Element getProbXmlElement(){
		Element el = problem.toXmlElement();
		for (String str : dataSets) {
			Element e = new Element("dataset");
			e.setAttribute("location", str);
			el.addContent(e);
		}
		return el;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
		log.addPropertyChangeListener(listener);
		alg.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	private void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}
}
