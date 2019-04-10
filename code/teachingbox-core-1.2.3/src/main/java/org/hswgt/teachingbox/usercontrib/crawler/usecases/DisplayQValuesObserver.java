package org.hswgt.teachingbox.usercontrib.crawler.usecases;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;
import org.hswgt.teachingbox.core.rl.env.gridworldeditor.env.GridworldEnvironment;
import org.hswgt.teachingbox.core.rl.gridworldeditor.model.GridModel;


class DisplayQValuesObserver implements ExperimentObserver
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1803790148689988629L;
	QFunction Q;
    
    /**
     * We need the actual QFunction to display and therefore we have
     * to provide it here
     * @param Q The Q function
     */
    public DisplayQValuesObserver(QFunction Q){
        this.Q = Q;
    }
    
    /* (non-Javadoc)
     * @see org.hswgt.teachingbox.experiment.ExperimentObserver#update(org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, org.hswgt.teachingbox.env.State, org.hswgt.teachingbox.env.Action, double, boolean)
     */
    public void update(State s, Action a, State sn, Action an, double r, boolean terminalState)
    {
    	this.sendValues(s);
    }
    
    public void sendValues(State s) {
        try
        {
            GridworldEnvironment.Display(s, Q);
           
        }
        catch (Exception e)
        {
            System.err.println("Cannot display Q-Function:");
            e.printStackTrace();
        }        
    }
    
    public void sendAllValues() {
    	
    	int maxX = GridModel.getInstance().getSize().width;
    	int maxY = GridModel.getInstance().getSize().height;
    	State s;

    	// send all values
		try {
	    	for (int x=0; x<maxX; x++) {
	    		for (int y=0; y<maxY; y++) {
	    			s= new State(new double[]{x,y});
						GridworldEnvironment.Display(s, Q);
	    		}
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
