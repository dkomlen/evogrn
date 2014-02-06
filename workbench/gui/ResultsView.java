package evogrn.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import evogrn.alg.Individual;
import evogrn.dataset.MicroArrayData;
import evogrn.model.LTMParams;
import evogrn.model.LinearTVModel;
import evogrn.problem.GRNInferenceProblem;
import evogrn.problem.Problem;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class ResultsView extends JInternalFrame {
	private JPanel panel;
	private JButton btnNewButton;
	private File resFile;
	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	public ResultsView(File file) {
		
		this.resFile = file;
		setResizable(true);
		setClosable(true);
		setMaximizable(true);
		setBounds(100, 100, 602, 399);
		final RSyntaxTextArea textArea = new RSyntaxTextArea();
		textArea.setCaretPosition(0);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		textArea.setCodeFoldingEnabled(true);
		textArea.setClearWhitespaceLinesEnabled(false);
		textArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_XML);
		
		try {
			textArea.read(new FileReader(file), null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setTitle("Results - " + file.getName());
		RTextScrollPane scrollPane = new RTextScrollPane(textArea, true);
		getContentPane().add(scrollPane);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(100,30));
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(null);
		
		btnNewButton = new JButton("Simulate");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulate();
			}

		});
		btnNewButton.setBounds(8, 4, 89, 23);
		panel.add(btnNewButton);
		
		JButton btnSimulate = new JButton("Visualise");
		btnSimulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainWindow.getMain().openFrame(new TestRunView(resFile));
				
			}
		});
		btnSimulate.setBounds(104, 4, 89, 23);
		panel.add(btnSimulate);
		
		JButton btnSave = new JButton("Save changes");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				try {
					textArea.write(new FileWriter(resFile));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnSave.setBounds(203, 4, 112, 23);
		panel.add(btnSave);
		setVisible(true);
	}
	
	private void simulate() {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileFilter(new FileNameExtensionFilter("Text Files","txt"));
		jfc.setMultiSelectionEnabled(true);
		
		if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
			File simFile = jfc.getSelectedFile();
			try {
				SAXBuilder builder = new SAXBuilder();
				Document document = (Document) builder.build(resFile);
				Element root =  document.getRootElement();
				String[] spt = root.getChild("results").getChild("individual").getTextTrim().split("\n");
				double[] params = new double[spt.length];
				
				for (int i = 0; i < spt.length; ++i)
					params[i] = Double.parseDouble(spt[i]);
				
				String madFile = root.getChild("problem").getChild("dataset").getAttributeValue("location");
				
				MicroArrayData mad = MicroArrayData.readFromFile(madFile);
				LinearTVModel ltv = new LinearTVModel(mad.getGeneCount(), new LTMParams());
				ltv.setParams(params);
				MicroArrayData madSim = ltv.simulate(mad.getDataForTime(0), mad.getTime());
				
//				Problem p = new GRNInferenceProblem(ltv,new MicroArrayData[]{mad});
//				double fit = p.evaluate(params);
				
				madSim.writeToFile(simFile.getPath());
				MainWindow.getMain().openFrame(new DataSetView(simFile));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
