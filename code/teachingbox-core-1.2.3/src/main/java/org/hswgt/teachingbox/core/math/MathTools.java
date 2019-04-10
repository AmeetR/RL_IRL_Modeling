
package org.hswgt.teachingbox.core.math;

/**
 * A collection of everyday math tools.
 * 
 * @author Richard Cubek
 *
 */
public class MathTools
{
	/**
	 * Rounds a value to a value with an arbitrary number of decimal places.
	 * @param value The value to round.
	 * @param places The number of decimal places.
	 * @return The rounded value
	 */
	public static double round(double value, int places)
	{
		long factor = (long)Math.pow(10, places);

		// shift the decimal the correct number of places to the right
		value = value * factor;

		// round to the nearest integer
		long tmp = Math.round(value);

		// shift the decimal the correct number of places back to the left
		return (double) tmp / factor;		
	}
}
