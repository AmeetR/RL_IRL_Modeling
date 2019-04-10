package org.hswgt.teachingbox.core.rl.unittests.nfq.features;

import static org.junit.Assert.*;

import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.features.type.BinaryIntegerFeatures;
import org.junit.Test;

public class TestBinaryIntegerFeatures {

	BinaryIntegerFeatures features = new BinaryIntegerFeatures(4); 
	
	@Test
	public void test0 () {	
		double f[] = features.getFeatures(0);
		assertTrue (f[0] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[1] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[2] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[3] == InputFeatures.MIN_NEURON_ACT);	
	}
	
	@Test
	public void test1 () {	
		double f[] = features.getFeatures(1);
		assertTrue (f[0] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[1] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[2] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[3] == InputFeatures.MIN_NEURON_ACT);	
	}
	
	@Test
	public void test3 () {	
		double f[] = features.getFeatures(3);
		assertTrue (f[0] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[1] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[2] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[3] == InputFeatures.MIN_NEURON_ACT);	
	}
	
	@Test
	public void test4 () {	
		double f[] = features.getFeatures(4);
		assertTrue (f[0] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[1] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[2] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[3] == InputFeatures.MIN_NEURON_ACT);	
	}

	@Test
	public void test7 () {	
		double f[] = features.getFeatures(7);
		assertTrue (f[0] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[1] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[2] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[3] == InputFeatures.MIN_NEURON_ACT);	
	}

	@Test
	public void test8 () {	
		double f[] = features.getFeatures(8);
		assertTrue (f[0] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[1] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[2] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (f[3] == InputFeatures.MAX_NEURON_ACT);	
	}

	@Test
	public void test15 () {	
		double f[] = features.getFeatures(15);
		assertTrue (f[0] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[1] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[2] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (f[3] == InputFeatures.MAX_NEURON_ACT);	
	}
}

