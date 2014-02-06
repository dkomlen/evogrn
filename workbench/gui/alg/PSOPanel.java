package evogrn.gui.alg;

import javax.swing.JPanel;

import evogrn.alg.Algorithm;
import evogrn.alg.pso.PSOAlgorithm;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class PSOPanel extends JPanel implements IAlgCreator {
	private JLabel label;
	private JTextField txtPop;
	private JLabel lblParamK;
	private JTextField txtK;
	private JLabel lblParamC;
	private JTextField txtC1;
	private JLabel lblParamC_1;
	private JTextField txtC2;

	/**
	 * Create the panel.
	 */
	public PSOPanel() {
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
		
		lblParamK = new JLabel("Param k:");
		lblParamK.setBounds(10, 48, 76, 16);
		add(lblParamK);
		
		txtK = new JTextField();
		txtK.setText("2");
		txtK.setHorizontalAlignment(SwingConstants.RIGHT);
		txtK.setColumns(10);
		txtK.setBounds(102, 43, 86, 20);
		add(txtK);
		
		lblParamC = new JLabel("Param c1:");
		lblParamC.setBounds(10, 80, 76, 16);
		add(lblParamC);
		
		txtC1 = new JTextField();
		txtC1.setText("2");
		txtC1.setHorizontalAlignment(SwingConstants.RIGHT);
		txtC1.setColumns(10);
		txtC1.setBounds(102, 75, 86, 20);
		add(txtC1);
		
		lblParamC_1 = new JLabel("Param c2:");
		lblParamC_1.setBounds(10, 112, 76, 16);
		add(lblParamC_1);
		
		txtC2 = new JTextField();
		txtC2.setText("2");
		txtC2.setHorizontalAlignment(SwingConstants.RIGHT);
		txtC2.setColumns(10);
		txtC2.setBounds(102, 107, 86, 20);
		add(txtC2);

	}

	@Override
	public Algorithm<?> getAlgorithm() {
		int pop = Integer.parseInt(txtPop.getText());
		int k = Integer.parseInt(txtK.getText());
		int c1 = Integer.parseInt(txtC1.getText());
		int c2 = Integer.parseInt(txtC2.getText());
		
		return new PSOAlgorithm(pop,k,c1,c2);
	}

}
