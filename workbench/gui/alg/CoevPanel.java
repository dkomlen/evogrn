package evogrn.gui.alg;

import javax.swing.JPanel;

import evogrn.alg.Algorithm;
import evogrn.alg.coev.CoevAlgorithm;
import evogrn.alg.coev.problem.CSPGrnInference;
import evogrn.alg.ga.gahooke.GAHookeJeeves;
import evogrn.alg.ga.population.RealInd;
import evogrn.model.LTMParams;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;

public class CoevPanel extends JPanel implements IAlgCreator {
	private JComboBox cboxAlg;
	private JScrollPane scpAlg;
	private JLabel lblSubproblemCount;
	private JTextField txtSub;
	private JLabel lblIterationsPerAlg;
	private JTextField txtIter;

	/**
	 * Create the panel.
	 */
	public CoevPanel() {
		setLayout(null);
		
		cboxAlg = new JComboBox();
		cboxAlg.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
			
				JPanel panel = null;
				switch(cboxAlg.getSelectedIndex()){
					case 0: panel = new GAPanel(); break;
					case 1: panel = new DEPanel(); break;
					case 2: panel = new PSOPanel(); break;
					case 3: panel = new HJPanel(); break;
					case 4: panel = new CoevPanel(); break;
				}
				scpAlg.getViewport().removeAll();
				scpAlg.setViewportView(panel);
				panel.setPreferredSize(new Dimension(0,200));
			
			}
		});
		cboxAlg.setModel(new DefaultComboBoxModel(new String[] {"Genetic Algorithm", "Differential Evolution", "Particle Swarm Optimization", "Hooke-Jeeves GA"}));
		cboxAlg.setBounds(10, 68, 291, 26);
		add(cboxAlg);
		
		scpAlg = new JScrollPane();
		scpAlg.setViewportView(new GAPanel());
		scpAlg.setBounds(10, 105, 291, 156);
		add(scpAlg);
		
		lblSubproblemCount = new JLabel("Subproblem count:");
		lblSubproblemCount.setBounds(10, 13, 104, 16);
		add(lblSubproblemCount);
		
		txtSub = new JTextField();
		txtSub.setText("5");
		txtSub.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSub.setColumns(10);
		txtSub.setBounds(124, 11, 86, 20);
		add(txtSub);
		
		lblIterationsPerAlg = new JLabel("Iterations per alg.:");
		lblIterationsPerAlg.setBounds(10, 42, 104, 16);
		add(lblIterationsPerAlg);
		
		txtIter = new JTextField();
		txtIter.setText("10");
		txtIter.setHorizontalAlignment(SwingConstants.RIGHT);
		txtIter.setColumns(10);
		txtIter.setBounds(124, 40, 86, 20);
		add(txtIter);

	}

	@Override
	public Algorithm<?> getAlgorithm() {
		ArrayList<Algorithm<?>> algs = new ArrayList<Algorithm<?>>();
		int nGenes = Integer.parseInt(txtSub.getText());
		int iter = Integer.parseInt(txtIter.getText());
		
		int[] iters = new int[nGenes];
		for (int i = 0; i < nGenes; ++i) {
			iters[i] = iter;
			
			algs.add(((IAlgCreator) ((JViewport) scpAlg.getComponent(0)).getComponent(0)).getAlgorithm());
		}
		
		return new CoevAlgorithm(algs, iters, new CSPGrnInference(new LTMParams()));
	}
}
