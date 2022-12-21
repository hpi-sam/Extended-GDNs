package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation;

import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager.EdgeIndexType;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.FeatureSpecification;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;

public class ReteEdgeLookup extends ReteEdgeContinuation {

	private int mask[];

	public ReteEdgeLookup(int sourceId, int targetId, double constraintFiltering, int id,
			int[] tableMask, FeatureSpecification feature, ModelIndexManager indexManager, MLSDMReferenceIndex referenceAdapter) {
		super(indexManager.getEdgeIndex(feature, EdgeIndexType.LOOKUP), sourceId, targetId, constraintFiltering, id, tableMask, feature, indexManager, referenceAdapter);
		this.mask = new int[0];
	}
	
	@Override
	public double getContinuationCost(List<Object> tuple) {
		return referenceAdapter.getReferences(feature.feature).size();
	}

	@Override
	public int getKeySize() {
		return 0;
	}

	@Override
	public int[] getMask() {
		return mask;
	}

	@Override
	protected void setTableMask(int[] tableMask) {
		
	}

}
