package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteNodeExecutor;

public abstract class ReteTupleGenerator extends ReteDistributor {

	public abstract ReteNodeExecutor createExecutor();

}
