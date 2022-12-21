package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMA;

public class LWDynamicCostFunction implements LWCostFunction {

	@Override
	public double computeCost(MLSDMLightweightMA activeAction) {
		return activeAction.getCardinality();
	}

}
