package evogrn.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import java.awt.BorderLayout;
import javax.swing.JTree;
import javax.swing.JToolBar;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Button;
import java.awt.Choice;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileNameExtensionFilter;

import evogrn.dataset.DataSetManager;
import evogrn.dataset.MicroArrayData;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;

class MyFile extends File { 

	public MyFile(String pathname) {
		super(pathname);
	}

	@Override
	public String toString() {
		return getName();
	}
}

//singleton
public class MainWindow implements DragSourceListener{

	private JFrame frmEvogrn;
	private JDesktopPane jdpMain;
	private static MainWindow window;
	private JTree treeFiles;
	private JSplitPane splitPane;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new MainWindow();
					window.frmEvogrn.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static MainWindow getMain(){
		return window;
	}
	
	public void openFrame(JInternalFrame frame){
		jdpMain.add(frame);
		frame.toFront();
		frame.setVisible(true);
		try {
			frame.setSelected(true);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		frame.requestFocus();
	}
	
	/**
	 * Create the application.
	 */
	private MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEvogrn = new JFrame();
		frmEvogrn.setTitle("EvoGRN");
		frmEvogrn.setBounds(100, 100, 900, 800);
		frmEvogrn.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		MyFile root = new MyFile( System.getProperty("user.dir"));
		root = new MyFile(root.getParent());
		
		splitPane = new JSplitPane();
		frmEvogrn.getContentPane().add(splitPane, BorderLayout.CENTER);
		jdpMain = new JDesktopPane();
		splitPane.setRightComponent(jdpMain);
		
		treeFiles = new JTree();
		
		treeFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				 
		        if (e.getClickCount() >= 2) {  
		        	if (treeFiles.getSelectionPath() == null) return;
		        	DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeFiles.getSelectionPath().getLastPathComponent();
		        	
		        	File f = (MyFile) node.getUserObject(); 
		        	if (f.getName().endsWith(".txt")){
		        		openFrame(new DataSetView(f));
		        	} 
		        	if (f.getName().endsWith(".xml")){
		        		openFrame(new ResultsView(f));
		        	}
		        }
			}
		});
		
		JPanel panel = new JPanel(new BorderLayout());
		
		JScrollPane jsp = new JScrollPane(treeFiles);
		panel.add(jsp, BorderLayout.CENTER);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				treeFiles.setModel(new DefaultTreeModel(
						
						getNode(new MyFile((new MyFile( System.getProperty("user.dir"))).getParentFile().getParentFile().getPath()))
					
				));
			}
		});
		panel.add(btnRefresh, BorderLayout.SOUTH);
		
		treeFiles.setScrollsOnExpand(true);
		
		splitPane.setLeftComponent(panel);
		
		treeFiles.setModel(new DefaultTreeModel(
				getNode(new MyFile(root.getParentFile().getPath()))
			
		));
		
		treeFiles.setDragEnabled(true);
		
		treeFiles.setTransferHandler(new TransferHandler() {
 
			
           @Override
        protected Transferable createTransferable(JComponent c) {
        	
               Transferable transferable = new Transferable() {

                   @Override
                   public Object getTransferData(DataFlavor flavor)
                           throws UnsupportedFlavorException, IOException {
                	   
                	   DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeFiles.getSelectionPath().getLastPathComponent();
                	   return ((MyFile) node.getUserObject()).getPath(); 
                   }

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] {DataFlavor.stringFlavor};
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor arg0) {
					return arg0.equals(DataFlavor.stringFlavor);
				}

               };
               
               return transferable;
        }
           public int getSourceActions(JComponent c) {
               return COPY;
           }
            
            
        });
		
		
		JMenuBar menuBar = new JMenuBar();
		frmEvogrn.setJMenuBar(menuBar);
		
		JMenu mnTest = new JMenu("File");
		menuBar.add(mnTest);
		
		JMenuItem mntmNew = new JMenuItem("New test");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new NewTest());
			}
		});
		mnTest.add(mntmNew);
		
		JMenuItem mntmOpenResults = new JMenuItem("Open results");
		mntmOpenResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileFilter(new FileNameExtensionFilter("XML Files","xml"));
				jfc.setMultiSelectionEnabled(false);
				
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					openFrame(new ResultsView(jfc.getSelectedFile()));
				}
			}
		});
		mnTest.add(mntmOpenResults);
		
		JMenuItem mntmViewDataset = new JMenuItem("View dataset");
		mntmViewDataset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileFilter(new FileNameExtensionFilter("Text Files","txt"));
				jfc.setMultiSelectionEnabled(false);
				
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					try {
						
						//Matlab verzija
//						MicroArrayData mad = MicroArrayData.readFromFile(jfc.getSelectedFile().getPath());
//						DataSetManager.exportMatlab(mad, "matlab.temp");
//						Runtime rt = Runtime.getRuntime();
//						rt.exec("datasetplot.exe matlab.temp");
						
						openFrame(new DataSetView(jfc.getSelectedFile()));
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});
		mnTest.add(mntmViewDataset);
		
		JMenuItem mntmCompareResults = new JMenuItem("Compare results");
		mntmCompareResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFrame(new ResultsComparator());
			}
		});
		mnTest.add(mntmCompareResults);
		
		JSeparator separator = new JSeparator();
		mnTest.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		mnTest.add(mntmExit);
		
		
	}

	private MutableTreeNode getNode(MyFile file) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
		for (File f : file.listFiles()){
			if (f.isDirectory()){
				node.add(getNode(new MyFile(f.getPath())));
			} else {
				if (f.getName().endsWith(".txt") || f.getName().endsWith(".xml")){
					
					node.add(new DefaultMutableTreeNode(new MyFile(f.getPath())));
				}
			}
		}
		return node;
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragEnter(DragSourceDragEvent e) {
		
		
	}

	@Override
	public void dragExit(DragSourceEvent dse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
		// TODO Auto-generated method stub
		
	}
}
