package evogrn.model;

import org.jdom.Element;

import evogrn.dataset.MicroArrayData;
import evogrn.problem.GRNInferenceProblem;
import evogrn.problem.Problem;

public class LinearTVSubModel implements GRNModel  {

	int id;
	int nGenes;
	
	double[] params;
	
	int ofAlpha, ofBeta, ofOmega, ofPhi;
	LTMParams ltmParams;
	
	
	public LinearTVSubModel(int id, int nGenes, LTMParams ltmParams) {
		this.id = id;
		this.ofAlpha = 0;
		this.ofBeta = nGenes;
		this.ofPhi = 2*ofBeta;
		this.ofOmega = 3*ofBeta;
		this.ltmParams = ltmParams;
		this.nGenes = nGenes;
		this.params = new double[3*nGenes + 1];
	}

	@Override
	public double evaluate(MicroArrayData[] mads) {
		assert (mads[0].getGeneCount() == nGenes) :
			"model and test data have different number of genes";
		
		double err = 0;
		for (MicroArrayData mad : mads) {
			
			double time[] = mad.getTime();
			int nSteps = mad.getStepCount();
			//double initData[] = mad.getDataForTime(0);
			
			MicroArrayData simMad = simulate(mad, time);
			
			for (int t = 1; t < nSteps; ++t) {
				
				double dataObs[] = mad.getDataForTime(t);
				double dataSim[] = simMad.getDataForTime(t);

				double e = (dataSim[id] - dataObs[id]) / dataObs[id];

				err += e*e;
			}
		}
		
		return err;
	}

	public MicroArrayData simulate(MicroArrayData mad, double[] time) {
		MicroArrayData madSim = new MicroArrayData(mad);
		
		int nSteps = time.length;
		//double[] z = new double[nGenes];
		
		for (int t = 0; t < nSteps -1; ++t) {
				
			double z = 0;
			for (int i = 0; i < nGenes; ++i) {
				z += getW(i,t+1) * madSim.getData(i, t);
			}
			
			double data = 1 / (1 + Math.pow(Math.E, -z));

			madSim.setData(id, t+1, data);
		
		}
		
		return madSim;
	}
	
	@Override
	public MicroArrayData simulate(double[] initData, double[] time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getParams() {
		return this.params;
	}

	@Override
	public int getParamCount() {
		return params.length;
	}

	@Override
	public double[] getParamMin() {
		double min[] = new double[getParamCount()];

		min[ofOmega] = ltmParams.omegaMin;
		for (int i = 0; i < nGenes; ++i) {
			min[ofAlpha + i] = ltmParams.alphaMin;
			min[ofBeta + i] = ltmParams.betaMin;
			min[ofPhi + i] = ltmParams.phiMin;
		}
		
		return min;
	}

	@Override
	public double[] getParamMax() {
		double max[] = new double[getParamCount()];
			
		max[ofOmega] = ltmParams.omegaMax;
		for (int i = 0; i < nGenes; ++i) {
			max[ofAlpha + i] = ltmParams.alphaMax;
			max[ofBeta + i] = ltmParams.betaMax;
			max[ofPhi + i] = ltmParams.phiMax;
		}
	
		return max;
	}
	
	private double getW(int i, double t) {
		double w = 0;
		double alpha = getAplha(i);
		double beta = getBeta (i);
		double omega = getOmega();
		double phi = getPhi (i);
		
		w = alpha * Math.sin(omega * t + phi) + beta;
		return w;
	}
	
	private double getPhi(int i) {
		return params[ofPhi + i];
	}

	private double getBeta(int i) {
		return params[ofBeta + i];
	}

	private double getOmega() {
		return params[ofOmega];
	}

	private double getAplha(int i) {
		return params[ofAlpha + i];
	}
	
	@Override
	public void setParams(double[] params) {
		this.params = params;
	}

	@Override
	public Element toXmlElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fromXmlElement(Element el) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double[][] getTransitionMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

}
