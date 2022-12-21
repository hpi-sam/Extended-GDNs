package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteCollectionIndex extends ReteIndex {

	protected Map<ReteTuple, ReteTuple> collection;

	public ReteCollectionIndex() {
		super(0);
		this.collection = new HashMap<ReteTuple, ReteTuple>();
	}

	@Override
	public boolean addTuple(ReteTuple tuple) {
		ReteTuple oldTuple = collection.get(tuple);
		boolean add = oldTuple == null || (oldTuple.isPreliminary() != tuple.isPreliminary());
		if(add) {
			collection.put(tuple, tuple);
			if(oldTuple == null) {
				size++;	
			}
		}
		return add;
	}

	@Override
	public boolean removeTuple(ReteTuple tuple) {
		ReteTuple oldTuple = collection.get(tuple);
		boolean removed = oldTuple != null;
		if(removed) {
			collection.remove(oldTuple);
			size--;
		}
		return removed;
	}

	@Override
	public Collection<ReteTuple> getTuples(List<Object> key) {
		return collection.keySet();
	}

	@Override
	public Map<List<Object>, Map<ReteTuple, ReteTuple>> getAllTuples() {
		Map<List<Object>, Map<ReteTuple, ReteTuple>> all = new HashMap<List<Object>, Map<ReteTuple, ReteTuple>>(); 
		all.put(new ReteTuple(Collections.emptyList()), collection);
		return all;
	}
	
	@Override
	public int size() {
		return collection.size();
	}
}
