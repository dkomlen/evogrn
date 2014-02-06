package evogrn.alg.de.diff;

import evogrn.alg.de.Vector;

public class DeBest1 implements IDiff {
 
	@Override
	public Vector makeMutant(Vector[] vectors, Vector target, double f) {
		
		Vector[] r = new Vector[3];
		for (int i = 0; i < 2; ++i) {
			
			while (r[i] == null){
				Vector choice = vectors[target.getRandom().nextInt(vectors.length)];
				if (!choice.equals(target)) {
					for (int j = 0; j < i; ++j){
						if (choice.equals(r[i])) continue;
					}
					r[i] = choice;
				}
			}
		}
		
		Vector best = vectors[0];
		for (int i = 0; i < vectors.length; ++i) {
			if (vectors[i].getFitness() > best.getFitness())
				best = vectors[i];
		}
		
		Vector mutant = new Vector(target);
		for (int i = 0; i < target.getParamCount(); ++i) {
			mutant.setElem(i, best.getElem(i) + f * (r[1].getElem(i) - r[0].getElem(i)));
		}
			
		return mutant;
	}
	
	@Override
	public String toString() {
		return "DeBest1";
	}

}
