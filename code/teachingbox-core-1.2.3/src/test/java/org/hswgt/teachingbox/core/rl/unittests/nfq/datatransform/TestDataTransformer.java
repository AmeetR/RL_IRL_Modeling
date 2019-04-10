package org.hswgt.teachingbox.core.rl.unittests.nfq.datatransform;

import static org.junit.Assert.*;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.DataTransformer;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer.ClipTransformer;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer.LogisticTransformer;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer.ScaleTransformer;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer.TanhTransformer;
import org.hswgt.teachingbox.core.rl.nfq.valuefunction.datatransform.transformer.ZTransformer;
import org.junit.Before;
import org.junit.Test;

public class TestDataTransformer {

	private final double data[][] = new double[][]{
			{1, 2}, {3, 4}
			//{5, 2}, {7, 2},
			//{-3, 3}, {5, 4}
	};
	
	DataTransformer dataTransform = new DataTransformer(2);
	
	@Before
	public void init() {
		// clean up old transformation chain
		dataTransform.getTransformationChain().clear();
	}
	
	@Test
	public void testScaleTransformer() {
		
		double testdata[] = new double[]{1, 4};
		double evaldata[] = testdata.clone();

		final int targetMin = -5;
		final int targetMax = 5;
		
		dataTransform.addTransformer(new ScaleTransformer(targetMin, targetMax));
		dataTransform.computeParameters(data);
		
		dataTransform.transformDataVector(evaldata);
		assertEquals (targetMin, evaldata[0], 0.0001);
		assertEquals (targetMax, evaldata[1], 0.0001);
		
		dataTransform.backTransformDataVector(evaldata);
		assertEquals (testdata[0], evaldata[0], 0.0001);
		assertEquals (testdata[1], evaldata[1], 0.0001);		
	}

	@Test
	public void testZTransformer() {
		
		double testdata[] = new double[]{1, 4};
		double evaldata[] = testdata.clone();

		dataTransform.addTransformer(new ZTransformer());
		dataTransform.computeParameters(data);
		
		dataTransform.transformDataVector(evaldata);
		//System.out.println("transform: testdata=" + ArrayUtils.toString(testdata) + ", evaldata=" + ArrayUtils.toString(evaldata));
		assertTrue (testdata[0] != evaldata[0]);
		assertTrue (testdata[1] != evaldata[1]);
		
		dataTransform.backTransformDataVector(evaldata);
		//System.out.println("back-transform: testdata=" + ArrayUtils.toString(testdata) + ", evaldata=" + ArrayUtils.toString(evaldata));
		assertEquals (testdata[0], evaldata[0], 0.0001);
		assertEquals (testdata[1], evaldata[1], 0.0001);		
	}

	@Test
	public void testLogisticTransformer() {
		
		double testdata[] = new double[]{1, 4};
		double evaldata[] = testdata.clone();

		dataTransform.addTransformer(new LogisticTransformer());
		dataTransform.computeParameters(data);
		
		dataTransform.transformDataVector(evaldata);
		assertTrue (testdata[0] != evaldata[0]);
		assertTrue (testdata[1] != evaldata[1]);
		
		dataTransform.backTransformDataVector(evaldata);
	}

	@Test
	public void testTanhTransformer() {
		
		double testdata[] = new double[]{1, 4};
		double evaldata[] = testdata.clone();

		dataTransform.addTransformer(new TanhTransformer());
		dataTransform.computeParameters(data);
		
		dataTransform.transformDataVector(evaldata);
		assertTrue (testdata[0] != evaldata[0]);
		assertTrue (testdata[1] != evaldata[1]);
		
		dataTransform.backTransformDataVector(evaldata);
		assertEquals (testdata[0], evaldata[0], 0.0001);
		assertEquals (testdata[1], evaldata[1], 0.0001);		
	}

	@Test
	public void testClipTransformer() {
		
		double testdata[] = new double[]{0, 5};
		double evaldata[] = testdata.clone();

		final int targetMin = 1;
		final int targetMax = 4;
		
		dataTransform.addTransformer(new ClipTransformer(targetMin, targetMax));
		dataTransform.computeParameters(data);
		
		dataTransform.transformDataVector(evaldata);
		assertEquals (targetMin, evaldata[0], 0.0001);
		assertEquals (targetMax, evaldata[1], 0.0001);
		
		dataTransform.backTransformDataVector(evaldata);
		assertEquals (targetMin, evaldata[0], 0.0001);
		assertEquals (targetMax, evaldata[1], 0.0001);
	}
	
	@Test
	public void testTransformationChain() {
		
		double testdata[] = new double[]{1, 4};
		double evaldata[] = testdata.clone();

		final int targetMin = -5;
		final int targetMax = 5;
		
		dataTransform.addTransformer(new ScaleTransformer(targetMin, targetMax));
		dataTransform.addTransformer(new TanhTransformer());
		dataTransform.addTransformer(new LogisticTransformer());
		dataTransform.addTransformer(new ClipTransformer(targetMin, targetMax));
		dataTransform.computeParameters(data);
		
		dataTransform.transformDataVector(evaldata);
		assertTrue (testdata[0] != evaldata[0]);
		assertTrue (testdata[1] != evaldata[1]);

		dataTransform.backTransformDataVector(evaldata);
		assertEquals (testdata[0], evaldata[0], 0.0001);
		assertEquals (testdata[1], evaldata[1], 0.0001);		
	}
}
