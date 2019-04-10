package org.hswgt.teachingbox.usecases.thrift;

import org.apache.thrift.transport.TTransportException;
import org.hswgt.teachingbox.core.rl.env.ThriftMountainCarEnv;

public class Server {

	/**
	 * Start a new server
	 * @param args The command-line arguments
	 * @throws TTransportException  The Exception
	 */
	public static void main(String[] args) throws TTransportException {
		new ThriftMountainCarEnv(7911);
	}
}
