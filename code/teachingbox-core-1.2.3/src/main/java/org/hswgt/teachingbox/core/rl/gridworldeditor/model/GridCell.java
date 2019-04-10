package org.hswgt.teachingbox.core.rl.gridworldeditor.model;

import java.io.Serializable;

/**
*A single Cell of the GridModel. Contains the rewards of a state. Optionally obstacles.
*/
public class GridCell implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 171196891086231103L;
	private double rewards[];
	private double qValues[];
	private boolean wall[];
	private int row, col;
	private boolean isTerminalState;
	private boolean isStartState;

	public boolean isTerminalState() {
		return isTerminalState;
	}

	public void setTerminalState(boolean isTerminalState) {
		this.isTerminalState = isTerminalState;
	}

	/**
	 * Constructor
	 * @param col Column of the Cell
	 * @param row Row of the Cell
	 */
	public GridCell(int col, int row){
		this.col = col;
		this.row = row;
		rewards = new double[4];
		for(int i = 0; i < 4; i++){
			rewards[i] = 0;
		}
		qValues = new double[4];
		for(int i = 0; i < 4; i++){
			qValues[i] = 0;
		}
		wall = new boolean[4];
		for(int i = 0; i < 4; i++){
			wall[i] = false;
		}
		isTerminalState = false;
		isStartState = false;
	}
	
	public boolean isStartState() {
		return isStartState;
	}

	public void setStartState(boolean isStartState) {
		this.isStartState = isStartState;
	}

	/**
	 * Returns the Reward of one direction
	 * @param direction Direction
	 * @return reward of the direction
	 */
	public double getReward(int direction){
		return rewards[direction];
	}
	
	/**
	 * Set the Reward of one direction
	 * @param reward reward
	 * @param direction direction
	 */
	public void setReward(double reward, int direction){
		rewards[direction] = reward;
	}
	/**
	 * Return the QValue of one Direction
	 * @param direction direction
	 * @return QValue
	 */
	public double getQValue(int direction){
		return qValues[direction];
	}
	/**
	 * Set the QValue in one direction
	 * @param qValue QValue to set
	 * @param direction direction of the qValue
	 */
	public void setQValue(double qValue, int direction){
		qValues[direction] = qValue;
	}
	
	/**
	 * Set the wall of one direction.
	 * @param direction direction
	 */
	public void setWall(int direction){
		wall[direction] = true;
	}
	
	/**
	 * Return the Row of the Cell
	 * @return Row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Return the Column of the Cell
	 * @return Column
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Returns the status of a wall
	 * @param direction direction to check
	 * @return true if there is a wall else false.
	 */
	public boolean getWall(int direction){
		return wall[direction];
	}
	
	/**
	 * Remove all walls in the state
	 */
	public void resetWall(){
		for(int i = 0; i < 4; i++){
			wall[i] = false;
		}
	}
}
