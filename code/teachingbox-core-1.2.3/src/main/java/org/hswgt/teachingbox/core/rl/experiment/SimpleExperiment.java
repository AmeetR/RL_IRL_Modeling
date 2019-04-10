package org.hswgt.teachingbox.core.rl.experiment;


import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.datastructures.ActionSet;
import org.hswgt.teachingbox.core.rl.env.DynamicCliffEnvironment;
import org.hswgt.teachingbox.core.rl.env.Environment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.etrace.ETraceType;
import org.hswgt.teachingbox.core.rl.experiment.EpisodicCumulativeParameterAverager;
import org.hswgt.teachingbox.core.rl.experiment.EpisodicCumulativeRewardAverager;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.experiment.ScalarAverager;
import org.hswgt.teachingbox.core.rl.experiment.StepwiseParameterAverager;
import org.hswgt.teachingbox.core.rl.learner.Learner;
import org.hswgt.teachingbox.core.rl.learner.TabularQLearner;
import org.hswgt.teachingbox.core.rl.learner.TabularSarsaLearner;
import org.hswgt.teachingbox.core.rl.plot.DataAveragePlotter;
import org.hswgt.teachingbox.core.rl.policy.EpisodicReinforcePolicy;
import org.hswgt.teachingbox.core.rl.policy.EpsilonGreedyPolicy;
import org.hswgt.teachingbox.core.rl.policy.GreedyPolicy;
import org.hswgt.teachingbox.core.rl.policy.LocalReinforcePolicy;
import org.hswgt.teachingbox.core.rl.policy.MaxBoltzmannExplorationPolicy;
import org.hswgt.teachingbox.core.rl.policy.Policy;
import org.hswgt.teachingbox.core.rl.policy.SoftmaxActionSelection;
import org.hswgt.teachingbox.core.rl.policy.TabularVDBEPolicy;
import org.hswgt.teachingbox.core.rl.policy.TabularVdbeSoftmaxPolicy;
import org.hswgt.teachingbox.core.rl.reinforce.BasicReinforceGaussianLearner;
import org.hswgt.teachingbox.core.rl.reinforce.EpisodicReinforceLearner;
import org.hswgt.teachingbox.core.rl.reinforce.LocalReinforceLearner;
import org.hswgt.teachingbox.core.rl.tabular.HashQFunction;
import org.hswgt.teachingbox.core.rl.tabular.TabularQFunction;
import org.hswgt.teachingbox.core.rl.tools.ObjectSerializer;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFunction;
//import org.hswgt.teachingbox.learner.NeuralQLearner;

public class SimpleExperiment {


	// global parameters
	private String dataFolder = null;
	private String[] commandShellArgs;
	// dynamic file configuration
	String envFile = null;
	String policyFile = null;
	String configPath;
	String plottingStyle = PLOTTING_EPISODIC;
	int plottingMaxSteps = 0;
	State initState = null;
	double initQvalue = 0;
	
	double[] outputBounds = null;
	double[][] inputBounds = null;
	
	double rewardRange[]  = new double[] {-1000, 1000};

	/**
	 * sets the plotting reward range (y-axis)
	 * @param rr The reward range
	 */
	public void setRewardRange(double[] rr) {
		this.rewardRange = rr;
	}
	
	/**
	 * @param inputBounds the input bounds to set
	 */
	public void setInputBounds(double[][] inputBounds) {
		this.inputBounds = inputBounds;
	}

	/**
	 * @param outputBounds the output bounds to set
	 */
	public void setOutputBounds(double[] outputBounds) {
		this.outputBounds = outputBounds;
	}

	/**
	 * @return the initQvalue
	 */
	public double getInitQvalue() {
		return initQvalue;
	}

	/**
	 * @param initQvalue the initQvalue to set
	 */
	public void setInitQvalue(double initQvalue) {
		this.initQvalue = initQvalue;
	}


	/**
	 * @return the initState
	 */
	public State getInitState() {
		return initState;
	}

	/**
	 * @param initState the initState to set
	 */
	public void setInitState(State initState) {
		this.initState = initState;
	}
	
	public void setETraces (ETraceType e, double lambdas[]) {
		this.etrace = e; 
		this.lambdas = lambdas;
	}

	public final static String PLOTTING_STEPWISE = "stepwise";
	public final static String PLOTTING_EPISODIC = "episodic";
	
	
	public final static String LEARNER_SARSA = "org.hswgt.teachingbox.core.rl.learner.TabularSarsaLearner";
	public final static String LEARNER_QLEARNING= "org.hswgt.teachingbox.core.rl.learner.TabularQLearner";
	//public final static String LEARNER_NEURALQLEARNING= "org.hswgt.teachingbox.learner.NeuralQLearner";
	
	//public final static String ENVIRONMENT_WALLWORLD = "org.hswgt.teachingbox.wallWorld.env.WallWorldEnvironment";
	//public final static String ENVIRONMENT_CRAWLER = "org.hswgt.teachingbox.gridworldeditor.env.StochasticGridworldEnvironment";
	public final static String ENVIRONMENT_MCAR_ORIG_CONTINUOUS = "org.hswgt.teachingbox.core.rl.env.MountainCarEnv";
	//public final static String ENVIRONMENT_MCAR_ORIG = "org.hswgt.teachingbox.mountaincar.env.DiscreteMountainCarOrigEnv";
	//public final static String ENVIRONMENT_MCAR_2GOALS = "org.hswgt.teachingbox.mountaincar.env.DiscreteMountainTwoGoalsCarEnv";
	//public final static String ENVIRONMENT_MCAR_2GOALS_CONTINUOUS = "org.hswgt.teachingbox.mountaincar.env.MountainTwoGoalsCarEnv";
	//public final static String ENVIRONMENT_BANDIT_WORLD = "org.hswgt.teachingbox.banditWorld.env.BanditWorldEnv";
	
