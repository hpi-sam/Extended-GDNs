package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public abstract class ReteImmutableFilter extends ReteDistributor {

	@Override
	public void doAddTuple(ReteTuple tuple, ReteNode source) {
		if(!filter(tuple)) {
			super.doAddTuple(tuple, source);
		}
	}

	@Override
	public void doRemoveTuple(ReteTuple tuple, ReteNode source) {
		if(!filter(tuple)) {
			super.doRemoveTuple(tuple, source);
		}
	}

	protected abstract boolean filter(ReteTuple tuple);
	
}
