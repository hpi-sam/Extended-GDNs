package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteIsomorphyFilter extends ReteImmutableFilter {

	private int index1;
	private int index2;
	
	public ReteIsomorphyFilter(int index1, int index2) {
		this.index1 = index1;
		this.index2 = index2;
	}
	
	@Override
	protected boolean filter(ReteTuple tuple) {
		return tuple.get(index1) == tuple.get(index2);
	}

}
