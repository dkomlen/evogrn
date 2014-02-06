package evogrn.model;

import org.jdom.Element;

import evogrn.dataset.MicroArrayData;

public class LinearTVModelBackup implements GRNModel {

	double[] params;
	int nGenes;
	int ofAlpha, ofBeta, ofOmega, ofPhi;
	LTMParams ltmParams;
	
	public LinearTVModelBackup(int nGenes, LTMParams params) {
		 
		this.ltmParams = params;
		this.nGenes = nGenes;
		this.ofAlpha = 0;
		this.ofBeta = nGenes*nGenes;
		this.ofPhi = 2*ofBeta;
		this.ofOmega = 3*ofBeta;
		this.params = new double[nGenes * (3*nGenes + 1)];
	}
	
	@Override
	public double evaluate(MicroArrayData[] mads) {
		
		assert (mads[0].getGeneCount() == nGenes) :
			"model and test data have different number of genes";
		
		double err = 0;
		for (MicroArrayData mad : mads) {
			
			double time[] = mad.getTime();
			int nSteps = mad.getStepCount();
			double initData[] = mad.getDataForTime(0);
			
			MicroArrayData simMad = simulate(initData, time);
			
			for (int t = 1; t < nSteps; ++t) {
				
				double dataObs[] = mad.getDataForTime(t);
				double dataSim[] = simMad.getDataForTime(t);
				
				for (int g = 0; g < nGenes; ++g){
					double e = (dataSim[g] - dataObs[g]) / dataObs[g];
					err += e*e;
				}
				
			}
			
		}
		
		return err;
	}

	public double[] getParamMin(){
		double min[] = new double[getParamCount()];
		
		for (int i = 0; i < nGenes; ++i) {
			
			min[ofOmega + i] = ltmParams.omegaMin;
			for (int j = 0; j < nGenes; ++j) {
				min[ofAlpha + nGenes*i+j] = ltmParams.alphaMin;
				min[ofBeta + nGenes*i+j] = ltmParams.betaMin;
				min[ofPhi + nGenes*i+j] = ltmParams.phiMin;
				
			}
		}
		
		return min;
	}
	
	public double[] getParamMax(){
		double max[] = new double[getParamCount()];
		
		for (int i = 0; i < nGenes; ++i) {
			
			max[ofOmega + i] = ltmParams.omegaMax;
			for (int j = 0; j < nGenes; ++j) {
				max[ofAlpha + nGenes*i+j] = ltmParams.alphaMax;
				max[ofBeta + nGenes*i+j] = ltmParams.betaMax;
				max[ofPhi + nGenes*i+j] = ltmParams.phiMax;
				
			}
		}
		
		return max;
	}
	
	@Override
	public MicroArrayData simulate(double[] initData, double[] time) {
		
		MicroArrayData mad = new MicroArrayData(nGenes, time);
		int nSteps = time.length;
		//double[] z = new double[nGenes];
		
		assert(initData.length == nGenes) : "wrong init data size";
		
		//setting initial microarray data
		for (int i = 0; i < nGenes; ++i) {
			mad.setData(i, 0, initData[i]);
		}
		
		for (int t = 1; t < nSteps; ++t) {
			
			
			for (int g = 0; g < nGenes; ++g){
				
				double z = 0;
				for (int j = 0; j < nGenes; ++j) {
					z += getW(g,j,time[t]) * mad.getData(j, t-1);
				}
				
				double data = 1 / (1 + Math.pow(Math.E, -z));
				mad.setData(g, t, data);
			}
		}
		return mad;
	}

	//da li int t ili double t?
	private double getW(int i, int j, double t) {
		double w = 0;
		double alpha = getAplha(i,j);
		double beta = getBeta (i,j);
		double omega = getOmega(i);
		double phi = getPhi (i,j);
		
		w = alpha * Math.sin(omega * t + phi) + beta;
		
		return w;
	}

	private double getPhi(int i, int j) {
		return params[ofPhi + nGenes*i+j];
	}

	private double getBeta(int i, int j) {
		return params[ofBeta + nGenes*i+j];
	}

	private double getOmega(int i) {
		return params[ofOmega + i];
	}

	private double getAplha(int i, int j) {
		return params[ofAlpha + nGenes*i+j];
	}
	

	@Override
	public double[] getParams() {
		return this.params;
	}

	@Override
	public void setParams(double[] params) {
		this.params = params;
	}

	@Override
	public int getParamCount() {
		return params.length;
	}

	@Override
	public Element toXmlElement() {
		Element el = new Element("LTV-model");
		el.setAttribute("gene-count", ""+nGenes);
		el.addContent(ltmParams.toXmlElement());
		
		return el;
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
