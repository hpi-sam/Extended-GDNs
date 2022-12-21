package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.List;

import de.mdelab.mlsdm.incremental.util.CompositeList;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteJoinExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteNodeExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteJoin extends ReteTupleGenerator  {
	
	public enum ReteJoinSideEnum {LEFT, RIGHT};
	
	protected final ReteIndexer leftIndexer;
	protected final ReteIndexer rightIndexer;
	protected final int joinSize;
	
	public ReteJoin(ReteIndexer leftIndexer, ReteIndexer rightIndexer, int joinSize) {
		this.leftIndexer = leftIndexer;
		this.rightIndexer = rightIndexer;
		this.joinSize = joinSize;
		
		this.leftIndexer.addSuccessor(this);
		this.rightIndexer.addSuccessor(this);
	}
	
	@Override
	public void doAddTuple(ReteTuple tuple, ReteNode source) {
		List<Object> key = tuple.subList(0, joinSize);
		if(source == leftIndexer) {
			for(ReteTuple opposite:rightIndexer.getTuples(key)) {
				ReteTuple join = constructJoin(tuple, opposite);
				propagateAddition(join);
			}
		}
		else {
			for(ReteTuple opposite:leftIndexer.getTuples(key)) {
				ReteTuple join = constructJoin(opposite, tuple);
				propagateAddition(join);
			}
		}
	}

	public ReteTuple constructJoin(ReteTuple left, ReteTuple right) {
		ReteTuple join = new ReteTuple(new CompositeList<Object>(left, right.subList(joinSize, right.size())));
		join.setIsPreliminary(left.isPreliminary() || right.isPreliminary());
		return join;
	}

	@Override
	public void doRemoveTuple(ReteTuple tuple, ReteNode source) {
		List<Object> key = tuple.subList(0, joinSize);
		if(source == leftIndexer) {
			for(ReteTuple opposite:rightIndexer.getTuples(key)) {
				ReteTuple join = constructJoin(tuple, opposite);
				propagateRemoval(join);
			}
		}
		else {
			for(ReteTuple opposite:leftIndexer.getTuples(key)) {
				ReteTuple join = constructJoin(opposite, tuple);
				propagateRemoval(join);
			}
		}
	}

	@Override
	public ReteNodeExecutor createExecutor() {
		ReteIndexer enumeratedIndexer = leftIndexer.size() <= rightIndexer.size() ? leftIndexer : rightIndexer;
		ReteJoinSideEnum enumeratedSide;
		ReteIndexer joinIndexer;
		if(enumeratedIndexer == leftIndexer) {
			enumeratedSide = ReteJoinSideEnum.LEFT;
			joinIndexer = rightIndexer;
		}
		else {
			enumeratedSide = ReteJoinSideEnum.RIGHT;
			joinIndexer = leftIndexer;
		}
		return new ReteJoinExecutor(this, enumeratedIndexer.getAllTuples().entrySet().iterator(), enumeratedSide, joinIndexer);
	}

	public ReteIndexer getLeftIndexer() {
		return leftIndexer;
	}

	public ReteIndexer getRightIndexer() {
		return rightIndexer;
	}

	@Override
	public boolean isJoin() {
		return true;
	}

	public boolean isTupleGenerator() {
		return true;
	}

}
