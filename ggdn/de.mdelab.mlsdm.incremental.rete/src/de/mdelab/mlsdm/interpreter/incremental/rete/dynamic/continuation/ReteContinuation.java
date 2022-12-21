package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation;

import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;

public abstract class ReteContinuation {

	protected ReteIndexer indexer;
	
	protected int tableCode;
	protected double constraintFiltering;
	protected int id;
	
	public ReteContinuation(ReteIndexer indexer, int tableCode, double constraintFiltering, int id) {
		this.indexer = indexer;
		this.tableCode = tableCode;
		this.constraintFiltering = constraintFiltering;
		this.id = id;
	}
	
	public abstract double getContinuationCost(List<Object> tuple);
	public abstract int getKeySize();
	public abstract int[] getMask();
	public abstract ReteContinuation deriveContinuation(int tableCode, int[] tableMapping);
	public abstract int[] getTableMask();
	public abstract int getTableMappingIntoIndexer(int nodeId);

	
	public ReteIndexer getIndexer() {
		return indexer;
	}

	public int getTableCode() {
		return tableCode;
	}

	public double getConstraintFiltering() {
		return constraintFiltering;
	}

	public int getId() {
		return id;
	}

	public boolean isImmutableProperty() {
		return false;
	}

}
