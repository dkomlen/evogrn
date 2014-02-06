package evogrn.xml;

import org.jdom.Element;

public interface XmlSerializable {

	public Element toXmlElement();
	public void fromXmlElement(Element el);
}
