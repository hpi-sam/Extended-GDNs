package de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteJoin;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteJoin.ReteJoinSideEnum;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteJoinExecutor extends ReteNodeExecutor {

	private ReteJoin node;
	private ReteJoinSideEnum enumeratedSide;
	private ReteIndexer joinIndexer;
	
	private Iterator<Entry<List<Object>, Map<ReteTuple, ReteTuple>>> iterator;
	private Entry<List<Object>, Map<ReteTuple, ReteTuple>> currentEnumerated;
	private Iterator<ReteTuple> enumerateIterator;
	private ReteTuple enumeratedTuple;
	private Collection<ReteTuple> currentJoinTuples;
	private Iterator<ReteTuple> joinIterator;
	
	private ReteTuple next;

	public ReteJoinExecutor(ReteJoin node,
			Iterator<Entry<List<Object>, Map<ReteTuple, ReteTuple>>> iterator,
			ReteJoinSideEnum enumeratedSide, ReteIndexer joinIndexer) {
		this.node = node;
		this.iterator = iterator;
		this.enumeratedSide = enumeratedSide;
		this.joinIndexer = joinIndexer;
		this.next = null;
	}

	@Override
	public boolean hasNextTuple() {
		if(next == null) {
			next = computeNextTuple();
		}
		return next != null;
	}

	private ReteTuple computeNextTuple() {
		while(joinIteratorHasNext()  || enumerateIteratorHasNext() || iterator.hasNext()) {
			if(joinIteratorHasNext()) {
				ReteTuple joinTuple = joinIterator.next();
				ReteTuple join = constructJoin(enumeratedTuple, joinTuple);
				return join;
			}
			else if(enumerateIteratorHasNext()) {
				enumeratedTuple = enumerateIterator.next();
				joinIterator = currentJoinTuples.iterator();
			}
			else {
				currentEnumerated = iterator.next();
				enumerateIterator = currentEnumerated.getValue().keySet().iterator();
				currentJoinTuples = joinIndexer.getTuples(currentEnumerated.getKey());
			}
		}
		return null;
	}

	private ReteTuple constructJoin(ReteTuple enumeratedTuple, ReteTuple joinTuple) {
		return enumeratedSide == ReteJoinSideEnum.LEFT ? node.constructJoin(enumeratedTuple, joinTuple) :
															node.constructJoin(joinTuple, enumeratedTuple);
	}

	private boolean joinIteratorHasNext() {
		return joinIterator != null && joinIterator.hasNext();
	}

	private boolean enumerateIteratorHasNext() {
		return enumerateIterator != null && enumerateIterator.hasNext();
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
