package evogrn.gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import evogrn.dataset.DataSetManager;
import evogrn.dataset.MicroArrayData;
import javax.swing.JButton;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TestRunView extends JInternalFrame {
	private JButton btnExport;
	private BufferedImage image;

	/**
	 * Create the frame.
	 */
	public TestRunView(File file) {
		setMaximizable(true);
		setClosable(true);
		setBounds(100, 100, 450, 300);
		setSize(600, 406);
		try {
						
			JFreeChart chart = ChartFactory.createXYLineChart("Test - "+file.getName(), "iterations", "error", getDataSet(file), PlotOrientation.VERTICAL, true, false, false);
			chart.getTitle().setFont(new Font("Arial", Font.PLAIN, 12));
			image = chart.createBufferedImage(500,300);
			getContentPane().setLayout(null);
			JLabel lblChart = new JLabel();
			lblChart.setBounds(0, 0, 584, 350);
			lblChart.setIcon(new ImageIcon(image));
			
			getContentPane().add(lblChart);
			
			btnExport = new JButton("Export");
			btnExport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					JFileChooser jfc = new JFileChooser();
					jfc.setFileFilter(new FileNameExtensionFilter("Image files","png"));
					jfc.setMultiSelectionEnabled(true);
					
					if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
						
					    try {
							ImageIO.write(image, "png", jfc.getSelectedFile());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
			});
			btnExport.setBounds(45, 333, 89, 30);
			getContentPane().add(btnExport);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

setVisible(true);
	}
	
	
	private XYDataset getDataSet(File f) throws Exception{
		SAXBuilder builder = new SAXBuilder();
		
		Document document = (Document) builder.build(f);
		Element rootNode = document.getRootElement();
		List<Element> runs = rootNode.getChild("results").getChildren("run"); 
		
		XYSeriesCollection xyDataset = new XYSeriesCollection();
		for (int i = 0; i < runs.size(); ++i) {
		
			Element run = runs.get(i);
			String spt[] = run.getChild("log").getText().split("\n");
			
			XYSeries series = new XYSeries("Run "+i);
			for (int j = 0; j < spt.length; ++j) {
				int iter = Integer.parseInt(spt[j].split("\t")[0].split(":")[0]);
				double fit = Double.parseDouble(spt[j].split("\t")[1].replace(',', '.')); 
				series.add(iter,-fit);
			}
			
			
			xyDataset.addSeries(series);
		}
		return xyDataset;
	}
}
