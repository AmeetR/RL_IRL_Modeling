package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Listener for the Menu of the GUI. Load and save Files, show the toolbars and the settings. 
 */
public class MenuActionListener implements ActionListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1502511418004466357L;
	private final JFileChooser fc;
	private GridWorldGUI parent;
	private File loadFile = null;
	private File saveFile = null;
	private boolean showToolBar = false;
	private boolean showSizeBar = false;
	private boolean showSpeedBar = false;

	public MenuActionListener(GridWorldGUI parent) {
		this.parent = parent;
		fc = new JFileChooser();
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".feedback")
						|| f.isDirectory();
			}

			public String getDescription() {
				return "Reward/Feedback-File(*.feedback)";
			}

			@Override
			public String toString() {
				return "feedback";
			}
		});
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".gridworld")
						|| f.isDirectory();
			}

			public String getDescription() {
				return "GridWorld-File(*.gridworld)";
			}

			@Override
			public String toString() {
				return "gridworld";
			}
		});
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals("exit")) {
			System.exit(0);
		}
		if (arg0.getActionCommand().equals("load")) {
			int returnVal = fc.showOpenDialog(this.parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				loadFile = fc.getSelectedFile();
				if (!loadFile(loadFile)) {
					JOptionPane.showMessageDialog(parent, "File "
							+ loadFile.getPath() + " could not be loaded!",
							"Error!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				parent.setTitle("GridWorld-Editor " + loadFile.getPath());
				parent.validate();
				GridWorldPropertyManager.setLastFilePath(loadFile.getPath());
			}
		}
		if (arg0.getActionCommand().equals("save")) {
			if (loadFile == null) {
				int returnVal = fc.showSaveDialog(parent);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					saveFile = fc.getSelectedFile();
					if (saveFile.exists()) {
						returnVal = JOptionPane.showConfirmDialog(parent,
								"Overwrite?", "Overwrite?",
								JOptionPane.YES_NO_OPTION);
						if (returnVal == JOptionPane.NO_OPTION) {
							return;
						}
					}
					GridWorldPropertyManager.setLastFilePath(saveFile.getPath());
					String fileName = saveFile.getAbsolutePath().trim();
					int dotPos = fileName.lastIndexOf(".");
					String extension = fileName.substring(dotPos);
					if (extension.toLowerCase().equals(".gridworld")) {
						saveXMLFile(saveFile);
						return;
					}
					saveFile(saveFile);
				}
			} else {
				GridWorldPropertyManager.setLastFilePath(saveFile.getPath());
				String fileName = loadFile.getAbsolutePath().trim();
				int dotPos = fileName.lastIndexOf(".");
				String extension = fileName.substring(dotPos);
				if (extension.toLowerCase().equals(".gridworld")) {
					saveXMLFile(loadFile);
					return;
				}
				saveFile(loadFile);
			}
		}
		if (arg0.getActionCommand().equals("saveas")) {
			int returnVal = fc.showSaveDialog(parent);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				saveFile = fc.getSelectedFile();
				if (saveFile.exists()) {
					returnVal = JOptionPane.showConfirmDialog(parent,
							"Overwrite?", "Overwrite?",
							JOptionPane.YES_NO_OPTION);
					if (returnVal == JOptionPane.NO_OPTION) {
						return;
					}
				}
				String fileName = saveFile.getAbsolutePath().trim();

				if (fileName.endsWith(".gridworld")) {
					GridWorldPropertyManager.setLastFilePath(saveFile.getPath());
					saveXMLFile(saveFile);
					return;
				}
				if (fileName.endsWith(".feedback")) {
					GridWorldPropertyManager.setLastFilePath(saveFile.getPath());
					saveFile(saveFile);
					return;
				}
				if (fc.getFileFilter().toString().equals("gridworld")) {
					
					saveFile = new File(saveFile.getAbsolutePath()
							+ ".gridworld");
					if (saveFile.exists()) {
						returnVal = JOptionPane.showConfirmDialog(parent,
								"Overwrite?", "Overwrite?",
								JOptionPane.YES_NO_OPTION);
						if (returnVal == JOptionPane.NO_OPTION) {
							return;
						}
					}
					GridWorldPropertyManager.setLastFilePath(saveFile.getPath()+ ".gridworld");
					saveXMLFile(saveFile);
					return;
				}
				if (fc.getFileFilter().toString().equals("feedback")) {
					
					saveFile = new File(saveFile.getAbsolutePath()
							+ ".feedback");
					if (saveFile.exists()) {
						returnVal = JOptionPane.showConfirmDialog(parent,
								"Overwrite?", "Overwrite?",
								JOptionPane.YES_NO_OPTION);
						if (returnVal == JOptionPane.NO_OPTION) {
							return;
						}
					}
					GridWorldPropertyManager.setLastFilePath(saveFile.getPath()+ ".feedback");
					saveFile(saveFile);
					return;
				}

			}
		}
		if (arg0.getActionCommand().equals("sizeBar")) {
			if (showSizeBar) {
				parent.removeSizeBar();
				showSizeBar = false;
			} else {
				parent.showSizeBar();
				showSizeBar = true;
			}
		}
		if (arg0.getActionCommand().equals("rewardEditor")) {
			if (showToolBar) {
				parent.removeToolbar();
				showToolBar = false;
			} else {
				parent.showToolBar();
				showToolBar = true;
			}
		}
		if (arg0.getActionCommand().equals("speedSlider")) {
			if (showSpeedBar) {
				parent.removeSpeedBar();
				showSpeedBar = false;
			} else {
				parent.showSpeedBar();
				showSpeedBar = true;
			}
		}
		if (arg0.getActionCommand().equals("close")) {

			try {
				GridModel.getInstance().setSize(0, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			parent.getTablePanel().getTable().getModel()
					.fireTableStructureChanged();
			parent.getQTablePanel().getQTable().getModel().fireTableStructureChanged();
			parent.pack();
			parent.setTitle("GridWorld-Editor");
		}
		if (arg0.getActionCommand().equals("new")) {
			try {
				GridModel.getInstance().setSize(0, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			parent.getTablePanel().getTable().getModel()
					.fireTableStructureChanged();
			parent.getQTablePanel().getQTable().getModel().fireTableStructureChanged();
			parent.pack();
			parent.showSizeBar();
			showSizeBar = true;
			parent.setTitle("GridWorld-Editor *New");
		}
		if (arg0.getActionCommand().equals("help")) {
			JOptionPane.showMessageDialog(parent,
					"God helps those who help themselves", "Help",
					JOptionPane.INFORMATION_MESSAGE);
		}
		if (arg0.getActionCommand().equals("print")) {
			try {
				parent.getTablePanel().getTable().print();
			} catch (PrinterException e) {
				JOptionPane.showMessageDialog(parent, "PrintError: "
						+ e.getMessage(), "PrintError",
						JOptionPane.ERROR_MESSAGE);
				// e.printStackTrace();
			}
		}
		if (arg0.getActionCommand().equals("settings")) {
			SettingsDialog sd = new SettingsDialog(parent);
			sd.pack();
			sd.setVisible(true);
			if (sd.getReturnStatus() == SettingsDialog.RET_OK) {
				parent.getTablePanel().getTable().setRewardFont(sd.getFont());
				parent.getQTablePanel().getQTable().setRewardFont(sd.getFont());
				parent.getTablePanel().getTable().setActiveColor(sd.getColor());
				GridWorldPropertyManager.setColor(sd.getColor());
				parent.getTablePanel().getTable().setRowHeight(sd.getCellHeight());
				parent.getTablePanel().getTable().setColSize(sd.getCellWidth());
				parent.getTablePanel().getRowHeader().setRowHeight(sd.getCellHeight());
				parent.getQTablePanel().getQTable().setRowHeight(sd.getCellHeight());
				parent.getQTablePanel().getQTable().setColSize(sd.getCellWidth());
				parent.getQTablePanel().getRowHeader().setRowHeight(sd.getCellHeight());
			}
		}
		if (arg0.getActionCommand().equals("lastFile")) {
			if (!loadFile(new File(((JMenuItem)arg0.getSource()).getText()))) {
				JOptionPane.showMessageDialog(parent, "File "
						+ ((JMenuItem)arg0.getSource()).getText() + " could not be loaded!",
						"Error!", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}

	/**
	 * Loads a File into the Editor
	 * @param loadFile File to load
	 * @return true if the file could load successfuly else false
	 */
	public boolean loadFile(File loadFile) {
		if (!GridModel.getInstance().loadXMLFile(loadFile)) {
			if (!GridModel.getInstance().parseFile(loadFile)) {
				return false;
			}
		}
		parent.getTablePanel().getTable().getModel().fireTableStructureChanged();
		parent.getQTablePanel().getQTable().getModel().fireTableStructureChanged();
		parent.getTablePanel().getTable().packCols();
		parent.getTablePanel().getTable().packRows();
		parent.getQTablePanel().getQTable().packCols();
		parent.getQTablePanel().getQTable().packRows();
		parent.pack();
		return true;
	}

	/**
	 * Save the GridModel to a gridworld-File
	 * 
	 * @param file
	 *            gridmodel-File
	 */
	private void saveXMLFile(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			Element gridWorldNode = doc.createElement("GridWorld");
			gridWorldNode.setAttribute("cols", Integer.toString(GridModel.getInstance()
					.getSize().width));
			gridWorldNode.setAttribute("rows", Integer.toString(GridModel.getInstance()
					.getSize().height));
			gridWorldNode.normalize();
			for (int col = 0; col < GridModel.getInstance().getSize().width; col++) {
				for (int row = 0; row < GridModel.getInstance().getSize().height; row++) {
					Element stateNode = doc.createElement("State");
					stateNode.setAttribute("col", Integer.toString(col));
					stateNode.setAttribute("row", Integer.toString(row));
					stateNode.setAttribute("isTerminalState", Boolean.toString(GridModel.getInstance().getCell(col, row).isTerminalState()));
					Element upNode = doc.createElement("Up");
					Element upRewardNode = doc.createElement("reward");
					Text text = doc.createTextNode(Double.toString(GridModel.getInstance()
							.getCell(col, row).getReward(GridModel.UP)));
					upRewardNode.appendChild(text);
					Element upWallNode = doc.createElement("wall");
					if (GridModel.getInstance().getCell(col, row).getWall(GridModel.UP)) {
						text = doc.createTextNode("1");
					} else {
						text = doc.createTextNode("0");
					}
					upWallNode.appendChild(text);
					upNode.appendChild(upRewardNode);
					upNode.appendChild(upWallNode);

					Element downNode = doc.createElement("Down");
					Element downRewardNode = doc.createElement("reward");
					text = doc.createTextNode(Double.toString(GridModel.getInstance()
							.getCell(col, row).getReward(GridModel.DOWN)));
					downRewardNode.appendChild(text);
					Element downWallNode = doc.createElement("wall");
					if (GridModel.getInstance().getCell(col, row).getWall(GridModel.DOWN)) {
						text = doc.createTextNode("1");
					} else {
						text = doc.createTextNode("0");
					}
					downWallNode.appendChild(text);
					downNode.appendChild(downRewardNode);
					downNode.appendChild(downWallNode);

					Element leftNode = doc.createElement("Left");
					Element leftRewardNode = doc.createElement("reward");
					text = doc.createTextNode(Double.toString(GridModel.getInstance()
							.getCell(col, row).getReward(GridModel.LEFT)));
					leftRewardNode.appendChild(text);
					Element leftWallNode = doc.createElement("wall");
					if (GridModel.getInstance().getCell(col, row).getWall(GridModel.LEFT)) {
						text = doc.createTextNode("1");
					} else {
						text = doc.createTextNode("0");
					}
					leftWallNode.appendChild(text);
					leftNode.appendChild(leftRewardNode);
					leftNode.appendChild(leftWallNode);

					Element rightNode = doc.createElement("Right");
					Element rightRewardNode = doc.createElement("reward");
					text = doc.createTextNode(Double.toString(GridModel.getInstance()
							.getCell(col, row).getReward(GridModel.RIGHT)));
					rightRewardNode.appendChild(text);
					Element rightWallNode = doc.createElement("wall");
					if (GridModel.getInstance().getCell(col, row).getWall(GridModel.RIGHT)) {
						text = doc.createTextNode("1");
					} else {
						text = doc.createTextNode("0");
					}
					rightWallNode.appendChild(text);
					rightNode.appendChild(rightRewardNode);
					rightNode.appendChild(rightWallNode);

					stateNode.appendChild(upNode);
					stateNode.appendChild(downNode);
					stateNode.appendChild(leftNode);
					stateNode.appendChild(rightNode);
					gridWorldNode.appendChild(stateNode);
				}
			}
			doc.appendChild(gridWorldNode);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			DOMSource source = new DOMSource(doc);
			FileOutputStream os = new FileOutputStream(file);
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Save the GridModel to a Feedback-File
	 * 
	 * @param file
	 *            feedback-File
	 */
	private void saveFile(File file) {
		int cols = parent.getTablePanel().getTable().getColumnCount();
		int rows = parent.getTablePanel().getTable().getRowCount();
		try {
			FileOutputStream os = new FileOutputStream(file);
			os.write(fillWord(rows));
			os.write(fillWord(cols));
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					//for (int dir = 0; dir < 4; dir++) {
						os.write(Conversion.convertToByteArray((int) GridModel.getInstance()
								.getCell(col, row).getReward(GridModel.UP)));
						os.write(Conversion.convertToByteArray((int) GridModel.getInstance()
								.getCell(col, row).getReward(GridModel.DOWN)));
						os.write(Conversion.convertToByteArray((int) GridModel.getInstance()
								.getCell(col, row).getReward(GridModel.LEFT)));
						os.write(Conversion.convertToByteArray((int) GridModel.getInstance()
								.getCell(col, row).getReward(GridModel.RIGHT)));
				}
			}
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Convert an Integer to 4 Byte Word
	 * 
	 * @param Value
	 *            Integer to Convert
	 * @return 4 Byte Word in a byte-Array
	 */
	private byte[] fillWord(int Value) {
		byte[] returnVal = { 0, 0, 0, 0 };
		returnVal[3] = (byte) Value;
		return returnVal;
	}

	
	
}
