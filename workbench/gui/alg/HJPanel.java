package evogrn.gui.alg;

import javax.swing.JPanel;

import evogrn.alg.Algorithm;
import evogrn.alg.ga.gahooke.GAHookeJeeves;
import evogrn.alg.ga.population.BinaryInd;
import evogrn.alg.ga.population.GAIndividual;
import evogrn.alg.ga.population.RealInd;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class HJPanel extends JPanel implements IAlgCreator {
	private JLabel label;
	private JTextField txtPop;
	private JLabel label_1;
	private JComboBox cboxGen;
	private JLabel lblInitDelta;
	private JTextField txtDelta;
	private JLabel lblPrecision;
	private JTextField txtPrec;

	/**
	 * Create the panel.
	 */
	public HJPanel() {
		setLayout(null);
		
		label = new JLabel("Pop. size:");
		label.setBounds(10, 16, 76, 16);
		add(label);
		
		txtPop = new JTextField();
		txtPop.setText("100");
		txtPop.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPop.setColumns(10);
		txtPop.setBounds(102, 11, 86, 20);
		add(txtPop);
		
		label_1 = new JLabel("Genotype:");
		label_1.setBounds(10, 47, 86, 16);
		add(label_1);
		
		cboxGen = new JComboBox();
		cboxGen.setModel(new DefaultComboBoxModel(new String[] {"Real", "Binary"}));
		cboxGen.setBounds(102, 45, 107, 20);
		add(cboxGen);
		
		lblInitDelta = new JLabel("Init. delta:");
		lblInitDelta.setBounds(10, 79, 76, 16);
		add(lblInitDelta);
		
		txtDelta = new JTextField();
		txtDelta.setText("1");
		txtDelta.setHorizontalAlignment(SwingConstants.RIGHT);
		txtDelta.setColumns(10);
		txtDelta.setBounds(102, 74, 86, 20);
		add(txtDelta);
		
		lblPrecision = new JLabel("Precision:");
		lblPrecision.setBounds(10, 111, 76, 16);
		add(lblPrecision);
		
		txtPrec = new JTextField();
		txtPrec.setText("0.000001");
		txtPrec.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPrec.setColumns(10);
		txtPrec.setBounds(102, 106, 86, 20);
		add(txtPrec);

	}

	@Override
	public Algorithm<?> getAlgorithm() {
		GAIndividual ind = null;
		
		switch (cboxGen.getSelectedIndex()) {
			case 0: ind = new RealInd(); break;
			case 1: ind = new BinaryInd(); break;
		}
		int pop = Integer.parseInt(txtPop.getText());
		int del = Integer.parseInt(txtDelta.getText());
		double prec = Double.parseDouble(txtPrec.getText());
		
		return new GAHookeJeeves(ind, pop, del, prec);
	}
}
