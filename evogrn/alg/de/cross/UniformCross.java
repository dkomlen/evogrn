package evogrn.alg.de.cross;

import java.util.Random;

import evogrn.alg.de.Vector;

public class UniformCross implements ICross {

	@Override
	public Vector apply(Vector target, Vector mutant, double pC) {
		
		int n = target.getParamCount();
		Random rnd = target.getRandom();
		Vector trial = new Vector (target);
		int j = rnd.nextInt(n);
		 
		for (int i = 0; i < n; ++i) {
			if (i == j || rnd.nextDouble() < pC) {
				trial.setElem(i, mutant.getElem(i));
			}
		}
		
		return trial;
	}
	
	@Override
	public String toString() {
		return "Uniform";
	}

}
