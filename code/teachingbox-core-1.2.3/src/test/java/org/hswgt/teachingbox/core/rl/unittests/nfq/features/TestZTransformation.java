package org.hswgt.teachingbox.core.rl.unittests.nfq.features;

import static org.junit.Assert.*;

import org.hswgt.teachingbox.core.rl.nfq.util.ZTransformation;
import org.junit.Before;
import org.junit.Test;


public class TestZTransformation {

	double testArray[][] = new double[][] {{1, 2}, {2, 0}, {-3, 1.0234}};
	double mean[] = null;
	double std[] = null;
	
	@Before
	public void init() {
		this.mean = ZTransformation.getMean(testArray);
		this.std =  ZTransformation.getStd(testArray, mean);		
	}
	
	@Test
	public void testSingleValue() {
		double tValue= ZTransformation.transformValue(testArray[0][0], mean[0], std[0]);
		double tbValue = ZTransformation.transformBackValue(tValue, mean[0], std[0]);
		assertEquals (testArray[0][0], tbValue, 0.0001); 
	}
	
	@Test
	public void testArray()  {
		
		System.out.println ("BIG Array test:");
		double tVal[][] = ZTransformation.transformValues(testArray, mean, std);
		double tbVal[][] = ZTransformation.transformBackValues(tVal, mean, std);
		
		for (int row=0; row<tVal.length; row++) {
			for (int col=0; col<tVal[0].length; col++) {
				assertEquals (testArray[row][col], tbVal[row][col], 0.0001);
			}			
		}
	}
}
