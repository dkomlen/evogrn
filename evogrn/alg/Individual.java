package evogrn.alg;

import java.util.Random;

import org.jdom.Element;

import evogrn.problem.Problem;
import evogrn.xml.XmlSerializable;

public abstract class Individual implements Comparable<Individual> {

	protected double fitness;
	public static Random rand = new Random();
	
	/**
	 * Inicijalizacija varijabli kromosma na nasumične vrijednosti.
	 */
	public abstract void init(Problem problem);
	
	public abstract double evaluate(Problem problem);
	
	//public abstract Individual loadFromInd(Individual ind);
	
	/**
	 * @return Iznos posljednje evaluirane dobrote.
	 */
	public double getFitness(){
		return this.fitness; 
	}
	
	public abstract double[] getValues(Problem problem);
	public abstract void setValue(double value, int i);
	
	public boolean isBetter(Individual ind) {
		if (fitness > ind.getFitness()) return true;
		return false;
	}
	
	@Override
	public int compareTo(Individual ind) {
		
		if (fitness < ind.getFitness()) return 1;
		if (fitness > ind.getFitness()) return -1;
		return ind.equals(this) ? 0 : this.hashCode() - ind.hashCode();
	}
	
	public abstract Object clone();
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return this.hashCode() == obj.hashCode();
	}
	
	public Element toXmlElement(Problem problem){
		Element el = new Element("individual");
		String value = "";
		double[] vals = getValues(problem);
		for (double d : vals) value += (d+"\n");
		el.setText(value);
		el.setAttribute("size",(new Integer(vals.length).toString()));
		el.setAttribute("fitness", (new Double(fitness)).toString());
		return el;
	}
	

}
