package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteIndex {

	public static int TOTAL_TUPLES = 0;
	
	protected Map<List<Object>, Map<ReteTuple, ReteTuple>> index;
	protected int keySize;
	protected int size;
	
	public ReteIndex(int keySize) {
		this.index = new HashMap<List<Object>, Map<ReteTuple, ReteTuple>>();
		this.keySize = keySize;
		this.size = 0;
	}

	public boolean addTuple(ReteTuple tuple) {
		List<Object> key = tuple.subList(0, keySize);
		Map<ReteTuple, ReteTuple> tuples = index.get(key);
		if(tuples == null) {
			tuples = new HashMap<ReteTuple, ReteTuple>();
			index.put(key, tuples);
		}
		ReteTuple oldTuple = tuples.get(tuple);
		boolean add = oldTuple == null || (oldTuple.isPreliminary() != tuple.isPreliminary());
		if(add) {
			tuples.put(tuple, tuple);
			if(oldTuple == null) {
				TOTAL_TUPLES++;
				size++;
			}
		}
		return add;
	}
	
	public boolean removeTuple(ReteTuple tuple) {
		boolean remove = false;
		List<Object> key = tuple.subList(0, keySize);
		Map<ReteTuple, ReteTuple> tuples = index.get(key);
		if(tuples != null) {
			ReteTuple oldTuple = tuples.get(tuple);
			remove = oldTuple != null;
			if(remove) {
				tuples.remove(oldTuple);
				TOTAL_TUPLES--;
				size--;
				if(tuples.isEmpty()) {
					index.remove(key);
				}
			}
		}
		return remove;
	}
	
	public Collection<ReteTuple> getTuples(List<Object> key) {
		Map<ReteTuple, ReteTuple> tuples = index.get(key);
		return tuples != null ? tuples.keySet() : Collections.emptyList();
	}

	public Map<List<Object>, Map<ReteTuple, ReteTuple>> getAllTuples() {
		return index;
	}

	public int getKeySize() {
		return keySize;
	}
	
	public int size() {
		return size;
	}

}
