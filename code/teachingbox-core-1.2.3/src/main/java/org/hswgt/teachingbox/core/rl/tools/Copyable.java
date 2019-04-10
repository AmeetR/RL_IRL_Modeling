/**
 *
 * $Id: Copyable.java 679 2010-06-14 14:53:38Z twanschik $
 *
 * @version $Rev: 679 $
 * @author $Author: twanschik $
 * @date $Date: 2010-06-14 16:53:38 +0200 (Mo, 14 Jun 2010) $
 *
 */
package org.hswgt.teachingbox.core.rl.tools;

/* Interface for copyable objects.
 *
 * Example:
 * public class Example implements Copyable<Example> {
 *      protected int number;
 *
 *      public Example(int number) {
 *          this.number = number;
 *      }
 *
 *      public Example copy() {
 *          return new Example(this.number);
 *      }
 * }
 */

public interface Copyable <T> {

    public T copy();
    
}
