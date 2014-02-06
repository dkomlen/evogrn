package evogrn.alg.ga.population;

import evogrn.alg.Population;
import evogrn.problem.Problem;

/**
 * Vrsta kromosoma kod kojeg su sve varijable predstavljene binarnim zapisom
 * @author dkomlen
 */
public class BinaryInd extends GAIndividual {

	private boolean[] bits; //Bitovi koji predstavljaju varijable
	private int bitCount; //Broj bitova po svakoj varijabli
	
	public boolean[] getBits(){
		return bits;
	}
	
	public BinaryInd(){
		//potrebno za stvaranje prototipa
	}
	
	/**
	 * @param n - broj varijabli
	 * @param nBits - broj bitova za svaku varijablu
	 */ 
	public BinaryInd(int n, int nBits){
		super(n);
		this.bitCount = nBits;
		bits = new boolean[n*nBits];
		init(null);
	}
	
	/**
	 * @param n - broj varijabli
	 * @param nBits - broj bitova po svakoj varijabli
	 * @param fitness - zadana dobrota
	 */
	public BinaryInd(int n, int nBits, double fitness){
		super(n, fitness);
		this.bitCount = nBits;
		bits = new boolean[n*nBits];
		init(null);
	}
	
	@Override
	public double[] getPhenotype(Problem problem) {
		
		double[] vector = new double[nParams];
		double max[] = problem.getMax();
		double min[] = problem.getMin();
		
		for (int i = 0; i < nParams; ++i){
			double num = decode(i) / (Math.pow(2, bitCount) -1);
			num *= max[i] - min[i];
			vector[i] = num + min[i];
		}
		return vector;
	}

	/**
	 * Dekodiranje binarnog zapisa u cijelobrojni.
	 * @param n - redni broj varijable u kromsomu
	 * @return Cijelobrojna vrijednost koju odgovarajući binarni zapis predstavlja.
	 */
	private int decode(int n){
		int num = 0;
		for (int i = 0; i < bitCount; ++i){
			num *= 2;
			num += bits[i+n*bitCount] ? 1 : 0;
		}
		return num;
	}
	@Override
	public void init(Problem p) {
		
		for (int i = 0; i < bitCount*nParams; ++i){
			bits[i] = rand.nextBoolean();
		}
	}

	@Override
	public Population<GAIndividual> crossover(GAIndividual parent, double pC) {
		
		//Križanje sa jednom točkom prijeloma 
		
		BinaryInd par = (BinaryInd) parent;
		BinaryInd child1 = (BinaryInd) par.clone();
		BinaryInd child2 = (BinaryInd) par.clone();
		Population<GAIndividual> offsprings = new Population<GAIndividual>(2);
		
		//Računanje točke prijeloma
		int point = rand.nextInt(bits.length);
		
		if (rand.nextDouble() < pC) {
			for (int i = 0; i < bits.length; ++i){
				
				if (i < point){
					child1.getBits()[i] = bits[i];
					child2.getBits()[i] = par.getBits()[i];
				} else {
					child2.getBits()[i] = bits[i];
					child1.getBits()[i] = par.getBits()[i];
				}
			}
			offsprings.add(child1);
			offsprings.add(child2);
		} else {
			//Ukoliko nije zadovoljena vjerojatnost, potomci su kopije roditelja
			
			offsprings.add((GAIndividual) this.clone());
			offsprings.add((GAIndividual) parent.clone());
		}
		
		return offsprings;
	}

	@Override
	public void mutate(double pM) {
		
		for (int i = 0; i < bits.length; ++i){
			if (rand.nextDouble() < pM) bits[i] = !bits[i]; 
		}

	}
	@Override
	public Object clone() {
		
		BinaryInd newInd = new BinaryInd(nParams, bitCount, fitness);
		for (int i = 0; i < bits.length; ++i){
			newInd.getBits()[i] = bits[i];
		}
		
		return newInd;
	}
	
	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < nParams; ++i){
			for (int j = 0; j< bitCount; ++j){
				str += bits[j+i*nParams] ? "1" : "0";
			}
			if (i < nParams-1) str += ", ";
		}
		str += ": " + fitness;
		return str;
	}

	@Override
	public void setValue(double value, int i) {
		// TODO Auto-generated method stub
		
	}
}
