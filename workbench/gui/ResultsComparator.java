package evogrn.gui;

import java.awt.EventQueue;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import evogrn.dataset.DataSetManager;
import evogrn.dataset.MicroArrayData;
import evogrn.model.LTMParams;
import evogrn.model.LinearTVModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JTextPane;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.ButtonGroup;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class ResultsComparator extends JInternalFrame {
	private JButton btnAdd;
	private JRadioButton rdbtnTestRunComarison;
	private JRadioButton rdbtnBestResultComparison;
	private JButton btnCompare;
	private JLabel lblResultFiles;
	private JList lstDataSets;
	private JComboBox cboxCat;
	private JTextField txtCat;
	private JButton btnAddCategory;
	
	Map<String, ArrayList<String> > files;
	private JTextArea txtTypes;
	private JButton btnRemove;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rdbtnExpression;
	private JSpinner spnGene;
	private JPanel panel;
	private JLabel lblLabels;
	private JLabel lblFiles;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ResultsComparator frame = new ResultsComparator();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ResultsComparator() {
		setClosable(true);
		setBounds(100, 100, 528, 353);
		getContentPane().setLayout(null);
		
		files = new HashMap<String, ArrayList<String> >();
		
		btnAdd = new JButton("Add file");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				jfc.setFileFilter(new FileNameExtensionFilter("Text Files","txt"));
				jfc.setMultiSelectionEnabled(true);
			
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					DefaultListModel model = new DefaultListModel();
					for (File f : jfc.getSelectedFiles()){
						model.addElement(f.getPath());
						files.get(cboxCat.getSelectedItem().toString()).add(f.getPath());
					
					}
					lstDataSets.setModel(model);
				}
			}
			});
		btnAdd.setBounds(385, 158, 89, 23);
		getContentPane().add(btnAdd);
		
		btnCompare = new JButton("Compare");
		btnCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFreeChart chart = rdbtnBestResultComparison.isSelected() ? getBestCompChart() : 
					(rdbtnTestRunComarison.isSelected() ? getRunCompChart() : getGeneChart());
				
				chart.getTitle().setFont(new Font("Arial", Font.PLAIN, 12));
				MainWindow.getMain().openFrame(new ComparisonView(chart));
				
			}

			
		});
		btnCompare.setBounds(385, 273, 89, 32);
		getContentPane().add(btnCompare);
		
		lblResultFiles = new JLabel("Category:");
		lblResultFiles.setBounds(69, 17, 89, 14);
		getContentPane().add(lblResultFiles);
		
		lstDataSets = new JList();
		lstDataSets.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lstDataSets.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		lstDataSets.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		lstDataSets.setDragEnabled(true);
		lstDataSets.setBounds(126, 60, 348, 87);
		
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
                		files.get(cboxCat.getSelectedItem().toString()).add(file.getPath());
                	}
                } else {
                	model.add(lstDataSets.getModel().getSize(), data);
                	files.get(cboxCat.getSelectedItem().toString()).add(data);
                }
                
                
                lstDataSets.setModel(model);
                return true;
            }
			
		} );
		
		getContentPane().add(lstDataSets);
		
		cboxCat = new JComboBox();
		cboxCat.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
				//lstDataSets.removeAll();
				
				DefaultListModel model = new DefaultListModel();
				
				
					for (String s: files.get(cboxCat.getSelectedItem().toString())){
						model.addElement(s);
					}
					lstDataSets.setModel(model);
				
			}		
		});
		
		cboxCat.setBounds(123, 14, 173, 20);
		getContentPane().add(cboxCat);
		
		txtCat = new JTextField();
		txtCat.setColumns(10);
		txtCat.setBounds(308, 14, 102, 20);
		getContentPane().add(txtCat);
		
		btnAddCategory = new JButton("Add");
		btnAddCategory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				files.put(txtCat.getText(), new ArrayList<String>());
				DefaultComboBoxModel model = (DefaultComboBoxModel) cboxCat.getModel();
				model.addElement(txtCat.getText());
				
			}
		});
		btnAddCategory.setBounds(413, 13, 61, 23);
		getContentPane().add(btnAddCategory);
		
		txtTypes = new JTextArea();
		txtTypes.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtTypes.setBounds(10, 63, 102, 84);
		getContentPane().add(txtTypes);
		
		btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int index = lstDataSets.getSelectedIndex();
				if (index < 0) return;
				DefaultListModel model = (DefaultListModel) lstDataSets.getModel();
				model.remove(index);
				
				files.get(txtCat.getText()).remove(index);
				
			}
		});
		btnRemove.setBounds(284, 158, 89, 23);
		getContentPane().add(btnRemove);
		
		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Comparison type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 190, 250, 118);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		spnGene = new JSpinner();
		spnGene.setBounds(141, 83, 39, 20);
		panel.add(spnGene);
		spnGene.setModel(new SpinnerNumberModel(1, 1, 10, 1));
		
		rdbtnTestRunComarison = new JRadioButton("Test run comparison");
		rdbtnTestRunComarison.setBounds(8, 25, 154, 23);
		panel.add(rdbtnTestRunComarison);
		buttonGroup.add(rdbtnTestRunComarison);
		rdbtnTestRunComarison.setSelected(true);
		
		rdbtnBestResultComparison = new JRadioButton("Best result comparison");
		rdbtnBestResultComparison.setBounds(8, 53, 154, 23);
		panel.add(rdbtnBestResultComparison);
		buttonGroup.add(rdbtnBestResultComparison);
		
		rdbtnExpression = new JRadioButton("Expression of gene:");
		rdbtnExpression.setBounds(8, 81, 154, 23);
		panel.add(rdbtnExpression);
		buttonGroup.add(rdbtnExpression);
		
		lblLabels = new JLabel("Labels:");
		lblLabels.setBounds(10, 41, 46, 14);
		getContentPane().add(lblLabels);
		
		lblFiles = new JLabel("Files:");
		lblFiles.setBounds(126, 112, 46, 14);
		getContentPane().add(lblFiles);

	}
	
	protected JFreeChart getGeneChart() {
		
		SAXBuilder builder = new SAXBuilder();
		XYSeriesCollection xyDataset = new XYSeriesCollection();
		int gene = Integer.parseInt(spnGene.getValue().toString()) -1;
		
		try {
			
			Document document = (Document) builder.build(lstDataSets.getModel().getElementAt(0).toString());
			Element root = document.getRootElement();
			String madFile = root.getChild("problem").getChild("dataset").getAttributeValue("location");
		
			MicroArrayData mad = MicroArrayData.readFromFile(madFile);
			
			String[] types = txtTypes.getText().split("\n");
			
			XYSeries series;
			series = DataSetManager.getChartDataSet(mad, gene);
			series.setKey("Dataset");
			xyDataset.addSeries(series);
			
			for (int i = 0; i < lstDataSets.getModel().getSize(); ++i) {
								
				document = (Document) builder.build(lstDataSets.getModel().getElementAt(i).toString());
				root =  document.getRootElement();
				String[] spt = root.getChild("results").getChild("individual").getTextTrim().split("\n");
				double[] params = new double[spt.length];
				
				for (int j = 0; j < spt.length; ++j)
					params[j] = Double.parseDouble(spt[j]);
				
				LinearTVModel ltv = new LinearTVModel(mad.getGeneCount(), new LTMParams());
				ltv.setParams(params);
				MicroArrayData madSim = ltv.simulate(mad.getDataForTime(0), mad.getTime());
				
				series = DataSetManager.getChartDataSet(madSim, gene);
				series.setKey(types[i]);
				xyDataset.addSeries(series);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ChartFactory.createXYLineChart("Gene" +gene+" comparison", "time", "gene expression", xyDataset, PlotOrientation.VERTICAL, true, false, false);
	}

	private JFreeChart getRunCompChart() {
		
		SAXBuilder builder = new SAXBuilder();
		Document document;
		
		int size = 0; //najveci broj logova od svih mjerenja
		List<ArrayList<Double> > compData = new ArrayList<ArrayList<Double>>();

		for (int j = 0; j < lstDataSets.getModel().getSize(); ++j) {
			String f = lstDataSets.getModel().getElementAt(j).toString();
			
			try {
				document = (Document) builder.build(f);
				Element rootNode = document.getRootElement();
				
				int n = Integer.MAX_VALUE;
				ArrayList<Double> data = new ArrayList<Double>();
				boolean first = true;
				
				List<Element> runs = rootNode.getChild("results").getChildren("run");
				int m = runs.size();
				if (m > size) size = m;
				
				for (Element el : runs) {
					String[] spt = el.getChild("log").getText().split("\n");
					if (spt.length < n) n = spt.length;
					
					for (int i = 0; i < n; ++i) {
						
						double d = Double.parseDouble(spt[i].split("\t")[1].replace(',', '.'));
						
						if (first) data.add(d);
						else data.set(i, data.get(i) + d);
					}
					first = false;
				}
				int i = 0;
				for (; i < n; ++i) {
					data.set(i, data.get(i) / m);
				}
				int k = data.size();
				for (; i < k; ++i){
					data.remove(data.size()-1);
				}
				compData.add(data);
				
			} catch (Exception e){
				
			}
		}
			
		int i = 0;
		XYSeriesCollection xyDataset = new XYSeriesCollection();
		String[] types = txtTypes.getText().split("\n");
		
		for (ArrayList<Double> data : compData){
			
			XYSeries series = new XYSeries(types[i++]);
			double dx = (double) (3000000) / data.size(), d = 0;
			for (int j = 0; j < data.size(); ++j) {
				series.add(d, Math.log10(-data.get(j)));
				d += dx;
			}
			series.add(3000000, Math.log10(-data.get(data.size()-1)));
			xyDataset.addSeries(series);
			
		}

		return ChartFactory.createXYLineChart("Result comparison", "evaluations", "log(error)", xyDataset, PlotOrientation.VERTICAL, true, false, false);
	}

	private JFreeChart getBestCompChart() {
		
		DefaultStatisticalCategoryDataset dataset = new DefaultStatisticalCategoryDataset();

		String[] input = new String[lstDataSets.getModel().getSize()];
		SAXBuilder builder = new SAXBuilder();
		String[] types = txtTypes.getText().split("\n");
		
		for (String type : files.keySet()) {
			int i = 0;
			for (String f : files.get(type)) {
				
				Document document;
				try {
					
					document = (Document) builder.build(f);
					Element rootNode = document.getRootElement();
					String desc = types[i++];
					
					double avg =  -Double.parseDouble(rootNode.getChild("results").getAttribute("fit_avg").getValue());
					double dev =  Double.parseDouble(rootNode.getChild("results").getAttribute("fit_dev").getValue());
					
					dataset.add(avg, dev, desc, type);
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	 
				
			 }
		}

		final CategoryAxis xAxis = new CategoryAxis("Experiments");
        xAxis.setLowerMargin(0.01d); // percentage of space before first bar
        xAxis.setUpperMargin(0.01d); // percentage of space after last bar
        xAxis.setCategoryMargin(0.05d); // percentage of space between categories
        final ValueAxis yAxis = new NumberAxis("Best solutions");

        // define the plot
        final CategoryItemRenderer renderer = new StatisticalBarRenderer();
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart("Result comparison", plot);
		
		return chart;
	}
}
