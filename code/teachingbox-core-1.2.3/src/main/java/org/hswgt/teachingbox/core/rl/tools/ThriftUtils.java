package org.hswgt.teachingbox.core.rl.tools;

import java.util.LinkedList;
import java.util.List;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;

public class ThriftUtils implements java.io.Serializable {

	private static final long serialVersionUID = 2837027658791429063L;

	public static class Convert implements java.io.Serializable {
		private static final long serialVersionUID = -4281946231291701622L;

		/**
		 * Convert State to LinkedList
		 * @param s The @State
		 * @return The list
		 */
		public static LinkedList<Double> StateToList(State s) {
			LinkedList<Double> thriftState = new LinkedList<Double>();
			for( double tmpState : s.toArray() )
				thriftState.add(tmpState);
			return thriftState;
		}
		
		/**
		 * Convert Action to LinkedList
		 * @param a The @Action
		 * @return The list
		 */
		public static LinkedList<Double> ActionToList(Action a) {
			LinkedList<Double> thriftState = new LinkedList<Double>();
			for( double tmpAction : a.toArray() )
				thriftState.add(tmpAction);
			return thriftState;		
		}
		
		/**
		 * Convert List to State
		 * @param l The @List
		 * @return The @State
		 */
		public static State ListToState(List<Double> l) {
			State s = new State(l.size());
			// convert List<Double> into State
			for(int i=0; i < (l.size()); i++) {
				s.set(i, l.get(i));
			}
			return s;
		}
		
		/**
		 * Convert List to Action
		 * @param l The List
		 * @return The @Action
		 */
		public static Action ListToAction(List<Double> l) {
			Action a = new Action(l.size());
			// convert List<Double> into Action
			for(int i=0; i < (l.size()); i++) {
				a.set(i, l.get(i));
			}
			return a;
		}
	}	
}