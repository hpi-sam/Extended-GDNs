package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import de.mdelab.mlsdm.incremental.ChangeListener;

public abstract class ReteInput extends ReteTupleGenerator implements ChangeListener {

	public boolean isInput() {
		return true;
	}

	public boolean isTupleGenerator() {
		return true;
	}

	public boolean isEdge() {
		return false;
	}

	public boolean isNode() {
		return false;
	}

}
