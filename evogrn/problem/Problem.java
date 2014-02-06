package evogrn.problem;

import org.jdom.Element;

import evogrn.xml.XmlSerializable;

public abstract class Problem implements XmlSerializable {

	private double[] min, max; //granice intervala za pojedinu dimenziju
	private double minFun, maxFun; //granice vrijednosti funkcije
	private int n; //broj varijabli
	private int counter = 0; //brojac evaluacija

	public double[] getMin() { return min; }
	public double[] getMax() { return max; }
	public double getMinFun() { return minFun; }
	public double getMaxFun() { return maxFun; }
	
	public double getMin(int i) { return min[i]; }
	public double getMax(int i) { return max[i]; }
	
	public int getParamCount() { return n; }
	
	 
	public Problem(int n) {
		
		double[] min = new double[n];
		double[] max = new double[n];
		for (int i = 0; i < n; ++i) {
			min[i] = 0;
			max[i] = 1;
		}
		
		this.min = min;
		this.max = max;
		this.n = n;
		this.minFun = 0;
		this.maxFun = 1;
	}
	
	public Problem(int n, double[] min, double[] max, double minFun, double maxFun){
		this.min = min;
		this.max = max;
		this.n = n;
		this.minFun = minFun;
		this.maxFun = maxFun;
	}
	
	protected abstract double eval (double[] params);
	
	public double evaluate(double [] params){
		counter++;
		return eval(params);
	}
	
	public int getEvalCount(){
		return counter;
	}
	public void init() {
		counter = 0;
	}
	
	@Override
	public Element toXmlElement() {
		return new Element("problem");
	}


	@Override
	public void fromXmlElement(Element el) {
		// TODO Auto-generated method stub
		
	}
	
}
