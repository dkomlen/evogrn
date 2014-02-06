package evogrn.gui.alg;

import javax.swing.JPanel;

import evogrn.alg.Algorithm;
import evogrn.alg.ga.GenerationalGA;
import evogrn.alg.ga.population.BinaryInd;
import evogrn.alg.ga.population.GAIndividual;
import evogrn.alg.ga.population.RealInd;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

public class GAPanel extends JPanel implements IAlgCreator{
	private JLabel label;
	private JTextField txtPopSize;
	private JLabel lblOffspringCoef;
	private JTextField txtOffspring;
	private JLabel lblGenotype;
	private JComboBox cboxGen;

	/**
	 * Create the panel.
	 */
	public GAPanel() {
		setLayout(null);
		
		label = new JLabel("Pop. size:");
		label.setBounds(18, 15, 76, 16);
		add(label);
		
		txtPopSize = new JTextField();
		txtPopSize.setText("100");
		txtPopSize.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPopSize.setColumns(10);
		txtPopSize.setBounds(110, 10, 86, 20);
		add(txtPopSize);
		
		lblOffspringCoef = new JLabel("Offspring coef:");
		lblOffspringCoef.setBounds(18, 41, 86, 16);
		add(lblOffspringCoef);
		
		txtOffspring = new JTextField();
		txtOffspring.setText("2");
		txtOffspring.setHorizontalAlignment(SwingConstants.RIGHT);
		txtOffspring.setColumns(10);
		txtOffspring.setBounds(110, 36, 86, 20);
		add(txtOffspring);
		
		lblGenotype = new JLabel("Genotype:");
		lblGenotype.setBounds(18, 66, 86, 16);
		add(lblGenotype);
		
		cboxGen = new JComboBox();
		cboxGen.setModel(new DefaultComboBoxModel(new String[] {"Real", "Binary"}));
		cboxGen.setBounds(104, 62, 107, 20);
		add(cboxGen);

	}

	@Override
	public Algorithm<?> getAlgorithm() {
		
		GAIndividual ind = null;
		
		switch (cboxGen.getSelectedIndex()) {
			case 0: ind = new RealInd(); break;
			case 1: ind = new BinaryInd(); break;
		}
		
		int pop = Integer.parseInt(txtPopSize .getText());
		int off = Integer.parseInt(txtOffspring.getText());
		
		return new GenerationalGA (ind, pop, off);
	}

}
