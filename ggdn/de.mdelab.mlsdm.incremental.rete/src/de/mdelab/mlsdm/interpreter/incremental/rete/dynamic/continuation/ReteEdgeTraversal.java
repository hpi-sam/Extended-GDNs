package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager.EdgeIndexType;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.FeatureSpecification;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;

public class ReteEdgeTraversal extends ReteEdgeContinuation {

	private int[] mask;

	public ReteEdgeTraversal(int sourceId, int targetId, double constraintFiltering, int id,
			int[] tableMask, FeatureSpecification feature, ModelIndexManager indexManager, MLSDMReferenceIndex referenceAdapter) {
		super(indexManager.getEdgeIndex(feature, EdgeIndexType.TRAVERSE), sourceId, targetId, constraintFiltering, id, tableMask, feature, indexManager, referenceAdapter);
		this.mask = new int[1];
		this.mask[0] = sourceIndex;
	}
	
	@Override
	public double getContinuationCost(List<Object> tuple) {
		EObject source = (EObject) tuple.get(sourceIndex);
		if(feature.feature.isMany()) {
			return ((Collection<?>) source.eGet(feature.feature)).size();
		}
		else {
			return source.eGet(feature.feature) != null ? 1 : 0;
		}
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
		tableMask[sourceIndex] = 0;
	}

}