	public final static String ENVIRONMENT_N_ARMED_BANDIT = "org.hswgt.teachingbox.core.rl.env.nArmedBandit";
	public final static String ENVIRONMENT_CLIFFWALK = "org.hswgt.teachingbox.core.rl.env.CliffWalkEnvironment";
	public final static String ENVIRONMENT_DYNAMIC_CLIFF = "org.hswgt.teachingbox.core.rl.env.DynamicCliffEnvironment";
	
	
	public final static String EXP_GLOBAL_EGREEDY= "egreedy";
	public final static String EXP_GLOBAL_SOFTMAX= "softmax";
	public final static String EXP_GLOBAL_VDBE= "vdbe";
	public final static String EXP_GLOBAL_VDBE_SOFTMAX= "vdbeSoftmax";
	public final static String EXP_GLOBAL_MBE = "mbe";
	
	public final static String EXP_GLOBAL_REINFORCE_EGREEDY = "reinforceEgreedy";
	//public final static String EXP_GLOBAL_NEURAL_REINFORCE_EGREEDY = "reinforceNeuralEgreedy";
	public final static String EXP_LOCAL_REINFORCE_EGREEDY = "reinforceEgreedyLocal";
	public final static String EXP_GLOBAL_REINFORCE_SOFTMAX = "reinforceSoftmax";
	public final static String EXP_LOCAL_REINFORCE_SOFTMAX = "reinforceSoftmaxLocal";
	public final static String EXP_GLOBAL_REINFORCE_MBE = "reinforceMBE";
	public final static String EXP_LOCAL_REINFORCE_MBE = "reinforceMBELocal";
	public final static String EXP_GLOBAL_REINFORCE_VDBE = "reinforceVdbe";
	public final static String EXP_LOCAL_REINFORCE_VDBE= "reinforceVdbeLocal";	
	public final static String EXP_GLOBAL_REINFORCE_VDBE_SOFTMAX = "reinforceVdbeSoftmax";
	public final static String EXP_LOCAL_REINFORCE_VDBE_SOFTMAX = "reinforceVdbeSoftmaxLocal";
	
	// environment parameters
	private String envString = null;
	private Object[] envParams = null;
	private ActionSet ACTION_SET = null;

	// learner parameters
	String learnerString = "org.hswgt.teachingbox.core.rl.learner.TabularQLearner";
	String learnerName = "Q";
	
	// observable environment parameters (for plotting)
	String[] obsEnvParams = new String[]{};
	
	
	// Learner parameters
	int MAX_STEPS;
	int MAX_EPISODES;
	int MAX_EXPERIMENTS;
	double gamma;
	double alphas[];
	boolean stochasticRewards;
	
	// Exploration Parameters
	double[] expParameter = new double[] {-1};
	//double[] parameter2 = new double[] {-1};
	//Policy pi;
	String expParameterName;
	String expAlgorithm;
	int expAlgorithmVersion = 0;
	   	
	
	// reinforce parameters
	double reinforceInitMean;
	double reinforceMinMean;
	double reinforceMaxMean;
	double reinforceInitSD;
	double reinforceMinSD;
	double reinforceMaxSD;
	
	// etraces
	ETraceType etrace = ETraceType.none;
	//double lambda = 0.0;
	double lambdas[];
	
	

