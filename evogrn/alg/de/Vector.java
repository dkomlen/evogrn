package evogrn.alg.de;

import java.util.Random;
import evogrn.alg.Individual;
import evogrn.problem.Problem;

/**
 * @author dkomlen
 * Jedinka u populaciji algoritma.
 */
public class Vector extends Individual{
 
	private double[] elements;
	private int n; //velicina vektora
	
	private static Problem problem = null;
	
	public Vector (){
		//potrebno za stvaranje prototipa
	}
	
	/**
	 * @param n velicina vektora
	 * @param minElem minimalne vrijednosti elemenata
	 * @param maxElem maksimalne vrijednosti elemenata
	 * @param problem problem koji se rjesava
	 */
	public Vector (Problem prob) {	
		this.n = prob.getParamCount();
		init(prob);
		evaluate(prob);
	}
	
	public void init (Problem p){
		
		this.n = p.getParamCount();
		elements = new double[n];
		
		if (rand == null) rand = new Random();
		problem = p;
		
		//inicijalizacija vektora na nasumicne vrijednosti
		double[] minElem = problem.getMin();
		double[] maxElem = problem.getMax();
		
		for (int i = 0; i < n; ++i){
			elements[i] = (maxElem[i] - minElem[i]) * rand.nextDouble() + minElem[i];
		}
		
	}
	
	/**
	 * Copy konstruktor
	 * @param old
	 */
	public Vector(Vector old){
		this.n = old.n;
		this.fitness = old.fitness;
		elements = new double[n];
		for (int i = 0; i < n; ++i) {
			elements[i] = old.elements[i];
		}
	}

	@Override
	public double evaluate(Problem problem) {
		fitness = problem.evaluate(elements);
		return fitness;
		
	}
	
	public Random getRandom(){
		return rand;
	}
	
	public int getParamCount() {
		return n;
	}
	
	public double getElem(int i) {
		return elements[i];
	}
	
	public void setElem(int i, double val) {
		if (val < problem.getMin(i)) val = problem.getMin(i);
		if (val > problem.getMax(i)) val = problem.getMax(i);
		elements[i] = val;
	}
	
	public double[] getElements(){
		return elements;
	}
	
	@Override
	public Object clone() {
		return new Vector(this);
	}

	@Override
	public double[] getValues(Problem problem) {
		return elements;
	}

	@Override
	public void setValue(double value, int i) {
		elements[i] = value;
		
	}

}
