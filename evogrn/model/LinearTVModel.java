package evogrn.model;

import org.jdom.Element;

import evogrn.dataset.MicroArrayData;
 
public class LinearTVModel implements GRNModel {

	double[] params;
	int nGenes;
	int ofAlpha, ofBeta, ofOmega, ofPhi,parPerGene;
	LTMParams ltmParams;
	
	public LinearTVModel(int nGenes, LTMParams params) {
		
		this.ltmParams = params;
		this.parPerGene = 3*nGenes + 1;
		this.nGenes = nGenes;
		this.ofAlpha = 0;
		this.ofBeta = nGenes;
		this.ofPhi = 2*ofBeta;
		this.ofOmega = 3*ofBeta;
		this.params = new double[nGenes * parPerGene];
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
			
			min[parPerGene*i + ofOmega] = ltmParams.omegaMin;
			for (int j = 0; j < nGenes; ++j) {
				min[parPerGene*i + ofAlpha + j] = ltmParams.alphaMin;
				min[parPerGene*i + ofBeta + j] = ltmParams.betaMin;
				min[parPerGene*i + ofPhi + j] = ltmParams.phiMin;
				
			}
		}
		
		return min;
	}
	
	public double[] getParamMax(){
		double max[] = new double[getParamCount()];
		
		for (int i = 0; i < nGenes; ++i) {
			
			max[parPerGene*i + ofOmega] = ltmParams.omegaMax;
			for (int j = 0; j < nGenes; ++j) {
				max[parPerGene*i + ofAlpha + j] = ltmParams.alphaMax;
				max[parPerGene*i + ofBeta + j] = ltmParams.betaMax;
				max[parPerGene*i + ofPhi + j] = ltmParams.phiMax;
				
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
		
		//promjenjeno for(t=1, nSteps)
		for (int t = 0; t < nSteps-1; ++t) {
			
			
			for (int g = 0; g < nGenes; ++g){
				
				double z = 0;
				for (int j = 0; j < nGenes; ++j) {
					//promjenjeno getData(t-1)
					z += getW(g,j,t+1) * mad.getData(j, t);
				}
				
				double data = 1 / (1 + Math.pow(Math.E, -z));
				//promjenjeno setData(t)
				mad.setData(g, t+1, data);
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
		return params[parPerGene*i + ofPhi + j];
	}

	private double getBeta(int i, int j) {
		return params[parPerGene*i + ofBeta + j];
	}

	private double getOmega(int i) {
		return params[parPerGene*i + ofOmega];
	}

	private double getAplha(int i, int j) {
		return params[parPerGene*i + ofAlpha + j];
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
		el.setAttribute("gene_count", ""+nGenes);
		el.addContent(ltmParams.toXmlElement());
		
		return el;
	}

	@Override
	public void fromXmlElement(Element el) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double[][] getTransitionMatrix() {
		
		double w[][] = new double[nGenes][nGenes];
		for (int i = 0; i < nGenes; ++i)
			for (int j = 0; j < nGenes; ++j) {
//				for (int k = 1; k <= 11; ++k){
//					w[i][j] = getW(i, j, k);
//				}
//				w[i][j] /= 11;
				w[i][j] = getW(i, j, 1);
			}
		
		return w;
	}

}
