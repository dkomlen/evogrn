package evogrn.model;

import org.jdom.Element;

import evogrn.xml.XmlSerializable;

public class LTMParams implements XmlSerializable{

	public double alphaMin = -20;
	public double alphaMax = 20;
	
	public double betaMin = -5;
	public double betaMax = 5;
	
	public double omegaMin = -Math.PI/2;
	public double omegaMax = Math.PI/2;
	
	public double phiMin = -Math.PI/2;
	public double phiMax = Math.PI/2;
	
//	public double alphaMin = 0;
//	public double alphaMax = 10;
//	
//	public double betaMin = -3;
//	public double betaMax = 3;
//	
//	public double omegaMin = -Math.PI/2;
//	public double omegaMax = Math.PI/2;
//	
//	public double phiMin = -Math.PI/2;
//	public double phiMax = Math.PI/2;
	
	@Override
	public Element toXmlElement() {
		Element el = new Element("params");
		
		Element e = new Element("alphaMin");
		e.setText(""+alphaMin);
		el.addContent(e);
		
		e = new Element("alphaMax");
		e.setText(""+alphaMax);
		el.addContent(e);
		
		e = new Element("betaMin");
		e.setText(""+betaMin);
		el.addContent(e);
		
		e = new Element("betaMax");
		e.setText(""+betaMax);
		el.addContent(e);
		
		e = new Element("omegaMin");
		e.setText(""+omegaMin);
		el.addContent(e);
		
		e = new Element("omegaMax");
		e.setText(""+omegaMax);
		el.addContent(e);
		
		e = new Element("phiMin");
		e.setText(""+phiMin);
		el.addContent(e);
		
		e = new Element("phiMax");
		e.setText(""+phiMax);
		el.addContent(e);
		
		return el;
	}
	@Override
	public void fromXmlElement(Element el) {
		// TODO Auto-generated method stub
		
	}
	
} 
