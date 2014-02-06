package evogrn.alg.ga;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import evogrn.alg.Algorithm;
import evogrn.alg.Individual;
import evogrn.alg.Population;
import evogrn.alg.ga.population.GAIndividual;
import evogrn.problem.Problem;
import evogrn.test.Log;

/**
 * Apstraktni razred koji predstavlja općeniti sekvencijski genetski algoritam.
 * @author dkomlen
 * @see {@link GAIndividual}, {@link Problem}
 */
public abstract class GeneticAlgorithm extends Algorithm<GAIndividual> {

	protected double pC = 0.8; //Vjerojatnost križanja
	protected double pM = 0.05; //Vjerojatnost mutacije
	protected int maxIter; //maksimalan broj iteracija
	
	//protected Population<GAIndividual> population; //Populacija kromosma
	//protected Problem problem; //Optimizacijski problem koji se rješava pomoću GA
	//protected GAIndividual prototip; //Prototip koji definira vrstu kromosoma
	
	double[] fitness;
	
	/**
	 * Općeniti konstruktor za sve vrste genetskog algoritma 
	 * @param problem - optimizacijski problem koji rješavamo
	 * @param prototip - primjerak vrste kromosoma koji se koristi u populaciji 
	 * @param velPop - veličina populacije
	 */
	public GeneticAlgorithm(GAIndividual prototip, int velPop){
		super(velPop,prototip);
		//this.problem = problem; 
		//population = new Population(velPop);
		//this.prototip = prototip;
	}
	
	public void setPopulation(Population<GAIndividual> p) {
		this.population = p; 
	}
	
	/**
	 * Postavljanje parametara GA.
	 * @param pC - vjerojatnost križanja
	 * @param pM - vjerojatnost mutacije
	 */
	public void setParams (double pC, double pM){
		this.pC = pC;
		this.pM = pM;
	}
	
	/**
	 * Okvirna metoda koja pokreće rad genetskog algoritma. <br>
	 * Za stvaranje nove generacije kromosma koristi se apstraktna metoda <i>generirajNovuPop</i>. <br>
	 * Algoritam se zaustavlja ako je bilo koji od zadanih uvjeta ispunjen.
	 * @param maxIter - maksimalan broj iteracija
	 * @param maxDobrota - maksimalna tražena dobrota
	 * @return Najbolji kromosom iz posljednje generacije.
	 */
//	public Individual run(int maxIter, Population<GAIndividual> initPop) {
//		
//		this.maxIter = maxIter;
//		
//		int iter = 0;
//		fitness = new double[maxIter+1];
//		
//		//Inicijalizacija početne populacije
//		if (initPop != null) {
//			population = initPop;
//		} else {
//			population.init(prototip, problem);
//		}
//		population.evaluate(problem);
//		
//		while (iter <= maxIter) {
//			
//			log("iteration: " + iter + ", best = " + population.best().getFitness());
//			
//			//Stvaranje nove populacije
//			boolean nastavi = generateNewPop();
//		    
//			population.evaluate(problem);
//			fitness[iter] = population.best().getFitness();
//			iter++;
//			if (!nastavi) break;
//		}
//
//		return population.best();
//	}
//
//	@Override
//	public Individual run(int maxIter, Problem problem) {
//		return run(maxIter, population);
//	}

	public double[] getFitness(){
		return fitness;
	}
	
	/**
	 * Metoda koja stvara novu populaciju ovisno konkretnoj vrsti genetskog algoritma.
	 * @return false ako je algoritam zadovoljio specifični uvjet zaustavljanja, true inače
	 */
	
	public GAIndividual getProportional(Population<GAIndividual> pop){
		
		GAIndividual[] indList = pop.getIndList();
		
		double sum = 0;
		double fitness[] = new double[indList.length];
		double worst = pop.worst().getFitness();
		for (int i = 0; i < indList.length; ++i) {
			fitness[i] = indList[i].getFitness() - worst;
			sum += fitness[i];
		}
		
		double selPoint = sum * Individual.rand.nextDouble();
		
		sum = 0;
		int sel = 0;
		for (int i = 0; i < pop.getSize(); ++i) {
			sum += fitness[i];
			if (sum >= selPoint) {
				sel = i;
				break;
			}
		}
		return (GAIndividual) indList[sel].clone();
	}
	
	/**
	 * Izvođenje mutacije nad svim kromosomima u populaciji.
	 * @param pM - vjerojatnost mutacije
	 */
	public void mutate(double pM){
		for (GAIndividual krom : population.getIndTreeSet()){
			krom.mutate(pM);
		}
	}
	
	@Override
	public List<Element> getXmlParams() {
		List<Element> params = new ArrayList<Element>();
		
		params.add(paramToXml("pop_size", population.getSize()));
		params.add(paramToXml("pm", pM));
		params.add(paramToXml("pc", pC));
		return params;
	}
	
	@Override
	public void fromXmlElement(Element el) {
		// TODO Auto-generated method stub
		
	}
}
