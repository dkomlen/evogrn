package evogrn.alg.de.cross;

import evogrn.alg.de.Vector;

/**
 * Strategija za krizanje vektora
 * @author dkomlen
 *
 */
public interface ICross { 

	/**
	 * Krizanje cinljnog i mutantskog vektora ovisno o strategiji
	 * @param target ciljni vektor
	 * @param mutant mutantni vektor
	 * @param pC vjerojatnost krizanja
	 * @return rezultat krizanja
	 */
	public Vector apply (Vector target, Vector mutant, double pC);
}
