package evogrn.alg.de.diff;

import evogrn.alg.de.Vector;

public class DeRand1 implements IDiff {

	@Override
	public Vector makeMutant(Vector[] vectors, Vector target, double f) {
		
		Vector[] r = new Vector[3];
		for (int i = 0; i < 3; ++i) {
			
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
		
		Vector mutant = new Vector(target);
		for (int i = 0; i < target.getParamCount(); ++i) {
			mutant.setElem(i, r[0].getElem(i) + f * (r[2].getElem(i) - r[1].getElem(i)));
		}
			
		return mutant;
	}

	@Override
	public String toString() {
		return "DeRand1";
	}
}
