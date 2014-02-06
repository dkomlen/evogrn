package evogrn.alg.de.diff;

import evogrn.alg.de.Vector;

/**
 * Strategija diferncijacije za algortam diferncijske evolucije
 * @author dkomlen
 *
 */
public interface IDiff {
	
	Vector makeMutant(Vector[] vectors, Vector target, double f);

} 
