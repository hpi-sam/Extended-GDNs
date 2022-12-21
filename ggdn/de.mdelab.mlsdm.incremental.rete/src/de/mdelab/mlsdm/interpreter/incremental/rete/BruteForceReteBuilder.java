package de.mdelab.mlsdm.interpreter.incremental.rete;

import java.util.ArrayList;
import java.util.Collection;

import de.mdelab.mlsdm.interpreter.incremental.rete.diameter.DiameterBasedReteBuilder;
import de.mdelab.mlsdm.interpreter.incremental.rete.diameter.PatternPartition;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.DoublyWeightedEdge;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Edge;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Node;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Tuple;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.sdm.interpreter.core.SDMException;

public class BruteForceReteBuilder extends DiameterBasedReteBuilder {

	public BruteForceReteBuilder(StoryPattern pattern,
			ReteQueryManager queryManager) throws SDMException {
		this(pattern, queryManager.getModelIndex());
	}

	public BruteForceReteBuilder(StoryPattern pattern,
			StoryPatternModelIndex modelIndex) throws SDMException {
		super(pattern, modelIndex);
	}

	public Node<PatternPartition, Boolean> computePartitioning(
			StoryPattern storyPattern, StoryPatternModelIndex modelIndex) throws SDMException {
		this.modelIndex = modelIndex;
		prepareSearchModel(storyPattern);
		
		PatternPartition initialGraph = createWeightedGraph(storyPattern);
		
		Node<PatternPartition, Boolean> root = partitionGraph(initialGraph);

		enrichGraphWithConstraints(root, storyPattern);
		
		return root;
	}

	private Node<PatternPartition, Boolean> partitionGraph(
			PatternPartition partition) {
		if(isTerminal(partition)) {
			partition.cost = computePartitionCost(partition) * computePartitionFiltering(partition);
			Node<PatternPartition, Boolean> root = new Node<PatternPartition, Boolean>(-1, partition);
			return root;
		}
		
		double minCost = Double.MAX_VALUE;
		Tuple<Node<PatternPartition, Boolean>, Node<PatternPartition, Boolean>> bestPartitioning = null;
		
		for(int selectedEdgeNumber = 1; selectedEdgeNumber < partition.edges.size(); selectedEdgeNumber++) {
			int[] selectedIndices = new int[selectedEdgeNumber];
			for(int i = 0; i < selectedIndices.length; i++) {
				selectedIndices[i] = i;
			}
			
			while(selectedIndices[0] <= partition.edges.size() - selectedEdgeNumber) {
				Collection<DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink>> selection =
						new ArrayList<DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink>>();
				for(int i = 0; i < selectedIndices.length; i++) {
					selection.add(partition.edges.get(selectedIndices[i]));
				}

				PatternPartition p1 = partition.shallowCopy();
				PatternPartition p2 = new PatternPartition();
				moveEdges(p1, p2, selection, partition.allConstraints);
				
				Node<PatternPartition, Boolean> p1Node = partitionGraph(p1);
				Node<PatternPartition, Boolean> p2Node = partitionGraph(p2);
				
				double cost = p1Node.value.cost + p2Node.value.cost;
				
				if(cost < minCost) {
					bestPartitioning = new Tuple<Node<PatternPartition, Boolean>, Node<PatternPartition, Boolean>>(p1Node, p2Node);
					minCost = cost;
				}
						
				int currentLevel = selectedEdgeNumber - 1;
				selectedIndices[currentLevel]++;
				while(currentLevel > 0 && selectedIndices[currentLevel] > partition.edges.size() - (selectedEdgeNumber - currentLevel)) {
					currentLevel--;
					selectedIndices[currentLevel]++;
				}
				currentLevel++;
				while(currentLevel < selectedEdgeNumber) {
					selectedIndices[currentLevel] = selectedIndices[currentLevel - 1] + 1;
					currentLevel++;
				}
			}
		}
		
		double overallCost = minCost + computePartitionCost(partition) * computePartitionFiltering(partition);
		partition.cost = overallCost;
		
		Node<PatternPartition, Boolean> root = new Node<PatternPartition, Boolean>(-1, partition);

		Edge<PatternPartition, Boolean> e1 = new Edge<PatternPartition, Boolean>(root, bestPartitioning.e1, true);
		root.out.add(e1);
		bestPartitioning.e1.in.add(e1);
		
		Edge<PatternPartition, Boolean> e2 = new Edge<PatternPartition, Boolean>(root, bestPartitioning.e2, true);
		root.out.add(e2);
		bestPartitioning.e2.in.add(e2);
		
		return root;
	}

}
