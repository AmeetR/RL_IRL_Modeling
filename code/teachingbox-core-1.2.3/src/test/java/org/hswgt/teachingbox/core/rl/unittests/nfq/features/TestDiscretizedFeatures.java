package org.hswgt.teachingbox.core.rl.unittests.nfq.features;

import static org.junit.Assert.*;

import org.hswgt.teachingbox.core.rl.nfq.features.InputFeatures;
import org.hswgt.teachingbox.core.rl.nfq.features.type.DiscretizedFeatures;
import org.junit.Test;

public class TestDiscretizedFeatures {

	private final int min = -1; 
	private final int max = 1; 
	private final int discs = 5; 
	DiscretizedFeatures df = new DiscretizedFeatures(min, max, discs); 
	
	@Test
	public void testBounds() {

		double features[] = df.getFeatures(min); 		
		assertTrue (features[0] == InputFeatures.MAX_NEURON_ACT);
		for (int i=1; i<discs; i++) {			
			assertTrue (features[i] == InputFeatures.MIN_NEURON_ACT);
		}
		
		features = df.getFeatures(max); 		
		for (int i=0; i<discs-1; i++) {			
			assertTrue (features[i] == InputFeatures.MIN_NEURON_ACT);
		}
		assertTrue (features[discs-1] == InputFeatures.MAX_NEURON_ACT);
	}
	
	@Test
	public void testCenter() {
		double features[] = df.getFeatures(0); 		
		assertTrue (features[0] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (features[1] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (features[2] == InputFeatures.MAX_NEURON_ACT);
		assertTrue (features[3] == InputFeatures.MIN_NEURON_ACT);
		assertTrue (features[4] == InputFeatures.MIN_NEURON_ACT);
	}
}
