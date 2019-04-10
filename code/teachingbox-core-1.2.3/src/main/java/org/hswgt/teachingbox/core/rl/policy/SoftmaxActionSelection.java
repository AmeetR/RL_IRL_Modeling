package org.hswgt.teachingbox.core.rl.policy;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

/**
 * Implementation of the Softmax policy (aka Boltzman Exploration)
 *
 */
public class SoftmaxActionSelection extends GreedyPolicy {
	
	// private double mask[];
	private static final long serialVersionUID = -5074641449910403092L;

	/**
	 * It controls the border between exploration and exploitation.
	 */
	protected double temperature;
	
	protected boolean isNormalized = false;
	protected double normMin = -1;
	protected double normMax = 1;

	/**
	 * The constructor
	 * 
	 * @param Q
	 *            the Q-function
	 * @param actionSet
	 *            the action set
	 * @param temperature
	 *            the temperature
	 */
	public SoftmaxActionSelection(QFunction Q, ActionSet actionSet,
			double temperature) {
		super(Q, actionSet);
		this.temperature = temperature;
		// mask = new double[actionSet.size()];
	}
	
	/**
	 * This function sets normalization of the Q-values into the interval [normMin, normMax]
	 * @param normMin The normalized minimum value
	 * @param normMax The normalized maximum value
	 */
	public void setNormalization (double normMin, double normMax) {
		
		this.normMin = normMin;
		this.normMax = normMax;
		this.isNormalized = true;
	}
	
	/**
	 * This function disables normalization of the Q-values
	 */
	public void unsetNormalization () {
		this.isNormalized = false;
	}

	/**
	 * choose an action to perform. it uses the value "Temperature" to choose
	 * which Action to perform.
	 * @param state The State
	 */
	public Action getAction(final State state) {
		
		if (isNormalized) {
			return SoftmaxActionSelection.getNormalizedSoftmaxAction(this.Q, this.actionSet,
					this.temperature, state, normMin, normMax);
		} else {
			return SoftmaxActionSelection.getSoftmaxAction(this.Q, this.actionSet,
				this.temperature, state);
		}
	}

	public double getProbability(State state, Action action) {
		ActionSet validActions = this.actionSet.getValidActions(state);
		if (!validActions.contains(action))
			return 0.0;
		return getSoftmaxProbabilityForAction(Q, this.actionSet,
				this.temperature, state, action);
	}
	
	/**
	 * returns an action which is selected wrt. softmax action selection. all values are
	 * normalized in (<code>minNormValue, maxNormValue</code>).
	 * 
	 * @param Q the Q-function
	 * @param actionSet the action set
	 * @param temperature the temperature
	 * @param state the state
	 * @param minNormValue the minimum normalized value
	 * @param maxNormValue the maximum normalized value
	 * @return The Action
	 */
	public static Action getNormalizedSoftmaxAction(QFunction Q, ActionSet actionSet,
			double temperature, State state, double minNormValue, double maxNormValue) {

		double randNumber = Math.random();
		// System.out.println ("\nrandom number: " + randNumber);

		double[] aProbabilities = getNormalizedSoftmaxProbabilitiesByQFunction(
				Q, actionSet, temperature, state, minNormValue, maxNormValue);
		double aProbSum = 0.0;
		Action finalAction = null;
		ActionSet validActions = actionSet.getValidActions(state);
		int actionIndex = 0;

		// select action
		for (actionIndex = 0; actionIndex < aProbabilities.length; actionIndex++) {
			// increment probability sum
			aProbSum += aProbabilities[actionIndex];

			if (aProbSum >= randNumber) {
				finalAction = validActions.get(actionIndex);
				break;
			}
		}

		// System.out.println ("selecting action " + actionIndex + "\n");
		return finalAction;
	}

	

	/**
	 * returns an action selected according to softmax action selection
     * @param Q the Q function
	 * @param actionSet the action set
	 * @param temperature the temperature
	 * @param state the state
	 * @return The Action
	 */
	public static Action getSoftmaxAction(QFunction Q, ActionSet actionSet,
			double temperature, State state) {

		ActionSet validActions = actionSet.getValidActions(state);
		double[] qValues = new double[validActions.size()];
		
		// get q-values
		for (int actionIndex=0; actionIndex <validActions.size(); actionIndex++) {
			qValues[actionIndex] = Q.getValue(state, validActions.get(actionIndex));
		}
		
		// return softmax action
		return validActions.get(getSoftmaxAction(qValues, temperature));
		
	}
	
	/**
	 * returns the index of an random softmax action given a double[] array and temperature 
	 * @param vector The data input vector
	 * @param temperature The temperature parameter
	 * @return softmax normalized values
	 */
	public static int getSoftmaxAction(double[] vector, double temperature) {

		double randNumber = Math.random();
		//System.out.println ("\nRandom number: " + randNumber);

		double[] aProbabilities = getSoftmaxProbabilitiesByDoubleVector(vector, temperature);
		double aProbSum = 0.0;
		int actionIndex = 0;
		int finalAction = 0;

		// select action
		for (actionIndex = 0; actionIndex < aProbabilities.length; actionIndex++) {
			// increment probability sum
			aProbSum += aProbabilities[actionIndex];
			//System.out.println ("Sum action[" + actionIndex + "]: " + aProbSum + " - Prob(" + aProbabilities[actionIndex] + ") - Value(" + vector[actionIndex] + ")");

			if (aProbSum >= randNumber) {
				finalAction = actionIndex; 
				//System.out.println (aProbSum + ">" + randNumber);
				break;
			}
		}

		// System.out.println ("selecting action " + actionIndex + "\n");
		return finalAction;
	}

