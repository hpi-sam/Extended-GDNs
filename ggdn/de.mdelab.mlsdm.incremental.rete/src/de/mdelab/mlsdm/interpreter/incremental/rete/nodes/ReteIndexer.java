package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteIndexer extends ReteDistributor {
	
	protected int[] mask;
	protected ReteIndex index;
	
	private final String name;

	public ReteIndexer(int[] mask, int keySize) {
		this(mask, keySize, "NULL");
	}

	public ReteIndexer(int[] mask, int keySize, String name) {
		this.mask = mask;
		this.index = new ReteIndex(keySize);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public int[] getMask() {
		return mask;
	}

	public void setMask(int[] mask) {
		this.mask = mask;
	}

	@Override
	public void doAddTuple(ReteTuple tuple, ReteNode source) {
		tuple = fitToMask(tuple);
		tuple.setIsPreliminary(tuple.isPreliminary());
		if(index.addTuple(tuple)) {
			notifyAddition(tuple);
			super.propagateAddition(tuple);
		}
	}

	protected ReteTuple fitToMask(List<Object> tuple) {
		Object[] newTuple = new Object[mask.length];
		for(int i = 0; i < mask.length; i++) {
			newTuple[i] = tuple.get(mask[i]);
		}
		return new ReteTuple(Arrays.asList(newTuple));
	}

	@Override
	public void doRemoveTuple(ReteTuple tuple, ReteNode source) {
		tuple = fitToMask(tuple);
		if(index.removeTuple(tuple)) {
			notifyRemoval(tuple);
			super.propagateRemoval(tuple);
		}
	}

	public Collection<ReteTuple> getTuples(List<Object> key) {
		return index.getTuples(key);
	}

	public Map<List<Object>, Map<ReteTuple, ReteTuple>> getAllTuples() {
		return index.getAllTuples();
	}
	
	public int size() {
		return index.size();
	}
	
	public boolean isIndexer() {
		return true;
	}

	public int getKeySize() {
		return index.getKeySize();
	}

	@Override
	public String toString() {
		return "INDEXER <" + getName() + ">";
	}

}
