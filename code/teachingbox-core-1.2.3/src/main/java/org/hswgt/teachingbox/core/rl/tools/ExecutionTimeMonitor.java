/**
 *
 * $$Id: ExecutionTimeMonitor.java 475 2009-12-15 09:10:57Z Markus Schneider $$
 *
 * @version   $$Rev: 475 $$
 * @author    $$Author: Markus Schneider $$
 * @date      $$Date: 2009-12-15 10:10:57 +0100 (Tue, 15 Dec 2009) $$
 *
 */

package org.hswgt.teachingbox.core.rl.tools;

/**
 * @author mschneider
 *
 */
public class ExecutionTimeMonitor
{
    private long _start = 0;
    private long _stop  = 0;
    
    /**
     * Starts the timer
     */
    public void start()
    {
        _start = System.nanoTime();
    }
    
    /**
     * Stops the timer
     */
    public void stop()
    {
        _stop = System.nanoTime();
    }
    
    /**
     * @return The elapsed time in seconds
     */
    public double getElapsedTimeInSec()
    {
        return (_stop - _start) * 1.0e-9;
    }
    
    /**
     * @return The elapsed time in milliseconds
     */
    public double getElapsedTimeInMilliSec()
    {
        return (_stop - _start) * 1.0e-6;
    }
    
    /**
     * @return The elapsed time in microseconds
     */
    public double getElapsedTimeInMicroSec()
    {
        return (_stop - _start) * 1.0e-3;
    }
    
    /**
     * @return The elapsed time in nanoseconds
     */
    public double getElapsedTimeInNanoSec()
    {
        return (_stop - _start);
    }
    
    public String toString()
    {
        return "Time elapsed in msec: " + getElapsedTimeInMilliSec();
    }
}
