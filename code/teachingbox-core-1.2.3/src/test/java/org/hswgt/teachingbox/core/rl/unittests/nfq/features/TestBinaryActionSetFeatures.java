package org.hswgt.teachingbox.core.rl.unittests.nfq.features;

import static org.junit.Assert.*;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.features.type.BinaryActionSetFeatures;
import org.junit.Before;
import org.junit.Test;

public class TestBinaryActionSetFeatures {

	ActionSet actionSet = new ActionSet();
	BinaryActionSetFeatures features = null; 

	// define action set with three actions
	final Action RIGHT = new Action ( new double[]{-20} ); 
	final Action COAST = new Action ( new double[]{0} ); 
	final Action LEFT = new Action ( new double[]{20} );


	@Before
	public void init() {
		// the order is significant!
		actionSet.add(RIGHT);
		actionSet.add(COAST);
		actionSet.add(LEFT);	
		features = new BinaryActionSetFeatures (actionSet);
		
	}
	
	@Test
	public void testActionRight() {	
		double f[] = features.getFeatures(RIGHT.get(0));
		assertTrue (f[0] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[1] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[2] == InputFeatures.MIN_NEURON_ACT);	
	}
	
	@Test
	public void testActionCoast () {	
		double f[] = features.getFeatures(COAST.get(0));
		assertTrue (f[0] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[1] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[2] == InputFeatures.MIN_NEURON_ACT);	
	}

	@Test
	public void testActionLeft () {	
		double f[] = features.getFeatures(LEFT.get(0));
		assertTrue (f[0] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[1] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[2] == InputFeatures.MAX_NEURON_ACT);	
	}
}

