package evogrn.alg.coev;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import evogrn.alg.Algorithm;
import evogrn.alg.Individual;
import evogrn.alg.Termination;
import evogrn.alg.coev.problem.CoevSubProblem;
import evogrn.alg.pso.Particle;
import evogrn.problem.Problem;
import evogrn.test.Log;

public class CoevAlgorithm extends Algorithm<CoevIndividual> {

	ArrayList<Algorithm<?>> algs;
	ArrayList<Individual> bestInds;
	ArrayList<CoevSubProblem> subProblems;
	
	CoevSubProblem cspPrototip;
	int[] iters;
	int popCount;
	
	CoevIndividual best;
	
	public CoevAlgorithm(ArrayList<Algorithm<?>> algs, int[] iters, CoevSubProblem cspPrototip) {
		super(0, null);
		
		this.algs = algs;
		this.iters = iters;

		this.popCount = algs.size();
		this.cspPrototip = cspPrototip;
		
	}
	
	public void init(Problem problem) {
		this.bestInds = new ArrayList<Individual>();
		this.subProblems = new ArrayList<CoevSubProblem>();
		
		int id = 0;
		for (Algorithm<?> alg : algs) {
			CoevSubProblem csp = cspPrototip.getInstance(id++, algs.size(), problem);
			subProblems.add(csp);
			alg.init(csp);
			alg.setLog(null);
			bestInds.add(alg.getRandomInd());
		}
		
		best = new CoevIndividual(popCount, problem, bestInds, subProblems);
		best.evaluate(problem);
		
		id = 0;
		for (CoevSubProblem csp : subProblems) {
			csp.setParams(best);
			algs.get(id++).evaluate(csp);
		}

	}
	
	protected boolean runIteration(Termination term,Problem problem) {
		int i = 0;
		CoevIndividual newBest = null;
		
		for (Algorithm<?> alg : algs) {

			subProblems.get(i).setParams(best);
			bestInds.set(i,alg.run(new Termination(iters[i], -1, -1, -1, false) , subProblems.get(i++)));
			
			//stvaranje globalnog rjesenja od najboljih jedinki iz svake podpopulacije
			newBest = new CoevIndividual(popCount, problem, bestInds, subProblems);
			newBest.evaluate(problem);
			
			if (newBest.isBetter(best)){
				best = newBest;
			}
		}
		
		return true;
	}
	
	public Individual getBestInd(){
		return best;
	}
	
	public Individual getWorstInd(){
		return best;
	}
	
	@Override
	public double getFitnessDev() {
		return 0;
	}
		
	@Override
	public String getName() {
		return "CoopCoevAlg";
	}

	@Override
	public List<Element> getXmlParams() {
		List<Element> params = new ArrayList<Element>();
		int i = 0;
		params.add(cspPrototip.toXmlElement());
		for(Algorithm<?> alg : algs) {
			Element el = alg.toXmlElement();
			el.setAttribute("iterations", ""+iters[i++]);
			params.add(el);
		}
		return params;
	}
	
	public double getFitnessAvg(){
		return best.getFitness();
	}

	@Override
	public void fromXmlElement(Element el) {
		// TODO Auto-generated method stub
		
	}
}
