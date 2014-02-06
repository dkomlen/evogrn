package evogrn.gui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JEditorPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JProgressBar;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLabel;
import javax.swing.DefaultComboBoxModel;

import evogrn.alg.Algorithm;
import evogrn.alg.Termination;
import evogrn.alg.de.DeAlgorithm;
import evogrn.alg.de.cross.UniformCross;
import evogrn.alg.de.diff.DeBest1;
import evogrn.gui.alg.CoevPanel;
import evogrn.gui.alg.DEPanel;
import evogrn.gui.alg.GAPanel;
import evogrn.gui.alg.HJPanel;
import evogrn.gui.alg.IAlgCreator;
import evogrn.gui.alg.PSOPanel;
import evogrn.model.LTMParams;
import evogrn.test.Test;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JScrollPane;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.beans.PropertyVetoException;

import javax.swing.ScrollPaneConstants;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.JCheckBox;

public class NewTest extends JInternalFrame implements DropTargetListener {

	JList lstDataSets;
	private JTextField txtOuttxt;
	JComboBox cboxAlg;
	JSpinner spnNoTests;	
	JSpinner spnIter;
	private JScrollPane scpAlg;
	private JTabbedPane tabbedPane;
	private JPanel panel_4;
	private JLabel lblAlphaMin;
	private JTextField txtAlphaMin;
	private JLabel lblAlphaMax;
	private JTextField txtAlphaMax;
	private JLabel lblBetaMin;
	private JTextField txtBetaMin;
	private JLabel lblBetaMax;
	private JTextField txtBetaMax;
	private JLabel lblOmegaMin;
	private JTextField txtOmegaMin;
	private JLabel lblOmegaMax;
	private JTextField txtOmegaMax;
	private JLabel lblPhiMin;
	private JTextField txtPhiMin;
	private JLabel lblPhiMax;
	private JTextField txtPhiMax;
	private JButton btnRemove;
	private JPanel panel_5;
	private JCheckBox chkIter;
	private JCheckBox chkEval;
	private JSpinner spnEval;
	private JCheckBox chkTime;
	private JSpinner spnTime;
	private JLabel lblNewLabel;
	private JCheckBox chkStag;
	private JCheckBox chkFit;
	private JSpinner spnFit;
	private JLabel lblTestTitle;
	private JTextField txtTitle;
	/**
	 * Create the frame.
	 */
	public NewTest() {
		setTitle("New test");
		setClosable(true);
		setMaximizable(true);
		setBounds(100, 100, 384, 451);
		getContentPane().setLayout(null);
		
		JButton btnRun = new JButton("Start");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runTest();
			}

			
		});
		btnRun.setBounds(269, 380, 89, 31);
		getContentPane().add(btnRun);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 10, 346, 359);
		getContentPane().add(tabbedPane);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Algorithm", null, panel_1, null);
		panel_1.setBorder(null);
		panel_1.setLayout(null);
		
		
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
		cboxAlg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
			}
		});
		cboxAlg.setModel(new DefaultComboBoxModel(new String[] {"Genetic Algorithm", "Differential Evolution", "Particle Swarm Optimization", "Hooke-Jeeves GA", "Coevolutionary Algorithm"}));
		cboxAlg.setBounds(10, 22, 321, 26);
		panel_1.add(cboxAlg);
		
		scpAlg = new JScrollPane();
		scpAlg.setBounds(10, 61, 321, 259);
		JPanel pan = new GAPanel();
		scpAlg.setViewportView(pan);
		pan.setPreferredSize(new Dimension(0,200));
		panel_1.add(scpAlg);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Datasets", null, panel_3, null);
		panel_3.setBorder(null);
		panel_3.setLayout(null);
		
		lstDataSets = new JList();
		lstDataSets.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lstDataSets.setBounds(10, 22, 291, 87);
		lstDataSets.setDragEnabled(true);
		lstDataSets.setTransferHandler(new TransferHandler() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
		
			public boolean canImport(TransferHandler.TransferSupport info) {
                // for the demo, we'll only support drops (not clipboard paste)
                if (!info.isDrop()) {
                    return false;
                }
 
                return true;
            }
 
            public boolean importData(TransferHandler.TransferSupport info) {
                // if we can't handle the import, say so
                if (!canImport(info)) {
                    return false;
                }

                String data;
                try {
                    data = (String) info.getTransferable().getTransferData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
                
                DefaultListModel model = new DefaultListModel();
                for (int i = 0; i < lstDataSets.getModel().getSize(); ++i)
                	model.add(i,lstDataSets.getModel().getElementAt(i));
                	
                File f = new File(data);
                if (f.isDirectory()){
                	for (File file : f.listFiles()){
                		model.add(lstDataSets.getModel().getSize(), file.getPath());
                	}
                } else {
                	model.add(lstDataSets.getModel().getSize(), data);
                }
                lstDataSets.setModel(model);
                return true;
            }
			
		} );
		
		lstDataSets.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		lstDataSets.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		panel_3.add(lstDataSets);
		
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setFileFilter(new FileNameExtensionFilter("Text Files","txt"));
				jfc.setMultiSelectionEnabled(true);
			
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					DefaultListModel model = new DefaultListModel();
					for (File f : jfc.getSelectedFiles()){
						model.addElement(f.getPath());
					}
					lstDataSets.setModel(model);
				}
			}
		});
		btnAdd.setBounds(10, 120, 89, 23);
		panel_3.add(btnAdd);
		
		btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (lstDataSets.getSelectedIndex() < 0) return;
				DefaultListModel model = (DefaultListModel) lstDataSets.getModel();
				model.remove(lstDataSets.getSelectedIndex());
			}
		});
		btnRemove.setBounds(109, 120, 89, 23);
		panel_3.add(btnRemove);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Parameters", null, panel_2, null);
		panel_2.setBorder(null);
		panel_2.setLayout(null);
		
		txtOuttxt = new JTextField();
		txtOuttxt.setText("out.xml");
		txtOuttxt.setBounds(106, 21, 114, 20);
		panel_2.add(txtOuttxt);
		txtOuttxt.setColumns(10);
		
		spnNoTests = new JSpinner();
		spnNoTests.setModel(new SpinnerNumberModel(new Integer(5), new Integer(1), null, new Integer(1)));
		spnNoTests.setBounds(106, 50, 72, 20);
		panel_2.add(spnNoTests);
		
		JLabel lblResultsFilename = new JLabel("Results filename:");
		lblResultsFilename.setBounds(10, 23, 102, 16);
		panel_2.add(lblResultsFilename);
		
		JLabel lblIterations = new JLabel("Number of runs:");
		lblIterations.setBounds(10, 51, 102, 16);
		panel_2.add(lblIterations);
		
		panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Run termination", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_5.setBounds(10, 91, 321, 164);
		panel_2.add(panel_5);
		panel_5.setLayout(null);
		
		chkIter = new JCheckBox("Max iterations:");
		chkIter.setSelected(true);
		chkIter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spnIter.setEnabled(chkIter.isSelected());
			}
		});
		chkIter.setBounds(6, 24, 112, 23);
		panel_5.add(chkIter);
		
		spnIter = new JSpinner();
		spnIter.setBounds(133, 24, 72, 20);
		panel_5.add(spnIter);
		spnIter.setModel(new SpinnerNumberModel(new Integer(1000), new Integer(1), null, new Integer(1)));
		
		chkEval = new JCheckBox("Max evaluations:");
		chkEval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spnEval.setEnabled(chkEval.isSelected());
			}
		});
		chkEval.setBounds(6, 50, 121, 23);
		panel_5.add(chkEval);
		
		spnEval = new JSpinner();
		spnEval.setEnabled(false);
		spnEval.setBounds(133, 50, 72, 20);
		panel_5.add(spnEval);
		
		chkTime = new JCheckBox("Max time:");
		chkTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spnTime.setEnabled(chkTime.isSelected());
			}
		});
		chkTime.setBounds(6, 76, 112, 23);
		panel_5.add(chkTime);
		
		spnTime = new JSpinner();
		spnTime.setEnabled(false);
		spnTime.setModel(new SpinnerNumberModel(new Integer(100), new Integer(10), null, new Integer(1)));
		spnTime.setBounds(133, 76, 72, 20);
		panel_5.add(spnTime);
		
		lblNewLabel = new JLabel("sec");
		lblNewLabel.setBounds(215, 80, 46, 14);
		panel_5.add(lblNewLabel);
		
		chkStag = new JCheckBox("Stagnation");
		
		chkStag.setBounds(6, 102, 97, 23);
		panel_5.add(chkStag);
		
		chkFit = new JCheckBox("Max fitness:");
		chkFit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spnFit.setEnabled(chkFit.isSelected());
			}
		});
		chkFit.setBounds(6, 128, 112, 23);
		panel_5.add(chkFit);
		
		spnFit = new JSpinner();
		spnFit.setEnabled(false);
		spnFit.setBounds(133, 128, 72, 20);
		panel_5.add(spnFit);
		
		lblTestTitle = new JLabel("Test title:");
		lblTestTitle.setBounds(10, 266, 65, 14);
		panel_2.add(lblTestTitle);
		
		txtTitle = new JTextField();
		txtTitle.setBounds(85, 266, 202, 20);
		panel_2.add(txtTitle);
		txtTitle.setColumns(10);
		
		panel_4 = new JPanel();
		tabbedPane.addTab("Model params", null, panel_4, null);
		panel_4.setLayout(null);
		
		lblAlphaMin = new JLabel("Alpha min:");
		lblAlphaMin.setBounds(55, 40, 123, 15);
		panel_4.add(lblAlphaMin);
		
		txtAlphaMin = new JTextField();
		txtAlphaMin.setBounds(150, 36, 114, 19);
		txtAlphaMin.setColumns(10);
		panel_4.add(txtAlphaMin);
		
		lblAlphaMax = new JLabel("Alpha max:");
		lblAlphaMax.setBounds(55, 63, 123, 15);
		panel_4.add(lblAlphaMax);
		
		txtAlphaMax = new JTextField();
		txtAlphaMax.setColumns(10);
		txtAlphaMax.setBounds(150, 59, 114, 19);
		panel_4.add(txtAlphaMax);
		
		lblBetaMin = new JLabel("Beta min:");
		lblBetaMin.setBounds(55, 102, 123, 15);
		panel_4.add(lblBetaMin);
		
		txtBetaMin = new JTextField();
		txtBetaMin.setColumns(10);
		txtBetaMin.setBounds(150, 98, 114, 19);
		panel_4.add(txtBetaMin);
		
		lblBetaMax = new JLabel("Beta max:");
		lblBetaMax.setBounds(55, 125, 123, 15);
		panel_4.add(lblBetaMax);
		
		txtBetaMax = new JTextField();
		txtBetaMax.setColumns(10);
		txtBetaMax.setBounds(150, 121, 114, 19);
		panel_4.add(txtBetaMax);
		
		lblOmegaMin = new JLabel("Omega min:");
		lblOmegaMin.setBounds(55, 161, 123, 15);
		panel_4.add(lblOmegaMin);
		
		txtOmegaMin = new JTextField();
		txtOmegaMin.setColumns(10);
		txtOmegaMin.setBounds(150, 157, 114, 19);
		panel_4.add(txtOmegaMin);
		
		lblOmegaMax = new JLabel("Omega max:");
		lblOmegaMax.setBounds(55, 184, 123, 15);
		panel_4.add(lblOmegaMax);
		
		txtOmegaMax = new JTextField();
		txtOmegaMax.setColumns(10);
		txtOmegaMax.setBounds(150, 180, 114, 19);
		panel_4.add(txtOmegaMax);
		
		lblPhiMin = new JLabel("Phi min:");
		lblPhiMin.setBounds(55, 216, 123, 15);
		panel_4.add(lblPhiMin);
		
		txtPhiMin = new JTextField();
		txtPhiMin.setColumns(10);
		txtPhiMin.setBounds(150, 212, 114, 19);
		panel_4.add(txtPhiMin);
		
		lblPhiMax = new JLabel("Phi max:");
		lblPhiMax.setBounds(55, 239, 123, 15);
		panel_4.add(lblPhiMax);
		
		txtPhiMax = new JTextField();
		txtPhiMax.setColumns(10);
		txtPhiMax.setBounds(150, 235, 114, 19);
		panel_4.add(txtPhiMax);

		LTMParams ltm = new LTMParams();
		
		txtAlphaMax.setText("" + ltm.alphaMax);
		txtAlphaMin.setText("" + ltm.alphaMin);
		
		txtBetaMax.setText("" + ltm.betaMax);
		txtBetaMin.setText("" + ltm.betaMin);
		
		txtOmegaMax.setText("" + ltm.omegaMax);
		txtOmegaMin.setText("" + ltm.omegaMin);
		
		txtPhiMax.setText("" + ltm.phiMax);
		txtPhiMin.setText("" + ltm.phiMin);
		
		
		//setVisible(true);

	}
	private void runTest() {
		
		Algorithm<?> alg = ((IAlgCreator) ((JViewport) scpAlg.getComponent(0)).getComponent(0)).getAlgorithm();
		 String[] input = new String[lstDataSets.getModel().getSize()];
		 for (int i = 0; i < input.length; ++i)
			 input [i] = (String) lstDataSets.getModel().getElementAt(i);
		
		
		int noTests = Integer.parseInt(spnNoTests.getValue().toString());
		String output = txtOuttxt.getText();
		
		LTMParams ltm = new LTMParams();
		ltm.alphaMax = Double.parseDouble(txtAlphaMax.getText());
		ltm.alphaMin = Double.parseDouble(txtAlphaMin.getText());
		ltm.betaMax = Double.parseDouble(txtBetaMax.getText());
		ltm.betaMin = Double.parseDouble(txtBetaMin.getText());
		
		ltm.omegaMax = Double.parseDouble(txtOmegaMax.getText());
		ltm.omegaMin = Double.parseDouble(txtOmegaMin.getText());
		ltm.phiMax = Double.parseDouble(txtPhiMax.getText());
		ltm.phiMin = Double.parseDouble(txtPhiMin.getText());
		Test test;
		
		int maxIter = chkIter.isSelected() ? Integer.parseInt(spnIter.getValue().toString()) : -1;
		int maxEval = chkEval.isSelected() ? Integer.parseInt(spnEval.getValue().toString()) : -1;
		int maxTime = chkTime.isSelected() ? Integer.parseInt(spnTime.getValue().toString()) : -1;
		int maxFit = chkFit.isSelected() ? Integer.parseInt(spnFit.getValue().toString()) : -1;
		boolean stag = chkStag.isSelected();
		String title = txtTitle.getText();
		
		try { 
			test = new Test(title,input, alg, ltm, false);
			
			Termination term = new Termination(maxIter, maxEval, maxTime, maxFit, stag);
			
			MainWindow.getMain().openFrame(new TestRun(test,noTests, term, output));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void dragExit(DropTargetEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void drop(DropTargetDropEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
