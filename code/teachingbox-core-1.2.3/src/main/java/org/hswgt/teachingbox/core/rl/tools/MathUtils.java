/**
 *
 * $$Id: MathUtils.java 988 2015-06-17 19:48:01Z micheltokic $$
 *
 * @version   $$Rev: 988 $$
 * @author    $$Author: micheltokic $$
 * @date      $$Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $$
 *
 */

package org.hswgt.teachingbox.core.rl.tools;

/**
 * Some mathematical helper functions
 */
public class MathUtils
{
    /**
     * Computes the floating-point remainder of a/b.
     * @param a Variable a  
     * @param b Variable b
     * @return the result
     */
    public static double fmod(double a, double b)
    {
        int result = (int) Math.floor(a / b);
        return a - result * b;
    }

    /**
     * Returns true, if |a-b| &lt;= delta
     * 
     * @param a The first value
     * @param b The second value
     * @param delta The delta
     * @return true if vectors are equal+- delta
     */
    public static boolean equalsDelta(double a, double b, double delta)
    {
        if (Math.abs(a - b) < delta)
            return true;
        return false;
    }

    /**
     * Returns true, if the vectors have the same length and for all
     * |a[i]-b[i]| &lt;= delta
     * 
     * @param a The fist vector
     * @param b The second vector
     * @param delta The delta
     * @return true if vectors are equal+- delta
     */
    public static boolean equalsDelta(double[] a, double[] b, double delta)
    {
        if (a.length != b.length)
            return false;

        for (int i = 0; i < a.length; i++)
        {
            if (!MathUtils.equalsDelta(a[i], b[i], delta))
                return false;
        }
        return true;
    }

    /**
     * Calculates the cumulative sum of an array
     * 
     * @param input The input array
     * @param csum Memory for the cumulative sum. Must have the same size as
     *        input
     */
    public static void cumsum(final double input[], double csum[])
    {
        double sum = 0;
        for (int i = 0; i < input.length; i++)
        {
            sum = sum + input[i];
            csum[i] = sum;
        }
    }

    /**
     * Calculates the cumulative sum of an array
     * 
     * @param input The input array
     * @return The cumulative sum.
     */
    public static double[] cumsum(final double input[])
    {
        double[] csum = new double[input.length];
        double sum = 0;
        for (int i = 0; i < input.length; i++)
        {
            sum = sum + input[i];
            csum[i] = sum;
        }
        return csum;
    }
    
    /**
     * Calculates the cumulative sum of an array
     * 
     * @param input The input array
     * @return The cumulative sum.
     */
    public static double[] cumsum(final Double[] input)
    {
        double[] csum = new double[input.length];
        double sum = 0;
        for (int i = 0; i < input.length; i++)
        {
            sum = sum + input[i];
            csum[i] = sum;
        }
        return csum;
    }

    /**
     * Calculates the sum of all array elements
     * 
     * @param input The input array
     * @return The sum
     */
    public static double arraySum(final double input[])
    {
        double sum = 0;
        for (double v : input)
            sum = sum + v;
        return sum;
    }
    
    /**
     * Cut a value at its limits
     * @param x The value to evaluate
     * @param min The minimal value
     * @param max The maximal value
     * @return <pre>min &lt;= x &lt;= max</pre>
     */
    public static double setLimits(double x, double min, double max)
    {
        return Math.max(Math.min(x, max), min);
    }
    
    /**
     * Checks if x is \in [from, to]
     * @param x The variable
     * @param from The minimum value
     * @param to The maximum value
     * @return x \in [from, to]
     */
    public static boolean isInRange(double x, double from, double to)
    {
        return (x>=from && x<=to);
    }
    
