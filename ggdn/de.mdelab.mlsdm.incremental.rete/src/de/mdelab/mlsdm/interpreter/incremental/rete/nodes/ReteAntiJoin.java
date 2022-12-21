package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteAntiJoinExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteNodeExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteAntiJoin extends ReteJoin implements ReteConstraintCheck {
	
	private Object nac;

	public ReteAntiJoin(ReteIndexer leftIndexer, ReteIndexer rightIndexer, int joinSize) {
		this(leftIndexer, rightIndexer, joinSize, null);
	}

	public ReteAntiJoin(ReteIndexer leftIndexer, ReteIndexer rightIndexer, int joinSize, Object nac) {
		super(leftIndexer, rightIndexer, joinSize);
		this.nac = nac;
	}
	
	@Override
	public void doAddTuple(ReteTuple tuple, ReteNode source) {
		List<Object> key = tuple.subList(0, joinSize);
		if(source == leftIndexer) {
			if(rightIndexer.getTuples(key).isEmpty()) {
				ReteTuple join = new ReteTuple(tuple.getTupleValue());
				propagateAddition(join);
			}
		}
		else {
			if(rightIndexer.getTuples(key).size() < 2) {
				for(ReteTuple opposite:leftIndexer.getTuples(key)) {
					propagateRemoval(opposite);
				}
			}
		}
	}

	@Override
	public void doRemoveTuple(ReteTuple tuple, ReteNode source) {
		List<Object> key = tuple.subList(0, joinSize);
		if(source == leftIndexer) {
			if(rightIndexer.getTuples(key).isEmpty()) {
				propagateRemoval(tuple);
			}
		}
		else {
			if(rightIndexer.getTuples(key).size() < 1) {
				for(ReteTuple opposite:leftIndexer.getTuples(key)) {
					ReteTuple join = new ReteTuple(opposite.getTupleValue());
					propagateAddition(join);
				}
			}
		}
	}
	
	@Override
	public ReteNodeExecutor createExecutor() {
		ReteIndexer enumeratedIndexer = leftIndexer;
		ReteIndexer joinIndexer = rightIndexer;
		return new ReteAntiJoinExecutor(this, enumeratedIndexer.getAllTuples().entrySet().iterator(), joinIndexer);
	}

	@Override
	public boolean isAntiJoin() {
		return true;
	}

	@Override
	public Object getConstraint() {
		return nac;
	}
	
	@Override
	public double getConstraintFiltering() {
		ReteIndexer successor = (ReteIndexer) getSuccessors().get(0);
		ReteIndexer leftPredecessor = leftIndexer;
		return leftPredecessor.size() != 0 ? (double) successor.size()/(double) leftPredecessor.size() : 1;
	}
}
