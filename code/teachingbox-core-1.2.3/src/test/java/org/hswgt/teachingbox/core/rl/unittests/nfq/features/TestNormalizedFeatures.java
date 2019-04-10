package org.hswgt.teachingbox.core.rl.unittests.nfq.features;

import static org.junit.Assert.*;

import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.features.type.NormalizedFeatures;
import org.junit.Test;

public class TestNormalizedFeatures {

	private final int min = -1; 
	private final int max = 1; 
	NormalizedFeatures df = new NormalizedFeatures(min, max); 
	
	@Test
	public void testBounds() {

		double features[] = df.getFeatures(min); 		
		assertEquals(features[0], InputFeatures.MIN_NEURON_ACT, 0.001);
	
		features = df.getFeatures(0); 		
		assertEquals(InputFeatures.MIN_NEURON_ACT + ((InputFeatures.MAX_NEURON_ACT-InputFeatures.MIN_NEURON_ACT)/2.), features[0], 0.001);
		
		features = df.getFeatures(max); 		
		assertEquals(features[0], InputFeatures.MAX_NEURON_ACT, 0.001);
	}
}
