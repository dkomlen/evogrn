package evogrn.test;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.jdom.Element;

import evogrn.xml.XmlSerializable;

public class Log implements XmlSerializable {

	String content;
	boolean toConsole;
	static Log log = null;
	private PropertyChangeSupport propertyChangeSupport;
	
	public static Log getLog() {
		if (log == null) log = new Log(true);
		return log;
	}
	
	public Log(boolean toConsole){
		content = "";
		this.toConsole = toConsole;
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	
	public void clear(){
		content = "";
	}
	
	public void write(String txt){
		content += txt + "\n";
		if (toConsole) System.out.println(txt);
		firePropertyChange("log", null, txt);
	}
	
	@Override
	public void fromXmlElement(Element el) {
		
	} 
	@Override
	public Element toXmlElement() {
		Element el = new Element("log"); 
		el.addContent(content);
		return el;
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);

	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	private void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}
}
