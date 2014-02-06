package evogrn.alg;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import evogrn.problem.Problem;

  
/**
 * Populacija kromosoma nad kojom se mogu vršiti brojne operacije poput
 * traženja podskupa najboljih jedinki, odabir nasumičnih jedinki, evaluacija
 * i slično.
 * @author dkomlen
 */
public class Population<T extends Individual> implements Iterable<T> {

	//Kromosmi su pohranjeni u strukturi TreeSet radi brzog pronalaženja najboljih.
	//Također paralelno se drže i u listi kako bi brzo mogli odabrati nasumične.
	private TreeSet<T> individuals;
	private T[] indList;
	private List<T> indArrList;
	
	private int size; //Broj kromosoma sa kojima će se inicijalizirati populacija 
	boolean listReady; //Zastavica koja označava da li je lista ažurirana u odnosu na strukturu TreeSet
	
	/**
	 * @return TreeSet koji predstavlja sve kromosme u populaciji
	 */
	public TreeSet<T> getIndTreeSet(){
		return individuals;
	}
	

	@SuppressWarnings("unchecked")
	public T[] getIndList(){

		if (!listReady) {
			
			indList = (T[])  individuals.toArray((T[]) Array.newInstance(individuals.first().getClass() , 1));
			listReady = true;
		}
		return indList;
	}
	
	public List<T> getIndArrList(){
		
		indArrList = Arrays.asList(getIndList());
		return indArrList;
	}
	
	public T get(int i) {
		return getIndList()[i];
	}
		
	/**
	 * @return Zadani broj kromosma s kojim je populacija inicijalizirana.
	 */
	public int getSize (){
		return size;
	}
	
	
	/**
	 * @return Trenutni broj kromosoma u populaciji.
	 */
	public int getCurrentSize() {
		return individuals.size();
	}
	
	
	/**
	 * Konstruktor kojim se zadaje željena veličina populacije.
	 * @param velicina - broj kromosma s kojim će se populacija inicijalizirati
	 */
	public Population(int velicina){
		individuals = new TreeSet<T>();
		this.size = velicina;
		this.listReady = false;
	}
	
	/**
	 * Copy konsturktor kojim se stvara kopija stare populacije.
	 * @param pop - stara populacija
	 */
	public Population (Population<T> pop) {
		individuals = new TreeSet<T>();
		this.size = pop.getSize();
		this.listReady = false;
		add(pop);
	}
	
	/**
	 * Inicijalizacije populacije kojom se stvaraju nasumični kromsomi prema zadanom problemu.
	 * @param prototip - primjerak kromosma koji predstavlja vrstu svih kromsoma u populaciji
	 * @param problem - optimizacijski problem koji se rješava
	 */
	public void init(T prototip, Problem problem) {
		individuals.clear();
		
		for (int i = 0; i < size; ++i){
			@SuppressWarnings("unchecked")
			T krom = (T) prototip.clone();
			krom.init(problem);
			krom.evaluate(problem);
			add(krom);
		}
		
		listReady = false;
	}
	
	/**
	 * Evaluacija svih kromosma u populaciji.
	 * @param problem - optimizacijski problem koji se rješava.
	 */
	public void evaluate(Problem problem) {
		
		TreeSet<T> newIndividuals = new TreeSet<T>();
		for (T k : individuals){
			k.evaluate(problem);
			newIndividuals.add(k);
		}
		this.individuals = newIndividuals;
		listReady = false;
	}
	
	/**
	 * Dodavanje čitave populacije u trenutnu populaciju.
	 * @param newPop - populacija koja se dodaje
	 */
	public void add(Population<T> newPop){
		for (T krom : newPop.getIndTreeSet()){
			add(krom);
		}
	}
	
	/**
	 * Dodavanje novog kromsoma u populaciju.
	 * Ukoliko se on već nalazi u njoj, dodaje se njegova kopija.
	 * @param krom - kromosom koji se dodaje
	 */
	@SuppressWarnings("unchecked")
	public void add (T krom) {
		
		if (contains(krom)) individuals.add((T) krom.clone());
		else individuals.add(krom);
		listReady = false;

	}
	
	/**
	 * Izbacivanje čitave populacije iz trenutne.
	 * @param pop - populacija koja se izbuje
	 * @return <i>true</i> ako su svi kromosomi iz zadane populacije izbačeni. <i>false</i> inače. 
	 */
	public boolean remove(Population<T> pop){
		boolean rezult = true;
		for (T krom : pop.getIndTreeSet()){
			rezult = rezult && remove(krom);
		}
		return rezult;
	}
	
	/**
	 * Izbacivanje kromosma iz populacije.
	 * @param krom - kromosom koji se izbacuje
	 * @return <i>true</i> ako je operacija uspijela, <i>false</i> inače.
	 */
	public boolean remove(T krom){
		listReady = false;
		return individuals.remove(krom);
	}
	
	/**
	 * Dohvaćanje najboljeg podskupa populacije od <i>n</i> jedinki.
	 * @param n - broj jedinki koji se dohvaća
	 * @return Populacija koja predstavlja najbolje u trenutnoj populaciji.
	 */
	public Population<T> bestPop(int n){
		Population<T> newPop = new Population<T>(n);
		int i = 0;
		for (T k : individuals){
			if (i++ >= n) break;
			newPop.add(k);
		}
		return newPop;
	}
	
	/**
	 * @return Najbolja jedinka u populaciji.
	 */
	public T best(){
		return individuals.first();
	}
	
	/**
	 * @return Najgora jedinka u populaciji.
	 */
	public T worst(){
		return individuals.last();
	}
	
	/**
	 * @return Nasumična jedinka iz populacije.
	 */
	@SuppressWarnings("unchecked")
	public T random(){

		if (!listReady) {
			
			indList = (T[])  individuals.toArray((T[]) Array.newInstance(individuals.first().getClass() , 1));
			listReady = true;
		}
		T novi = (T) indList[T.rand.nextInt(indList.length)];
		return novi;
	}
	
	/**
	 * Provjeravanje da li populacij sadrži određenu jedinku.
	 * @param krom - kromsom koji se provjerava
	 * @return <i>true</i> ako jedinka postoji u populaciji, <i>false</i> inače.
	 */
	public boolean contains(T krom){
		return individuals.contains(krom);
	}
	
	/**
	 * @param n - broj kromsoma koji se želi dohvatiti
	 * @return Populacija od <i>n</i> nasumično odabranih različitih kromosoma.
	 */
	public Population<T> randomPop(int n) {

		ArrayList<Population<T>> popList = new ArrayList<Population<T>>();
		Population<T> newPop = new Population<T>(size);
		popList.add(newPop);
		
		while (newPop.getCurrentSize() < n) {
			T newInd = random();
			if (newPop.getIndTreeSet().size() == size) {
				newPop = new Population<T>(size);
				popList.add(newPop);
			}
			if (!newPop.contains(newInd)) newPop.add(newInd);
		}
		newPop = new Population<T>(n);
		for (Population<T> pop : popList){
			newPop.add(pop);
		}
		return newPop;
	}
	
	/**
	 * Ispis populacije na standardni izlaz.
	 */
	public void print(){
		for (T krom : individuals){
			System.out.println(krom);
		}
	}

	@Override
	public Iterator<T> iterator() {
		return getIndArrList().iterator(); 
	}

}
