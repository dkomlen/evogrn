package evogrn.alg;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.jdom.Element;

import evogrn.problem.Problem;
import evogrn.test.Log;
import evogrn.xml.XmlSerializable;

public abstract class Algorithm<T extends Individual> implements
		XmlSerializable {

	protected Population<T> population;
	protected T prototype;
	protected Problem problem;
	protected int iter = 0;
	protected long startTime;

	protected Log log = Log.getLog();
	protected int logInterval = 100;

	private PropertyChangeSupport propertyChangeSupport;

	public Algorithm(int popSize, T prototype) {
		population = new Population<T>(popSize);
		this.prototype = prototype;
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	protected abstract boolean runIteration(Termination term, Problem problem);

	public Individual run(Termination term, Problem problem) {

		startTime = System.currentTimeMillis();
		iter = 0;

		do {

			if (iter % logInterval == 0) {

				if (Thread.currentThread().isInterrupted())
					break;

				Double b = this.getBestInd().getFitness();

				firePropertyChange("alg", null,
						100 * term.percCompleted(this, problem));
				log(iter + ": \t" + b.toString().replace(".", ","));

			}
			// self-termination of algorithm
			if (!runIteration(term, problem))
				break;
			
			iter++;
			
		} while (!term.isOver(this, problem));

		return this.getBestInd();
	}

	public Population<T> getPopulation() {
		return population;
	}

	public double getTime() {
		double endTime = System.currentTimeMillis();
		return new Double((endTime - startTime) / 1000);
	}

	public Individual getBestInd() {
		return population.best();
	}

	public Individual getWorstInd() {
		return population.worst();
	}

	public Individual getRandomInd() {
		return population.random();
	}

	public void init(Problem problem) {
		this.problem = problem;
		population.init(prototype, problem);
	}

	public void evaluate(Problem problem) {
		population.evaluate(problem);
	}

	public T getPrototype() {
		return prototype;
	}

	public abstract String getName();

	public abstract List<Element> getXmlParams();

	protected Element paramToXml(String name, Object val) {
		Element el = new Element("param");
		el.setAttribute("name", name);
		el.setAttribute("val", val.toString());
		return el;
	}

	@Override
	public Element toXmlElement() {
		Element el = new Element("algorithm");
		el.setAttribute("name", getName());

		Element params = new Element("params");
		for (Element e : getXmlParams()) {
			params.addContent(e);
		}
		el.addContent(params);

		return el;
	}

	public double getFitnessAvg() {

		double avg = 0;
		for (Individual ind : population) {
			avg += ind.getFitness();
		}
		return avg / population.getCurrentSize();
	}

	public double getFitnessDev() {

		double dev = 0;
		double avg = getFitnessAvg();

		for (Individual ind : population) {
			dev += Math.pow(ind.getFitness() - avg, 2);
		}

		return Math.sqrt(dev / (population.getCurrentSize() - 1));

	}
	
	public void setLogInterval(int interval) {
		logInterval = interval;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	protected void log(String txt) {
		if (this.log != null)
		this.log.write(txt);
	}

	public int getIterCount() {
		return iter;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
		log.addPropertyChangeListener(listener);

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
