package evogrn.alg.pso;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import evogrn.alg.Algorithm;
import evogrn.alg.Individual;
import evogrn.alg.Termination;
import evogrn.alg.de.Vector;
import evogrn.problem.Problem;
import evogrn.test.Log;

/**
 * @author dkomlen
 * Algoritam roja cestica.
 * Model u MVC arhitekturi.
 */
public class PSOAlgorithm extends Algorithm<Particle>{ 
	
	private Particle[] particles; //skup cestica
	
	private double w; //faktor inercije
	private double W_MIN = 0.2; //minimalna vrijednost inercije
	private double W_MAX = 0.9; //maksimalna vrijednost inercije
	private Particle best; //najbolja cestica
	
	//private int iter; //broj iteracije
	private int neibFactor; //faktor susjedstva
	private double c1, c2;
	 
	private PropertyChangeSupport propertyChangeSupport;
	
	/**
	 * Konstruktor
	 * @param n broj cestica
	 * @param k faktor susjedstva
	 * @param problem problem koji se rjesava
	 * @param vMax vektor maksimalnih brzina
	 */
	public PSOAlgorithm(int n,int k, double c1, double c2){
		super(n, new Particle());
		
		this.particles = new Particle[n];
		
		this.neibFactor = k;
		
		this.c1 = c1;
		this.c2 = c2;
		
		propertyChangeSupport = new PropertyChangeSupport(this);

	}
		
	public void init(Problem problem) {
		this.problem = problem;
		population.init(prototype, problem);
		
		w = W_MAX;
		iter = 0;
		int nPart = particles.length;
		
		for (int i = 0; i < nPart; ++i) {
			particles[i] = new Particle(problem);
		}
		best = particles[0];
		
		for (int i = 0; i < nPart; ++i) {
			for (int j = 1; j <= neibFactor; ++j) {
				particles[i].addNeighbour(particles[(i+j)%nPart]);
				particles[i].addNeighbour(particles[(i-j + nPart)%nPart]);
			}
		}	
		
	}
	
	/**
	 * @param maxIter
	 * @param c1
	 * @param c2
	 * @return
	 */
	public boolean runIteration(Termination term,Problem problem) {

		int maxIter = term.getMaxIter();
		
		for (Particle c : particles) {
			c.evaluate(problem);
		}
		for (Particle c : particles) {
			c.setPBest();
		}
		for (Particle c : particles) {
			c.setLBest();
		}
		for (Particle c : particles) {
			c.update(w, c1,c2);
			
			if (c.getFitness() > best.getFitness()){
				best = new Particle(c);
			}
		}

		//drugi nacin racunanja faktora inercije
		//w = 0.4 + 0.6 * (1 - Math.tanh((double) iter / maxIter * 3));
		//double t = 0.8 * maxIter;
		//w = (iter <= t) ? (iter/t * (W_MIN - W_MAX) + W_MAX) : W_MIN;
		
		double perc = term.percCompleted(this, problem);
		w = (perc <= 0.8) ? (perc/0.8 * (W_MAX - W_MIN) + W_MAX) : W_MIN;
		
		return true;
	}
	

	public Individual getBestInd(){
		return best;
	}
	
	public Individual getWorstInd(){
		
		Individual worst = particles[0];
		for(Particle p : particles){
			if (worst.isBetter(p)) worst = p;
		}
		
		return worst;
		
	}
	
	@Override
	public String getName() {
		return "PSO";
	}
	@Override
	public List<Element> getXmlParams() {
		
		List<Element> params = new ArrayList<Element>();
		
		params.add(paramToXml("pop_size", particles.length));
		params.add(paramToXml("w_min", W_MIN));
		params.add(paramToXml("w_max", W_MAX));
		params.add(paramToXml("neib_factor", neibFactor));
		params.add(paramToXml("c1", c1));
		params.add(paramToXml("c2", c2));
		
		return params;
	}
	
	public double getFitnessAvg(){
		double avg = 0;
		for (Particle v : particles){
			avg += v.getFitness();
		}
		return avg / particles.length;
	}
	
	public double getFitnessDev() {
		double dev = 0;
		double avg = getFitnessAvg();
		
		for (Particle p : particles){
			dev += Math.pow(p.getFitness() - avg,2);
		}
		
		return Math.sqrt(dev / (particles.length-1));
		
	}

	@Override
	public void fromXmlElement(Element el) {
		
	}
}
