package de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteKeyProjector;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteKeyProjectorExecutor extends ReteNodeExecutor {

	private ReteKeyProjector node;
	private Iterator<List<Object>> keyIterator;

	public ReteKeyProjectorExecutor(ReteKeyProjector node,
			ReteIndexer inputIndexer) {
		this.node = node;
		this.keyIterator = inputIndexer.getAllTuples().keySet().iterator();
	}

	@Override
	public boolean hasNextTuple() {
		return keyIterator.hasNext();
	}

	@Override
	public void generateNextTuple() {
		if(!hasNextTuple()) {
            throw new NoSuchElementException();
		}
		ReteTuple tuple = new ReteTuple(keyIterator.next());
		node.propagateAddition(tuple);
	}

}
