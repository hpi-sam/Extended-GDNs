package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteImmutableFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;

public abstract class ReteImmutablePropertyContinuation extends ReteContinuation {

	public ReteImmutablePropertyContinuation(ReteIndexer indexer, int tableCode,
			double constraintFiltering, int id) {
		super(indexer, tableCode, constraintFiltering, id);
	}

	public boolean isImmutableProperty() {
		return true;
	}
	
	public abstract ReteImmutableFilter createFilterNode(int[] newTableMask);

}
