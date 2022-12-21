package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMMetadataIndex;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPC;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightSM;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost.LWContinuationCalculator.Adornment;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost.LWContinuationCalculator.CostModel;

public class LWHybridCostFunction implements LWCostFunction {

	protected Adornment[] continuationTable;
	protected LWContinuationCalculator costCalculator;
	protected MLSDMLightweightSM sm;

	public LWHybridCostFunction(MLSDMLightweightSM sm, MLSDMMetadataIndex adapter) {
		this.sm = sm;
		this.costCalculator = new LWContinuationCalculator(sm, adapter, CostModel.AVERAGE_CASE);
		this.continuationTable = costCalculator.calculateCotinuationCosts();
	}

	public void setCostCalculator(LWContinuationCalculator costCalculator) {
		this.costCalculator = costCalculator;
	}
	
	public void setContinuationTable(Adornment[] continuationTable) {
		this.continuationTable = continuationTable;
	}
	
	public void setSearchModel(MLSDMLightweightSM sm) {
		this.sm = sm;
	}
	
	@Override
	public double computeCost(MLSDMLightweightMA ma) {
		if(ma.getCardinality() == MLSDMLightweightPC.CHECK_ONLY) {
			return MLSDMLightweightPC.CHECK_ONLY;
		}
		
		int oldBindingCode = sm.getBindingCode();
		int newBindingCode = oldBindingCode;
		int newBindings = 0;
		for(MLSDMLightweightPN pn:ma.getPatternConstraint().getDependencies()) {
			if(!pn.isBound()) {
				newBindingCode = newBindingCode | (1 << pn.getId());
				newBindings++;
			}
		}
		
		double maCardinality = ma.getCardinality();
		double continuationFiltering = continuationTable[oldBindingCode].appliedFilters;
		double currentFiltering = continuationTable[newBindingCode].appliedFilters;
		double constraintFiltering = costCalculator.getFiltering(ma.getPatternConstraint().getExplicitCheckAction());
		double filtering = constraintFiltering == 0 || continuationFiltering == 0 ? 0 : currentFiltering / (constraintFiltering * continuationFiltering);
		return 	(maCardinality * newBindings) +
				(maCardinality * filtering * continuationTable[newBindingCode].totalCost);
	}
	
}
