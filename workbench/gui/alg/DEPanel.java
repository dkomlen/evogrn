package evogrn.gui.alg;

import javax.swing.JPanel;

import evogrn.alg.Algorithm;
import evogrn.alg.de.DeAlgorithm;
import evogrn.alg.de.cross.ExponentialCross;
import evogrn.alg.de.cross.ICross;
import evogrn.alg.de.cross.UniformCross;
import evogrn.alg.de.diff.DeBest1;
import evogrn.alg.de.diff.DeRand1;
import evogrn.alg.de.diff.DeRand2;
import evogrn.alg.de.diff.IDiff;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class DEPanel extends JPanel implements IAlgCreator {
	private JLabel lblPopSize;
	private JTextField txtPopSize;
	private JLabel lblNewLabel;
	private JComboBox cboxDiff;
	private JLabel lblCrossover;
	private JComboBox cboxCross;
	private JLabel lblParamF;
	private JTextField txtF;
	private JLabel lblParamPc;
	private JTextField txtPc;

	/**
	 * Create the panel.
	 */
	public DEPanel() {
		setLayout(null);
		
		lblPopSize = new JLabel("Pop. size:");
		lblPopSize.setBounds(0, 0, 76, 16);
		add(lblPopSize);
		
		txtPopSize = new JTextField();
		txtPopSize.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPopSize.setText("100");
		txtPopSize.setBounds(78, -2, 86, 20);
		add(txtPopSize);
		txtPopSize.setColumns(10);
		
		lblNewLabel = new JLabel("Diferentiation:");
		lblNewLabel.setBounds(0, 25, 86, 16);
		add(lblNewLabel);
		
		cboxDiff = new JComboBox();
		cboxDiff.setModel(new DefaultComboBoxModel(new String[] {"DeBest1", "DeRand1", "DeRand2"}));
		cboxDiff.setBounds(78, 24, 107, 20);
		add(cboxDiff);
		
		lblCrossover = new JLabel("Crossover:");
		lblCrossover.setBounds(0, 80, 86, 16);
		add(lblCrossover);
		
		cboxCross = new JComboBox();
		cboxCross.setModel(new DefaultComboBoxModel(new String[] {"Exponential", "Uniform"}));
		cboxCross.setBounds(78, 79, 107, 20);
		add(cboxCross);
		
		lblParamF = new JLabel("Param f:");
		lblParamF.setBounds(0, 55, 76, 16);
		add(lblParamF);
		
		txtF = new JTextField();
		txtF.setText("0.5");
		txtF.setHorizontalAlignment(SwingConstants.RIGHT);
		txtF.setColumns(10);
		txtF.setBounds(78, 54, 86, 20);
		add(txtF);
		
		lblParamPc = new JLabel("Param pC:");
		lblParamPc.setBounds(0, 107, 76, 16);
		add(lblParamPc);
		
		txtPc = new JTextField();
		txtPc.setText("0.9");
		txtPc.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPc.setColumns(10);
		txtPc.setBounds(78, 105, 86, 20);
		add(txtPc);

	}

	@Override
	public Algorithm<?> getAlgorithm() {
		
		IDiff diff = null;
		ICross cross = null;
		
		switch (cboxCross.getSelectedIndex()) {
			case 0: cross = new ExponentialCross(); break;
			case 1: cross = new UniformCross(); break;
		}
		
		switch (cboxDiff.getSelectedIndex()) {
			case 0: diff = new DeBest1(); break;
			case 1: diff = new DeRand1(); break;
			case 2: diff = new DeRand2(); break;
		}
		
		int pop = Integer.parseInt(txtPopSize.getText());
		double f = Double.parseDouble(txtF.getText());
		double pc = Double.parseDouble(txtPc.getText());
				
		
		return new DeAlgorithm(pop, diff, cross, f, pc);
	}

}

