package org.hswgt.teachingbox.core.rl.agent;

import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

public class ActionDebugger implements AgentObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4430053635455788552L;
	private final static Logger log4j = Logger.getLogger("ActionDebugger");	
	
	private int episode;
	private int step;
	
	private QFunction Q = null;
	
	public ActionDebugger (QFunction Q) {
		this.Q = Q;
	}
	
	@Override
	public void update(State state, Action action, State nextState,
			Action nextAction, double reward, boolean terminalState) {
		
		String qMsg = Q == null ? "" : ", Q=" +Q.getValue(state,  action);
		
		log4j.info("step=" + this.episode + "/" + this.step + 				
					", s=" + state.toString() + ", a=" + action.toString() + ", r=" + reward + 
					", sn=" + nextState.toString() + ", an=" + nextAction + ", terminal=" + terminalState +  qMsg);
		this.step++;
	}

	@Override
	public void updateNewEpisode(State initialState) {
		// TODO Auto-generated method stub
		log4j.info("=========== EPISODE " + this.episode);
		this.episode++;
		this.step=0;
	}
}