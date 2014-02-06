package evogrn.alg.ga.population;

import evogrn.alg.Population;
import evogrn.problem.Problem;

/**
 * Kromosom ciji je fenotip predstavljen realnim brojevima
 * @author dkomlen
 */
public class RealInd extends GAIndividual {

	private double[] params; //Lista realnih varijabli
	
	public double[] getParams(){
		return params;
	}
	public void setParams(double[] params){
		this.params = params;
	}
	
	public RealInd(){
		//potrebno za stvaranje prototipa
	}
	 
	/**
	 * @param brVar - broj varijabli koje kromosm predstavlja
	 */
	public RealInd(Problem p){
		super(p.getParamCount());
		params = new double[p.getParamCount()];
		init(p);
	}
	
	/**
	 * @param parCount - broj varijabli koje kromosm predstavlja
	 * @param fitness - zadana dobrota
	 */
	private RealInd(RealInd ind){
		this.fitness = ind.fitness;
		this.nParams = ind.nParams;
		this.prob = ind.prob;
		
		params = new double[nParams];
		for (int i = 0; i < nParams; ++i){
			params[i] = ind.params[i];
		}
		
	}
	
	@Override
	public double[] getPhenotype(Problem problem) {
		return params;
	}

	@Override
	public void init(Problem p) {
		
		nParams = p.getParamCount();
		params = new double[nParams];
		
		this.prob = p; 
		
		double min[] = p.getMin();
		double max[] = p.getMax();
		
		for (int i = 0; i < nParams; ++i){
			params[i] = min[i] + rand.nextDouble() * (max[i] - min[i]);
		}

	}

	@Override
	public Population<GAIndividual> crossover(GAIndividual parent, double pC) {
		
		//Krizanje se izvodi racunanjem srednje vrijednosti odgovarajucih brojeva u kromosmu
		Population<GAIndividual> offspring = new Population<GAIndividual>(1);
		
		RealInd par = (RealInd) parent;
		RealInd child = (RealInd) this.clone();
		
		if (rand.nextDouble() < pC) {
			
			int[] choice = new int[nParams];
			for (int i = 0; i < 10; ++i)
				choice[rand.nextInt(nParams)] = 1;
			
			RealInd p1 = this;
			RealInd p2 = par;
			
			double min[] = prob.getMin();
			double max[] = prob.getMax();
			
			for (int i = 0; i < nParams; ++i) {
				
				if (choice[i] == 1) {
					RealInd t = p1;
					p1 = p2; p2 = t;
				}
				//child.getParams()[i] = p1.getParams()[i];
				child.getParams()[i] = (fitness*params[i] + par.getFitness() * par.getParams()[i]) / (fitness + par.getFitness());
				
				if (child.getParams()[i] < min[i]) child.getParams()[i] = min[i];
				if (child.getParams()[i] > max[i]) child.getParams()[i] = max[i];
			}
			offspring.add(child);
		} else {
			offspring.add((GAIndividual) this.clone());
		}
		return offspring;
	}

	@Override
	public void mutate(double pM) {
		
		double min[] = prob.getMin();
		double max[] = prob.getMax();
		
		for (int i = 0; i < nParams; ++i){
			if (rand.nextDouble() < pM) {
				double x = params[i] + rand.nextGaussian();		

				if (x < min[i]) x = min[i];
				if (x > max[i]) x = max[i];
				
				params[i] = x;
			}
		}

	}
	@Override
	public Object clone() {
		return new RealInd(this);
	}
	public String toString() {
		
		String brStr = "";
		for (int i = 0; i < nParams; ++i)
			brStr += params[i] + " ";
		return brStr  +": " + fitness;
	}
	@Override
	public void setValue(double value, int i) {
		params[i] = value;
	}

}
