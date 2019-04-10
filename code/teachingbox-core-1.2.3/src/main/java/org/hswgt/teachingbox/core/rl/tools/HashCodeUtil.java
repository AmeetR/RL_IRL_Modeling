/**
 *
 * $$Id: HashCodeUtil.java 475 2009-12-15 09:10:57Z Markus Schneider $$
 *
 * @version   $$Rev: 475 $$
 * @author    $$Author: Markus Schneider $$
 * @date      $$Date: 2009-12-15 10:10:57 +0100 (Tue, 15 Dec 2009) $$
 *
 */

package org.hswgt.teachingbox.core.rl.tools;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * Collected methods which allow easy implementation of <code>hashCode</code>.
 * 
 * Example use case:
 * 
 * <pre>
 * public int hashCode()
 * {
 *     int result = HashCodeUtil.SEED;
 *     //collect the contributions of various fields
 *     result = HashCodeUtil.hash(result, fPrimitive);
 *     result = HashCodeUtil.hash(result, fObject);
 *     result = HashCodeUtil.hash(result, fArray);
 *     return result;
 * }
 * </pre>
 */
public final class HashCodeUtil implements Serializable
{
    private static final long serialVersionUID = -2311704388018469590L;
    
    /**
     * An initial value for a <code>hashCode</code>, to which is added
     * contributions from fields. Using a non-zero value decreases collisons of
     * <code>hashCode</code> values.
     */
    public static final int SEED = 23;

    /**
     * Hashes booleans.
     * 
     * @param seed The seed to use
     * @param value the value to hash
     * @return The hashcode
     */
    public static int hash(int seed, boolean value)
    {
        return firstTerm(seed) + (value ? 1 : 0);
    }

    /**
     * Hashes chars.
     * 
     * @param seed The seed to use
     * @param value the value to hash
     * @return The hashcode
     */
    public static int hash(int seed, char value)
    {
        System.out.println("char...");
        return firstTerm(seed) + value;
    }

    /**
     * Hashes ints.
     * 
     * @param seed The seed to use
     * @param value the value to hash
     * @return The hashcode
     */
    public static int hash(int seed, int value)
    {
        /*
         * Implementation Note Note that byte and short are handled by this
         * method, through implicit conversion.
         */
        System.out.println("int...");
        return firstTerm(seed) + value;
    }

    /**
     * Hashes longs.
     * 
     * @param seed The seed to use
     * @param value the value to hash
     * @return The hashcode
     */
    public static int hash(int seed, long value)
    {
        System.out.println("long...");
        return firstTerm(seed) + (int) (value ^ (value >>> 32));
    }

    /**
     * Hashes floats.
     * 
     * @param seed The seed to use
     * @param value the value to hash
     * @return The hashcode
     */
    public static int hash(int seed, float value)
    {
        return hash(seed, Float.floatToIntBits(value));
    }

    /**
     * Hashes doubles.
     * 
     * @param seed The seed to use
     * @param value the value to hash
     * @return The hashcode
     */
    public static int hash(int seed, double value)
    {
        return hash(seed, Double.doubleToLongBits(value));
    }

    /**
     * <code>aObject</code> is a possibly-null object field, and possibly an
     * array.
     * 
     * If <code>aObject</code> is an array, then each element may be a primitive
     * or a possibly-null object.
     * 
     * @param seed The seed to use
     * @param value the value to hash
     * @return The hashcode
     */
    public static int hash(int seed, Object value)
    {
        int result = seed;
        if (value == null)
        {
            result = hash(result, 0);
        }
        else if (!value.getClass().isArray())
        {
            result = hash(result, value.hashCode());
        }
        else
        {
            int length = Array.getLength(value);
            for (int idx = 0; idx < length; ++idx)
            {
                Object item = Array.get(value, idx);
                // recursive call!
                result = hash(result, item);
            }
        }
        return result;
    }

    // / PRIVATE ///
    private static final int fODD_PRIME_NUMBER = 37;

    private static int firstTerm(int seed)
    {
        return fODD_PRIME_NUMBER * seed;
    }
}