    /**
     * The logistic function is the most common sigmoid curve.
     * It is a function 
     * <pre> 
     * f:[-inf,inf] -&gt; [0,1] 
     * 
  1 ++----------+-----------+-----------+----------+-----------+----------++
     +           +           +           +          +  ++++++++ +           +
     |           :           :           :          ++++        :           |
     |           :           :           :        +++           :           |
 0.8 ++.........................................+++........................++
     |           :           :           :    +++   :           :           |
     |           :           :           :   ++     :           :           |
     |           :           :           :  ++      :           :           |
 0.6 ++...................................+++..............................++
     |           :           :           ++         :           :           |
     |           :           :          ++          :           :           |
     |           :           :         ++:          :           :           |
 0.4 ++..............................+++...................................++
     |           :           :      ++   :          :           :           |
     |           :           :     ++    :          :           :           |
     |           :           :   +++     :          :           :           |
 0.2 ++........................+++.........................................++
     |           :           +++         :          :           :           |
     |           :        ++++           :          :           :           |
     +           + ++++++++  +           +          +           +           +
   0 ++----------+-----------+-----------+----------+-----------+----------++
    -6          -4          -2           0          2           4           6
     * </pre>
     * 
     * @param x The input
     * @return 1/(1+exp(-x))
     */
    public static double logisticFunction(double x)
    {
        return 1 / (1+Math.exp(-x));
    }

    /**
     * Keeps value in boundaries with the higher boundary included or excluded i.e.
     * ( [ boundaries[0], boundaries[1] [ or [ boundaries[0], boundaries[1] ] )
     * @param value The value
     * @param lowerBoundary lower boundary to keep the value in
     * @param upperBoundary upper boundary to keep the value in
     * @param includeUpperBoundary whether to allow the result to equal upperBoundary
     * or not
     * @return value
     */
    public static double keepInBoundaries(double value, double lowerBoundary,
            double upperBoundary, boolean includeUpperBoundary) {
        // transform boundaries to fit into a positiv interval starting with 0
        double transformatedHigherBoundary = upperBoundary - lowerBoundary;
        double transformedValue = value - lowerBoundary;

        // calculate remainder and transform back
        double result = MathUtils.fmod(transformedValue, transformatedHigherBoundary)
                + lowerBoundary;
        if (includeUpperBoundary && value != lowerBoundary && result == 0)
            return upperBoundary;
        return result;
    }

    /**
     * Keeps value in boundaries with the higher boundary excluded i.e.
     * ( [ boundaries[0], boundaries[1] [ or [ boundaries[0], boundaries[1] ] )
     * @param value The value
     * @param lowerBoundary lower boundary to keep the value in
     * @param upperBoundary upper boundary to keep the value in
     * @return value
     */
    public static double keepInBoundaries(double value, double lowerBoundary,
            double upperBoundary) {
        return keepInBoundaries(value, lowerBoundary, upperBoundary, false);
    }

    /**
     * Calculates whether the upper boundary or the lower boundary is closer to
     * the return value of keepInBoundaries.
     * 
     * Method can be used to distinguish between boundary transitions assuming that
     * the transition with a smaller distance is correct.
     *
     * For example: let's assume that we want to distinguish between angular
     * state transitions. Angles are allowed to be in between [0, 360].
     * angle1=3 and angle2=357. What's the smallest angle between these two
     * angles? Obviously 5. shortestDistance(357-3, 0, 180, true) will result in
     * the desired value that is shortestDistance(357-3, 0, 180, true) = 5.
     * Note that we pass in 180 degrees as the upper boundary i.e. distances
     * bigger than are not allowed.
     *
     * @param value The value
     * @param lowerBoundary lower boundary to keep the value in
     * @param upperBoundary upper boundary to keep the value in
     * @param includeUpperBoundary whether to allow the result to equal upperBoundary
     * or not
     * @return shortest distance between keepInBoundaries' result and one of the boundaries
     */
    public static double shortestDistance(double value, double lowerBoundary,
            double upperBoundary, boolean includeUpperBoundary) {
        double result = keepInBoundaries(value, lowerBoundary, upperBoundary,
                includeUpperBoundary);
        double distanceFromUpperBoundary = Math.abs(upperBoundary - result);
        double distanceFromLowerBoundary = Math.abs(lowerBoundary - result);

        if (includeUpperBoundary && result == upperBoundary)
            return result;
        
        if (distanceFromLowerBoundary < distanceFromUpperBoundary)
            return distanceFromLowerBoundary;
        else
            return distanceFromUpperBoundary;
    }
}
