package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.incremental.rete.diameter.PatternPartition;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteTableFitter;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Node;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.interpreter.core.SDMException;

public class CostAwareDynamicQueryManager extends DynamicReteQueryManager {

	protected double recomputeCostFactor;

	public CostAwareDynamicQueryManager(StoryPattern storyPattern, EObject... eObjects) throws SDMException {
		this(storyPattern, 1, eObjects);
	}

	public CostAwareDynamicQueryManager(StoryPattern storyPattern, double recomputeCostFactor, EObject... eObjects) throws SDMException {
		super(storyPattern, eObjects);
		this.recomputeCostFactor = recomputeCostFactor;
	}

	@Override
	protected boolean improvesCurrentPartitioning(
			Node<PatternPartition, Boolean> newPartitioning) {
		double costOld = currentPartitioning != null ? computePartitioningCost(currentPartitioning) : Double.MAX_VALUE;
		double costNew = computePartitioningCost(newPartitioning);
		return costNew * recomputeCostFactor < costOld;
	}

	protected boolean recomputeDynamicNodes() {
		for(ReteNode node:currentNet.getNodes().values()) {
			if(node instanceof ReteTableFitter) {
				ReteTableFitter tableFitter = (ReteTableFitter) node;
				if(tableFitter.getTreeSize() > tableFitter.computeExpectedTreeSize() * recomputeCostFactor ) {
					return true;
				}
			}
		}
		return false;
	}

}
