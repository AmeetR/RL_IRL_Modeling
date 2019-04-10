package org.hswgt.teachingbox.core.rl.nfq.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.experiment.ExperimentObserver;


public class TransitionExporter implements ExperimentObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7373677224163140296L;
	
	File f = null;
	FileWriter fw = null;
	
	int episode = 0; 
	int step = 0;
	boolean headerExported = false;
	
	/**
	 * Constructor
	 * @param filename The filename to export to
	 */
	public TransitionExporter (String filename) {
		f = new File (filename);
		
		try {			
			fw = new FileWriter(f, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(State s, Action a, State sn, Action an, double r,
			boolean terminalState) {
		
		try {

			// write header
			if (!headerExported) {
				fw.write("# episode;step;");
				for (int i=0; i<s.size(); i++) {
					fw.write("s"+i+";");
				}
				for (int i=0; i<a.size(); i++) {
					fw.write("a"+i+";");
				}
				for (int i=0; i<sn.size(); i++) {
					fw.write("sn"+i+";");
				}
				fw.write("r;terminal\n");
				
				headerExported = true;
			}
			
			fw.write((episode-1) + ";" + step + ";");
			
			for (int i=0; i<s.size(); i++) {
				fw.write(s.get(i)+";");
			}
			for (int i=0; i<a.size(); i++) {
				fw.write(a.get(i)+";");
			}
			for (int i=0; i<sn.size(); i++) {
				fw.write(sn.get(i)+";");
			}
			fw.write(r + ";"); 
			fw.write(terminalState == true ? "1\n" : "0\n");
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		step++;
	}

	@Override
	public void updateNewEpisode(State initialState) {
		// TODO Auto-generated method stub
		episode++;
		step = 0;
	}

	@Override
	public void updateExperimentStop() {
		// TODO Auto-generated method stub
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updateExperimentStart() {
		// TODO Auto-generated method stub
	}
}
