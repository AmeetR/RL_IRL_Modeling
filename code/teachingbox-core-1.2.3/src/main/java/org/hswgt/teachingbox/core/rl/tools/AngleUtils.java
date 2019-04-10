/**
 *
 * $$Id: AngleUtils.java 988 2015-06-17 19:48:01Z micheltokic $$
 *
 * @version   $$Rev: 988 $$
 * @author    $$Author: micheltokic $$
 * @date      $$Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $$
 *
 */

package org.hswgt.teachingbox.core.rl.tools;

import java.io.Serializable;

/**
 * Helper class for angular porblems
 */
public class AngleUtils implements Serializable
{    
    private static final long serialVersionUID = 8158662704050195023L;

    /**
     * Keeps pi in [0,2pi[
     * @param angle The angle
     * @return angel The angle
     */
    public static double pi2pi(double angle)
    {
        double pi = MathUtils.fmod(angle, Math.PI + Math.PI);
        return (pi < 0) ? (Math.PI + Math.PI + pi) : pi;
    }

    /**
     * Transforms from radian units to degree units
     * @param rad The value in rad
     * @return The value in deg
     */   
    public static double rad2deg(double rad)
    {
        return (rad * (180 / Math.PI));
    }

    /**
     * Transforms from degree units to radian units
     * @param deg The value in deg
     * @return The value in rad
     */ 
    public static double deg2rad(double deg)
    {
        return (deg / (180 / Math.PI));
    }

    public static double clamp (double value, double low, double high) {
        return Math.min(Math.max(value, low), high);
    }

     /**
     * Transforms from quaternion units to euler units
     * @param quatX The x value of the quaternion
     * @param quatY The y value of the quaternion
     * @param quatZ The z value of the quaternion
     * @param quatW The w value of the quaternion
     * @return The euler anlges for the given quaternion
     */
    public static double[] quatToEuler(double quatX, double quatY, double quatZ,
            double quatW) {
        final double sqw = quatW*quatW;
        final double sqx = quatX*quatX;
        final double sqy = quatY*quatY;
        final double sqz = quatZ*quatZ;

        double[] euler = new double[3];

        // heading = rotation about z-axis
        euler[2] = (Math.atan2(2.0 * (quatX*quatY + quatZ*quatW),(sqx - sqy - sqz + sqw)));

        // bank = rotation about x-axis
        euler[0] = (Math.atan2(2.0 * (quatY*quatZ + quatX*quatW),(-sqx - sqy + sqz + sqw)));

        // attitude = rotation about y-axis
        euler[1] = Math.asin( clamp(-2.0f * (quatX*quatZ - quatY*quatW), -1.0f, 1.0f) );

        return euler;
    }
}
