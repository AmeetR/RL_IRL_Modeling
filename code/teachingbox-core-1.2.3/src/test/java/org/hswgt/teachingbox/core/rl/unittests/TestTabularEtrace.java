/**
 *
 * $Id: TestTabularEtrace.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.unittests;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.tabular.TabularQEtrace;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTabularEtrace
{
    final double DELTA = 0.0000001;
    TabularQEtrace e = new TabularQEtrace();
    State s = new State(new double[] {0,0} );
    Action a = new Action(new double[] {0,0} );
    State sn = new State(new double[] {1,1} );
    Action an = new Action(new double[] {1,1} );
    
    @Test
    public void testCorrectValues()
    { 
        // should return 0 for non existing entries
        assertEquals(0, e.get(s, a), DELTA);
        
        e.set(s, an, 1);
        assertEquals(1, e.get(s, an), DELTA);
        assertEquals(0, e.get(sn, a), DELTA);
    }
    
    @Test
    public void testIncrementSetValues()
    {
        e.set(s, a, 1);
        e.set(sn, a, 2);
        e.set(s, an, 3);
        e.set(sn, an, 4);
        assertEquals(1, e.get(s, a), DELTA);
        
        // test increment
        e.increment(s, a, 1);
        assertEquals(2, e.get(s, a), DELTA);
        assertEquals(2, e.get(sn, a), DELTA);
        assertEquals(3, e.get(s, an), DELTA);
        assertEquals(4, e.get(sn, an), DELTA);
        
        // test discount
        e.set(sn, an, e.MIN_VALUE);
        e.decay(0.9, 1.0);
        assertEquals(3, e.size());
       
        assertEquals(0.9*2, e.get(s, a), DELTA);
        assertEquals(0.9*2, e.get(sn, a), DELTA);
        assertEquals(0.9*3, e.get(s, an), DELTA);
    }
}
