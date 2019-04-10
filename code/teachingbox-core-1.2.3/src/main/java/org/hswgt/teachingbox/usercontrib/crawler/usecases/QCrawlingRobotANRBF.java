package org.hswgt.teachingbox.usercontrib.crawler.usecases;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hswgt.teachingbox.core.rl.agent.Agent;
import org.hswgt.teachingbox.core.rl.experiment.Experiment;
import org.hswgt.teachingbox.core.rl.learner.GradientDescentQLearner;
import org.hswgt.teachingbox.core.rl.network.Network;
import org.hswgt.teachingbox.core.rl.policy.PolicyConfigurator;
import org.hswgt.teachingbox.core.rl.network.adaption.NoNodeNearby;
import org.hswgt.teachingbox.core.rl.network.rbf.RadialBasisFunction;
import org.hswgt.teachingbox.core.rl.network.rbf.adaption.RBFDistanceCalculator;
import org.hswgt.teachingbox.core.rl.valuefunctions.QFeatureFunction;
import org.hswgt.teachingbox.usercontrib.crawler.env.CrawlerEnvironment;

/**
 * This use-case runs Q-learning on the hardware walking robot. The Q-function is approximated by an
 * adaptive radial basis network.
 * @author tokicm
 *
 */
public class QCrawlingRobotANRBF {
	public static void main(String[] args) throws Exception
    {
		Logger.getRootLogger().setLevel(Level.DEBUG);


        // choose simga
        final double X_STEP = 1;
        final double Y_STEP = 1;
        final double[] sigma = new double[]{X_STEP, Y_STEP};

        // create adaptive network adding rbfs
        Network net = new Network(new NoNodeNearby(
                new RadialBasisFunction(sigma,sigma), new RBFDistanceCalculator()));
        net.setIsNormalized(true);

        // setup Q-Function
        QFeatureFunction Q = new QFeatureFunction(net, CrawlerEnvironment.ACTION_SET);

        CrawlerEnvironment env = new CrawlerEnvironment(19200, "/dev/ttyUSB0");

        PolicyConfigurator pi = new PolicyConfigurator (Q, CrawlerEnvironment.ACTION_SET);

        // create agent
        Agent agent = new Agent(pi);

        // experiment setups
        final int MAX_EPISODES = 1;
        final int MAX_STEPS    = 3000;
        final double alpha     = 0.6;
        final double gamma     = 0.9;

        // setup experiment
        Experiment experiment = new Experiment(agent, env, MAX_EPISODES, MAX_STEPS);

        // setup learner
        GradientDescentQLearner learner = new GradientDescentQLearner(Q, net,
                env.ACTION_SET);
        learner.setAlpha(alpha);
        learner.setGamma(gamma);

        // attach learner to agent
        agent.addObserver(learner);

        // Display Q-Function after each step.
        experiment.addObserver(new DisplayQValuesObserver(Q) );


        /*
        // create a ValueFunctionPlotter drawing the maximum value of the QFunction
        Plotter Vplotter = new ValueFunctionSurfacePlotter(Q, "[0:0.1:4]",
                "[0:0.1:4]", "Crawling Robot Q-Function");

        // use a runtime plotter, that calls the ValueFunctionPlotter every timestep
        Vplotter = new RuntimePlotter(Vplotter, RuntimePlotter.Mode.STEP, 1, net);
        experiment.addObserver((RuntimePlotter)Vplotter);
        */
        experiment.run();
    }
}
