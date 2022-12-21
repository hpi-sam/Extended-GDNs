package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.ArrayList;
import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNet;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNotifier;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public abstract class ReteNode extends ReteNotifier {

	protected List<ReteNode> successors = new ArrayList<ReteNode>();
	protected List<ReteNode> predecessors = new ArrayList<ReteNode>();
	protected ReteNet net = new ReteNet();
	
	private int[] tableMask = null;
	
	public void addSuccessor(ReteNode successor) {
		this.successors.add(successor);
		successor.predecessors.add(this);
	}

	public void removeSuccessor(ReteNode successor) {
		this.successors.remove(successor);
		successor.predecessors.remove(this);
	}

	public void clearSuccessors() {
		for(ReteNode successor:new ArrayList<ReteNode>(successors)) {
			removeSuccessor(successor);
		}
	}
	
	public void addPredecessor(ReteNode predecessor) {
		this.predecessors.add(predecessor);
		predecessor.successors.add(this);
	}

	public void removePredecessor(ReteNode predecessor) {
		this.predecessors.remove(predecessor);
		predecessor.successors.remove(this);
	}

	public void clearPredecessors() {
		for(ReteNode predecessor:new ArrayList<ReteNode>(predecessors)) {
			removePredecessor(predecessor);
		}
	}
	
	public List<ReteNode> getSuccessors() {
		return successors;
	}

	public List<ReteNode> getPredecessors() {
		return predecessors;
	}

	public void addTuple(ReteTuple tuple, ReteNode source) {
		doAddTuple(tuple, source);
	}
	
	public void removeTuple(ReteTuple tuple, ReteNode source) {
		doRemoveTuple(tuple, source);
	}
	
	protected abstract void doAddTuple(ReteTuple tuple, ReteNode source);
	
	protected abstract void doRemoveTuple(ReteTuple tuple, ReteNode source);
	
	public int[] getTableMask() {
		return this.tableMask ;
	}

	public void setTableMask(int[] mask) {
		this.tableMask = mask;
	}

	public void setNet(ReteNet net) {
		this.net = net;
	}

	public ReteNet getNet() {
		return this.net;
	}

	public boolean isReteNode() {
		return true;
	}

	public boolean isIndexer() {
		return false;
	}

	public boolean isBlocker() {
		return false;
	}

	public boolean isInput() {
		return false;
	}

	public boolean isJoin() {
		return false;
	}

	public boolean isAntiJoin() {
		return false;
	}

	public boolean isTupleGenerator() {
		return false;
	}

	public boolean isFeatureSubscriber() {
		return false;
	}
 
	protected boolean defaultValueAccessed() {
		return false;
	}

}
