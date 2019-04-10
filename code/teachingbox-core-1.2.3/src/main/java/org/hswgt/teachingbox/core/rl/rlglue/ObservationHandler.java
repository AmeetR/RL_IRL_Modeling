package org.hswgt.teachingbox.core.rl.rlglue;

import org.hswgt.teachingbox.core.rl.env.State;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *  This class offers some methods for handling RL-Glue observations. 
 *  
 * @author tokicm
 *
 */
public class ObservationHandler {

	/**
	 * This method returns a Teachingbox state from an RL-Glue observation 
	 * @param o The @Observation
	 * @return The @State
	 */
	public static State getTbState (Observation o) {
		
		State s = new State (o.getNumInts() + o.getNumDoubles() + o.getNumChars());
		int dimIndex = 0;
		
		// 1) add <integer> state dimensions
		for (int i=0; i<o.getNumInts(); i++) {
			s.set(dimIndex, o.getInt(i));
			dimIndex++;
		}
		
		// 2) add <double> state dimensions
		for (int i=0; i<o.getNumDoubles(); i++) {
			s.set(dimIndex, o.getDouble(i));
			dimIndex++;			
		}
		
		// 3) add <char> state dimensions
		for (int i=0; i<o.getNumChars(); i++) {
			s.set(dimIndex, o.getChar(i));
			dimIndex++;
		}
		
		return s;
	}
}