	/**
	 * Constructor
	 * @param args The shell command args
	 * @param sFolder The folderName in data/ (expects to find file named data/sFolder/config/sFolder.properties 
	 * @param sEnv The java class of the environment
	 * @param envConstructorParameters An object list of constructor parametsr
	 * @param plottingStyle The plotting style
	 * @throws Exception The exception
	 */
	public SimpleExperiment (String[] args, String sFolder, String sEnv, Object[] envConstructorParameters, String plottingStyle) throws Exception {
		
		this.dataFolder = sFolder;
		this.envString = sEnv;
		this.envParams = envConstructorParameters;
		this.commandShellArgs = args;
		this.plottingStyle = plottingStyle;
		
		configPath = "data/" + dataFolder + "/config/";
		
		// determine the ACTION_SET
		Class<?> envClass = Class.forName(this.envString);
		Field asField = envClass.getField("ACTION_SET");
		asField.setAccessible(true);
		
		// get the action set
		ACTION_SET = (ActionSet)asField.get(null);
			
    	String customEnvFile = null; 
		
		/**********************************
    	 *  SETUP LEARNER FROM CONFIG FILE
    	 **********************************/
        String[] params;
    	if (commandShellArgs.length >= 2  || customEnvFile != null) {
    		
    		
    		// load hard coded file if specified
    		if (customEnvFile != null) {
    			envFile = customEnvFile;
    		// filename is specified in cmd arguments otherwise 
    		} else {
	    		envFile = commandShellArgs[1];	    			
    		}
    		System.out.println ("\nCONFIGURING LEARNER FROM FILE: " + configPath + envFile);
    		Properties propFile = new Properties();
    		propFile.load( new FileInputStream(configPath +  envFile));
    		
	        MAX_EPISODES = Integer.parseInt(propFile.getProperty("maxEpisodes"));
	        MAX_STEPS    = Integer.parseInt(propFile.getProperty("maxSteps"));
	        MAX_EXPERIMENTS = Integer.parseInt(propFile.getProperty("maxExperiments"));	        
	        gamma = Double.parseDouble(propFile.getProperty("gamma"));

	        
	        // read alphas
	        params = propFile.getProperty("alphas").split(",");
	        alphas = new double[params.length];
	        for (int i=0; i<alphas.length; i++) {
	        	alphas[i] = Double.parseDouble(params[i]);
	        }
	        
	        
	        // ELIGIBILITY TRACES
	        
	        if (propFile.getProperty("lambdas")!= null) {
	        	params = propFile.getProperty("lambdas").split(",");
	        } else {
	        	params = null;
	        }
	        if (params != null) {
		        // parse lambdas
	        	lambdas = new double[params.length];
		      
		        for (int i=0; i<lambdas.length; i++) {
		        	lambdas[i] = Double.parseDouble(params[i]);
		        }
		        
		        // parse etracetype
		        String traceType = propFile.getProperty("eTraceType");
		        if (traceType == null) {
		        	traceType = "";
		        }
		        System.out.println ("ETraceType: " + traceType);
		        
		        if (traceType.equals("replacing")) {
		        	etrace = ETraceType.replacing;
		        	System.out.print ("ETraces: 'replacing', using lambdas: " );
		        	
		        	for (int i=0; i<lambdas.length; i++) {
		        		System.out.print (lambdas[i] + ", ");
		        	}
		        	System.out.println();
		        	
		        } else if (traceType.equals("accumulating")) {
		        	etrace = ETraceType.accumulating;
		        	System.out.print ("ETraces: 'accumulating', using lambdas: ");
		        	
		        	for (int i=0; i<lambdas.length; i++) {
		        		System.out.print (lambdas[i] + ", ");
		        	}
		        	System.out.println();		        
		        } else {
		        	etrace = ETraceType.none;
		        	System.out.println ("ETraces: NO");
		        	//System.exit(-1);
		        }
		        	
	        } else {
	        	lambdas = new double[] {0.0};
	        	this.etrace = ETraceType.none;
	        }
	        
	        
	        // stochastic rewards
	    	stochasticRewards = Boolean.parseBoolean(propFile.getProperty("stochasticRewards"));    		
    	} 
    	

        /*************************************
         * SETUP EXPLORATION FROM CONFIG FILE
         *************************************/
    	if (commandShellArgs.length >= 3) {
    		policyFile = commandShellArgs[2];
    		System.out.println ("\nCONFIGURING EXPLORATION FROM FILE: " + configPath + policyFile);
	
	    	Properties expAlgFile = new Properties();
		        
	        // load exploration algorithm properties
	    	expAlgFile.load( new FileInputStream(configPath + policyFile));

	
	    	expAlgorithm = expAlgFile.getProperty("explorationAlgorithm");
	    	expAlgorithmVersion = Integer.parseInt(expAlgFile.getProperty("explorationAlgorithmVersion", "0"));
	    	expParameterName = expAlgFile.getProperty("parameterNames", "-1");
	    	if (expParameterName.equals("-1")) {
	    		expParameterName = expAlgFile.getProperty("parameterName");
	    	}
	    	
	    	// load first parameter
	    	if (expParameterName.length()  >= 1 ) {
	    		String paramString = expAlgFile.getProperty("expParam1", "-1");
	    		if (paramString.equals("-1")) {
	    			paramString = expAlgFile.getProperty("expParameter");
	    		}
	    		params = paramString.split(",");
	    		expParameter = new double[params.length];
	    		
	    		for (int i=0; i<params.length; i++) {
	    			expParameter[i] = Double.parseDouble(params[i]); 
	    			//System.out.println ("adding parameter " + expParameterNames[0] + "=" + expParameter[i]);
	    		}
	    	}
	    	
	    	// REINFORCE 
			this.reinforceInitMean = Double.parseDouble(expAlgFile.getProperty("initMean", "-1"));
			this.reinforceMinMean = Double.parseDouble(expAlgFile.getProperty("minMean", "-1"));
			this.reinforceMaxMean = Double.parseDouble(expAlgFile.getProperty("maxMean", "-1"));
			this.reinforceInitSD = Double.parseDouble(expAlgFile.getProperty("initVariance", "-1"));
			if (this.reinforceInitSD == -1) {
				this.reinforceInitSD = Double.parseDouble(expAlgFile.getProperty("initSD", "-1"));				
			}
			this.reinforceMinSD = Double.parseDouble(expAlgFile.getProperty("minVariance", "-1"));
			if (this.reinforceMinSD == -1) {
				this.reinforceMinSD = Double.parseDouble(expAlgFile.getProperty("minSD", "-1"));				
			}
			this.reinforceMaxSD = Double.parseDouble(expAlgFile.getProperty("maxVariance", "-1"));
			if (this.reinforceMaxSD == -1) {
				this.reinforceMaxSD = Double.parseDouble(expAlgFile.getProperty("maxSD", "-1"));				
			}
	
	    	// load second parameter
	    	/*
	    	if (parameterNames.length  >= 2 ) {
	    		String paramString = expAlgFile.getProperty("expParam2");
	    		params = paramString.split(",");
	    		parameter2 = new double[params.length];
	    		
	    		for (int i=0; i<params.length; i++) {        			
	    				parameter2[i] = Double.parseDouble(params[i]); 
	    				System.out.println ("adding parameter " + parameterNames[1] + "=" + parameter2[i]);
	    		}
	    	}
	    	*/
    	}    	
	}
	
	/**
	 * Setup exploration manually instead of file configuration
	 * @param expAlgorithm The exploration algorithm
	 * @param expParameterName The parameter name of the exploration algorithm
	 * @param expParameters The exploration parameters to investigate
	 * @param expAlgorithmVersion The version of the algorithm
	 */
	public void setupExploration (String expAlgorithm, String expParameterName, double expParameters[], int expAlgorithmVersion) {
		
		System.out.println ("\nMANUAL SETUP OF EXPLORATION ");
		
		this.expAlgorithm = expAlgorithm;
		this.expParameter = expParameters;
		this.expParameterName = expParameterName;
		this.expAlgorithmVersion = expAlgorithmVersion;
	}
	
	/**
	 * Setup a policy having the parameter adapted by REINFORCE
	 * @param initMean The initial mean 
	 * @param minMean The minimum mean
	 * @param maxMean The maximum mean
	 * @param initSD The initial standard deviation
	 * @param minSD The minimum standard deviation
	 * @param maxSD The maximum standard deviation
	 */
	public void setupReinforceParmeters (double initMean, double minMean, double maxMean, double initSD, double minSD, double maxSD) {
		
		System.out.println ("\nMANUAL SETUP OF REINFORCE PARAMETES");

		this.reinforceInitMean = initMean;
		this.reinforceMinMean = minMean;
		this.reinforceMaxMean = maxMean;
		this.reinforceInitSD = initSD;
		this.reinforceMinSD = minSD;
		this.reinforceMaxSD = maxSD;
		
		this.expParameterName = "initExpParameterMean";
		this.expParameter = new double[]{initMean};
	}

	
	/**
	 * setup the learner from hand
	 * @param maxSteps The maximum number of steps
	 * @param maxEpisodes The maximum number of episodes
	 * @param maxExperiments The maximum number of experiments
	 * @param gamma The discounting rate
	 * @param alphas An array of step-size parameters for learning the Q-function
	 * @param stochasticRewards A flag indicating if stochastic rewards are used
	 */
	public void setupExperiment (int maxSteps, int maxEpisodes, int maxExperiments, double gamma, double[] alphas, boolean stochasticRewards){
		
		System.out.println ("\nMANUAL SETUP OF EXPERIMENT");

		this.MAX_STEPS = maxSteps;
		this.MAX_EPISODES = maxEpisodes;
		this.MAX_EXPERIMENTS = maxExperiments;
		this.gamma = gamma;
		this.alphas =  alphas;
		this.stochasticRewards = stochasticRewards;
	}
	
