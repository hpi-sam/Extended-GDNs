package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation;

import java.util.List;

import org.eclipse.emf.ecore.EClass;

import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager.NodeIndexType;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;

public class ReteDomainCheck extends ReteDomainContinuation {

	public ReteDomainCheck(double constraintFiltering, int id, int[] tableMask, int nodeId,
			EClass classifier, ModelIndexManager indexManager, MLSDMReferenceIndex referenceAdapter) {
		super(indexManager.getNodeIndex(classifier, NodeIndexType.CHECK), constraintFiltering, id, tableMask, nodeId, classifier,
				indexManager, referenceAdapter);
	}

	@Override
	public double getContinuationCost(List<Object> tuple) {
		return 0.5;
	}

	@Override
	public int getKeySize() {
		return 1;
	}

	@Override
	protected void setTableMask(int[] tableMask) {
		tableMask[nodeId] = 0;
	}

	@Override
	protected int[] createMask() {
		int[] mask = new int[1];
		mask[0] = nodeIndex;
		return mask;
	}

}
