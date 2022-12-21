package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMA;

public interface LWCostFunction {

	public double computeCost(MLSDMLightweightMA activeAction);

}
