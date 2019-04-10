package org.hswgt.teachingbox.core.rl.nfq.util;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class ZTransformation {

	
	/** 
	 * returns the mean of all columns. Array is assumed to have the following format:
	 * 
	 * [[a1, b1, ...],
	 *  [a2, b2, ...],
	 *  [a3, b3, ...],
	 *  [a4, b4, ...],
	 *  ...
	 * ]
	 * @param array The data array
	 * @return mean [am, bm, ...]
	 */
	public static double[] getMean (double [][] array) {
		
		int rows = array.length;
		int cols = array[0].length;
		double meanArray[] = new double[cols];
		
		// initialize mean of all columns
		for (int c=0; c<cols; c++) {
			meanArray[c] = 0;
		}
		
		// sum up all columns
		for (int c=0; c<cols; c++) {
			for (int r=0; r<rows; r++) {
					meanArray[c] += array[r][c];
			}
		}
		
		// divide by amount of rows
		for (int c=0; c<cols; c++) {
			meanArray[c] /= (double)rows;
			
			// NaN check
			Preconditions.checkArgument(!Double.isInfinite(meanArray[c]) && !Double.isNaN(meanArray[c]),
				"!!! Double error!!! Debug: \n" + 
					"\n  array=" + ArrayUtils.toString(array) + 
					"\n  meanArray=" + ArrayUtils.toString(meanArray) +
					"\n  c=" +c);				
		}
		
		return meanArray;
	}

	/** 
	 * returns the standard deviation of all columns. Array is assumed to have the following format:
	 * 
	 * [[a1, b1, ...],
	 *  [a2, b2, ...],
	 *  [a3, b3, ...],
	 *  [a4, b4, ...],
	 *  ...
	 * ]
	 * @param array The data array
	 * @param means an array of means [am, bm, cm, .... ]
	 * @return standard deviations [am, bm, ...]
	 */
	public static double[] getStd (double array[][], double means[]) {
		
		int rows = array.length;
		int cols = array[0].length;
		double stdArray[] = new double[cols];
		
		// initialize mean of all columns
		for (int c=0; c<cols; c++) {
			stdArray[c] = 0;
		}
		
		// sum up all squared deviations from mean
		for (int c=0; c<cols; c++) {
			for (int r=0; r<rows; r++) {
					stdArray[c] += Math.pow(array[r][c]-means[c], 2);
			}
		}
		
		// divide by amount of rows
		for (int c=0; c<cols; c++) {
			
			stdArray[c] = Math.sqrt(stdArray[c]/(double)rows);
			
			// Bound minimum std for avoiding numeric problems (division by 0) 
			// in the transformValues() function! This case occurs if a  
			// state dimensions was constant during an episode.
			stdArray[c] = Math.max(stdArray[c],  0.01);
			
			// NaN check
			Preconditions.checkArgument(!Double.isInfinite(stdArray[c]) && !Double.isNaN(stdArray[c]),
					"NaN error!!! Debug: \n" + 
					"\n  array=" + ArrayUtils.toString(array) + 
					"\n  stdArray=" + ArrayUtils.toString(stdArray) +
					"\n  c=" +c);			
		}
		
		return stdArray;
	}
	
	
	// transforms an data array for neural processing: newValue = (values-mean)/std
	public static double [][] transformValues (double array[][], double means[], double[] std) {

		int rows = array.length;
		int cols = array[0].length;
		
		double retArray[][] = new double [rows][cols];
		double newValue  = 0;
		
		for (int c=0; c<cols; c++) {
			for (int r=0; r<rows; r++) {				
				
				newValue = (array[r][c]-means[c]) / std[c];
			
				// NaN check 
				Preconditions.checkArgument(!Double.isInfinite(newValue) && !Double.isNaN(newValue), 
					"!!! Double NaN error!!! Debug: \n" + 
						"\n  newValue=" + ArrayUtils.toString(newValue) + 
						"\n  array[c]=" + ArrayUtils.toString(array[r]) +
						"\n  mean=" + ArrayUtils.toString(means) +
						"\n  std=" + ArrayUtils.toString(std) +
						"\n  c=" + c);
				retArray[r][c]  = newValue;
			}
		}
		
		return retArray;
	}
	
	// back-transforms an data array for neural processing
	public static double[][] transformBackValues (double array[][], double means[], double[] std) {

		int rows = array.length;
		int cols = array[0].length;

		double retArray[][] = new double [rows][cols];
		double newValue;
		
		for (int c=0; c<cols; c++) {
			for (int r=0; r<rows; r++) {
				
				// backtransform
				newValue = (array[r][c] * std[c]) + means[c];
				
				retArray[r][c] = newValue;
			}
		}
		return retArray;
	}
	
	
	// transforms an data array for neural processing: newValue = (values-mean)/std
	public static double[] transformValues (double array[], double means[], double[] std) {
		return transformValues(new double [][]{array}, means, std)[0];
	}
	
	// transforms an data array for neural processing: newValue = (value - mean) / std
	public static double transformValue (double value, double mean, double std) {
		return transformValues (new double [][]{{value}}, new double[]{mean}, new double[]{std}) [0][0];
	}

	// back-transforms an data value for neural processing
	public static double transformBackValue (double value, double mean, double std) {
		return transformBackValues (new double [][]{{value}}, new double []{mean},new double []{std})[0][0];
	}
}
