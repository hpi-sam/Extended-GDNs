package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteResultTuple;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteResultProvider extends ReteIndexer {
	
	public ReteResultProvider() {
		super(new int[0], 0, "RESULT");
		this.index = new ReteCollectionIndex();
	}

	@Override
	protected ReteTuple fitToMask(List<Object> tuple) {
		return new ReteResultTuple(tuple, getTableMask());
	}

}