	// for each parameter a separate plot is generated
	public void setObservableEnvironmentParameters(String[] params) {		
		System.out.println ("\nMANUAL SETUP OF OBSERVABLE PARAMETERS");
		obsEnvParams = params;		
	}

	/**
	 * set the learner type (e.g. TabularQLeaner, TabularSarsaLearner etc...)
	 * @param lClass The actual (implemented) class of the Learner
	 */
	public void setupLearner(String lClass) {

		System.out.println ("\nMANUAL SETUP OF LEARNER: " + lClass);

		this.learnerString = lClass;
		
		if (lClass.equals(LEARNER_QLEARNING)) {
			this.learnerName = "QLearning";
		} else if (lClass.equals(LEARNER_SARSA)) {
			this.learnerName = "Sarsa";
			/*
		} else if (lClass.equals(LEARNER_NEURALQLEARNING)) {
			this.learnerName = "NeuralQLearning";
			*/			
		}
	}
	
	/**
	 * return a new learner object
	 * @param Q The Q-function for the constructor
	 * @return The created Learner object
	 * @throws Exception The exception
	 */
	public Learner getNewLearner (TabularQFunction Q) throws Exception {
		
		Class<?> learnerClass = Class.forName(learnerString);
		Class<?>[]  paramClasses = new Class[1];
		paramClasses[0] = TabularQFunction.class;//Q.getClass();
		Object[] learnerObjs = new Object[]{Q};
		
		Object learnerObject = learnerClass.getConstructor(paramClasses).newInstance(learnerObjs);
		return (Learner)learnerObject;
	}
	
	
	/**
	 * return a new instance of the environment 
	 * @throws Exception The exception
	 * @return The instantiated Environment
	 */
	public Environment getNewEnvironment() throws Exception {
		
		
		// use java reflection API for constructor call  
    	Class<?> envClass = Class.forName(this.envString);
    	Class<?>[]  paramClasses;
    	Object envObject;
    	
    	if (envParams != null) {
    		paramClasses= new Class[envParams.length];
    	
	    	// get parameter classes
	    	for (int i=0; i<envParams.length; i++) {
	    		paramClasses[i] = envParams[i].getClass();
	    	}	    
	    	envObject = envClass.getConstructor(paramClasses).newInstance(envParams);
	    	
    	} else {
    		
    		//System.out.print ("\nCALLING constructor ");
    		envObject = envClass.getConstructor().newInstance();	    	
    		//System.out.println (" OK ");
    	}
    	
    	return (Environment)envObject;		
	}
	
	/**
	 * returns a new basis policy according to expAlgorithm 
	 */
	private Policy getNewPolicy(QFunction Q, ActionSet actionSet, double policyParameter) {
		
		Policy pi = new GreedyPolicy(Q, actionSet);
			
		// create basis policy
		if (expAlgorithm.toLowerCase().contains("egreedy")) {
			pi = new EpsilonGreedyPolicy(Q, actionSet, policyParameter); 
		} 
		if (expAlgorithm.toLowerCase().contains("softmax")) {
			pi = new SoftmaxActionSelection(Q, actionSet, policyParameter);
		}
		if (expAlgorithm.toLowerCase().contains("mbe")) {
			pi = new MaxBoltzmannExplorationPolicy(Q, actionSet, policyParameter);
			((MaxBoltzmannExplorationPolicy)pi).setNormBoundaries(-1, 1);
		}
		if (expAlgorithm.toLowerCase().contains("vdbe")) {
			pi = new TabularVDBEPolicy(Q, actionSet, policyParameter);
		}
		// correctly overwrite vdbe or softmax!
		if (expAlgorithm.toLowerCase().contains("vdbesoftmax")) {
			pi = new TabularVdbeSoftmaxPolicy(Q, actionSet, policyParameter);
			((TabularVdbeSoftmaxPolicy)pi).setNormBoundaries(-1, 1);
		}
		
		// debug effective policy used
		System.out.print("using " + pi.getClass().getSimpleName() + "(" + expParameterName + "=" + policyParameter + ") policy");
				
		return pi;
	}

