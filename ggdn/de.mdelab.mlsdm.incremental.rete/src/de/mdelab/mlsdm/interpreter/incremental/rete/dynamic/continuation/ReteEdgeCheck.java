package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation;

import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager.EdgeIndexType;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.FeatureSpecification;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;

public class ReteEdgeCheck extends ReteEdgeContinuation {

	private int[] mask;

	public ReteEdgeCheck(int sourceId, int targetId, double constraintFiltering, int id,
			int[] tableMask, FeatureSpecification feature, ModelIndexManager indexManager, MLSDMReferenceIndex referenceAdapter) {
		super(indexManager.getEdgeIndex(feature, EdgeIndexType.CHECK), sourceId, targetId,
				constraintFiltering, id, tableMask, feature, indexManager, referenceAdapter);
		this.mask = new int[2];
		this.mask[0] = sourceIndex;
		this.mask[1] = targetIndex;
	}
	
	@Override
	public double getContinuationCost(List<Object> tuple) {
		return 0.5;
	}

	@Override
	public int getKeySize() {
		return 2;
	}

	@Override
	public int[] getMask() {
		return mask;
	}

	@Override
	protected void setTableMask(int[] tableMask) {
		tableMask[sourceId] = 0;
		tableMask[targetId] = 1;
	}

}
