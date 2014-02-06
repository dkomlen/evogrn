package evogrn.alg.coev.problem;

import java.util.Arrays;

import org.jdom.Element;

import evogrn.alg.coev.CoevIndividual;
import evogrn.problem.Problem;

public class CopyOfCSPSimple extends CoevSubProblem {

	double[] params;
	
	public CopyOfCSPSimple(){}
	
	
	public CopyOfCSPSimple(int id, int n, Problem p) {
		super(id,n,p);
		params = new double[p.getParamCount()];
	}
	
	private void setParams(double[] p, int from) {
		for (double d : p)
			params[from++] = d;
	}

	@Override
	protected double eval(double[] par) {

		int k = mainProblem.getParamCount() / subProbCount;
		setParams(par, id * k);
				
		return mainProblem.evaluate(params);
	}
	
	public void setParams(CoevIndividual ind){
		params = ind.getValues(mainProblem);
	}

	@Override
	public CoevSubProblem getInstance(int id, int n, Problem mainProblem) {
		return new CopyOfCSPSimple(id,n,mainProblem);	
	}

	@Override
	public Element toXmlElement() {
		Element el = new Element("csp");
		el.setAttribute("name", "CSPSimple");
		return el;
	}
}
