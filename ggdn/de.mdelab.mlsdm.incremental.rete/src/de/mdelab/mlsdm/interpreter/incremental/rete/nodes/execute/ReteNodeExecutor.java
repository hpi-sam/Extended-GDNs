package de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute;

public abstract class ReteNodeExecutor {

	public abstract boolean hasNextTuple();
	
	public abstract void generateNextTuple();
	
}
