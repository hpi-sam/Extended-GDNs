package de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteAntiJoin;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteAntiJoinExecutor extends ReteNodeExecutor {

	private ReteAntiJoin node;
	private ReteIndexer joinIndexer;
	
	private Iterator<Entry<List<Object>, Map<ReteTuple, ReteTuple>>> iterator;
	private Entry<List<Object>, Map<ReteTuple, ReteTuple>> currentEnumerated;
	private Iterator<ReteTuple> enumerateIterator;
	private ReteTuple next;

	public ReteAntiJoinExecutor(ReteAntiJoin node,
			Iterator<Entry<List<Object>, Map<ReteTuple, ReteTuple>>> iterator,
			ReteIndexer joinIndexer) {
		this.node = node;
		this.iterator = iterator;
		this.joinIndexer = joinIndexer;
	}

	@Override
	public boolean hasNextTuple() {
		if(next == null) {
			next = computeNextTuple();
		}
		return next != null;
	}

	private ReteTuple computeNextTuple() {
		while(enumerateIteratorHasNext() || iterator.hasNext()) {
			if(enumerateIteratorHasNext()) {
				ReteTuple enumeratedTuple = enumerateIterator.next();
				if(!currentKeyHasOpposite()) {
					ReteTuple join = new ReteTuple(enumeratedTuple.getTupleValue());
					return join;
				}
			}
			else {
				while((!enumerateIteratorHasNext() || currentKeyHasOpposite()) && iterator.hasNext()) {
					currentEnumerated = iterator.next();
					enumerateIterator = currentEnumerated.getValue().keySet().iterator();
				}
			}
		}
		return null;
	}
	
	private boolean enumerateIteratorHasNext() {
		return enumerateIterator != null && enumerateIterator.hasNext();
	}

	private boolean currentKeyHasOpposite() {
		return !joinIndexer.getTuples(currentEnumerated.getKey()).isEmpty();
	}

	@Override
	public void generateNextTuple() {
		if(!hasNextTuple()) {
            throw new NoSuchElementException();
		}
		else {
			ReteTuple propagated = next;
			next = null;
			node.propagateAddition(propagated);
		}
	}

}
