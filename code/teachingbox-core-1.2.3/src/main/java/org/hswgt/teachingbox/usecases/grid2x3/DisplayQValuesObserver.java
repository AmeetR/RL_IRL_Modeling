package org.hswgt.teachingbox.usecases.grid2x3;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.Grid2x3Environment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;

/**
 * This is an example how to implement an experiment observer. Imagine
 * that you want to call the <pre>Grid2x3Environment.Display(Q);</pre> method after
 * every step of the agent. This can be easily done with the help of an
 * <pre>ExperimentObserver</pre>. The <pre>update</pre> method will be called
 * after every step and the <pre>updateNewEpisode</pre> informs that a new episode 
 * has started
 */
public class DisplayQValuesObserver implements ExperimentObserver
{
    private static final long serialVersionUID = -4936384338773368610L;
    QFunction Q;
    
    /**
     * We need the actual QFunction to display and therefore we have
     * to provide it here
     * @param Q The Q-function
     */
    public DisplayQValuesObserver(QFunction Q){
        this.Q = Q;
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#update(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public void update(State s, Action a, State sn, Action an, double r, boolean terminalState)
    {
        try
        {
            System.out.println("s: "+s);
            System.out.println("a: "+a);
            System.out.println("r: "+r);
            Grid2x3Environment.Display(Q);
            System.out.print("press any key: ");
            System.in.read();
        }
        catch (Exception e)
        {
            System.err.println("Cannot display Q-Function:");
            e.printStackTrace();
        }
    }
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateNewEpisode(org.hswgt.teachingbox.env.State)
     */
    public void updateNewEpisode(State initialState)
    {
        // ...
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateExperimentStart()
     */
    public void updateExperimentStart()
    {
        // ... 
        
    }

    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#updateExperimentStop()
     */
    public void updateExperimentStop()
    {
        // ...
    }
}
