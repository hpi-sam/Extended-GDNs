package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMatcher;

public interface LWCostFunctionFactory {

	public LWCostFunction createCostFunction(MLSDMLightweightMatcher matcher);
	
}
