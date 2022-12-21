package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation;

import java.util.List;

import org.eclipse.emf.ecore.EReference;

import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ReteContinuationDiscriminator;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.FeatureSpecification;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;

public abstract class ReteEdgeContinuation extends ReteContinuation {

	protected int sourceIndex;
	protected int targetIndex;
	protected FeatureSpecification feature;
	protected ModelIndexManager indexManager;
	protected int sourceTableCode;
	protected int targetTableCode;
	protected int sourceId;
	protected int targetId;
	protected int[] tableMask;
	protected MLSDMReferenceIndex referenceAdapter;
	
	public ReteEdgeContinuation(ReteIndexer indexer, int sourceId, int targetId, double constraintFiltering, int id,
			int[] tableMask, FeatureSpecification feature, ModelIndexManager indexManager, MLSDMReferenceIndex referenceAdapter) {
		super(indexer, ((1 << sourceId) | (1 << targetId)), constraintFiltering, id);
		this.referenceAdapter = referenceAdapter;
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.sourceTableCode = 1 << sourceId;
		this.targetTableCode = 1 << targetId;
		this.sourceIndex = tableMask[sourceId];
		this.targetIndex = tableMask[targetId];
		this.feature = feature;
		this.indexManager = indexManager;
		this.tableMask = createTableMask(tableMask);
	}

	protected int[] createTableMask(int[] baseMask) {
		int[] mask = new int[baseMask.length];
		for(int i = 0; i < mask.length; i++) {
			mask[i] = ReteContinuationDiscriminator.NO_MAPPING;
		}
		setTableMask(mask);
		return mask;
	}

	protected abstract void setTableMask(int[] mask);
	
	public int[] getTableMask() {
		return tableMask;
	}
	
	public int getSourceIndex() {
		return sourceIndex;
	}
	
	public int getTargetIndex() {
		return targetIndex;
	}
	
	public EReference getFeature() {
		return feature.feature;
	}
	
	public int transformIndex(int index, List<int[]> transformations) {
		int transformed = index;
		for(int[] transformation:transformations) {
			for(int i = 0; i < transformation.length; i++) {
				if(transformation[i] == transformed) {
					transformed = i;
					break;
				}
			}
		}
		return transformed;
	}

	public ReteContinuation deriveContinuation(int newTableCode, int[] tableMapping) {
		if((newTableCode & tableCode) == tableCode) {
			return deriveCheck(tableMapping);
		}
		else if ((newTableCode & sourceTableCode) == sourceTableCode) {
			return deriveTraversal(tableMapping);
		}
		else if((newTableCode & targetTableCode) == targetTableCode) {
			return deriveReversal(tableMapping);
		}
		else {
			return deriveLookup(tableMapping);
		}
	}
	
	protected ReteEdgeContinuation deriveCheck(int[] tableMapping) {
		return new ReteEdgeCheck(sourceId, targetId, constraintFiltering, id,
				tableMapping, feature, indexManager, referenceAdapter);
	}

	protected ReteEdgeContinuation deriveTraversal(int[] tableMapping) {
		return new ReteEdgeTraversal(sourceId, targetId, constraintFiltering, id,
				tableMapping, feature, indexManager, referenceAdapter);
	}

	protected ReteEdgeContinuation deriveReversal(int[] tableMapping) {
		return new ReteEdgeReversal(sourceId, targetId, constraintFiltering, id,
				tableMapping, feature, indexManager, referenceAdapter);
	}
	
	protected ReteEdgeContinuation deriveLookup(int[] tableMapping) {
		return new ReteEdgeLookup(sourceId, targetId, constraintFiltering, id,
				tableMapping, feature, indexManager, referenceAdapter);
	}

	@Override
	public int getTableMappingIntoIndexer(int nodeId) {
		if(nodeId == this.sourceId) {
			return 0;
		}
		else if(nodeId == this.targetId) {
			return 1;
		}
		else {
			return ReteContinuationDiscriminator.NO_MAPPING;
		}
	}

}
