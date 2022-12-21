package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ReteContinuationDiscriminator;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.FeatureSpecification;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager.EdgeIndexType;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;

public class ReteEdgeReversal extends ReteEdgeContinuation {

	private int[] mask;

	public ReteEdgeReversal(int sourceId, int targetId, double constraintFiltering, int id,
			int[] tableMask, FeatureSpecification feature, ModelIndexManager indexManager, MLSDMReferenceIndex referenceAdapter) {
		super(indexManager.getEdgeIndex(feature, EdgeIndexType.REVERSE), sourceId, targetId, constraintFiltering, id, tableMask, feature, indexManager, referenceAdapter);
		this.mask = new int[1];
		this.mask[0] = targetIndex;
	}
	
	@Override
	public double getContinuationCost(List<Object> tuple) {
		return referenceAdapter.getInverseReferences((EObject) tuple.get(targetIndex), feature.feature).size();
	}

	@Override
	public int getKeySize() {
		return 1;
	}

	@Override
	public int[] getMask() {
		return mask;
	}

	@Override
	protected void setTableMask(int[] tableMask) {
		tableMask[targetId] = 0;
	}

	@Override
	public int getTableMappingIntoIndexer(int nodeId) {
		if(nodeId == this.sourceId) {
			return 1;
		}
		else if(nodeId == this.targetId) {
			return 0;
		}
		else {
			return ReteContinuationDiscriminator.NO_MAPPING;
		}
	}

}
