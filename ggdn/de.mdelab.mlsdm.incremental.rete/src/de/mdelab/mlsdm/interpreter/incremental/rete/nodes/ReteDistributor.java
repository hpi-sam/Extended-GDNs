package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.ArrayList;
import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteDistributor extends ReteNode {

	protected int currentCalls = 0;
	protected List<ReteNode> pendingSuccessors = new ArrayList<ReteNode>();
	
	@Override
	public void doAddTuple(ReteTuple tuple, ReteNode source) {
		propagateAddition(tuple);
	}

	public void propagateAddition(ReteTuple tuple) {
		currentCalls++;
		for(ReteNode consumer:successors) {
			consumer.addTuple(tuple, this);
		}
		currentCalls--;
		if(currentCalls == 0) {
			addPendingSuccessors();
		}
	}

	@Override
	public void doRemoveTuple(ReteTuple tuple, ReteNode source) {
		propagateRemoval(tuple);
	}
	
	protected void propagateRemoval(ReteTuple tuple) {
		currentCalls++;
		for(ReteNode consumer:successors) {
			consumer.removeTuple(tuple, this);
		}
		currentCalls--;
	}
	
	@Override
	public void addSuccessor(ReteNode successor) {
		if(currentCalls == 0) {
			doAddSuccessor(successor);
		}
		else {
			pendingSuccessors.add(successor);
		}
	}
	
	protected void addPendingSuccessors() {
		for(ReteNode pendingSuccessor:pendingSuccessors) {
			doAddSuccessor(pendingSuccessor);
		}
		pendingSuccessors.clear();
	}

	protected void doAddSuccessor(ReteNode pendingSuccessor) {
		super.addSuccessor(pendingSuccessor);
	}

}
