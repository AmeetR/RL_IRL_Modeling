/**
 *
 * $Id: TestGreedyPolicy.java 988 2015-06-17 19:48:01Z micheltokic $
 *
 * @version   $Rev: 988 $
 * @author    $Author: micheltokic $
 * @date      $Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $
 *
 */

package org.hswgt.teachingbox.core.rl.unittests;

import static org.junit.Assert.*;

import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.policy.GreedyPolicy;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;
import org.hswgt.teachingbox.core.rl.tabular.TabularQFunction;
import org.junit.Test;

public class TestGreedyPolicy
{
    // dummy actionset
    ActionSet as = new ActionSet();
    TabularQFunction Q = new HashQFunction(0);
    GreedyPolicy pi;
    // dummy state 
    State s = new State(new double[]{0}); 
    
    public TestGreedyPolicy()
    {
        as.add( new Action( new double[]{0}) );
        as.add( new Action( new double[]{1}) );
        as.add( new Action( new double[]{2}) );
        as.add( new Action( new double[]{3}) );
        as.add( new Action( new double[]{4}) );
        pi = new GreedyPolicy(Q,as);
    }
    
    @Test
    public void testSingleBestAction()
    {
        // Set all values to 0, but action 2 to 10
        Q.setValue(s, as.get(2), 10);
        
        // Greedy Policy must return as(2)
        for(int i=0; i<100; i++){
            Action best = pi.getAction(s);
            assertTrue( as.get(2).equals(best) );
        }
    }
    
    @Test
    public void test2BestActions()
    {
        // Set all values to 0, but action 2 and 3 to 10
        Q.setValue(s, as.get(1), 9.99999);
        Q.setValue(s, as.get(2), 10);
        Q.setValue(s, as.get(3), 10);
        
        
        int[] counter = new int[as.size()];
        int runs = 1000;
        
        // Greedy Policy must return as(2)
        for(int i=0; i<runs; i++){
            Action a= pi.getAction(s);
            int index = as.getActionIndex(a);
            counter[index]++;
        }
        
        // 0, 1 and 4 must be 0
        assertEquals(counter[0], 0);
        assertEquals(counter[1], 0);
        assertEquals(counter[4], 0);
        
        // 2 and 3 should have runs/2 +- 5%
        assertEquals(counter[2], runs/2, runs/20);
        assertEquals(counter[3], runs/2, runs/20);
    }
}