	/**
	 * returns the softmax probability of action a in state s (wrt. to the given
	 * temperature)
	 * 
	 * @param Q the Q-function
	 * @param actionSet the action set
	 * @param temperature the temperature parmaeter
	 * @param state the state
	 * @param action the action
	 * @return the softmax probability for "action" in "state"
	 */
	public static double getSoftmaxProbabilityForAction(QFunction Q,
			ActionSet actionSet, double temperature, State state, Action action) {

		ActionSet validActions = actionSet.getValidActions(state);

		double values[] = new double[validActions.size()];
		int actionIndex = -1;

		for (int i = 0; i < validActions.size(); i++) {
			values[i] = Q.getValue(state, validActions.get(i));
			if (validActions.get(i).equals(action)) {
				actionIndex = i;
			}
		}

		return getSoftmaxProbabilitiesByDoubleVector(values, temperature)[actionIndex];
	}

	/**
	 * This function normalizes the double array <code>values</code>to the boundaries 
	 * (<code>minNormValue, maxNormValue</code>)
	 * @param minNormValue the minimum normalized value
	 * @param maxNormValue the maximum normalized value
	 * @param values the values to be normalized
	 * @return The normalized values
	 */
	public static double[] getNormalizedValues(double minNormValue, double maxNormValue, double[] values) {

		double[] normValues = new double[values.length];
		Double minValue = Double.POSITIVE_INFINITY;
		Double maxValue = Double.NEGATIVE_INFINITY;
		
		// determine minValue and maxValue
		for (int i=0; i<values.length; i++) {
			if (values[i] < minValue) {
				minValue=values[i];
			}
			if (values[i] > maxValue) {
				maxValue=values[i];
			}
		}
		
		/**
		 * normalize values
		 */
		// if minValue=maxValue, then the denominator of the normalizsation equation goes to zero! 
		// solution: set all values equally to minBoundValue
		if (Math.abs(minValue - maxValue) < 0.0001) {
			for (int i=0; i<values.length; i++) {
				normValues[i] = minNormValue;
			}			
		}
		else {
			double normFactor = ((maxNormValue-minNormValue) / (maxValue - minValue));
			for (int i=0; i<values.length; i++) {
				normValues[i] = ((values[i] - minValue)*normFactor) + minNormValue;
			}			
		}
		
		return normValues;
	}

	/**
	 * returns softmax probabilities of all actions in state s (wrt. the
	 * given Q-function and temperature)
	 * 
	 * @param Q the Q-function
	 * @param actionSet the action set
	 * @param temperature the temperature
	 * @param state the state
	 * @return The softmax probabilities
	 */
	public static double[] getSoftmaxProbabilitiesByQFunction(QFunction Q,
			ActionSet actionSet, double temperature, State state) {

		ActionSet validActions = actionSet.getValidActions(state);

		double values[] = new double[validActions.size()];

		for (int i = 0; i < validActions.size(); i++) {
			values[i] = Q.getValue(state, validActions.get(i));
		}

		// System.out.println ("valid.size()=" + valid.size());

		return getSoftmaxProbabilitiesByDoubleVector(values, temperature);
	}
	
	/**
	 * returns softmax probabilities of all actions in state s (wrt. the
	 * given Q-function and temperature). Values are normalized in (<code>minNormValue,maxNormValue</code>)
	 * 
	 * @param Q the Q-function
	 * @param actionSet the action set
	 * @param temperature the temperature
	 * @param state the state
	 * @param minNormValue the minimum normalized value
	 * @param maxNormValue the maximum normalized value
	 * @return The normalized Softmax probabilities
	 */
	public static double[] getNormalizedSoftmaxProbabilitiesByQFunction(QFunction Q,
			ActionSet actionSet, double temperature, State state, double minNormValue, double maxNormValue) {

		ActionSet validActions = actionSet.getValidActions(state);

		double values[] = new double[validActions.size()];

		for (int i = 0; i < validActions.size(); i++) {
			values[i] = Q.getValue(state, validActions.get(i));
		}
		
		double[] normValues = new double[validActions.size()];
		normValues = getNormalizedValues(minNormValue, maxNormValue, values);

		// System.out.println ("valid.size()=" + valid.size());

		return getSoftmaxProbabilitiesByDoubleVector(normValues, temperature);
	}

