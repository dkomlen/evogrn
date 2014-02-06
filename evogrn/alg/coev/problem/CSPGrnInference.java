package evogrn.alg.coev.problem;

import java.util.ArrayList;

import org.jdom.Element;

import evogrn.alg.coev.CoevIndividual;
import evogrn.dataset.MicroArrayData;
import evogrn.model.GRNModel;
import evogrn.model.LTMParams;
import evogrn.model.LinearTVModel;
import evogrn.model.LinearTVSubModel;
import evogrn.problem.GRNInferenceProblem;
import evogrn.problem.Problem;

public class CSPGrnInference extends CoevSubProblem {

	GRNModel model;
	LinearTVSubModel subModel;
	//GRNInferenceProblem grnProblem;
	LTMParams ltmParams;
	MicroArrayData[] dataSets;
	MicroArrayData[] testSets; //podatkovni skupovi za evaluaciju
	
	double[][] initValues;
	double[][] timeValues;
	boolean start = true;
	
	public CSPGrnInference(LTMParams params){
		this.ltmParams = params;
	}
	
	private CSPGrnInference(int id, int n, GRNInferenceProblem p) {
		super(id,n,p);
		//grnProblem = p;
		subModel = new LinearTVSubModel(id,n, ltmParams);
		model = new LinearTVModel(n, ltmParams);
		
		this.dataSets = p.getDataSets();
		this.testSets = p.getDataSets();
		
		MicroArrayData[] mads = p.getDataSets();
		this.initValues = new double[mads.length][];
		this.timeValues = new double[mads.length][];
		
		int i = 0;
		for (MicroArrayData mad : mads){
			this.initValues[i] = mad.getDataForTime(0);
			this.timeValues[i++] = mad.getTime();
		}
	}
	
	@Override
	public CoevSubProblem getInstance(int id, int n, Problem mainProblem) {
		return new CSPGrnInference(id,n,(GRNInferenceProblem) mainProblem);	
	}

	@Override
	public void setParams(CoevIndividual ind) {
		
		ArrayList<MicroArrayData> mads = new ArrayList<MicroArrayData>();
		
		double params[] = ind.getValues(mainProblem);
		model.setParams(params);
		for (int i = 0; i < timeValues.length; ++i) {
			 MicroArrayData mad = model.simulate(initValues[i], timeValues[i]);
			 mad.setDataForGene(id, testSets[i].getDataForGene(id));
			 mads.add(mad);
		}
		this.dataSets = mads.toArray(new MicroArrayData[] {});
	}

	@Override
	protected double eval(double[] params) {
		
		subModel.setParams(params);
		double ret = -subModel.evaluate(dataSets);

		return ret;

	}

	@Override
	public Element toXmlElement() {
		Element el = new Element("csp");
		el.setAttribute("name", "CSPGrnInference");
		return el;
	}
	
}
