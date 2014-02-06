package evogrn.alg.coev;

import java.util.List;

import evogrn.alg.Individual;
import evogrn.alg.coev.problem.CoevSubProblem;
import evogrn.problem.Problem;

public class CoevIndividual extends Individual {
	
	double[] params; 

	public CoevIndividual(int n, Problem problem, List<Individual> inds, List<CoevSubProblem> problems){
		params = new double[problem.getParamCount()];

		int i = 0, id = 0;
		
		for (Individual ind : inds) {
			CoevSubProblem csp = problems.get(id++);
			for (double d : ind.getValues(csp))
				params[i++] = d;
		}
		
	}
	
	@Override
	public void init(Problem problem) {
		return;
	}

	@Override
	public double evaluate(Problem problem) {
		fitness = problem.evaluate(params);
		return fitness;
	}

	@Override
	public double[] getValues(Problem problem) {
		return params;
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(double value, int i) {
		params[i] = value;
		
	}

}
