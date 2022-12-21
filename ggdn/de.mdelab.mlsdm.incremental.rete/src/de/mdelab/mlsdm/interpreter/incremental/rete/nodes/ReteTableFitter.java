package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.Arrays;
import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNotificationReceiver;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNotifier;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.hybrid.MLSDMContinuationCostCalculator;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.hybrid.MLSDMContinuationCostCalculator.Adornment;

public class ReteTableFitter extends ReteDistributor implements ReteNotificationReceiver {

	private long treeSize;
	private MLSDMContinuationCostCalculator costCalculator;
	
	public ReteTableFitter(MLSDMContinuationCostCalculator costCalculator) {
		this.costCalculator = costCalculator;
		this.treeSize = 0;
	}
	
	@Override
	public void doAddTuple(ReteTuple tuple, ReteNode source) {
		ReteTuple child = fitToMask(tuple, source.getTableMask());
		child.setIsPreliminary(tuple.isPreliminary());
		super.doAddTuple(child, this);
	}

	@Override
	public void doRemoveTuple(ReteTuple tuple, ReteNode source) {
		super.doRemoveTuple(fitToMask(tuple, source.getTableMask()), this);
	}
	
	protected ReteTuple fitToMask(List<Object> tuple, int[] mask) {
		Object[] newTuple = new Object[mask.length];
		for(int i = 0; i < mask.length; i++) {
			newTuple[i] = tuple.get(mask[i]);
		}
		return new ReteTuple(Arrays.asList(newTuple));
	}
	
	@Override
	public void notifyAddition(ReteTuple tuple, ReteNotifier notifier) {
		treeSize++;
	}
	
	@Override
	public void notifyRemoval(ReteTuple tuple, ReteNotifier notifier) {
		treeSize--;
	}

	public long getTreeSize() {
		return treeSize;
	}

	public long computeExpectedTreeSize() {
		Adornment[] costs = costCalculator.calculateCotinuationCosts();
		return (long) costs[0].totalCost;
	}

}
