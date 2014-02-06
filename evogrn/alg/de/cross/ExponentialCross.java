package evogrn.alg.de.cross;

import java.util.Random;

import evogrn.alg.de.Vector;

public class ExponentialCross implements ICross {

	@Override
	public Vector apply(Vector target, Vector mutant, double pC) {
		
		int n = target.getParamCount();
		Random rnd = target.getRandom();
		Vector trial = new Vector (target);
		 
		int i = rnd.nextInt(n), k = 0;
		
		do {
			trial.setElem(i, mutant.getElem(i));
			i = (i + 1) % n;
			k++;
		} while (rnd.nextDouble() < pC && k <= trial.getParamCount());
		
		return trial;
	}
	
	@Override
	public String toString() {
		return "Exponential";
	}

}
