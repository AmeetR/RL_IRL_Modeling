package org.hswgt.teachingbox.core.rl.gridworldeditor.model;


import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.gridworldeditor.gui.SpeedPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
* Model of the Gridworld. Contains GridCells with the rewards. Singleton Class.
*/
public class GridModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2551588588671599641L;
	/**
	 * The one and only instance of the GridModel
	 */
	private static GridModel instance = new GridModel();
	/**
	 * Table of GridCells
	 */
	private static GridCell table[][];
	/**
	 * Size of the Model
	 */
	private static Dimension size;
	/**
	 * Position of the actual Row
	 */
	private static int actRow;
	/**
	 * Position of the actual Column
	 */
	private static int actCol;
	/**
	 * time to wait with the return of an reward
	 */
	private static int sleepTime;
	

	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	
	/*
	public static final Action ACTION_LEFT = new Action(new double[] { -1, 0 });
	public static final Action ACTION_RIGHT = new Action(new double[] { +1, 0 });
	public static final Action ACTION_UP = new Action(new double[] { 0, +1 });
	public static final Action ACTION_DOWN = new Action(new double[] { 0, -1 });
	*/
	
	public static final Action ACTION_UP = new Action(new double[] { UP });
	public static final Action ACTION_DOWN = new Action(new double[] { DOWN });
	public static final Action ACTION_LEFT = new Action(new double[] { LEFT });
	public static final Action ACTION_RIGHT = new Action(new double[] { RIGHT });
	
	
	public void resetStartState(){
		for(int col = 0; col < size.width; col++){
			for(int row = 0; row < size.height; row++){
				getCell(col, row).setStartState(false);
			}
		}
		
	}
	
	/**
	 * Return the time that the gridmodle will wait between two steps
	 * @return the time to wait with return of an reward
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * Return the time that the gridmodle will wait between two steps
	 * @param sleepTime set the time to wait with the return of an reward
	 */
	public void setSleepTime(int sleepTime) {
		GridModel.sleepTime = sleepTime;
	}
	
	/**
	 * Return the one an only Instance of the GridModel
	 * @return Instance of the GridModel
	 */
	public static synchronized GridModel getInstance(){
		return instance;
	}
	
	/**
	 * Set Size of the Grid
	 * @param row Number of rows
	 * @param col Number of columns
	 * @return indicates if successful or not
	 */
	public boolean setSize(int col, int row){
		try{
		table = new GridCell[col][];
		for(int i = 0; i < col; i++){
			table[i] = new GridCell[row];
			for(int y = 0; y < row; y++){
				table[i][y] = new GridCell(i, y);
			}
		}
		size = new Dimension();
		size.width = col;
		size.height = row;
		actRow = 0;
		actCol = 0;
		}catch(OutOfMemoryError ex){
			return false;
		}
		return true;

	}

	/**
	 * private Constructor
	 */
	private GridModel(){
		GridModel.sleepTime = 200;
	}
	
	/**
	 * Return the Size of the GridModel
	 * @return Dimenson Size of the Model
	 */
	public Dimension getSize(){
		return size;
	}
	
	/**
	 * Returns a single Cell(state) of the GridModel
	 * @param col Column of the GridModel
	 * @param row Row of the GridModel
	 * @return GridCell
	 */
	public GridCell getCell(int col, int row){
		return table[col][row];
	}
	
	
	/**
	 * Execute one step(action)
	 * @param a Action
	 * @return direct feedback of the action
	 */
	public double doAction(Action a){
		double retValue = 0;
		//Sleep between the Action
		try {
			
			// wait if sleep-time is Maximum (slowest)
			while (getSleepTime() == SpeedPanel.SPEED_MAX) {
				Thread.sleep(100);
			}
			
			// sleep dependent on speed slider
			Thread.sleep(getSleepTime());
			
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		if(a.equals(ACTION_UP)){
			//UP
			retValue = getCell(actCol, actRow).getReward(GridModel.UP);
			if(testBorders(GridModel.UP) && !checkWall(GridModel.UP)){
				actRow = actRow-1;
			}
		}
		if(a.equals(ACTION_DOWN)){
			//DOWN
			retValue = getCell(actCol, actRow).getReward(GridModel.DOWN);
			if(testBorders(GridModel.DOWN)&& !checkWall(GridModel.DOWN)){
				actRow = actRow+1;
			}
		}
		if(a.equals(ACTION_LEFT)){
			//LEFT
			retValue = getCell(actCol, actRow).getReward(GridModel.LEFT);
			if(testBorders(GridModel.LEFT)&& !checkWall(GridModel.LEFT)){
				actCol = (actCol-1);
			}
		}
		if(a.equals(ACTION_RIGHT)){
			//RIGHT
			retValue = getCell(actCol, actRow).getReward(GridModel.RIGHT);
			if(testBorders(GridModel.RIGHT)&& !checkWall(GridModel.RIGHT)){
				actCol = (actCol+1);
			}
		}
		return retValue;
	}
	
	/**
	 * Set the Row of the actual State
	 * @param actRow The row
	 */
	public void setActRow(int actRow) {
		GridModel.actRow = actRow;
	}

	/**
	 * Set the Column of the actual State
	 * @param actCol The column
	 */
	public void setActCol(int actCol) {
		GridModel.actCol = actCol;
	}

	/**
	 * Check if a wall is in direction
	 * @param direction direction of step
	 * @return true for wall; false no wall
	 */
	private boolean checkWall(int direction){
		return getCell(actCol, actRow).getWall(direction);
	}

	/**
	 * Return the Row of the actual State
	 * @return Row of the actual State
	 */
	public int getActRow() {
		return actRow;
	}

	/**
	 * Return the Column of the actual State
	 * @return Column of the actual State
	 */
	public int getActCol() {
		return actCol;
	}

	/**
	 * Check the next step
	 * @param direction direction of the next step
	 * @return true if step is possible else false
	 */
	private boolean testBorders(int direction){
		switch(direction){
			//Up
			case(0):{
				if(actRow-1 < 0){
					return false;
				}else{
					return true;
				}
			}
			//Down
			case(1):{
				if(actRow+1 > size.height-1){
					return false;
				}else{
					return true;
				}
			}
			//Left
			case(2):{
				if(actCol-1 < 0){
					return false;
				}else{
					return true;
				}
			}
			//Right
			case(3):{
				if(actCol+1 > size.width-1){
					return false;
				}else{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Loads a .gridworld File into the GridModel
	 * 
	 * @param file
	 *            XML-File
	 * @return true if file is succesfully loaded else false
	 */
	public boolean loadXMLFile(File file) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);

			NodeList ndList = document.getElementsByTagName("GridWorld");
			for (int i = 0; i < ndList.getLength(); i++) {
				Node world = ndList.item(i);
				NodeList states = world.getChildNodes();

				int stateCount = 0;
				for (int j = 0; j < states.getLength(); j++) {
					Node state = states.item(j);
					if (state.getNodeName().equals("State")) {
						stateCount++;
					}
				}
				if (stateCount == Integer.parseInt(world.getAttributes()
						.getNamedItem("rows").getNodeValue())
						* Integer.parseInt(world.getAttributes().getNamedItem(
								"cols").getNodeValue())) {
					GridModel.getInstance().setSize(Integer.parseInt(world.getAttributes()
							.getNamedItem("cols").getNodeValue()), Integer
							.parseInt(world.getAttributes()
									.getNamedItem("rows").getNodeValue()));
					for (int j = 0; j < states.getLength(); j++) {
						Node state = states.item(j);
						if (state.getNodeName().equals("State")) {
							if(state.getAttributes().getNamedItem("isTerminalState")!= null){
								if(state.getAttributes().getNamedItem("isTerminalState").getNodeValue().equals("true")){
								GridModel.getInstance().getCell(
											Integer.parseInt(state
													.getAttributes()
													.getNamedItem("col")
													.getNodeValue()),
											Integer.parseInt(state
													.getAttributes()
													.getNamedItem("row")
													.getNodeValue())).setTerminalState(true);
								}
							}
							NodeList directions = state.getChildNodes();
							for (int k = 0; k < directions.getLength(); k++) {
								Node dir = directions.item(k);
								if (dir.getNodeName().equals("Up")) {
									GridModel.getInstance().getCell(
											Integer.parseInt(state
													.getAttributes()
													.getNamedItem("col")
													.getNodeValue()),
											Integer.parseInt(state
													.getAttributes()
													.getNamedItem("row")
													.getNodeValue()))
											.setReward(loadReward(dir), 0);
									if (loadWall(dir)) {
										GridModel.getInstance().getCell(
												Integer.parseInt(state
														.getAttributes()
														.getNamedItem("col")
														.getNodeValue()),
												Integer.parseInt(state
														.getAttributes()
														.getNamedItem("row")
														.getNodeValue()))
												.setWall(0);
									}
								}
								if (dir.getNodeName().equals("Down")) {
									GridModel.getInstance().getCell(
											Integer.parseInt(state
													.getAttributes()
													.getNamedItem("col")
													.getNodeValue()),
											Integer.parseInt(state
													.getAttributes()
													.getNamedItem("row")
													.getNodeValue()))
											.setReward(loadReward(dir), 1);
									if (loadWall(dir)) {
										GridModel.getInstance().getCell(
												Integer.parseInt(state
														.getAttributes()
														.getNamedItem("col")
														.getNodeValue()),
												Integer.parseInt(state
														.getAttributes()
														.getNamedItem("row")
														.getNodeValue()))
												.setWall(1);
									}
								}
								if (dir.getNodeName().equals("Left")) {
									GridModel.getInstance().getCell(
											Integer.parseInt(state
													.getAttributes()
													.getNamedItem("col")
													.getNodeValue()),
											Integer.parseInt(state
													.getAttributes()
													.getNamedItem("row")
													.getNodeValue()))
											.setReward(loadReward(dir), 2);
									if (loadWall(dir)) {
										GridModel.getInstance().getCell(
												Integer.parseInt(state
														.getAttributes()
														.getNamedItem("col")
														.getNodeValue()),
												Integer.parseInt(state
														.getAttributes()
														.getNamedItem("row")
														.getNodeValue()))
												.setWall(2);
									}
								}
								if (dir.getNodeName().equals("Right")) {
									GridModel.getInstance().getCell(
											Integer.parseInt(state
													.getAttributes()
													.getNamedItem("col")
													.getNodeValue()),
											Integer.parseInt(state
													.getAttributes()
													.getNamedItem("row")
													.getNodeValue()))
											.setReward(loadReward(dir), 3);
									if (loadWall(dir)) {
										GridModel.getInstance().getCell(
												Integer.parseInt(state
														.getAttributes()
														.getNamedItem("col")
														.getNodeValue()),
												Integer.parseInt(state
														.getAttributes()
														.getNamedItem("row")
														.getNodeValue()))
												.setWall(3);
									}
								}
							}
						}
					}
				}

			}

		} catch (ParserConfigurationException e) {
			return false;
		} catch (SAXException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Read the reward out of a Directionnode from the gridworldfile
	 * 
	 * @param dir
	 *            Direction Node of the GridworldFile
	 * @return Reward
	 * @throws Exception
	 */
	private double loadReward(Node dir) throws Exception {
		NodeList dirList = dir.getChildNodes();
		for (int i = 0; i < dirList.getLength(); i++) {
			Node node = dirList.item(i);
			if (node.getNodeName().toLowerCase().equals("reward")) {
				return Double.parseDouble(node.getTextContent());
			}
		}
		throw new Exception("Kein Reward gefunden!");
	}

	/**
	 * Read the wall out of a Directionnode from the gridworldfile
	 * 
	 * @param dir
	 *            DirectionNode of the gridworldFile
	 * @return true for a wall else false
	 * @throws Exception
	 */
	private boolean loadWall(Node dir) throws Exception {
		NodeList dirList = dir.getChildNodes();
		for (int i = 0; i < dirList.getLength(); i++) {
			Node node = dirList.item(i);
			if (node.getNodeName().toLowerCase().equals("wall")) {
				return 0 != Integer.parseInt(node.getTextContent());
			}
		}
		throw new Exception("Kein Reward gefunden!");
	}

	/**
	 * Loads a Feedback-File into the GridModel
	 * 
	 * @param file
	 *            Feedback File
	 * @return true if file is succesfully loaded else false
	 */
	public boolean parseFile(File file) {
		byte[] bytes = null;
		bytes = loadFileStream(file);
		if (bytes == null) {
			return false;
		}
		int rowSize = byteArrayToInt(bytes, 0);
		int colSize = byteArrayToInt(bytes, 4);
		if (!GridModel.getInstance().setSize(colSize, rowSize)) {
			return false;
		}
		int calculatedFilesize = (rowSize * colSize * 16) + 8;
		if (file.length() != calculatedFilesize) {
			return false;
		}
		int counter = 0;
		for (int i = 8; i < calculatedFilesize; i += 16) {
			for (int direction = 0; direction < 4; direction++) {
				GridModel.getInstance().getCell(counter % colSize,
						(int) Math.floor(counter / rowSize)).setReward(
						byteArrayToDouble(bytes, i + (direction * 4)),
						direction);
			}
			counter++;
		}
		return true;
	}
	/**
	 * Create a byte-Array of a .feedback File
	 * 
	 * @param file
	 *            feedback-File
	 * @return byte-Array including contents of file
	 */
	private byte[] loadFileStream(File file) {
		byte[] bytes = null;
		if (file.canRead()) {
			try {
				InputStream is = new FileInputStream(file);
				long length = file.length();
				if (length > Integer.MAX_VALUE) {
					return null;
				}
				bytes = new byte[(int) length];
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length
						&& (numRead = is.read(bytes, offset, bytes.length
								- offset)) >= 0) {
					offset += numRead;
				}

				// Ensure all the bytes have been read in
				if (offset < bytes.length) {
					return null;
				}

				// Close the input stream and return bytes
				is.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			//JOptionPane.showMessageDialog(parent, "Could not read vom File",
			//		"Error", JOptionPane.ERROR_MESSAGE);
			bytes = null;
		}
		return bytes;
	}

	
	/**
	 * Convert the byte array to an int starting from the given offset.
	 * 
	 * @param b
	 *            The byte array
	 * @param offset
	 *            The array offset
	 * @return The integer
	 */
	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	/**
	 * Convert the byte array to an int starting from the given offset.
	 * 
	 * @param b
	 *            The byte array
	 * @param offset
	 *            The array offset
	 * @return The integer
	 */
	private static double byteArrayToDouble(byte[] b, int offset) {
		double value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}



}