	/**
	 * returns softmax probabilities for all given values wrt. the temperature
	 * 
	 * @param values the values
	 * @param temperature the temperature
	 * @return The softmax probabilities
	 */
	public static double[] getSoftmaxProbabilitiesByDoubleVector(
			double values[], double temperature) {

		Double denominator = new Double(0);
		double[] aProbabilities = new double[values.length];
		boolean foundNAN = false;

		// compute denominator
		for (int i = 0; i < values.length; i++) {
			denominator = denominator + Math.exp(values[i] / temperature);
			
		}
		if (Double.isNaN(denominator)) {
			foundNAN = true;
		}

		// compute action probabilities
		for (int i = 0; i < aProbabilities.length; i++) {
			aProbabilities[i] = Math.exp(values[i] / temperature) / denominator;
			
			if (Double.isNaN(aProbabilities[i])) {				
				foundNAN = true;
			}
		}
		
		// select a greedy action if the denominator becomes zero or infinity (numeric
		// limitations).  
		// This effect is caused when all exponents (Q(s)/temperature)
		// are too highly negative, or too highly positive.
		// Reasonable fallback behavior => greedy action selection
		if (foundNAN && (denominator == 0.00000000000000000000 || denominator.equals(Double.POSITIVE_INFINITY))) {
			
			// determine maximum q-value
			double maxQvalue = Double.NEGATIVE_INFINITY;
			double maxQvalueCounter = 0;
			
			for (int i=0; i<values.length; i++) {
				if (values[i] > maxQvalue) {
					maxQvalueCounter = 1;
					maxQvalue = values[i];
				} else if (values[i] == maxQvalue) {
					maxQvalueCounter++;
				}
			}
			
			// set greedy action probabilities
			for (int i=0; i<values.length; i++) {
				if (values[i] == maxQvalue) {
					aProbabilities[i] = 1.0/maxQvalueCounter;
				} else {
					aProbabilities[i] = 0.0;
				}
			}

		// Debug output for other cases
		} else if (foundNAN){
			System.out.println ("\n\nFound NaN! Temperature: " + temperature +
								"- Denominator: " + denominator +
								" - aProbabilities:" );
			
			for (int i = 0; i < values.length; i++) {
				 System.out.print (aProbabilities[i] + ",");
			}
			System.out.println ("\nValues:");
			
			for (int i = 0; i < values.length; i++) {
				 System.out.print (values[i] + ",");
			}
			
		}
		

		/*
		 * //if (foundNAN) { for(int i = 0; i< values.length; i++){
		 * System.out.print (values[i] + ", "); } System.out.println ("");
		 * 
		 * for(int i = 0; i< values.length; i++){ System.out.print
		 * (aProbabilities[i] + ", "); } System.out.println (""); //}
		 */

		return aProbabilities;
	}

	/**
	 * returns the current value of "Temperature".
	 * 
	 * @return Temperature
	 */
	public double getTemperature() {
		return this.temperature;
	}

	/**
	 * @param temperature the temperature to be set
	 */
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	
	public static void main(String[] args) throws Exception {
    	
    	//double[] Qarray = new double[] {1,2,3,4,5,4,3,2,1,2,3,4,6};
    	double[] Qarray = new double[] {-3.0, -3.2, -3.0, -3.1};
		//double[] Qarray = new double[] {0.0, 0.0, 0.0, 1000.0};
    	double[] QnormArray = SoftmaxActionSelection.getNormalizedValues(-1, 1, Qarray);
    	double[] QnormQArray = SoftmaxActionSelection.getSoftmaxProbabilitiesByDoubleVector(QnormArray, 1);
    	   	
    	System.out.print ("Qarray = ");
    	for (int i=0; i<Qarray.length; i++) {
    		System.out.print (Qarray[i] + ", ");
    	}

    	System.out.print ("\nQNormArray = ");
    	for (int i=0; i<QnormArray.length; i++) {
    		System.out.print (QnormArray[i] + ", ");
    	}
    	System.out.print ("\nQNormQArray = ");
    	for (int i=0; i<QnormArray.length; i++) {
    		System.out.print (QnormQArray[i] + ", ");
    	}
    	
    	double[] actionProbabilities = SoftmaxActionSelection.getSoftmaxProbabilitiesByDoubleVector(Qarray, 1);
    	System.out.print ("\nactionProbabilities t=1: ");
    	for (int i=0; i<actionProbabilities.length; i++) {
    		System.out.print (actionProbabilities[i] + ", ");
    	}
    	
    	actionProbabilities = SoftmaxActionSelection.getSoftmaxProbabilitiesByDoubleVector(Qarray, 0.0000001);
    	System.out.print ("\nactionProbabilities t=0.0000001: ");
    	for (int i=0; i<actionProbabilities.length; i++) {
    		System.out.print (actionProbabilities[i] + ", ");
    	}
    	
    	actionProbabilities = SoftmaxActionSelection.getSoftmaxProbabilitiesByDoubleVector(Qarray, 1000000);
    	System.out.print ("\nactionProbabilities t=1000000: ");
    	for (int i=0; i<actionProbabilities.length; i++) {
    		System.out.print (actionProbabilities[i] + ", ");
    	}
    	
    	int action = SoftmaxActionSelection.getSoftmaxAction(Qarray, 1);
    	System.out.print ("getSoftmaxAction t=1: " + action);
    	
    	


    	
    	System.exit(1);    	
	}
}