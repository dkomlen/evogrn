package evogrn.alg.ga.population;

import evogrn.alg.Individual;
import evogrn.alg.Population;
import evogrn.problem.Problem;

/**
 * Apstraktni razred koji predstavlja evolucijsku jedinicu genetskog algoritma.
 * Konkretne vrste kromosma definiraju vlastite operatore mutacije i križanja.
 * @author dkomlen
 * 
 */
public abstract class GAIndividual extends Individual {

	protected int nParams; //Broj varijabli koje koje kromsom predstavlja
	protected Problem prob;
	/**
	 * @param n - broj varijabli
	 */
	public GAIndividual(int n){
		this.nParams = n;
	}
	
	public GAIndividual(){
		//potrebno za stvaranje prototipa
	}
	
	/**
	 * @param n - broj varijabli
	 * @param fit - zadana dobrota
	 */
	public GAIndividual(int n, double fit){
		this.nParams = n;
		this.fitness = fit;
	}
	
	/**
	 * Metoda koja računa vrijednost dobrote prema zadanom optimizacijskom problemu.
	 * @param problem - problem koji se optimira
	 */
	public double evaluate(Problem problem) {
		fitness = problem.evaluate(getPhenotype(problem));
		return fitness;
	}
	
	
	/**
	 * Dohvaćanje fenotipa koji je predstavljen kromosomom.
	 * @param problem - optimizacijski problem koji se rješava
	 * @return Polje brojeva koji predstavljaju vrijednosti varijabli. 
	 */
	public abstract double[] getPhenotype(Problem problem);
	
	/**
	 * Operator mutacije kromosoma.
	 * @param pM - vjerojatnost izvođenja mutacije
	 */
	public abstract void mutate(double pM);
	
	/**
	 * Stvaranje potomaka na temelju dva kromosma roditelja.
	 * @param roditelj - kromsom sa kojim se križa
	 * @param pC - vjerojatnost križanja
	 * @return Populacija koja sadrži potomke.
	 */ 
	public abstract Population<GAIndividual> crossover(GAIndividual roditelj, double pC);

@Override
	public double[] getValues(Problem problem) {
		return getPhenotype(problem);
	}
}
