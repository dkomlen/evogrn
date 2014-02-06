package evogrn.model;

import org.jdom.Element;

import evogrn.dataset.MicroArrayData;
import evogrn.xml.XmlSerializable;

public interface GRNModel extends XmlSerializable {
	
	public double evaluate(MicroArrayData[] mads);
	public MicroArrayData simulate(double[] initData, double[] time);
	public double[] getParams();
	public int getParamCount();
	public double[] getParamMin();
	public double[] getParamMax();
	public void setParams(double[] params);
	public double[][] getTransitionMatrix();

} 
