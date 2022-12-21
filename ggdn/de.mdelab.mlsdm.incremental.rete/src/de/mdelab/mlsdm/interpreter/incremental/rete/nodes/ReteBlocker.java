package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteBlocker extends ReteNode {

	private ReteNode predecessor;
	private ReteNode successor;

	public ReteBlocker(ReteNode predecessor, ReteNode successor) {
		this.predecessor = predecessor;
		this.successor = successor;
	}

	@Override
	public void doAddTuple(ReteTuple tuple, ReteNode source) {
		
	}

	@Override
	public void doRemoveTuple(ReteTuple tuple, ReteNode source) {
		
	}

	public boolean isBlocker() {
		return true;
	}

	public ReteNode getPredecessor() {
		return predecessor;
	}

	public ReteNode getSuccessor() {
		return successor;
	}

}
