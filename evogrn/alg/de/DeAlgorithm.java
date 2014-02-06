package evogrn.alg.de;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import evogrn.alg.Algorithm;
import evogrn.alg.Individual;
import evogrn.alg.Termination;
import evogrn.alg.de.cross.ICross;
import evogrn.alg.de.diff.IDiff;

import evogrn.problem.Problem;
import evogrn.test.Log;

/**
 * Algoritam diferencijske evolucije
 * @author dkomlen
 */
public class DeAlgorithm extends Algorithm<Vector> {

	private Vector[] vectors; //populacija vektora
	private Vector best; //najvbbolji vektor iz populacije
	
	private IDiff diff = null; //strategija diferencijacije
	private ICross cross = null; //strategija krizanja
	
	private double f;
	private double pC;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	public void setDiff(IDiff dif) {
		this.diff = dif;
	}
	
	public void setCross(ICross cross) {
		this.cross = cross;
	}
	
	public DeAlgorithm(int popSize, IDiff dif, ICross cross, double f, double pC) {
		super(popSize, new Vector());

		this.f = f;
		this.pC = pC;
		setCross(cross);
		setDiff(dif);
		
		vectors = new Vector[popSize];
		
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public void init(Problem problem) {
		this.problem = problem;
		population.init(prototype, problem);
		
		best = null;
		
		for (int i = 0; i < vectors.length; ++i) {
			vectors[i] = new Vector(problem);
			
			if (best == null) {
				best = vectors[i];
			} else {
				if (vectors[i].getFitness() > best.getFitness())
					best = vectors[i];
			}
		}
	}

	public Individual getBestInd(){
		return best;
	}
	
	public Individual getWorstInd(){
		
		Individual worst = vectors[0];
		for(Vector v : vectors){
			if (worst.isBetter(v)) worst = v;
		}
		
		return worst;
		
	}
	
	public void evaluate(Problem problem) {
		for (int i = 0; i < vectors.length; ++i) {
			vectors[i].evaluate(problem);
		
		if (best == null) {
			best = vectors[i];
		} else {
			if (vectors[i].getFitness() > best.getFitness())
				best = vectors[i];
		}
		}
	}

	/**
	 * Izvodenje jedne itercije algoritma
	 * @param f utjecaj okolnih vektora na promjenu polozaja
	 * @param pC vjerotnost krizanja
	 * @return
	 */
	public boolean runIteration(Termination term,Problem problem){
		
		for (int i = 0; i < vectors.length; ++i){
			
			Vector target = vectors[i];
			Vector mutant = diff.makeMutant(vectors, target, f);
			
			Vector trial = cross.apply(target, mutant, pC);

			trial.evaluate(problem);
			if (trial.getFitness() >= target.getFitness()) {
				vectors[i] = trial;
			}
			
			if (trial.getFitness() > best.getFitness()){
				best = trial;
			}
		}
		
		return true;
	}


	@Override
	public String getName() {
		return "DEAlgorithm";
	}
	
	
	@Override
	public List<Element> getXmlParams() {
		List<Element> params = new ArrayList<Element>();
		
		params.add(paramToXml("pop_size", vectors.length));
		params.add(paramToXml("f", f));
		params.add(paramToXml("pc", pC));
		params.add(paramToXml("diff", diff));
		params.add(paramToXml("cross", cross));
		
		return params;
	}
	 
	public double getFitnessAvg(){
		double avg = 0;
		for (Vector v : vectors){
			avg += v.getFitness();
		}
		return avg / vectors.length;
	}
	
	public double getFitnessDev() {
		double dev = 0;
		double avg = getFitnessAvg();
		
		for (Vector v : vectors){
			dev += Math.pow(v.getFitness() - avg,2);
		}
		
		return Math.sqrt(dev / (vectors.length-1));
		
	}
	
	@Override
	public void fromXmlElement(Element el) {
		// TODO Auto-generated method stub
		
	}
}
