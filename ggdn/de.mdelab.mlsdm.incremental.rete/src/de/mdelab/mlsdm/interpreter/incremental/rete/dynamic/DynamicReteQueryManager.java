package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import de.mdelab.mlsdm.interpreter.incremental.rete.StoryPatternModelIndex;
import de.mdelab.mlsdm.incremental.ChangeListener;
import de.mdelab.mlsdm.incremental.QueryManagerNotificationReceiver;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMEdgeChange;
import de.mdelab.mlsdm.incremental.change.SDMNodeChange;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent.SDMChangeEnum;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNet;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNotificationReceiver;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNotifier;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteQueryManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.diameter.PatternPartition;
import de.mdelab.mlsdm.interpreter.incremental.rete.diameter.DiameterBasedReteBuilder;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteBlocker;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteConstraintCheck;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteEdgeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNodeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteResultProvider;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteTupleGenerator;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteNodeExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Edge;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.FeatureSpecification;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Node;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteUtil;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Tuple;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternElement;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.sdm.interpreter.core.SDMException;

public abstract class DynamicReteQueryManager extends ReteQueryManager implements ReteNotificationReceiver {

	protected StoryPattern storyPattern;
	protected ReteResultProvider resultProvider;
	protected DiameterBasedReteBuilder reteBuilder;
	protected Node<PatternPartition, Boolean> currentPartitioning;
	protected ReteNet currentNet;
	private long currentNetSize;
	protected StoryPatternModelIndex repopulateIndex;
	
	public long totalChanges;
	
	/*
	 * information required for reverting to old net if recomputation
	 * exceeds tuple limit
	*/
	protected ReteNet oldNet;
	protected long oldNetSize;
	protected Node<PatternPartition, Boolean> oldPartitioning;
	protected Map<ReteIndexer, Tuple<Collection<ReteNode>, Collection<ReteNode>>> reusedOldNodes;
	protected Map<ReteIndexer, int[]> oldMasks;

	public DynamicReteQueryManager(StoryPattern storyPattern, EObject... eObjects) throws SDMException {
		this.storyPattern = storyPattern;
		this.repopulateIndex = new StoryPatternModelIndex();
		observePatternStatistics(storyPattern);
		
		for(EObject eObject:eObjects) {
			registerEObject(eObject);
		}
		
		this.resultProvider = new ReteResultProvider();
		this.reteBuilder = createReteBuilder();
		initializeReteNet();
	}

	@Override
	public void observePatternStatistics(StoryPattern sp) {
		super.observePatternStatistics(sp);
		repopulateIndex.observePatternStatistics(sp);
	}

	protected DiameterBasedReteBuilder createReteBuilder() throws SDMException {
		return new DiameterBasedReteBuilder(storyPattern, this.modelIndex);
	}

	protected void initializeReteNet() {
		currentNetSize = 0;
		computeNewNetStructure();
		populateCurrentNet();
	}

	private void populateCurrentNet() {
		for(Entry<EClassifier, Collection<EObject>> entry:getDomains().entrySet()) {
			for(EObject eObject:entry.getValue()) {
				notifyListeners(new SDMNodeChange(eObject, SDMChangeEnum.CREATE));
			}
		}
		for(Entry<FeatureSpecification, Collection<Tuple<EObject, EObject>>> reference:getReferences().entrySet()) {
			for(Tuple<EObject, EObject> edge:reference.getValue()) {
				notifyListeners(new SDMEdgeChange(edge.e1, (EReference) reference.getKey().feature, edge.e2, SDMChangeEnum.CREATE));
			}
		}
	}

	public ReteResultProvider getResultProvider() {
		return resultProvider;
	}
	
	public ReteNet getCurrentNet() {
		return currentNet;
	}

	public Node<PatternPartition, Boolean> getCurrentPartitioning() {
		return currentPartitioning;
	}
	
	public long computeCurrentNetSize() {
		long size = 0;
		for(ReteNode node:currentNet.getNodes().values()) {
			if(node instanceof ReteIndexer) {
				size += ((ReteIndexer) node).size();
			}
		}
		return size;
	}

