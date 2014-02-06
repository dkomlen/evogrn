package evogrn.alg;

import org.jdom.Element;

import evogrn.problem.Problem;
import evogrn.xml.XmlSerializable;

public class Termination  implements XmlSerializable{
	
	private int maxIter;
	private int maxEval;
	private double maxTime;
	private double maxFit;
	
	private boolean isStagnation;
	
	private int stagInterval = 100;
	private double stagDelta = 0.01;

	boolean isStart;
	double lastFit;
	int lastIter;
	
	
	public int getMaxIter() {
		return maxIter;
	}

	public int getMaxEval() {
		return maxEval;
	}

	public double getMaxTime() {
		return maxTime;
	}

	public Termination(int maxIter, int maxEval, double maxTime, double maxFit, boolean isStagnation){
		this.maxEval = maxEval;
		this.maxIter = maxIter;
		this.maxTime = maxTime;
		this.maxFit = maxFit;
		
		this.isStagnation = isStagnation;
		this.isStart = true;
	}
	
	public double percCompleted(Algorithm<?> alg, Problem prob) {
		
		double a = (double) alg.getIterCount() / maxIter;
		double b = (double) prob.getEvalCount() / maxEval;
		double c = (double) alg.getTime() / maxTime;
		 
		if (a > b) {  
			if (a > c) return a;
		} else if (b > c) return b;
		
		return c;
	}
	
	public boolean isOver(Algorithm<?> alg, Problem prob) {

		if (isStagnation && alg.getIterCount() > 0 && isStart){
			isStart = false;
			lastFit =  alg.getBestInd().getFitness();
			lastIter = alg.getIterCount();
		}
		
		//stagnation check
		if (isStagnation && alg.getIterCount() - lastIter >= stagInterval){
			lastIter = alg.getIterCount();
			double fit = alg.getBestInd().getFitness();
			
			if (Math.abs(fit - lastFit) < stagDelta) return true;
			lastFit = fit;
		}
		
		if (maxIter > 0 && alg.getIterCount() >= maxIter) return true;
		if (maxEval > 0 && prob.getEvalCount() >= maxEval) return true;
		if (maxTime > 0 && alg.getTime() >= maxTime) return true;
		if (maxFit > 0 && alg.getBestInd().getFitness() >= maxFit) return true;
		
		return false;
	}

	@Override
	public Element toXmlElement() {
		Element el = new Element("termination"); 
		if (maxIter > 0) el.setAttribute("max_iter", ""+maxIter);
		if (maxEval > 0) el.setAttribute("max_eval", ""+maxEval);
		if (maxTime > 0) el.setAttribute("max_time", ""+maxTime);
		if (maxFit > 0) el.setAttribute("max_fit", "" + maxFit);
		
		if (isStagnation) {
			el.setAttribute("stag_interval", ""+stagInterval);
			el.setAttribute("stag_delta", ""+stagDelta);
		}
		
		return el;
	}

	@Override
	public void fromXmlElement(Element el) {
		// TODO Auto-generated method stub
		
	}
}
