package de.mdelab.mlsdm.interpreter.incremental.rete.util;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ReteDiscriminator;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;

public class ReteTuple extends AbstractList<Object> {

	protected List<Object> tupleValue;
	private boolean isPreliminary;
	
	private boolean dirty;
	private int cashedHash;
	
	private Map<ReteDiscriminator, ReteNode> decisions;

	public ReteTuple(List<Object> tupleValue) {
		this.tupleValue = tupleValue;
		this.isPreliminary = false;
		this.dirty = true;
		this.cashedHash = -1;
	}

	public List<Object> getTupleValue() {
		return tupleValue;
	}

	@Override
	public Object get(int index) {
		return tupleValue.get(index);
	}

	@Override
	public int size() {
		return tupleValue.size();
	}
	
	@Override
	public int hashCode() {
		if(dirty) {
			cashedHash = super.hashCode();
			dirty = false;
		}
		return cashedHash;
	}

	protected Map<ReteDiscriminator, ReteNode> getDecisions() {
		if(decisions == null) {
			decisions = new HashMap<ReteDiscriminator, ReteNode>();
		}
		return decisions;
	}

	public void setDecision(ReteDiscriminator discriminator, ReteNode consumer) {
		getDecisions().put(discriminator, consumer);
	}
	
	public ReteNode getDecision(ReteDiscriminator discriminator) {
		return getDecisions().get(discriminator);
	}
	
	public void setIsPreliminary(boolean isPreliminary) {
		this.isPreliminary = isPreliminary;
	}
	
	public boolean isPreliminary() {
		return isPreliminary;
	}

}
