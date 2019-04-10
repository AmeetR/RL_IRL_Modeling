package org.hswgt.teachingbox.core.rl.nfq.learner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.env.TransitionSampleSARS;
import org.hswgt.teachingbox.core.rl.nfq.env.NFQEnvironment;
import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.Nfq;
import org.hswgt.teachingbox.core.rl.plot.Plotter;

import com.google.common.base.Preconditions;

public class NFQLearner implements Plotter, Serializable {
	
	private static final long serialVersionUID = -2642816212614337725L;

	// Logger
    private final static Logger log4j = Logger.getLogger("NFQLearner");
	
    // default: no "hint-to-goal" heuristic
	protected int goalHeuristicPercent = 0;

	public static final String PARAM_BATCH_SIZE = "BATCH_SIZE";

	protected ActionSet actionSet;

	protected int iEpisode = 0;
	protected int steps = 0;
	protected boolean episodicTransitionBatchCleaning = false;
	
	protected double alpha=0.001;
	protected double gamma=1.0;
	
	protected double R = Double.NEGATIVE_INFINITY;
	protected double Rbest = Double.NEGATIVE_INFINITY;
	protected int RbestEpoch = 0; 
	
	protected int sampleEpisodes; 
	protected int trainEpochs; 
	protected int nfqIterations;
	protected NFQEnvironment nfqEnv = null;
	
	protected QLearningBatch transitionBatch; 
	protected int iTransitionSamples = 0;
	protected Nfq Q;
    	
	/**
	 * This function sets the option for cleaning the transition batch after each learning episode. (default: false)
	 * @param status The status 
	 */
	public void setEpisodicTransitionBatchCleaning(boolean status) {
		this.episodicTransitionBatchCleaning = status;
	}
	
	public double getParameter(State s, Action a, String parameter) {
		if (parameter == PARAM_BATCH_SIZE) {
			return transitionBatch.size();
		} else {
			return -1;
		}
	}
	
	/**
	 * sets the learning rate for Q-learning
	 * @param alpha The step-size parameter
	 */
	public void setAlpha(double alpha) {
		this.alpha=alpha;
	}
	
	/**
	 * sets the discounting parameter for Q-learning
	 * @param gamma The discounting parameter
	 */
	public void setGamma(double gamma) {
		this.gamma=gamma;
	}
	
	/**
	 * Adds a transition to the transition batch
	 * @param state The state
	 * @param action The action
	 * @param nextState The next state
	 * @param reward The reward
	 */
	public void addTransition(State state, Action action, State nextState, double reward) {
        
		// put transition sample to the batch
       	QValue q = transitionBatch.putTransitionSample(new TransitionSampleSARS(state, action, nextState, reward));
       	InputFeatures feat = Q.getInputFeatures();       	
       	double featVector[] = feat.getInputFeatures(state, action);
       	q.setInputFeatures(featVector.clone());
	}

	/**
	 * This function imports transitions from a file
	 * @param filename The filename 
	 * @param delimiter The column delimiter
	 * @param stateIdx The column indices for the state
	 * @param actionIdx The column indices for the action
	 * @param nextStateIdx The column indices for the next state
	 * @param rewardIdx the column index for the reward
	 * @param skipFirstLine indicator if the first line should be skipped
	 */
	public void importCSV (String filename, String delimiter, int stateIdx[], int actionIdx[], int nextStateIdx[], int rewardIdx, boolean skipFirstLine) {
		
		// create File object
		final File f = new File (filename);
		final double minFields = stateIdx.length + actionIdx.length + nextStateIdx.length +1;
		int lineno = 0;
		int importedSamples = 0;
				
		try {
			final BufferedReader reader = new BufferedReader(new FileReader (f));
			
			while (reader.ready()) {
				final State s = new State(stateIdx.length);
				final State sn = new State (nextStateIdx.length);
				final Action a = new Action (actionIdx.length);
				final double reward; 
				
				String line = reader.readLine();
				
				if (lineno == 0 && skipFirstLine) {
					lineno++;
					continue;
				}
				
				if (!line.startsWith("#")) {
					String fields[] = line.split(delimiter);					
					Preconditions.checkArgument( fields.length >= minFields, "Data file %s line %s contains only %s fields. %s fields are minimum.", filename, lineno, fields.length, minFields);
					
					for (int i=0; i<s.size(); i++) {
						s.set(i, Double.parseDouble(fields[stateIdx[i]]));
					}
					for (int i=0; i<a.size(); i++) {
						a.set(i, Double.parseDouble(fields[actionIdx[i]]));
					}
					for (int i=0; i<sn.size(); i++) {
						sn.set(i, Double.parseDouble(fields[nextStateIdx[i]]));
					}
					reward = Double.parseDouble(fields[rewardIdx]);
					this.addTransition(s, a, sn, reward);
					importedSamples++;
					this.iTransitionSamples++;
				}				
				lineno++;
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log4j.info("Imported samples: " + importedSamples + " from file " + filename);
	}
	
	public int size() {
		return this.transitionBatch.size(); // + this.goalHeuristicBatch.size();
	}
	
	/**
	 * Debugs inputs and outputs of the last MLP training to the given text file
	 * @param filename The filename 
	 */
	public void debugTrainingData(String filename) {
		
		File f = new File (filename);		
		
		try {
			FileWriter fw = new FileWriter (f, false);
			
			
			for (int line=0; line<transitionBatch.getQTable().size() ; line++) {
				
				State s = transitionBatch.getQTable().get(line).getState();
				Action a = transitionBatch.getQTable().get(line).getAction();
				double r = transitionBatch.getQTable().get(line).getReward();
				double q = transitionBatch.getQTable().get(line).getQ();
				
				if (line==0) {
					for (int i=0; i<s.size(); i++) {
						fw.write("s" + i + " ");
					}
					fw.write("| ");
					for (int i=0; i<a.size(); i++) {
						fw.write("a" + i + " ");
					}
					fw.write ("| r | Q_target Q_actual\n"); 
				}
				
				
				fw.write("" + line);
				for (int i=0; i<s.size(); i++) {
					fw.write(" " + s.get(i));
				}
				fw.write(" | ");
				for (int i=0; i<a.size(); i++) {
					fw.write(a.get(i) + " ");
				}
				fw.write("| " + r + " | ");
				fw.write(q + " " + Q.getValue(s, a));
				fw.write("\n");
			}
			
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 		
	}
	
	@Override
	public void plot() {
		this.debugTrainingData("data/NFQLearner-qTable-episode" + this.iEpisode + ".txt");
	}

	/**
	 * @return the goalHeuristicPercent
	 */
	public int getGoalHeuristicPercent() {
		return goalHeuristicPercent;
	}

	/**
	 * @param goalHeuristicPercent the goalHeuristicPercent to set
	 */
	public void setGoalHeuristicPercent(int goalHeuristicPercent) {
		this.goalHeuristicPercent = goalHeuristicPercent;
	}
}
