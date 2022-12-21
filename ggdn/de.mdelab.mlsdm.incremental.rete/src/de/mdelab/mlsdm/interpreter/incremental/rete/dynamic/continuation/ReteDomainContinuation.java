package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation;

import org.eclipse.emf.ecore.EClass;

import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ReteContinuationDiscriminator;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteDomainFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteImmutableFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;

public abstract class ReteDomainContinuation extends ReteImmutablePropertyContinuation {

	protected final int nodeIndex;
	protected final int[] mask;
	protected final int[] tableMask;
	protected final int nodeId;
	protected final EClass classifier;
	protected ModelIndexManager indexManager;
	protected MLSDMReferenceIndex referenceAdapter;
	
	public ReteDomainContinuation(ReteIndexer indexer,
			double constraintFiltering, int id, int[] tableMask,
			int nodeId, EClass classifier, ModelIndexManager indexManager,
			MLSDMReferenceIndex referenceAdapter) {
		super(indexer, (1 << nodeId), constraintFiltering, id);
		this.nodeId = nodeId;
		this.nodeIndex = tableMask[nodeId];
		this.mask = createMask();
		this.tableMask = createTableMask(tableMask);
		this.classifier = classifier;
		this.indexManager = indexManager;
		this.referenceAdapter = referenceAdapter;
	}

	protected abstract int[] createMask();

	@Override
	public int[] getMask() {
		return mask;
	}

	@Override
	public ReteContinuation deriveContinuation(int newTableCode, int[] tableMapping) {
		if((newTableCode & tableCode) == tableCode) {
			return deriveCheck(tableMapping);
		}
		else {
			return deriveLookup(tableMapping);
		}
	}
	
	protected ReteContinuation deriveCheck(int[] tableMask) {
		return new ReteDomainCheck(constraintFiltering, id,
				tableMask, nodeId, classifier, indexManager, referenceAdapter);
	}

	protected ReteContinuation deriveLookup(int[] tableMapping) {
		return new ReteDomainLookup(constraintFiltering, id,
				tableMask, nodeId, classifier, indexManager, referenceAdapter);
	}

	@Override
	public int[] getTableMask() {
		return tableMask;
	}

	protected int[] createTableMask(int[] baseMask) {
		int[] mask = new int[baseMask.length];
		for(int i = 0; i < mask.length; i++) {
			mask[i] = ReteContinuationDiscriminator.NO_MAPPING;
		}
		setTableMask(mask);
		return mask;
	}

	protected abstract void setTableMask(int[] tableMask);

	@Override
	public int getTableMappingIntoIndexer(int nodeId) {
		return nodeId == this.nodeId ? 0 : ReteContinuationDiscriminator.NO_MAPPING;
	}

	@Override
	public ReteImmutableFilter createFilterNode(int[] newTableMask) {
		return new ReteDomainFilter(classifier, newTableMask[nodeId]);
	}

}
