package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic;

import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public abstract class ReteDiscriminator extends ReteNode {

	@Override
	public void doAddTuple(ReteTuple tuple, ReteNode source) {
		ReteNode consumer = selectConsumer(tuple);
		tuple.setDecision(this, consumer);
		consumer.addTuple(tuple, this);
	}

	@Override
	public void doRemoveTuple(ReteTuple tuple, ReteNode source) {
		ReteNode consumer = tuple.getDecision(this);
		consumer.removeTuple(tuple, this);
	}

	protected abstract ReteNode selectConsumer(List<Object> tuple);

}
