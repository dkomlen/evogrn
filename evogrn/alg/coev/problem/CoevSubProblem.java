package evogrn.alg.coev.problem;

import java.util.Arrays;

import evogrn.alg.coev.CoevIndividual;
import evogrn.problem.Problem;
import evogrn.xml.XmlSerializable;

public abstract class CoevSubProblem extends Problem {

	
	Problem mainProblem;
	protected int id;
	int subProbCount;
	
	public CoevSubProblem(){
		super(0);
	}
	
	public CoevSubProblem(int id, int n, Problem p) {
		super(getParCount(id,n,p), getMin(id,n,p), getMax(id,n,p), p.getMinFun(), p.getMaxFun());
		
		this.mainProblem = p;
		this.id = id;
		this.subProbCount = n;
	}

	private static int getParCount(int id, int n, Problem p) {
		int m = p.getParamCount();
		if (id == n-1) return m - (m/n) * (n-1);
		return m/n;
	}

	private static double[] getMax(int id, int n, Problem p) {
		int m = p.getParamCount();
		return Arrays.copyOfRange(p.getMax(), id*(m/n), id == (n-1) ? m : (id+1) * (m/n));
	}

	private static double[] getMin(int id, int n, Problem p) {
		int m = p.getParamCount();
		return Arrays.copyOfRange(p.getMin(), id*(m/n), id == (n-1) ? m : (id+1) * (m/n));
	}
	
	public abstract void setParams(CoevIndividual ind);

	public abstract CoevSubProblem getInstance(int id, int n, Problem mainProblem);

}