	/**
	 * run the experiment
	 * @throws Exception The exception
	 */
	public void run() throws Exception  {
							
    	Logger.getRootLogger().setLevel(Level.OFF);
	
    	// default property files
    	
    	//envFile = dataFolder + ".properties";
    	//String policyFile = "softmax.properties";
    	//String policyFile = "eGreedy.properties";
    	//String policyFile = "vdbeEpsilonGreedySoftmax.properties";
    	//String policyFile = "reinforceVdbeSoftmax.properties";
    	//policyFile = "reinforceMBELocal.properties";
    	//policyFile = "eGreedy.properties";

    	//String policyFile = "vdbeBoltzmannAllDecay.properties";
    	//String policyFile = "vdbeEpsilonGreedySoftmax.properties";
    		
		/***********************
		 * DEBUG OUTPUT 
		 ***********************/
        for (int i=0; i<alphas.length; i++) {
        	System.out.println ("  => adding alpha="+alphas[i]);
        }        
        System.out.println(	"maxEpisodes=" + MAX_EPISODES + 
        					"\nmaxSteps=" + MAX_STEPS + 
        					"\nmaxExperiments=" + MAX_EXPERIMENTS + 
        					"\nstochasticRewards=" + stochasticRewards + 
        			    	"\nGamma = " + gamma);

	        

    	System.out.println ("\nexplorationAlgorithm: " + expAlgorithm + 
    						"\nexpAlgorithmVersion: " + expAlgorithmVersion + 
    						"\nexpParameterName: " + expParameterName);
    	
    	for (int i=0; i<expParameter.length; i++) {
    		System.out.println ("  => adding " + expParameterName + "=" + expParameter[i]);
    	}
		System.out.println ("REINFORCE: " + 
				"initMean=" + reinforceInitMean + ", minMean=" + reinforceMinMean + ", maxMean=" + reinforceMaxMean + ", " +
				"initSD=" + reinforceInitSD+ ", minSD=" + reinforceMinSD+ ", maxSD=" + reinforceMaxSD + "\n");

        // experiment setups
    	String rewardFolder = "deterministic-rewards";
    	if (stochasticRewards) {
    		rewardFolder = "stochastic-rewards";
    	}
    	System.out.println ("rewardFolder= " + rewardFolder);
        final String gammaFolder = rewardFolder + "/gamma=" + gamma;
        
        /***************************
         * CONFIGURE PLOTTING RANGE
         ***************************/
		// Plotting ranges
		if (plottingStyle.equals(PLOTTING_EPISODIC)) {			
			plottingMaxSteps = MAX_EPISODES; 
		} else {
			plottingMaxSteps = MAX_STEPS;
		}
		System.out.println ("Plotting style: " +  plottingStyle + "(" + plottingMaxSteps + ")");
		

        
        /**********************
         * iterate over alphas
         **********************/
		for (double lambda : lambdas) {
	        for (double alpha : alphas) {
	        	
	        	System.out.print ("\rProcessing alpha=" + alpha);
	        	
	        	String alphaString = "alpha=" + alpha;
		        
	        	// set alphaString
	        	if (alpha < 0.0 && Math.abs(alpha) <= 1.0) {
	        		alphaString = "alpha=" + Math.abs(alpha) + ",sampleAverageBandits";
	        	} else if ((int)alpha == -2) {
	        		alphaString = "alpha=sampleAverage";
	        	}
	        	
	        	String eTraceFolder = "";
	        	if (this.etrace != ETraceType.none) {
	        		eTraceFolder = "/etrace-lambda=" + lambda;
	        	}
	        	
	        	String folder = "data/" + dataFolder + "/results/" + gammaFolder + eTraceFolder + "/" + alphaString;
	        	System.out.println ("Creating directory: " + folder + "/data/" + expAlgorithm);
	        	Runtime.getRuntime().exec("mkdir -p " + folder + "/data/" + expAlgorithm );       	
	        	
	        	// create plotter objects
	        	DataAveragePlotter rewardPlotter = new DataAveragePlotter(folder + "/" + "rewardPlot-"+ learnerName + "-" + expAlgorithm + ".gnuplot",learnerName+"-learning - Average Reward");
	
	        	// generate Plotter for each
	        	DataAveragePlotter envParamPlotter[] = new DataAveragePlotter[obsEnvParams.length];
	        	for (int i=0; i<obsEnvParams.length; i++) {
	        		envParamPlotter[i] = new DataAveragePlotter(folder + "/" + obsEnvParams[i] + "-"+ learnerName + "-" + expAlgorithm + ".gnuplot",learnerName + " - " + obsEnvParams[i]);
	        	} 
	        	
	        	DataAveragePlotter reinforceMuePlotter = new DataAveragePlotter(folder + "/" + "rMuePlot-"+ learnerName + "-"     + expAlgorithm + ".gnuplot", learnerName+"-learning - Reinforce Mue");
	        	DataAveragePlotter reinforceSigmaPlotter = new DataAveragePlotter(folder + "/" + "rSigmaPlot-"+ learnerName + "-"     + expAlgorithm + ".gnuplot", learnerName+"-learning - Reinforce Sigma");
	        	DataAveragePlotter reinforceExpParamPlotter = new DataAveragePlotter(folder + "/" + "rExpParamPlot-"+ learnerName + "-"     + expAlgorithm + ".gnuplot", learnerName+"-learning - Reinforce Exp Param");
	        	DataAveragePlotter reinforceBaselinePlotter = new DataAveragePlotter(folder + "/" + "rBaselinePlot-"+ learnerName + "-"     + expAlgorithm + ".gnuplot", learnerName+"-learning - Reinforce Baseline");
	        	
	        	
	        	/****************************************
	        	 * Iterate over exploration parameters 
	        	 ***************************************/
		        for (int iParam=0; iParam< expParameter.length; iParam++) {
		        	
		        	//int t=0;
		        	
		        	//if (parameterNames.length == 1) {
		        		System.out.println ("processing " + expParameterName + "=" + expParameter[iParam]);
		        	//} 
		        	
		        	/*else {
		        		System.out.println ("processing " + parameterNames[0] + "=" + parameter1[iParam] + ", " + 
		        											parameterNames[1] + "=" + parameter2[iParam]);        		
		        	}*/
		        	
			        // average rewards+optimalAction over all bandits
		        	String plotString = "";
		        	//if  (parameterNames.length == 1) {
		        		plotString = learnerName + " alpha=" + alphaString + " " + "method="+ expAlgorithm + " " + expParameterName + "=" + expParameter[iParam]; // + parameterNames[1] + "=" + 
		        	/*} else {
		        		plotString = learnerName + " alpha=" + alphaString + " " + "method="+ expAlgorithm + " " + parameterNames[0] + "=" + parameter1[iParam] + parameterNames[1] + "=" + parameter2[iParam];        		
		        	}*/	        	
	
		        	ScalarAverager ra;// = new ScalarAverager(MAX_STEPS, plotString);
		        	
		        	// generate ScalarAverager for each environment parameter
		        	ScalarAverager ecpa[];
		        	ScalarAverager reinforceMueAverager;
		        	ScalarAverager reinforceSigmaAverager;
		        	ScalarAverager reinforcePolicyParameterAverager;
		        	ScalarAverager reinforceRewardBaselineAverager;
		        	
		        	if (plottingStyle.equals(PLOTTING_EPISODIC)) {
		        		
		        		// reward
		        		ra = new EpisodicCumulativeRewardAverager(plottingMaxSteps, plotString);
		        		 
		        		// optional parameters
			        	ecpa = new EpisodicCumulativeParameterAverager[obsEnvParams.length];
			        	for (int i=0; i<obsEnvParams.length; i++) {
				        	ecpa[i] = new EpisodicCumulativeParameterAverager(plottingMaxSteps, obsEnvParams[i] + " - " + plotString);
				        	((EpisodicCumulativeParameterAverager)ecpa[i]).setParameter(obsEnvParams[i]);
			        	}
			        
			        	// REINFORCE
			        	reinforceMueAverager = new EpisodicCumulativeParameterAverager(plottingMaxSteps, "Mue - " + plotString);
		        		((EpisodicCumulativeParameterAverager)reinforceMueAverager).setParameter(EpisodicReinforceLearner.MEAN);
			        	
			        	reinforceSigmaAverager = new EpisodicCumulativeParameterAverager(plottingMaxSteps, "Standard Deviation- " + plotString);
			        	((EpisodicCumulativeParameterAverager)reinforceSigmaAverager).setParameter(EpisodicReinforceLearner.VARIANCE);
			        	
			        	reinforcePolicyParameterAverager = new EpisodicCumulativeParameterAverager(plottingMaxSteps, "PolicyParameter - " + plotString);
			        	((EpisodicCumulativeParameterAverager)reinforcePolicyParameterAverager).setParameter(EpisodicReinforceLearner.POLICY_PARAMETER);
			        	
			        	reinforceRewardBaselineAverager = new EpisodicCumulativeParameterAverager(plottingMaxSteps, "RewardBaseline - " + plotString);
			        	((EpisodicCumulativeParameterAverager)reinforceRewardBaselineAverager).setParameter(EpisodicReinforceLearner.REWARD_BASELINE);
			        	
		        	} else {
		        		
		        		// reward
		        		ra = new ScalarAverager(plottingMaxSteps, plotString);
		        		 
		        		// optional parameters
			        	ecpa = new StepwiseParameterAverager[obsEnvParams.length];
			        	for (int i=0; i<obsEnvParams.length; i++) {
				        	ecpa[i] = new StepwiseParameterAverager(plottingMaxSteps, obsEnvParams[i] + " - " + plotString);
				        	((StepwiseParameterAverager)ecpa[i]).setParameter(obsEnvParams[i]);
			        	}
			        
			        	// REINFORCE
			        	reinforceMueAverager = new StepwiseParameterAverager(plottingMaxSteps, "Mue - " + plotString);
		        		((StepwiseParameterAverager)reinforceMueAverager).setParameter(EpisodicReinforceLearner.MEAN);
			        	
			        	reinforceSigmaAverager = new StepwiseParameterAverager(plottingMaxSteps, "Standard Deviation- " + plotString);
			        	((StepwiseParameterAverager)reinforceSigmaAverager).setParameter(EpisodicReinforceLearner.VARIANCE);
			        	
			        	reinforcePolicyParameterAverager = new StepwiseParameterAverager(plottingMaxSteps, "PolicyParameter - " + plotString);
			        	((StepwiseParameterAverager)reinforcePolicyParameterAverager).setParameter(EpisodicReinforceLearner.POLICY_PARAMETER);
			        	
			        	reinforceRewardBaselineAverager = new StepwiseParameterAverager(plottingMaxSteps, "RewardBaseline - " + plotString);
			        	((StepwiseParameterAverager)reinforceRewardBaselineAverager).setParameter(EpisodicReinforceLearner.REWARD_BASELINE);
		        	}
		        	
		        	// reinforce observation	        	
		        	BasicReinforceGaussianLearner erpl = null;
			        	        
		        	/**********************
		        	 * average experiments
		        	 **********************/
			        for (int episode=0; episode<MAX_EXPERIMENTS; episode++) {
			        	
			        	System.out.print("\r"+ learnerName + " Agent no. " + (episode+1)+ ": ");
	
			        	Environment env = getNewEnvironment();
			
			            // init new Q-Function with default value 0
			        	HashQFunction Q = new HashQFunction(initQvalue, ACTION_SET);
        
			        			        	
			        	/*********************
			        	 *  create new policy
			        	 *********************/
			        	Policy pi = getNewPolicy(Q, ACTION_SET, expParameter[iParam]);
			        	
			        	/*
			        	// setup alpha Object
	        			StepSizeCalculator alphaObject;
	        			if (alpha <0) {
	        				System.out.println ("USING SAMPLE_AVERAGE_ALPHA");
	        				alphaObject = new SampleAverageAlpha();
	        			} else {
	        				System.out.println ("USING CONSTANT_ALPHA=" + alpha);
	        				alphaObject = new ConstantAlpha(alpha);
	        			}
	        			*/
			        	
			        	// attach policy to reinforce
			        	//System.out.println ("EXP_ALGORITHM=" + expAlgorithm.toLowerCase());
			        	if (expAlgorithm.toLowerCase().contains("reinforce")) {
			        		
			        			// local adaption (erpl is by itself a policy)
			        			if (expAlgorithm.toLowerCase().contains("local")) {
			        				/*
			        				erpl = new LocalReinforcePolicyLearner (Q, pi,  
			        	    				this.reinforceInitMean,
			        	    				this.reinforceMinMean,
			        	    				this.reinforceMaxMean,
			        	    				this.reinforceInitSD,
			        	    				this.reinforceMinSD,
			        	    				this.reinforceMaxSD,
			        	    				alphaObject,
			        	    				gamma
			        				);*/
			        				
			        				LocalReinforcePolicy lrlp = new LocalReinforcePolicy(pi);
			        				erpl = new LocalReinforceLearner (lrlp, Q,  
			        	    				this.reinforceInitMean,
			        	    				this.reinforceMinMean,
			        	    				this.reinforceMaxMean,
			        	    				this.reinforceInitSD,
			        	    				this.reinforceMinSD,
			        	    				this.reinforceMaxSD,
			        	    				alpha,
			        	    				gamma
			        				);
			        				lrlp.setReinforceLearner((LocalReinforceLearner)erpl);
			        				
			        				

			        				
			        				// agent should use LocalReinforcePolicyLearner for action selection,
			        				// because of dynamically setting the exploration parameter
			        				pi = (Policy)lrlp;
			        				
			        				System.out.print(" + REINFORCE_LOCAL_STEPWISE");
			        				
			        			// global
			        			} else {
			        				
			        				// wrapper for returning the policy + erpl
			        				/*
			        				erpl = new EpisodicReinforcePolicyLearner(pi,  
			        	        				this.reinforceInitMean,
			        	        				this.reinforceMinMean,
			        	        				this.reinforceMaxMean,
			        	        				this.reinforceInitSD,
			        	        				this.reinforceMinSD,
			        	        				this.reinforceMaxSD,
			        	        				alphaObject,
			        	        				gamma
			        	        	);
			        	        	*/
			        				erpl = new EpisodicReinforceLearner(
			        						new EpisodicReinforcePolicy(pi),  
		        	        				this.reinforceInitMean,
		        	        				this.reinforceMinMean,
		        	        				this.reinforceMaxMean,
		        	        				this.reinforceInitSD,
		        	        				this.reinforceMinSD,
		        	        				this.reinforceMaxSD,
		        	        				alpha
		        	        	);
			        				
			        				System.out.print(" + REINFORCE_GLOBAL_EPISODIC");
			        			}
			        		//}
			        	}
				        
				        System.out.print(" alpha=" + alpha);
				        
				        // create agent
				        Agent agent = new Agent(pi);
				        
				        // setup experiment
				        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);
				        if (initState != null) {
				        	experiment.setInitState(initState);
				        }
				        
				        // setup learner
				        Learner learner = (Learner)getNewLearner(Q);
				        
				        // setup learner parameters
				        if (learnerString.equals(LEARNER_QLEARNING)) {
					        TabularQLearner ql = (TabularQLearner)learner;
					        //ql.setAlpha(alpha);
					        
					        ql.setAlpha(alpha);
					        ql.setGamma(gamma);
					        
					        ql.setEtraceType(this.etrace);
					        ql.setLambda(lambda);
					        
				        } else if (learnerString.equals(LEARNER_SARSA)) {
					        TabularSarsaLearner sl = (TabularSarsaLearner)learner;
					        //sl.setAlpha(alpha);
					       
					        sl.setAlpha(alpha);
					        sl.setGamma(gamma);			        	
					        sl.setEtraceType(this.etrace);
					        sl.setLambda(lambda);
					        
					        //System.out.println ("USING SARSA");
				        }
				        
				        // reinforce 
				        if (expAlgorithm.toLowerCase().contains("reinforce")) {
				        	
				        	//System.out.println ("attaching REINFORCE LEARNER TO AGENT");
				        	agent.addObserver(erpl);
				        	
			        		experiment.addObserver(reinforceSigmaAverager);
			        		experiment.addObserver(reinforceMueAverager);
			        		experiment.addObserver(reinforcePolicyParameterAverager);
			        		experiment.addObserver(reinforceRewardBaselineAverager);
	
	
				        	if (this.plottingStyle.equals(PLOTTING_EPISODIC)) {
	
				        		((EpisodicCumulativeParameterAverager)reinforceMueAverager).setParameterObserver(erpl);
				        		((EpisodicCumulativeParameterAverager)reinforceSigmaAverager).setParameterObserver(erpl);
				        		((EpisodicCumulativeParameterAverager)reinforcePolicyParameterAverager).setParameterObserver(erpl);
						        ((EpisodicCumulativeParameterAverager)reinforceRewardBaselineAverager).setParameterObserver(erpl);
	
				        	} else { //if (this.plottingStyle.equals(PLOTTING_STEPWISE)) {
					        	
				        		((StepwiseParameterAverager)reinforceMueAverager).setParameterObserver(erpl);
				        		((StepwiseParameterAverager)reinforceSigmaAverager).setParameterObserver(erpl);
				        		((StepwiseParameterAverager)reinforcePolicyParameterAverager).setParameterObserver(erpl);
						        ((StepwiseParameterAverager)reinforceRewardBaselineAverager).setParameterObserver(erpl);
				        	}				        
				        }
				        
			        	
				        // attach learner to agent
				        agent.addObserver(learner);
				        			     
				        // observe value difference when using vdbe policies
				        if (expAlgorithm.toLowerCase().contains ("vdbe")) {
				        	
				        	TabularVDBEPolicy vdbePi = null;
				        		        	
				        	// REinforce (local) stepwise VDBE-Update
				        	if (expAlgorithm.toLowerCase().contains("local")) {
				        		vdbePi = (TabularVDBEPolicy) ((LocalReinforcePolicy)pi).getPi(); 
			        		
				        	} else {
				        		vdbePi = (TabularVDBEPolicy) pi;
				        	}
	
		
					        // attach VDBE-Policy to learner (observing alpha + td-error)
					        System.out.println ("; VDBE: observing TD-error + alpha from learner");
					        if (learnerString.equals(LEARNER_QLEARNING)) {
						        TabularQLearner ql = (TabularQLearner)learner;
						        ql.addObserver(vdbePi);
					        } else if (learnerString.equals(LEARNER_SARSA)) {
					        	TabularSarsaLearner sl = (TabularSarsaLearner)learner;
					        	sl.addObserver(vdbePi);
					        }
					        
					        
				        	if (expAlgorithmVersion == 2) {
				        		((TabularVDBEPolicy) vdbePi).setVdbeType(TabularVDBEPolicy.VDBE_ACTIVATION_EXPONENTIAL);
				        		//System.out.println ("using VDBEv2 formula");
				        	}
	
				        } 
				        
				        // attach reward averager
				        experiment.addObserver(ra);
				        
				        // attach parameter averagers for the environment
				        for (int i=0; i<obsEnvParams.length; i++) {
				        	
				        	if (this.plottingStyle.equals(PLOTTING_EPISODIC)) {			        		
				        		((EpisodicCumulativeParameterAverager)ecpa[i]).setParameterObserver((org.hswgt.teachingbox.core.rl.experiment.ParameterObserver) env);
				        	} else {		        		
				        		((StepwiseParameterAverager)ecpa[i]).setParameterObserver((org.hswgt.teachingbox.core.rl.experiment.ParameterObserver) env);
				        	}
					        experiment.addObserver(ecpa[i]);			        	
				        }
				  
				        // run experiment
				        experiment.run();
			        }
			        
			        rewardPlotter.addScalarAverager(ra);		        
			        rewardPlotter.setRange(new double[]{0, plottingMaxSteps}, this.rewardRange);
			        
			        // add averagers to plotter (ignore plot range)
			        for (int i=0; i<obsEnvParams.length; i++) {
			        	envParamPlotter[i].addScalarAverager(ecpa[i]);
			        }
			        
			        
			        if (expAlgorithm.contains ("reinforce")) {
			        
				        reinforceMuePlotter.addScalarAverager(reinforceMueAverager);
				        //reinforceMueQPlotter.setRange(new double[]{0, MAX_EPISODES}, new double[]{0, Math.exp(Double.parseDouble(expAlgFile.getProperty("maxMean")))});
				        reinforceMuePlotter.setRange(new double[]{0, plottingMaxSteps}, new double[]{0, this.reinforceMaxMean});
				            
				        reinforceSigmaPlotter.addScalarAverager(reinforceSigmaAverager);
				        //reinforceSigmaQPlotter.setRange(new double[]{0, MAX_EPISODES}, new double[]{0, Math.exp(Double.parseDouble(expAlgFile.getProperty("maxVariance")))});
				        reinforceSigmaPlotter.setRange(new double[]{0, plottingMaxSteps}, new double[]{0, this.reinforceMaxSD});
				        
				        reinforceExpParamPlotter.addScalarAverager(reinforcePolicyParameterAverager);
				        //reinforceExpParamQPlotter.setRange(new double[]{0, MAX_EPISODES}, new double[]{0, Math.exp(Double.parseDouble(expAlgFile.getProperty("maxMean")))});
				        reinforceExpParamPlotter.setRange(new double[]{0, plottingMaxSteps}, new double[]{0, this.reinforceMaxMean});
				        	
				        reinforceBaselinePlotter.addScalarAverager(reinforceRewardBaselineAverager);
				        reinforceBaselinePlotter.setRange(new double[]{0, plottingMaxSteps}, this.rewardRange);
				        
				        ObjectSerializer.save(folder + "/data/" + expAlgorithm + "/reinfMue-"+ learnerName + "-" + expParameter[iParam] + ".txt", reinforceMueAverager);
				        ObjectSerializer.save(folder + "/data/" + expAlgorithm + "/reinfSigma-"+ learnerName + "-" + expParameter[iParam] + ".txt", reinforceSigmaAverager);
				        ObjectSerializer.save(folder + "/data/" + expAlgorithm + "/reinfExpParam-"+ learnerName + "-" + expParameter[iParam] + ".txt", reinforcePolicyParameterAverager);
				        ObjectSerializer.save(folder + "/data/" + expAlgorithm + "/reinfBaseline-"+ learnerName + "-" + expParameter[iParam] + ".txt", reinforceRewardBaselineAverager);			        
			        }
	
			        ObjectSerializer.save(folder + "/data/" + expAlgorithm + "/reward-"+ learnerName + "-" + expParameter[iParam] + ".txt", ra);
			        
			        // save environment parameter averagers
			        for (int i=0; i<obsEnvParams.length; i++) {
			        	ObjectSerializer.save(folder + "/data/" + expAlgorithm + "/" + obsEnvParams[i] + "-"+ learnerName + "-" + expParameter[iParam] + ".txt", ecpa[i]);
			        }
			        
			        
			        rewardPlotter.plotGraph();
			        
			        
			        // plot environment parameters
			        for (int i=0; i<obsEnvParams.length; i++) {
					    	envParamPlotter[i].plotGraph();
			        }
			        
			        if (expAlgorithm.contains ("reinforce")) {
			        	reinforceMuePlotter.plotGraph();
		
			        	reinforceSigmaPlotter.plotGraph();
		
			        	reinforceExpParamPlotter.plotGraph();
		
			        	reinforceBaselinePlotter.plotGraph();
			        }
		        }	
	
		       // Runtime.getRuntime().exec("killall gnuplot");
	        }
		}
    }

	/**
	 * Example usage for the StochasticGridworldEnvironment
	 * @param args The command shell parameters
	 * @throws Exception The exception
	 */
	public static void main(String[] args) throws Exception {
	
		System.out.println ("starting dynamicCliffWorld");
		
		
		
		args = new String[] {null, "dynamicCliff.properties", "reinforceVdbeSoftmax.properties"};
		SimpleExperiment se = new SimpleExperiment ( 
				args, "dynamicCliff",
				SimpleExperiment.ENVIRONMENT_DYNAMIC_CLIFF, 
				null,
				SimpleExperiment.PLOTTING_EPISODIC
		);
		//fe.setupExperiment (200, 3000, 300, 1.0, new double[]{0.21, 0.14, 0.29}, false);
		se.setInitState(DynamicCliffEnvironment.DEFAULT_INIT_STATE);
		
		// add extra plot parameters

		/*String plotParameters[]  = new String[] {
				DynamicCliffEnvironment.LAST_GOAL_1,
				DynamicCliffEnvironment.LAST_GOAL_2,
				DynamicCliffEnvironment.LAST_STEP,
				DynamicCliffEnvironment.LAST_WALL					
		};*/		
		//se.setObservableEnvironmentParameters(plotParameters);
		//se.setPlottingSmoothNeighbours(5);

		// run Q-learning
		se.setupLearner(SimpleExperiment.LEARNER_QLEARNING);
		se.run();
		
		// run SARSA
		se.setupLearner(SimpleExperiment.LEARNER_SARSA);
		se.run();
	}
}
