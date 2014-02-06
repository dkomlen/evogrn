package evogrn.alg.ga;

import evogrn.alg.Individual;
import evogrn.alg.Population;
import evogrn.alg.Termination;
import evogrn.alg.ga.population.GAIndividual;
import evogrn.problem.Problem;

/**
 * Generacijski genetski algoritam kod kojega se nova generacija stvara
 * uzimanjem N najboljih jedinki iz unije prethodne populacije roditelja i
 * određenog broja djece. Za stvaranje popomaka koristi se mating pool koji se
 * puni turnirskom selekcijom.
 * 
 * @author dkomlen
 */ 
public class GenerationalGA extends GeneticAlgorithm {

	private int brDjece; // broj potomaka koji se stvara svake generacije

	/**
	 * Konstruktor za generacijski genetski algoritam.
	 * 
	 * @param problem
	 *            - optimizacijski problem koji se rješava
	 * @param prototip
	 *            - primjerak kromosoma čija se vrsta koristi u populaciji
	 * @param velPop
	 *            - veličina populacije kromosma
	 * @param velMP
	 *            - veličina mating pool-a
	 * @param brDjece
	 *            - broj potomaka koji se stvara svake generacije
	 */
	public GenerationalGA(GAIndividual prototip, int velPop,
			int brDjece) {
		super(prototip, velPop);

		this.brDjece = brDjece;
	}

	private double iter = 0;

	@Override
	protected boolean runIteration(Termination term, Problem problem) {

//		if (iter % 500 == 0 && iter != 0 && pM >= 0.01) {
//			pM -= 0.01;
//		}
		iter++;

		int velPop = population.getSize();
		Population<GAIndividual> p = new Population<GAIndividual>(population.bestPop((int) (velPop * 0.1)));

		while (p.getCurrentSize() < brDjece * velPop) {

			//proporcionalna selekcija
			GAIndividual p1 = getProportional(population);
			GAIndividual p2 = getProportional(population);

			//turnirska selekcija:
			
			// Populacija turnir = pop.nasumicnaPop(3);
			// Kromosom p1 = turnir.najbolji();
			// turnir.izbaci(p1);
			// Kromosom p2 = turnir.najbolji();

			GAIndividual child = p1.crossover(p2, pC).best();
			child.mutate(pM);
			child.evaluate(problem);

			p.add(child);

		}

		this.population = p.bestPop(velPop);

		return true;
	}
	
	@Override
	public String getName() {
		return "GenerationalGA";
	}

}
