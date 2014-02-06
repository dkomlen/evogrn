package evogrn.gui;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.prefs.BackingStoreException;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.SwingWorker;

import evogrn.alg.Termination;
import evogrn.test.Log;
import evogrn.test.Test;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;
import java.awt.Font;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Box;

public class TestRun extends JInternalFrame implements PropertyChangeListener {

	Test test;
	int noTests;
	Termination term;
	
	String out;
	private JProgressBar barProgress;
	private JButton btnResults;
	private File results;
	
	private SwingWorker<Object, Object> worker;
	private JTabbedPane tabbedPane;
	private JScrollPane scrollPane; 
	private JTextArea txtConsole;
	private JProgressBar barRun;
	private JLabel lblTestProgress;
	private JLabel lblRunProgress;
	
	/**
	 * Create the frame.
	 */
	public TestRun(Test test,int noTests, Termination term, String out) {
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				worker.cancel(true);
				
			}
		});
		setClosable(true);
		setTitle("Test Run");
		setBounds(100, 100, 482, 323);
		
		this.test = test;
		this.noTests = noTests;
		this.term = term;
		
		this.out = out;
		getContentPane().setLayout(null);
		
		barProgress = new JProgressBar();
		barProgress.setBounds(86, 11, 373, 20);
		getContentPane().add(barProgress);
		
		btnResults = new JButton("View results");
		btnResults.setBounds(368, 255, 89, 32);
		btnResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { 
				MainWindow.getMain().openFrame(new ResultsView(results));
			}
		});
		btnResults.setEnabled(false);
		getContentPane().add(btnResults);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 71, 447, 173);
		getContentPane().add(tabbedPane);
		
		scrollPane = new JScrollPane();
		tabbedPane.addTab("Log", null, scrollPane, null);
		
		txtConsole = new JTextArea();
		txtConsole.setFont(new Font("Consolas", Font.PLAIN, 12));
		scrollPane.setRowHeaderView(txtConsole);
		
		barRun = new JProgressBar();
		barRun.setBounds(86, 38, 373, 20);
		getContentPane().add(barRun);
		
		lblTestProgress = new JLabel("Test progress:");
		lblTestProgress.setBounds(10, 11, 89, 14);
		getContentPane().add(lblTestProgress);
		
		lblRunProgress = new JLabel("Run progress:");
		lblRunProgress.setBounds(10, 38, 73, 14);
		getContentPane().add(lblRunProgress);
		
		setVisible(true);
		run();
	}

	private void run(){
		
		test.addPropertyChangeListener(this);
		
		worker = new SwingWorker<Object, Object>() {
			
			@Override
			protected Object doInBackground() throws Exception {
				test.run(noTests,  term, out);
				return null;
			}
			
		};
		
		worker.execute();
	}
	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("log")) {
			txtConsole.setText(txtConsole.getText() + (String) evt.getNewValue() + "\n");
			txtConsole.setCaretPosition(txtConsole.getText().length());
		}
		if (evt.getPropertyName().equals("test")) {
			barProgress.setValue((Integer) evt.getNewValue());
		}
		
		if (evt.getPropertyName().equals("alg")) {
			
			double d = (Double) evt.getNewValue();
			
			barRun.setValue((int) Math.round(d));
		}
		if (evt.getPropertyName().equals("test-end")) {
			
			results = new File(((String) evt.getNewValue()));
			btnResults.setEnabled(true);
		}
	}
}
