package evogrn.problem;

import org.jdom.Element;

import evogrn.dataset.MicroArrayData;
import evogrn.model.GRNModel;

public class GRNInferenceProblem extends Problem {

	private GRNModel model;
	private MicroArrayData[] dataSets;
	
	public GRNInferenceProblem(GRNModel model, MicroArrayData[] testData){
		super(model.getParamCount(), model.getParamMin(), model.getParamMax(), 0, 1);
		this.model = model;
		this.dataSets = testData;
	}
	
	public void setModel(GRNModel model) {
		this.model = model;
	}
	
	public GRNInferenceProblem(GRNModel model, double[] min, double[] max, double maxFun) {
		super(model.getParamCount(), min, max,0, maxFun);
	}
	
	@Override
	protected double eval(double[] params) {
		model.setParams(params);
		return -model.evaluate(dataSets);
	}

	public MicroArrayData[] getDataSets() {
		return dataSets;
	}

	@Override
	public Element toXmlElement() {
		Element el = new Element("problem");
		el.addContent(model.toXmlElement());
		return el;
	}

	@Override
	public void fromXmlElement(Element el) {
		// TODO Auto-generated method stub
		
	}

	
}
