package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.Collection;
import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteKeyProjectorExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteNodeExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteKeyProjector extends ReteTupleGenerator  {

	private ReteIndexer inputIndexer;

	public ReteKeyProjector(ReteIndexer inputIndexer) {
		this.inputIndexer = inputIndexer;
	}

	@Override
	public void doAddTuple(ReteTuple tuple, ReteNode source) {
		List<Object> key = tuple.subList(0, inputIndexer.getKeySize());
		Collection<ReteTuple> tuples = inputIndexer.getTuples(key);
		if(tuples.size() == 1) {
			ReteTuple projection = new ReteTuple(key);
			projection.setIsPreliminary(tuple.isPreliminary());
			propagateAddition(projection);
		}
	}

	@Override
	public void doRemoveTuple(ReteTuple tuple, ReteNode source) {
		List<Object> key = tuple.subList(0, inputIndexer.getKeySize());
		Collection<ReteTuple> tuples = inputIndexer.getTuples(key);
		if(tuples.size() == 0) {
			propagateRemoval(new ReteTuple(key));
		}
	}

	@Override
	public ReteNodeExecutor createExecutor() {
		return new ReteKeyProjectorExecutor(this, inputIndexer);
	}

	public boolean isTupleGenerator() {
		return true;
	}

}
