package evogrn.alg.ga.gahooke;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import evogrn.alg.Individual;
import evogrn.alg.Population;
import evogrn.alg.Termination;
import evogrn.alg.ga.GeneticAlgorithm;
import evogrn.alg.ga.population.GAIndividual;
import evogrn.problem.Problem;

public class GAHookeJeeves extends GeneticAlgorithm {

	double[] delta;
	double precision;
	double initDelta;
	GAIndividual[] basePop;
	
	public GAHookeJeeves(GAIndividual prototip, int velPop, double del, double prec) {
		super(prototip, velPop);
		
		setLogInterval(10);
		
		initDelta = del;
		delta = new double[velPop];
		for (int i = 0; i < velPop; ++i) 
			delta[i] = del;
		
		this.precision = prec;
		
	}
	
	public void init(Problem problem) {
		this.problem = problem;
		population.init(prototype, problem);
		basePop = (new Population<GAIndividual>(population)).getIndList();
	}

	@Override
	protected boolean runIteration(Termination term,Problem problem) {

		int k = 0;
		GAIndividual[] pop = population.getIndList();

		for (GAIndividual ind : pop){
			
			GAIndividual base = basePop[k];
			population.remove(ind);
			
			double[] fit = new double[3];
			fit[0] = ind.getFitness();
			
			double[] values = ind.getValues(problem);
			
			//pretrezivanje okoline trenutnog rjesenja
			for (int i = 0; i < values.length; ++i) {
				
				ind.setValue(values[i] + delta[k], i);
				fit[1] = ind.evaluate(problem);
				
				ind.setValue(values[i] - 2*delta[k], i);
				fit[2] = ind.evaluate(problem);
				
				ind.setValue(values[i] + delta[k], i);
				
				if (fit[0] > fit[1] && fit[0] > fit[2]);
				else if (fit[1] > fit[2]) {
					ind.setValue(values[i] + delta[k], i);
					fit[0] = fit[1];
				}
				else {
					ind.setValue(values[i] - delta[k], i);
					fit[0] = fit[2];
				}
				
			}
			
			double[] baseValues = base.getValues(problem);
			values = ind.getValues(problem);
			ind.evaluate(problem);
			
			if (ind.isBetter(base)) {
				//stvaranje nove bazne tocke
				basePop[k] = (GAIndividual) ind.clone();
				
				//preslikavanje tocke preko bazne
				for (int i = 0; i < values.length; ++i) {
					ind.setValue(2 * values[i] - baseValues[i], i);
				}
				ind.evaluate(problem);
				
			} else {
				//stvaranje nove bazne tocke
				delta[k] /= 2;
				ind = (GAIndividual) base.clone();
				
				double fitBest = population.best().getFitness();
				double fitInd = ind.getFitness();
				
				if (delta[k] < precision && fitBest >= fitInd){
					GAIndividual second, first;
					second = first = getProportional(population);
					
					while(second == first)
						second = getProportional(population);
					
					ind = first.crossover(second, pC).best();
					ind.mutate(pM);
					ind.evaluate(problem);
					
					delta[k] = initDelta;
					basePop[k] = (GAIndividual) ind.clone();
				} 
				
			}
			
			population.add(ind);
			k++;
		}
		
		return true;
	}
	
	public List<Element> getXmlParams() {
		
		
		List<Element> params = super.getXmlParams();
		
		params.add(paramToXml("precision", precision));
		params.add(paramToXml("init-delta", initDelta));

		return params;
	}

	@Override
	public String getName() {
		return "GAHookeJeeves";
	}

}
