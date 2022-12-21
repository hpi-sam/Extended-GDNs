package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation;

import java.util.List;

import org.eclipse.emf.ecore.EClass;

import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager.NodeIndexType;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;

public class ReteDomainLookup extends ReteDomainContinuation {

	public ReteDomainLookup(double constraintFiltering, int id, int[] tableMask, int nodeId,
			EClass classifier, ModelIndexManager indexManager, MLSDMReferenceIndex referenceAdapter) {
		super(indexManager.getNodeIndex(classifier, NodeIndexType.LOOKUP), constraintFiltering, id, tableMask, nodeId, classifier,
				indexManager, referenceAdapter);
	}

	@Override
	public double getContinuationCost(List<Object> tuple) {
		return referenceAdapter.getDomain(classifier).size();
	}

	@Override
	public int getKeySize() {
		return 0;
	}

	@Override
	protected void setTableMask(int[] tableMask) {
		tableMask[nodeId] = 0;
	}

	@Override
	protected int[] createMask() {
		int[] mask = new int[0];
		return mask;
	}

}
