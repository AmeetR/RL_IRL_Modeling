# tbox_env.thrift
namespace java org.hswgt.teachingbox.core.rl.thrift

service ThriftEnvironment {
	double doAction(1:list<double> a),
	list<double> getState(),
	bool isTerminalState(),
	void initRandom(),
	void init(1:list<double> s)
}
	