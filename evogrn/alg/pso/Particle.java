package evogrn.alg.pso;

import java.util.ArrayList;
import java.util.Random;

import evogrn.alg.Individual;
import evogrn.problem.Problem;

/**
 * Razred koji predstavlja jednu cesticu u algoritmu.
 * @author dkomlen
 */
public class Particle extends Individual {
	
	private double[] x; //polozaj cestice
	private double[] v; //brzina cestice
	private int n; //broj varijabli
	
	private double pBestFitness; //najbolje pronadeno rjesenje
	private double[] pBest; //polozaj najboljeg rjesenja
	private Particle bestNeighbour  = null; //najbolja cestica iz susjedstva 
	
	private Problem problem;
	private ArrayList<Particle> neighbours; //skup svih susjeda
	
	private double[] vMax; //granicne vrijednosti brzina
	private double V_MAX = 0.005;
	
	public double[] getPosition() {
		return x;
	}
	
	public double[] getVelocity() {
		return v;
	}
	
	public double getPBestFitness() {
		return pBestFitness;
	}
	
	public Particle getBestNeighbour() {
		return bestNeighbour;
	}
	
	public ArrayList<Particle> getNeighbours (){
		return neighbours;
	}
	
	public void load(double[] vals) {
		for (int i = 0; i < vals.length; ++i)
			x[i] = vals[i];
	}
	 
	public Particle(){
		//potrebno za stvaranje prototipa
	}
	
	/**
	 * Konstruktor.
	 * @param problem problem koji se rjesava
	 * @param vMax granicne vrijednosti brzina
	 */
	public Particle(Problem problem) {
		init(problem);
	}
	
	public Particle (Particle part) {
		
		this.problem = part.problem;
		this.vMax = part.vMax;
		
		if (problem == null) return;
		this.n = problem.getParamCount();
		x = new double[n];
		v = new double[n];
		pBest = new double[n];
		
		for (int i = 0; i < n; ++i) {
			x[i] = part.x[i];
			v[i] = part.v[i];
			pBest[i] = part.pBest[i];
		}
		
		neighbours = new ArrayList<Particle>(part.neighbours);
		pBestFitness = part.getPBestFitness();
		fitness = part.getFitness();
		
	}
	
	/**
	 * Inicijalizacija cestice na nasumicnu poziciju, s
	 * nasumicnom brzinom.
	 */
	public void init(Problem problem){
		
		this.n = problem.getParamCount();
		x = new double[n];
		v = new double[n];
		pBest = new double[n];
		
		vMax = new double[problem.getParamCount()];
		double min[] = problem.getMin();
		double max[] = problem.getMax();
		
		for (int i = 0; i < problem.getParamCount(); ++i){
			vMax[i] = V_MAX * (max[i] - min[i]);
		}
		
		this.problem = problem;
		neighbours = new ArrayList<Particle>();
		pBestFitness = -Double.MAX_VALUE;
		
		if (rand == null) rand = new Random();
		
		double[] xMax = problem.getMax();
		double[] xMin = problem.getMin();
		
		for (int i = 0; i < n; ++i) {
			x[i] = rand.nextDouble() * (xMax[i] - xMin[i]) + xMin[i];
			v[i] = rand.nextDouble() * vMax[i];
		}
	}
	
	public void addNeighbour(Particle neighbour) {
		neighbours.add(neighbour);
	}
	
	/**
	 * Provjera da li je pronasla osobno najbolje rjesenje.
	 */
	public void setPBest(){
		if (fitness > pBestFitness) {
			
			pBestFitness = fitness;
			for (int i = 0; i < n; ++i) {
				pBest[i] = x[i];
			}
		}
	}

	/**
	 * Provjeravanje najboljeg susjeda.
	 */
	public void setLBest(){
		if (bestNeighbour == null) bestNeighbour = neighbours.get(0);
		for (Particle s : neighbours) {
			if (s.getFitness() > bestNeighbour.getFitness()) {
				bestNeighbour = s;
			}
		}
	}

	/**
	 * Racunanje novih vrijednosti pozicije i brzine
	 * @param w faktor inercije
	 * @param c1 utjecaj vlastitog najboljeg rjesenja
	 * @param c2 utjecaj susjedstva
	 */
	public void update(double w, double c1, double c2) {
		
		double[] lBest = bestNeighbour.getPosition();
		double[] xMin = problem.getMin();
		double[] xMax = problem.getMax();
		
		for (int i = 0; i < n; ++i) {
			v[i] = w*v[i] +  c1 * rand.nextDouble() * (pBest[i] - x[i])
					+ c2 * rand.nextDouble() * (lBest[i] - x[i]);
			
			if (v[i] < -vMax[i]) {
				v[i] = -vMax[i];
			} else if (v[i] > vMax[i]) {
				v[i] = vMax[i];
			}
			
			x[i] += v[i];
			
			if (x[i] < xMin[i]) {
				x[i] = xMin[i];
			} else if (x[i] > xMax[i]){
				x[i] = xMax[i];
			}
		}
	}

	@Override
	public double evaluate(Problem problem) {
		this.fitness = problem.evaluate(x);
		return fitness;
	}
	
	@Override
	public Object clone() {
		return new Particle(this);
	}
	
	@Override
	public double[] getValues(Problem problem) {
		return x;
	}

	@Override
	public void setValue(double value, int i) {
		x[i] = value;
	}

}