	public boolean computeNewNetStructure() {
		try {
			if(currentNet != null) {
				updateConstraintFilteringRates();
			}
			Node<PatternPartition, Boolean> newPartitioning = reteBuilder.computePartitioning();
			boolean recompute = improvesCurrentPartitioning(newPartitioning) || recomputeDynamicNodes();
			if(recompute) {
				prepareRecompute();
				clearChangeListeners();
				
				Map<Tuple<Node<PatternPartition, Boolean>, Node<PatternPartition, Boolean>>, ReteIndexer> reuseableIndexers = findReuseableIndexers(newPartitioning, currentPartitioning);
				conserveCurrentNet(disconnectNodes(reuseableIndexers.values()));
				resultProvider.clearPredecessors();
				
				currentPartitioning = newPartitioning;
				ReteNet newNet = reteBuilder.buildReteNet(currentPartitioning, reuseableIndexers).e1;
				ReteNode newRoot = newNet.getRoot();
				newRoot.addSuccessor(resultProvider);
				resultProvider.setTableMask(newRoot.getTableMask());
				
				DynamicReteNet dynamicNet = new DynamicReteNet(this);
				dynamicNet.addNodes(newNet.getNodes());
				dynamicNet.addNode(resultProvider);
				dynamicNet.setRoot(resultProvider);
				currentNet = dynamicNet;
			}
			return recompute;
		} catch (SDMException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void updateConstraintFilteringRates() {
		for(ReteNode node:currentNet.getNodes().values()) {
			if(node instanceof ReteConstraintCheck) {
				Object constraint = ((ReteConstraintCheck) node).getConstraint();
				double filteringRate = ((ReteConstraintCheck) node).getConstraintFiltering();
				reteBuilder.setFiltering(constraint, filteringRate);
			}
		}
	}

	private void conserveCurrentNet(
			Map<ReteIndexer, Tuple<Collection<ReteNode>, Collection<ReteNode>>> disconnectedNodes) {
		oldNet = currentNet;
		oldNetSize = currentNetSize;
		oldPartitioning = currentPartitioning;

		oldMasks = new LinkedHashMap<ReteIndexer, int[]>();
		for(ReteIndexer indexer:disconnectedNodes.keySet()) {
			oldMasks.put(indexer, indexer.getMask());
		}
		
		reusedOldNodes = disconnectedNodes;
		reusedOldNodes.put(resultProvider, new Tuple<Collection<ReteNode>, Collection<ReteNode>>(new ArrayList<ReteNode>(resultProvider.getPredecessors()), Collections.emptyList()));
	}

	private Map<ReteIndexer, Tuple<Collection<ReteNode>, Collection<ReteNode>>> disconnectNodes(Collection<ReteIndexer> nodes) {
		Map<ReteIndexer, Tuple<Collection<ReteNode>, Collection<ReteNode>>> connections = new LinkedHashMap<ReteIndexer, Tuple<Collection<ReteNode>, Collection<ReteNode>>>();
		for(ReteIndexer node:nodes) {
			Collection<ReteNode> predecessors = new ArrayList<ReteNode>(node.getPredecessors());
			Collection<ReteNode> successors = new ArrayList<ReteNode>(node.getSuccessors());
			connections.put(node, new Tuple<Collection<ReteNode>, Collection<ReteNode>>(predecessors, successors));
			
			node.clearPredecessors();
			node.clearSuccessors();
		}
		return connections;
	}

	private Map<Tuple<Node<PatternPartition, Boolean>, Node<PatternPartition, Boolean>>, ReteIndexer> findReuseableIndexers(
			Node<PatternPartition, Boolean> partitioning1,
			Node<PatternPartition, Boolean> partitioning2) {
		Map<Tuple<Node<PatternPartition, Boolean>, Node<PatternPartition, Boolean>>, ReteIndexer> reuseableNodes =
				new LinkedHashMap<Tuple<Node<PatternPartition, Boolean>, Node<PatternPartition, Boolean>>, ReteIndexer>();
		if(partitioning1 == null || partitioning2 == null) {
			return reuseableNodes;
		}
		
		Set<StoryPatternElement> elements1 = getStoryPatternElements(partitioning1.value);
		Set<StoryPatternElement> elements2 = getStoryPatternElements(partitioning2.value);
		
		if(elements1.equals(elements2)) {
			//partitions are isomorph, try to find isomorphic children
			for(Edge<PatternPartition, Boolean> out1:partitioning1.out) {
				Node<PatternPartition, Boolean> child1 = out1.target;
				Set<StoryPatternElement> elementsChild1 = getStoryPatternElements(child1.value);
				
				for(Edge<PatternPartition, Boolean> out2:partitioning2.out) {
					Node<PatternPartition, Boolean> child2 = out2.target;
					Set<StoryPatternElement> elementsChild2 = getStoryPatternElements(child2.value);
					if(elementsChild1.equals(elementsChild2)) {
						ReteNode parentNode = currentNet.getNode(partitioning2);
						ReteNode childNode = currentNet.getNode(child2);
						ReteIndexer indexer = findIndexerBetween(childNode, parentNode);
						reuseableNodes.put(new Tuple<Node<PatternPartition, Boolean>, Node<PatternPartition, Boolean>>(partitioning1, child1),
											indexer);
					}
				}
			}
		}
		
		if(!intersect(elements1, elements2).isEmpty()){
			
			//find immediate reuseable indexers
			Set<AbstractStoryPatternObject> joinNodes1 = getJoinNodes(partitioning1);
			Set<AbstractStoryPatternObject> joinNodes2 = getJoinNodes(partitioning2);
			if(joinNodes1.equals(joinNodes2)) {
				for(Edge<PatternPartition, Boolean> out1:partitioning1.out) {
					Node<PatternPartition, Boolean> child1 = out1.target;
					Set<StoryPatternElement> elementsChild1 = getStoryPatternElements(child1.value);
					
					for(Edge<PatternPartition, Boolean> out2:partitioning2.out) {
						Node<PatternPartition, Boolean> child2 = out2.target;
						Set<StoryPatternElement> elementsChild2 = getStoryPatternElements(child2.value);
						if(elementsChild1.equals(elementsChild2)) {
							ReteNode parentNode = currentNet.getNode(partitioning2);
							ReteNode childNode = currentNet.getNode(child2);
							ReteIndexer indexer = findIndexerBetween(childNode, parentNode);
							reuseableNodes.put(new Tuple<Node<PatternPartition, Boolean>, Node<PatternPartition, Boolean>>(partitioning1, child1),
												indexer);
						}
					}
				}
			}
			
			//recursively find reuseable indexers
			if(elements1.size() < elements2.size()) {
				for(Edge<PatternPartition, Boolean> out2:partitioning2.out) {
					reuseableNodes.putAll(findReuseableIndexers(partitioning1, out2.target));
				}
			}
			else if(elements1.size() > elements2.size()) {
				for(Edge<PatternPartition, Boolean> out1:partitioning1.out) {
					reuseableNodes.putAll(findReuseableIndexers(out1.target, partitioning2));
				}
			}
			else {
				for(Edge<PatternPartition, Boolean> out1:partitioning1.out) {
					for(Edge<PatternPartition, Boolean> out2:partitioning2.out) {
						reuseableNodes.putAll(findReuseableIndexers(out1.target, out2.target));
					}
				}
			}
		}
		
		return reuseableNodes;
	}

	private Set<AbstractStoryPatternObject> getJoinNodes(
			Node<PatternPartition, Boolean> partitioning) {
		Set<AbstractStoryPatternObject> joinNodes = new LinkedHashSet<AbstractStoryPatternObject>();
		if(partitioning.out.size() > 0) {
			Edge<PatternPartition, Boolean> out = partitioning.out.get(0);
			joinNodes.addAll(ReteUtil.collectNodeValues(out.target.value));
		}
		for(int i = 1; i < partitioning.out.size(); i++) {
			Edge<PatternPartition, Boolean> out = partitioning.out.get(i);
			joinNodes.retainAll(ReteUtil.collectNodeValues(out.target.value));
		}
		return joinNodes;
	}

	private ReteIndexer findIndexerBetween(
			ReteNode node,
			ReteNode target) {
		if(node instanceof ReteIndexer && node.getSuccessors().contains(target)) {
			return (ReteIndexer) node;
		}
		else {
			for(ReteNode successor:node.getSuccessors()) {
				ReteIndexer indexer = findIndexerBetween(successor, target);
				if(indexer != null) {
					return indexer;
				}
			}
		}
		return null;
	}

	private Set<StoryPatternElement> intersect(Set<StoryPatternElement> elements1,
			Set<StoryPatternElement> elements2) {
		Set<StoryPatternElement> intersection = new LinkedHashSet<StoryPatternElement>(elements1);
		intersection.retainAll(elements2);
		return intersection;
	}

	private Set<StoryPatternElement> getStoryPatternElements(
			PatternPartition partition) {
		//TODO ignores NACs!!!
		Set<StoryPatternElement> elements = new LinkedHashSet<StoryPatternElement>();
		for(Node<AbstractStoryPatternObject, StoryPatternLink> node:partition.nodes) {
			elements.add(node.value);
		}
		for(Edge<AbstractStoryPatternObject, StoryPatternLink> edge:partition.edges) {
			elements.add(edge.value);
		}
		return elements;
	}

	protected boolean improvesCurrentPartitioning(
			Node<PatternPartition, Boolean> newPartitioning) {
		return true;
	}

	public void registerReteNode(ReteNode node) {
		if(node instanceof ChangeListener) {
			addChangeListener((ChangeListener) node);
		}
		if(node instanceof ReteIndexer) {
			((ReteNotifier) node).addNotificationReceiver(this);
		}
	}
	
	public void unregisterReteNode(ReteNode node) {
		if(node instanceof ChangeListener) {
			removeChangeListener((ChangeListener) node);
		}
		if(node instanceof ReteIndexer) {
			((ReteNotifier) node).removeNotificationReceiver(this);
		}
	}

	@Override
	protected void flushEvent(SDMChangeEvent change) {
		super.flushEvent(change);
		
		//update repopulate index		
		if(change instanceof SDMNodeChange) {
			SDMNodeChange nodeChange = (SDMNodeChange) change;
			if(change.getModifier() == SDMChangeEnum.CREATE) {
				repopulateIndex.addEObject(nodeChange.getObject());
			}
			else if(change.getModifier() == SDMChangeEnum.DELETE) {
				repopulateIndex.removeEObject(nodeChange.getObject());
			}
		}
		else if(change instanceof SDMEdgeChange) {
			SDMEdgeChange edgeChange = (SDMEdgeChange) change;
			if(change.getModifier() == SDMChangeEnum.CREATE) {
				repopulateIndex.addReference(edgeChange.getSource(), edgeChange.getFeature(), edgeChange.getTarget());
			}
			else if(change.getModifier() == SDMChangeEnum.DELETE) {
				repopulateIndex.removeReference(edgeChange.getSource(), edgeChange.getFeature(), edgeChange.getTarget());
			}
		}
	}

	@Override
	public void flushUnhandledEvents() {
		FlushStatus previousFlushing = flushing;
		flushing = FlushStatus.FLUSH;
		for(int i = 0; i < unhandledChanges.size(); i++) {
			SDMChangeEvent change = unhandledChanges.get(i);
			notifyUpdateStart();
			flushEvent(change);
			if(triggerRecomputation()) {
				notifyRecomputeStart();
				if(computeNewNetStructure()) {
					notifyRepopulateStart();
					repopulateReteNet();
					notifyRepopulateEnd();
				}
				notifyRecomputeEnd();
				updateAfterRecompute();
			}
			notifyUpdateEnd();
			notifyCustom();
		}
		unhandledChanges.clear();
		flushing = previousFlushing;
	}

	public double computeCurrentNetCost() {
		return computePartitioningCost(currentPartitioning);
	}

	protected double computePartitioningCost(
			Node<PatternPartition, Boolean> partitioning) {
		if(partitioning == null) {
			return 0;
		}
		double cost = reteBuilder.computePartitionCost(partitioning.value) * reteBuilder.computePartitionFiltering(partitioning.value);
		for(Edge<PatternPartition, Boolean> out:partitioning.out) {
			cost += computePartitioningCost(out.target);
		}
		return cost;
	}

	protected void repopulateReteNet() {
		blockCurrentNet();
		currentNetSize = computeCurrentNetSize();
		List<ReteTupleGenerator> generators = getRequiredGeneratorsForRepopulate(currentNet);
		
		if(currentNetSize <= oldNetSize) {
			GENERATOR_LOOP: for(ReteTupleGenerator generator:generators) {
				if(generator.isInput()) {
					ReteInput input = (ReteInput) generator;
					
					for(Entry<EClassifier, Collection<EObject>> entry:repopulateIndex.getDomains().entrySet()) {	//TODO save unnecessary loops
						for(EObject eObject:entry.getValue()) {
							input.registerChange(new SDMNodeChange(eObject, SDMChangeEnum.CREATE));
							
							if(currentNetSize > oldNetSize) {
								restoreOldNet();
								break GENERATOR_LOOP;
							}
						}
					}
					
					for(Entry<FeatureSpecification, Collection<Tuple<EObject, EObject>>> reference:repopulateIndex.getReferences().entrySet()) {
						for(Tuple<EObject, EObject> edge:reference.getValue()) {
							input.registerChange(new SDMEdgeChange(edge.e1, (EReference) reference.getKey().feature, edge.e2, SDMChangeEnum.CREATE));
							
							if(currentNetSize > oldNetSize) {
								restoreOldNet();
								break GENERATOR_LOOP;
							}
						}
					}
				}
				else {
					ReteNodeExecutor executor = generator.createExecutor();
					while(executor.hasNextTuple()) {
						executor.generateNextTuple();
						
						if(currentNetSize > oldNetSize) {
							restoreOldNet();
							break GENERATOR_LOOP;
						}
					}
				}
			}
		}
		
		unblockCurrentNet();
	}

	private void restoreOldNet() {
		for(ReteNode node:currentNet.getNodes().values()) {
			if(node.isInput()) {
				removeChangeListener((ChangeListener) node);
			}
		}
		
		for(Entry<ReteIndexer, int[]> entry:oldMasks.entrySet()) {
			entry.getKey().setMask(entry.getValue());
		}
		
		reconnectNodes(reusedOldNodes);
		oldNet.addNodes(reusedOldNodes.keySet());
		resultProvider.setTableMask(resultProvider.getPredecessors().get(0).getTableMask());
		
		currentNet = oldNet;
		currentNetSize = oldNetSize;
		currentPartitioning = oldPartitioning;
	}

	private void reconnectNodes(Map<ReteIndexer, Tuple<Collection<ReteNode>, Collection<ReteNode>>> reusedNodes) {
		for(Entry<ReteIndexer, Tuple<Collection<ReteNode>, Collection<ReteNode>>> entry:reusedNodes.entrySet()) {
			ReteNode node = entry.getKey();
			node.clearPredecessors();
			for(ReteNode predecessor:entry.getValue().e1) {
				node.addPredecessor(predecessor);
			}
			
			node.clearSuccessors();
			for(ReteNode successor:entry.getValue().e2) {
				node.addSuccessor(successor);
			}
		}
	}

	private void blockCurrentNet() {
		for(ReteNode node:new ArrayList<ReteNode>(currentNet.getNodes().values())) {
			if(node.isIndexer()) {
				blockIndexer((ReteIndexer) node);
			}
			else if(node.isInput()) {
				blockInput((ReteInput) node);
			}
		}
	}

	private void blockIndexer(ReteIndexer indexer) {
		for(ReteNode successor:new ArrayList<ReteNode>(indexer.getSuccessors())) {
			ReteBlocker blocker = new ReteBlocker(indexer, successor);
			indexer.removeSuccessor(successor);
			indexer.addSuccessor(blocker);
			blocker.addSuccessor(successor);
			currentNet.addNode(blocker);
		}
		
		if(isReused(indexer)) {
			for(ReteNode predecessor:indexer.getPredecessors()) {
				ReteBlocker blocker = new ReteBlocker(predecessor, indexer);
				predecessor.removeSuccessor(indexer);
				predecessor.addSuccessor(blocker);
				blocker.addSuccessor(indexer);
				currentNet.addNode(blocker);
			}
		}
	}

	private void blockInput(ReteInput input) {
		Collection<ReteIndexer> indexers = getNextIndexers(input);
		boolean disconnectInput = true;
		for(ReteIndexer indexer:indexers) {
			if(!isReused(indexer)) {
				disconnectInput = false;
				break;
			}
		}
		if(disconnectInput) {
			unregisterReteNode(input);
		}
	}

	protected List<ReteTupleGenerator> getRequiredGeneratorsForRepopulate(ReteNet net) {
		List<ReteTupleGenerator> generators = orderGeneratorsTopologically(net);
		for(Iterator<ReteTupleGenerator> it = generators.iterator(); it.hasNext();) {
			ReteTupleGenerator generator = it.next();
			boolean recompute = false;
			for(ReteIndexer indexer:getNextIndexers(generator)) {
				if(!isReused(indexer)) {
					recompute = true;
					break;
				}
			}
			if(!recompute) {
				it.remove();
			}
		}
		return generators;
	}
	
	private boolean isReused(ReteIndexer indexer) {
		return indexer.size() != 0;
	}
	
	private void unblockCurrentNet() {
		for(ReteNode node:new ArrayList<ReteNode>(currentNet.getNodes().values())) {
			if(node.isBlocker()) {
				removeBlocker((ReteBlocker) node);
			}
			else if(node.isInput() && !isRegisteredListener((ChangeListener) node)) {
				addChangeListener((ChangeListener) node);
			}
		}
	}

	private boolean isRegisteredListener(ChangeListener listener) {
		return changeListeners.contains(listener);
	}

	private void removeBlocker(ReteBlocker blocker) {
		ReteNode predecessor = blocker.getPredecessor();
		ReteNode successor = blocker.getSuccessor();
		predecessor.removeSuccessor(blocker);
		blocker.removeSuccessor(successor);
		predecessor.addSuccessor(successor);
		currentNet.removeNode(blocker);
	}

	protected boolean recomputeDynamicNodes() {
		return false;
	}
	
	protected boolean triggerRecomputation() {
		return false;
	}
	
	protected void updateAfterRecompute() {
		disposeOldNet();
	}

	private void disposeOldNet() {
		oldNet = null;
		oldNetSize = 0;
		oldPartitioning = null;
		oldMasks = null;
		reusedOldNodes = null;
	}

	protected void prepareRecompute() {
		
	}

	protected void notifyRecomputeStart() {
		for(QueryManagerNotificationReceiver receiver:notificationReceivers) {
			receiver.notifyStartRecompute();
		}
	}

	protected void notifyRecomputeEnd() {
		for(QueryManagerNotificationReceiver receiver:notificationReceivers) {
			receiver.notifyEndRecompute();
		}
	}

	protected void notifyRepopulateStart() {
		for(QueryManagerNotificationReceiver receiver:notificationReceivers) {
			receiver.notifyStartRepopulate();
		}
	}

	protected void notifyRepopulateEnd() {
		for(QueryManagerNotificationReceiver receiver:notificationReceivers) {
			receiver.notifyEndRepopulate();
		}
	}

	protected void notifyCustom() {
		for(QueryManagerNotificationReceiver receiver:notificationReceivers) {
			receiver.notifyCustom(this);
		}
	}

	@Override
	public void notifyAddition(ReteTuple tuple, ReteNotifier notifier) {
		if(notifier.isReteNode() && ((ReteNode) notifier).isIndexer()) {
			currentNetSize++;
			totalChanges++;
		}
	}

	@Override
	public void notifyRemoval(ReteTuple tuple, ReteNotifier notifier) {
		if(notifier.isReteNode() && ((ReteNode) notifier).isIndexer()) {
			currentNetSize--;
			totalChanges++;
		}
	}

	public void setNet(DynamicReteNet net) {
		currentNet = net;
		if(currentNet.getRoot() instanceof ReteResultProvider) {
			ReteNode actualRoot = currentNet.getRoot().getPredecessors().get(0);
			actualRoot.removeSuccessor(currentNet.getRoot());
			actualRoot.addSuccessor(resultProvider);
			currentNet.removeNode(currentNet.getRoot());
			currentNet.addNode(resultProvider);
			currentNet.setRoot(resultProvider);
			for(ReteNode node:currentNet.getNodes().values()) {
				if(node instanceof ReteEdgeInput) {
					((ReteEdgeInput) node).setModelIndex(modelIndex);
				}
				else if(node instanceof ReteNodeInput) {
					((ReteNodeInput) node).setModelIndex(modelIndex);
				}
			}
		}
		changeListeners.clear();
		registerReteNet(net);
		net.setManager(this);
	}
	
	public void discardCurrentNet() {
		currentNet = null;
		currentPartitioning = null;
	}

}
