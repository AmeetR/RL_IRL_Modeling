package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;

/**
 * GUI Main Class. Contains all panels.
 */
public class GridWorldGUI extends JFrame implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6449867161310002804L;
	private JTabbedPane tabPane = null;
	private TablePanel tablePanel = null;
	private QValuePanel qValuePanel = null;
	private SizePanel sizePanel = null;
	private ToolPanel toolBar = null;
	private SpeedPanel speedPanel = null;
	private JMenuBar menuBar = null;
	private JMenu fileMenu = null, viewMenu = null, helpMenu = null,
			toolBarItem = null;
	private JMenuItem newFile = null, loadFile = null, saveFile = null,
			saveFileAs = null, close = null, exit = null, helpItem = null,
			printItem = null, settingsItem = null, lastFileItem = null;
	private JCheckBoxMenuItem setSize = null, setReward = null,
			setSpeed = null;
	private MenuActionListener menuListener = null;
	/**
	 * Constructor
	 */
	public GridWorldGUI() {
		try {
			GridModel.getInstance().setSize(0, 0);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		GridModel.getInstance().setSleepTime(0);
		this.setTitle("GridWorld-Editor");
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		toolBar = new ToolPanel("Gridworld Toolbar", this);
		tablePanel = new TablePanel(this);
		sizePanel = new SizePanel(tablePanel);
		speedPanel = new SpeedPanel();

		buildMenu("");

		this.setLayout(new BorderLayout());
		tablePanel.setBorder(BorderFactory.createTitledBorder("Gridworld"));
		this.add(tablePanel, BorderLayout.CENTER);
		this.pack();
		java.awt.Dimension screenSize = Toolkit.getDefaultToolkit()
				.getScreenSize();
		this.setLocation((screenSize.width) / 2, (screenSize.height ) / 2);
		this.setVisible(true);
	}

	/**
	 * Constructor
	 * 
	 * @param configFileName
	 *            file to load into the editor
	 */
	public GridWorldGUI(String configFileName) {
		GridWorldPropertyManager.loadConfigFile(configFileName);
		try {
			GridModel.getInstance().setSize(5, 5);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		String tmp = GridWorldPropertyManager.getElement("sleepTime");
		if (!tmp.equals("")) {
			GridModel.getInstance().setSleepTime(Integer.parseInt(tmp));
		} else {
			GridModel.getInstance().setSleepTime(0);
		}
		
		this.setTitle("GridWorld-Editor");
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		tabPane = new JTabbedPane();
		toolBar = new ToolPanel("Gridworld Toolbar", this);
		tablePanel = new TablePanel(this);
		qValuePanel = new QValuePanel();
		sizePanel = new SizePanel(tablePanel);
		speedPanel = new SpeedPanel();
		
		tmp = GridWorldPropertyManager.getElement("lastFilePath");
		buildMenu(tmp);

		this.setLayout(new BorderLayout());
		
		tabPane.addTab("Gridworld", tablePanel);
		tabPane.addTab("QValues", qValuePanel);
		this.add(tabPane, BorderLayout.CENTER);
		
		
		tmp = GridWorldPropertyManager.getElement("loadFilePath");
		if (!tmp.equals("")) {
			if (!menuListener.loadFile(new File(tmp))) {
				JOptionPane.showMessageDialog(this, "File " + tmp
						+ " could not be loaded!", "Error!",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		showToolBar();
		showSpeedBar();
		this.pack();
		tmp = GridWorldPropertyManager.getElement("showGUI");
		if (tmp.equals("1")) {
			this.setVisible(true);
		} else {
			
			this.setVisible(false);
		}
		
	}
	/**
	 * Constructor
	 * 
	 * @param filepath file to load into the editor
	 * @param sleepTime time to wait between two steps
	 * @param guiVisible true if the gui should be visible. false without gui
	 * @param lastFilePath The path to the "last file" in the menu
	 */
	public GridWorldGUI(String filepath, int sleepTime, boolean guiVisible,
			String lastFilePath) {

	}

	/**
	 * Getter of the Toolbar
	 * 
	 * @return the Toolbar
	 */
	public ToolPanel getToolBar() {
		return toolBar;
	}

	/**
	 * Create the Menu
	 */
	private void buildMenu(String lastFilePath) {
		menuListener = new MenuActionListener(this);
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		newFile = new JMenuItem("New...");
		newFile.setActionCommand("new");
		newFile.addActionListener(menuListener);
		fileMenu.add(newFile);
		loadFile = new JMenuItem("Load...");
		loadFile.setActionCommand("load");
		loadFile.addActionListener(menuListener);
		fileMenu.add(loadFile);
		saveFile = new JMenuItem("Save...");
		saveFile.setActionCommand("save");
		saveFile.addActionListener(menuListener);
		fileMenu.add(saveFile);
		saveFileAs = new JMenuItem("Save File as...");
		saveFileAs.setActionCommand("saveas");
		saveFileAs.addActionListener(menuListener);
		fileMenu.add(saveFileAs);
		fileMenu.addSeparator();
		printItem = new JMenuItem("Print Grid...");
		printItem.setActionCommand("print");
		printItem.addActionListener(menuListener);
		fileMenu.add(printItem);
		fileMenu.addSeparator();
		lastFileItem = new JMenuItem(lastFilePath);
		lastFileItem.setActionCommand("lastFile");
		lastFileItem.addActionListener(menuListener);
		fileMenu.add(lastFileItem);
		fileMenu.addSeparator();
		close = new JMenuItem("Close");
		close.setActionCommand("close");
		close.addActionListener(menuListener);
		fileMenu.add(close);
		exit = new JMenuItem("Exit");
		exit.setActionCommand("exit");
		exit.addActionListener(menuListener);
		fileMenu.add(exit);
		menuBar.add(fileMenu);
		viewMenu = new JMenu("View");
		toolBarItem = new JMenu("Toolbars");
		viewMenu.add(toolBarItem);
		setSize = new JCheckBoxMenuItem("Size Bar");
		setSize.setActionCommand("sizeBar");
		setSize.addActionListener(menuListener);
		toolBarItem.add(setSize);
		setReward = new JCheckBoxMenuItem("Reward Editor");
		setReward.setActionCommand("rewardEditor");
		setReward.addActionListener(menuListener);
		toolBarItem.add(setReward);
		setSpeed = new JCheckBoxMenuItem("SpeedSlider");
		setSpeed.setActionCommand("speedSlider");
		setSpeed.addActionListener(menuListener);
		toolBarItem.add(setSpeed);
		viewMenu.addSeparator();
		settingsItem = new JMenuItem("Settings...");
		settingsItem.setActionCommand("settings");
		settingsItem.addActionListener(menuListener);
		viewMenu.add(settingsItem);

		menuBar.add(viewMenu);
		helpMenu = new JMenu("Help");
		helpItem = new JMenuItem("Help");
		helpItem.setActionCommand("help");
		helpItem.addActionListener(menuListener);
		helpMenu.add(helpItem);
		menuBar.add(helpMenu);
		this.setJMenuBar(menuBar);
	}

	/**
	 * Getter of the TablePanel
	 * 
	 * @return TablePanel
	 */
	public TablePanel getTablePanel() {
		return tablePanel;
	}
	
	public QValuePanel getQTablePanel(){
		return qValuePanel;
	}

	/**
	 * Getter of the Size-Toolbar
	 * 
	 * @return Size-Toolbar
	 */
	public SizePanel getSizePanel() {
		return sizePanel;
	}

	/**
	 * Set the Reward-Toolbar visible
	 */
	public void showToolBar() {
		this.add(toolBar, BorderLayout.EAST);
		this.validate();
	}

	/**
	 * Set the Size-Toolbar visible
	 */
	public void showSizeBar() {
		this.add(sizePanel, BorderLayout.NORTH);
		this.validate();
	}

	/**
	 * Set the Reward-Toolbar invisible
	 */
	public void removeToolbar() {
		this.remove(toolBar);
		this.validate();
	}

	/**
	 * Set the Size-Toolbar invisible
	 */
	public void removeSizeBar() {
		this.remove(sizePanel);
		this.validate();
	}

	/**
	 * Set the Speed-Toolbar visible
	 */
	public void showSpeedBar() {
		this.add(speedPanel, BorderLayout.SOUTH);
		this.validate();
	}

	/**
	 * Set the Speed-Toolbar invisible
	 */
	public void removeSpeedBar() {
		this.remove(speedPanel);
		this.validate();
	}
	
	public void refreshTable(){
		this.getTablePanel().getTable().getModel().fireTableDataChanged();
		this.getQTablePanel().getQTable().getModel().fireTableDataChanged();
	}
}
