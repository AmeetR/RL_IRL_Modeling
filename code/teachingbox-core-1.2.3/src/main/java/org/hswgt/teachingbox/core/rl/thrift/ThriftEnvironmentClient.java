package org.hswgt.teachingbox.core.rl.thrift;

import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.hswgt.teachingbox.core.rl.env.Action;
import org.hswgt.teachingbox.core.rl.env.Environment;
import org.hswgt.teachingbox.core.rl.env.State;
import org.hswgt.teachingbox.core.rl.thrift.ThriftEnvironment.Client;
import org.hswgt.teachingbox.core.rl.thrift.ThriftEnvironmentClient;
import org.hswgt.teachingbox.core.rl.tools.ThriftUtils;

public class ThriftEnvironmentClient implements Environment {
	
	private static final long serialVersionUID = 7420649647906246264L;
	
	Client client;
	
	/**
	 * Connection to the server
	 * @param ip Server-IP
	 * @param port Server-Port
	 * @throws SocketException The exception
	 */
	public ThriftEnvironmentClient(String ip, int port) throws SocketException {
		TTransport transport = new TSocket(ip, port);	         
		TProtocol protocol = new TBinaryProtocol(transport);
		client = new Client(protocol);
		try {
			transport.open();
		} catch (TTransportException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Perform action
	 * @param a The action
	 * @return the reward for executing action a
	 */
	public double doAction(Action a) {
		try {
			return client.doAction(ThriftUtils.Convert.ActionToList(a));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Get the current State
	 * @return a <b>copy</b> of the agents state
	 */
	public State getState() {
		try {
			List<Double> thriftState = new LinkedList<Double>();
			thriftState = client.getState();
			return ThriftUtils.Convert.ListToState(thriftState);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Set the environment to state s
	 * @param s The state to set
	 */
	public void init(State s) {
		try {
			client.init(ThriftUtils.Convert.StateToList(s));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Set the environment to a valid random state
	 */
	public void initRandom() {
		try {
			client.initRandom();
		} catch (TException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Check if agent is in a final state
	 * @return true if terminal state reached
	 */
	public boolean isTerminalState() {
		try {
			return client.isTerminalState();
		} catch (TException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}