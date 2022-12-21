package de.mdelab.mlsdm.interpreter.incremental.rete.diameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.Activity;
import de.mdelab.mlsdm.ActivityEdge;
import de.mdelab.mlsdm.ActivityNode;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.interpreter.MLSDMExpressionInterpreterManager;
import de.mdelab.mlsdm.interpreter.facade.MLSDMMetamodelFacadeFactory;
import de.mdelab.mlsdm.interpreter.incremental.rete.StoryPatternModelIndex;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.DynamicReteBuilder;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNet;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteQueryManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteSDMWrapper;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ReteContinuationDiscriminator;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteAntiJoin;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteDomainFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteEdgeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIsomorphyFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteJoin;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNodeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteResultProvider;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.DoublyWeightedEdge;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Edge;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Node;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteUtil;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Tuple;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.WeightedGraph;
import de.mdelab.mlsdm.interpreter.notifications.MLSDMNotificationEmitter;
import de.mdelab.mlsdm.interpreter.searchModel.model.MLSDMSearchModel;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMSearchModelBasedMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMSearchModelBuilder;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.expressions.MLSDMOCLExpressionAnalyzer;
import de.mdelab.mlstorypatterns.AbstractStoryPatternLink;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.MlstorypatternsFactory;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.sdm.interpreter.core.SDMException;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.PatternNode;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.expressions.ExpressionAnalyzerManager;
import de.mdelab.sdm.interpreter.core.variables.NotifierVariablesScope;

public class DiameterBasedReteBuilder {

	private static final int TERMINAL_EDGES = 1;

	private static final boolean CHECK_ISOMORPHISM = false;
	
	protected StoryPatternModelIndex modelIndex;
	protected StoryPattern storyPattern;
	private MLSDMSearchModel searchModel;
	private Map<AbstractStoryPatternObject, PatternNode<?, AbstractStoryPatternObject, ?, ?, ?, ?>> spo2PatternNode;
	private MLSDMExpressionInterpreterManager expressionInterpreterManager;
	private ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression> expressionAnalyzerManager;
	private NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> variablesScope;

	private int lookAhead = 1;
	
	private Map<Object, Double> constraintFilterings;
//	private Map<ReteIndexer, PatternPartition> indexer2Partition;

	public DiameterBasedReteBuilder(StoryPattern storyPattern, ReteQueryManager queryManager) throws SDMException {
		this(storyPattern, queryManager.getModelIndex());
	}

	public DiameterBasedReteBuilder(StoryPattern storyPattern, StoryPatternModelIndex modelIndex) throws SDMException {
		this.expressionInterpreterManager = createExpressionInterpreterManager();
		this.expressionAnalyzerManager = createExpressionAnalyzerManager();
		this.variablesScope = new NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression>(new MLSDMNotificationEmitter());
		this.modelIndex = modelIndex;
		this.storyPattern = storyPattern;
		this.constraintFilterings = new LinkedHashMap<Object, Double>();
		prepareSearchModel(storyPattern);
	}
	
	private MLSDMExpressionInterpreterManager createExpressionInterpreterManager() {
		return new MLSDMExpressionInterpreterManager(DynamicReteBuilder.class.getClassLoader());
	}

	private ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression> createExpressionAnalyzerManager() {
		ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression> manager = new ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression>(MLSDMMetamodelFacadeFactory.INSTANCE.getExpressionFacade());
		manager.registerExpressionAnalyzer("OCL", new MLSDMOCLExpressionAnalyzer(manager));
		return manager;
	}

	public MLSDMSearchModel getSearchModel() {
		return searchModel;
	}
	
	public SDMQuery buildReteQuery() throws SDMException {
		ReteNet net = createReteNet().e1;
		ReteResultProvider resultProvider = new ReteResultProvider();
		int resultSize = storyPattern.getStoryPatternObjects().size();
		int[] tableMask = new int[resultSize];
		for(int i = 0; i < resultSize; i++) {
			tableMask[i] = i;
		}
		resultProvider.setTableMask(tableMask);
		net.getRoot().addSuccessor(resultProvider);
		net.addNode(resultProvider);
		net.setRoot(resultProvider);
		return new ReteSDMWrapper(resultProvider);
	}
	
	public Tuple<ReteNet, int[]> createReteNet() throws SDMException {
		Node<PatternPartition, Boolean> partitioning = computePartitioning();
		Tuple<ReteNet, int[]> rete = buildReteNet(partitioning);
		return rete;
	}

	public Node<PatternPartition, Boolean> computePartitioning() throws SDMException {		
		PatternPartition initialGraph = createWeightedGraph(storyPattern);
		
		Collection<Node<PatternPartition, Boolean>> nonTerminals = new ArrayList<Node<PatternPartition, Boolean>>();
		
		Node<PatternPartition, Boolean> root = new Node<PatternPartition, Boolean>(-1, initialGraph);
		nonTerminals.add(root);
		while(!nonTerminals.isEmpty()) {
			Collection<Node<PatternPartition, Boolean>> newNonTerminals = new ArrayList<Node<PatternPartition, Boolean>>();
			
			for(Node<PatternPartition, Boolean> graphNode:nonTerminals) {
				PatternPartition graph = graphNode.value;
				if(!isTerminal(graph)) {
					Tuple<PatternPartition, PatternPartition> partitions = partitionGraph(graph, initialGraph);

					Node<PatternPartition, Boolean> child1 = new Node<PatternPartition, Boolean>(-1, partitions.e1);
					Node<PatternPartition, Boolean> child2 = new Node<PatternPartition, Boolean>(-1, partitions.e2);

					graphNode.out.add(new Edge<PatternPartition, Boolean>(graphNode, child1, true));
					child1.in.add(new Edge<PatternPartition, Boolean>(child1, graphNode, true));
					
					graphNode.out.add(new Edge<PatternPartition, Boolean>(graphNode, child2, true));
					child2.in.add(new Edge<PatternPartition, Boolean>(child2, graphNode, true));
					
					newNonTerminals.add(child1);
					newNonTerminals.add(child2);
				}
			}
			
			nonTerminals = newNonTerminals;
		}

		enrichGraphWithConstraints(root, storyPattern);
		
		return root;
	}

	protected void enrichGraphWithConstraints(Node<PatternPartition, Boolean> treeRoot, StoryPattern storyPattern) {
		//add NACs
//		for(StoryPattern nac:storyPattern.getNegativeApplicationConditions()) {
//			Set<AbstractStoryPatternObject> requiredSpos = getRequiredSpos(nac);
//			Node<PatternPartition, Boolean> insertionPosition = getInsertionPosition(new Tuple<StoryPattern, Set<AbstractStoryPatternObject>>(nac, requiredSpos), treeRoot);
//			insertionPosition.value.nacs.add(nac);
//		}
		
		//add expression constraints
		for(MLExpression constraint:storyPattern.getConstraints()) {
			Collection<AbstractStoryPatternObject> requiredSpos = getRequiredSpos(constraint);
			insertIntoChildren(treeRoot, requiredSpos, constraint);
		}
		
		insertNACs(treeRoot);
		//TODO spo constraints not supported for now
//		for(AbstractStoryPatternObject spo:storyPattern.getStoryPatternObjects()) {
//			if(spo.eClass() == MlstorypatternsPackage.Literals.STORY_PATTERN_OBJECT) {
//				for(MLExpression constraint:((StoryPatternObject) spo).getConstraints()) {
//					Collection<AbstractStoryPatternObject> requiredSpos = getRequiredSpos(constraint);
//					if(!requiredSpos.contains(spo)) {
//						requiredSpos.add(spo);
//					}
//					insertIntoChildren(treeRoot, requiredSpos, constraint);
//				}
//			}
//		}
	}

	private void insertNACs(Node<PatternPartition, Boolean> partitioningRoot) {
		Collection<PartitionConstraint> leftConstraints = new LinkedHashSet<PartitionConstraint>();
		Collection<PartitionConstraint> rightConstraints = new LinkedHashSet<PartitionConstraint>();
		if(partitioningRoot.out.size() > 1) {
			leftConstraints.addAll(partitioningRoot.out.get(0).target.value.allConstraints);
			insertNACs(partitioningRoot.out.get(0).target);
			
			rightConstraints.addAll(partitioningRoot.out.get(1).target.value.allConstraints);
			insertNACs(partitioningRoot.out.get(1).target);
		}
		
		for(PartitionConstraint constraint:partitioningRoot.value.allConstraints) {
			if(constraint.constraint instanceof StoryPattern
					&& !leftConstraints.contains(constraint)
					&& !rightConstraints.contains(constraint)) {
				partitioningRoot.value.nacs.add((StoryPattern) constraint.constraint);
			}
		}
	}

	private Collection<AbstractStoryPatternObject> getRequiredSpos(
			MLExpression constraint) {
		Collection<String> spoNames = expressionAnalyzerManager.getVariableNames(constraint);
		Collection<AbstractStoryPatternObject> requiredSpos = new ArrayList<AbstractStoryPatternObject>();
		for(AbstractStoryPatternObject spo:storyPattern.getStoryPatternObjects()) {
			if(spoNames.contains(spo.getName())) {
				requiredSpos.add(spo);
			}
		}
		return requiredSpos;
	}

	private void insertIntoChildren(Node<PatternPartition, Boolean> treeRoot,
			Collection<AbstractStoryPatternObject> requiredSpos,
			MLExpression constraint) {
		if(hasAllRequiredSpos(treeRoot, requiredSpos)) {
			treeRoot.value.expressionConstraints.add(constraint);
		}
		for(Edge<PatternPartition, Boolean> out:treeRoot.out) {
			insertIntoChildren(out.target, requiredSpos, constraint);
		}
	}

//	private Set<AbstractStoryPatternObject> getRequiredSpos(StoryPattern nac) {
//		Set<AbstractStoryPatternObject> requiredSpos = new HashSet<AbstractStoryPatternObject>();
//		for(AbstractStoryPatternLink link:nac.getStoryPatternLinks()) {
//			AbstractStoryPatternObject source = link.getSource();
//			AbstractStoryPatternObject target = link.getTarget();
//			
//			if(source.getStoryPattern() != nac) {
//				requiredSpos.add(source);
//			}
//			if(target.getStoryPattern() != nac) {
//				requiredSpos.add(target);
//			}
//		}
//		return requiredSpos;
//	}
//
//	private Node<PatternPartition, Boolean> getInsertionPosition(Tuple<StoryPattern, Set<AbstractStoryPatternObject>> nac,
//			Node<PatternPartition, Boolean> treeRoot) {
//		boolean hasAllSpos = hasAllRequiredSpos(treeRoot, nac.e2);
//		if(hasAllSpos && (treeRoot.out.size() < 2 || (!hasAllRequiredSpos(treeRoot.out.get(0).target, nac.e2) && !hasAllRequiredSpos(treeRoot.out.get(1).target, nac.e2)))) {
//			return treeRoot;
//		}
//		else if(hasAllSpos) {
//			Node<PatternPartition, Boolean> leftInsertionPosition = getInsertionPosition(nac, treeRoot.out.get(0).target);
//			return leftInsertionPosition != null ? leftInsertionPosition : getInsertionPosition(nac, treeRoot.out.get(1).target);
//		}
//		else {
//			return null;
//		}
//	}

	private boolean hasAllRequiredSpos(Node<PatternPartition, Boolean> treeRoot, Collection<AbstractStoryPatternObject> spos) {
		for(AbstractStoryPatternObject spo:spos) {
			boolean hasSpo = false;
			for(Node<AbstractStoryPatternObject, StoryPatternLink> node:treeRoot.value.nodes) {
				if(node.value == spo) {
					hasSpo = true;
					break;
				}
			}
			
			if(!hasSpo) {
				return false;
			}
		}
		return true;
	}

	private Tuple<ReteNet, int[]> addNAC(Tuple<ReteNet, int[]> rete, Tuple<ReteNet, int[]> nacRete, Map<AbstractStoryPatternObject, PatternNode<?, AbstractStoryPatternObject, ?, ?, ?, ?>> nacSpo2PatternNode,
			StoryPattern nac) {
		//TODO update indexer2Partition!
		LinkedHashSet<AbstractStoryPatternObject> joinSpos = ReteUtil.intersect(spo2PatternNode.keySet(), nacSpo2PatternNode.keySet());

		List<AbstractStoryPatternObject> leftSpos = new ArrayList<AbstractStoryPatternObject>(getBoundSpos(rete.e2));
		List<AbstractStoryPatternObject> nacSpos = new ArrayList<AbstractStoryPatternObject>(nacSpo2PatternNode.keySet());
		
		int[] parentMask = transformMaskForJoin(rete.e2, leftSpos, joinSpos, spo2PatternNode);
		int[] nacMask = transformMaskForJoin(nacRete.e2, nacSpos, joinSpos, nacSpo2PatternNode);

		ReteIndexer parentIndexer = new ReteIndexer(parentMask, joinSpos.size(), nac.getName() + "_left");
		rete.e1.getRoot().addSuccessor(parentIndexer);
		rete.e1.addNode(parentIndexer);
		
		ReteIndexer nacIndexer = new ReteIndexer(nacMask, joinSpos.size(), nac.getName() + "_right");
		nacRete.e1.getRoot().addSuccessor(nacIndexer);
		rete.e1.addNode(nacIndexer);
		
		ReteNode reteRoot = new ReteAntiJoin(parentIndexer, nacIndexer, joinSpos.size(), nac);

		//add parent nodes to mask
		int tableMask[] = new int[searchModel.getPatternNodes().size()];
		for(int i = 0; i < searchModel.getPatternNodes().size(); i++) {
			tableMask[i] = ReteContinuationDiscriminator.NO_MAPPING;
		}
		for(AbstractStoryPatternObject spo:leftSpos) {
			PatternNode<?, AbstractStoryPatternObject, ?, ?, ?, ?> node = spo2PatternNode.get(spo);
			int id = node.getId();
			int indexInLeftChild = rete.e2[id];
			int indexInLeftIndex;
			for(indexInLeftIndex = 0; indexInLeftIndex < parentMask.length; indexInLeftIndex++) {
				if(parentMask[indexInLeftIndex] == indexInLeftChild) {
					break;
				}
			}
			tableMask[id] = indexInLeftIndex;
		}
		
		reteRoot.setTableMask(tableMask);
		rete.e1.setRoot(reteRoot);
		rete.e1.addNode(reteRoot);
		return new Tuple<ReteNet, int[]>(rete.e1, tableMask);
	}

	private Collection<AbstractStoryPatternObject> getBoundSpos(int[] nodeMask) {
		Collection<AbstractStoryPatternObject> boundSpos = new ArrayList<AbstractStoryPatternObject>();
		for(int id = 0; id < nodeMask.length; id++) {
			if(nodeMask[id] != ReteContinuationDiscriminator.NO_MAPPING) {
				boundSpos.add(searchModel.getNodeForId(id).getSpo());
			}
		}
		return boundSpos;
	}

	private int[] transformMaskForJoin(int[] nodeMask, List<AbstractStoryPatternObject> spos, Collection<AbstractStoryPatternObject> joinSpos, Map<AbstractStoryPatternObject, PatternNode<?, AbstractStoryPatternObject, ?, ?, ?, ?>> spoMap) {
		int[] transformed = new int[spos.size()];
		int i = 0;
		for(AbstractStoryPatternObject joinSpo:joinSpos) {
			transformed[i] = nodeMask[spoMap.get(joinSpo).getId()];
			i++;
		}
		for(AbstractStoryPatternObject nonJoinSpo:spos) {
			if(joinSpos.contains(nonJoinSpo)) {
				continue;
			}
			
			transformed[i] = nodeMask[spoMap.get(nonJoinSpo).getId()];
			i++;
		}
		return transformed;
	}

	private Collection<AbstractStoryPatternObject> pullDownContext(StoryPattern nac) {
		//TODO check whether this works for nested nacs
		Collection<AbstractStoryPatternObject> movedSpos = new LinkedHashSet<AbstractStoryPatternObject>();
		for(AbstractStoryPatternLink link:nac.getStoryPatternLinks()) {
			AbstractStoryPatternObject source = link.getSource();
			if(source.getStoryPattern() != nac) {
				nac.getStoryPatternObjects().add(source);
				movedSpos.add(source);
			}
			AbstractStoryPatternObject target = link.getTarget();
			if(target.getStoryPattern() != nac) {
				nac.getStoryPatternObjects().add(target);
				movedSpos.add(target);
			}
		}
		return movedSpos;
	}

	protected void prepareSearchModel(StoryPattern pattern) throws SDMException {
		searchModel = createSearchModel(pattern);
		spo2PatternNode = new LinkedHashMap<AbstractStoryPatternObject, PatternNode<?, AbstractStoryPatternObject, ?, ?, ?, ?>>();
		for(PatternNode<?, AbstractStoryPatternObject, ?, ?, ?, ?> pn:searchModel.getPatternNodes()) {
			spo2PatternNode.put(pn.getSpo(), pn);
		}
	}

	private MLSDMSearchModel createSearchModel(StoryPattern pattern) throws SDMException {
		MLSDMSearchModelBasedMatcher matcher = new MLSDMSearchModelBasedMatcher(pattern,
				variablesScope,
				expressionInterpreterManager,
				expressionAnalyzerManager,
				new MLSDMReferenceIndex(),
				new MLSDMNotificationEmitter());
		MLSDMSearchModelBuilder builder = new MLSDMSearchModelBuilder();
		builder.setMatcher(matcher);
		return builder.createSearchModel(pattern);
	}

	public Tuple<ReteNet, int[]> buildReteNet(Node<PatternPartition, Boolean> root) throws SDMException {
		return buildReteNet(root, Collections.emptyMap());
	}
	
	public Tuple<ReteNet, int[]> buildReteNet(Node<PatternPartition, Boolean> root,
			Map<Tuple<Node<PatternPartition, Boolean>,Node<PatternPartition, Boolean>>, ReteIndexer> reuseableIndexers) throws SDMException {
		//TODO refactor this abomination
		//TODO tableMask now directly available through RETE net root, no need to return tuple?
		PatternPartition pattern = root.value;
		ReteNet net = new ReteNet();
		
		List<Node<AbstractStoryPatternObject, StoryPatternLink>> newNodes = new ArrayList<Node<AbstractStoryPatternObject, StoryPatternLink>>();
		List<Tuple<Node<AbstractStoryPatternObject, StoryPatternLink>, Node<AbstractStoryPatternObject, StoryPatternLink>>> isomorphyChecks =
				new ArrayList<Tuple<Node<AbstractStoryPatternObject, StoryPatternLink>, Node<AbstractStoryPatternObject, StoryPatternLink>>>();
		
		int[] tableMask = createEmptyTableMask();
		ReteNode reteRoot;
		
		if(root.out.isEmpty()) {
			// terminal node
			int edgeNumber = pattern.edges.size();
			if(edgeNumber == 0) {
				Node<AbstractStoryPatternObject, StoryPatternLink> node = pattern.nodes.get(0);
				reteRoot = new ReteNodeInput(node.value, modelIndex);
				tableMask[spo2PatternNode.get(node.value).getId()] = 0;
				newNodes.add(node);
			}
			else if(edgeNumber == 1){
				Edge<AbstractStoryPatternObject, StoryPatternLink> edge = pattern.edges.get(0);
				reteRoot = new ReteEdgeInput(edge.value, modelIndex);
				tableMask[spo2PatternNode.get(edge.value.getSource()).getId()] = 0;
				tableMask[spo2PatternNode.get(edge.value.getTarget()).getId()] = 1;
				
				newNodes.add(edge.source);
				newNodes.add(edge.target);
				if(ReteUtil.typesMatch(edge.source.value.getType(), edge.target.value.getType())) {
					isomorphyChecks.add(new Tuple<Node<AbstractStoryPatternObject, StoryPatternLink>, Node<AbstractStoryPatternObject, StoryPatternLink>>(edge.source, edge.target));
				}
			}
			else {
				DynamicReteBuilder dynamicBuilder = new DynamicReteBuilder();
				Tuple<StoryPattern, Map<AbstractStoryPatternObject, AbstractStoryPatternObject>> storyPattern = createStoryPatternCopy(pattern);
				ReteNet dynamicNet = dynamicBuilder.buildReteQuery(storyPattern.e1, modelIndex, variablesScope);
				for(PatternNode<?, AbstractStoryPatternObject, ?, ?, ?, ?> pn:dynamicBuilder.getCurrentSearchModel().getPatternNodes()) {
					int originalId = spo2PatternNode.get(storyPattern.e2.get(pn.getSpo())).getId();
					tableMask[originalId] = pn.getId();
				}
				net.addNodes(dynamicNet.getNodes());
				reteRoot = dynamicNet.getRoot();
				//TODO add nodes to net and make sure dynamically created nodes are registered as required
			}
		}
		else {
			Node<PatternPartition, Boolean> leftChild = root.out.get(0).target;
			Tuple<ReteNet, int[]> leftRete = buildReteNet(leftChild, reuseableIndexers);
			net.addNodes(leftRete.e1.getNodes());
			
			Node<PatternPartition, Boolean> rightChild = root.out.get(1).target;
			Tuple<ReteNet, int[]> rightRete = buildReteNet(rightChild, reuseableIndexers);
			net.addNodes(rightRete.e1.getNodes());
			
			List<AbstractStoryPatternObject> joinNodes;
			
			ReteIndexer leftIndexer = null;
			ReteIndexer rightIndexer = null;
			int[] leftMask = null;
			int[] rightMask = null;

			boolean reuseLeft = reuseableIndexers.containsKey(new Tuple<Node<PatternPartition, Boolean>,Node<PatternPartition, Boolean>>(root, leftChild));
			boolean reuseRight = reuseableIndexers.containsKey(new Tuple<Node<PatternPartition, Boolean>,Node<PatternPartition, Boolean>>(root, rightChild));
			if(reuseLeft && reuseRight) {
				ReteIndexer leftCandidate = reuseableIndexers.get(new Tuple<Node<PatternPartition, Boolean>,Node<PatternPartition, Boolean>>(root, leftChild));
				ReteIndexer rightCandidate = reuseableIndexers.get(new Tuple<Node<PatternPartition, Boolean>,Node<PatternPartition, Boolean>>(root, rightChild));
				List<AbstractStoryPatternObject> joinNodeOrderLeft = getJoinNodeOrder(leftCandidate);
				List<AbstractStoryPatternObject> joinNodeOrderRight = getJoinNodeOrder(rightCandidate);
				
				boolean joinsCompatible = joinNodeOrderLeft.equals(joinNodeOrderRight);
				
				if(joinsCompatible) {
					joinNodes = joinNodeOrderLeft;
					
					leftIndexer = leftCandidate;
					leftMask = reorderMaskForReuse(leftIndexer.getMask(), leftIndexer.getTableMask(), leftRete.e2);
					leftIndexer.setMask(leftMask);
					
					rightIndexer = rightCandidate;
					rightMask = reorderMaskForReuse(rightIndexer.getMask(), rightIndexer.getTableMask(), rightRete.e2);
					rightIndexer.setMask(rightMask);
				}
				else if(leftCandidate.size() >= rightCandidate.size()) {
					joinNodes = joinNodeOrderLeft;
					
					leftIndexer = leftCandidate;
					leftMask = reorderMaskForReuse(leftIndexer.getMask(), leftIndexer.getTableMask(), leftRete.e2);
					leftIndexer.setMask(leftMask);
				}
				else {
					joinNodes = joinNodeOrderRight;
					
					rightIndexer = rightCandidate;
					rightMask = reorderMaskForReuse(rightIndexer.getMask(), rightIndexer.getTableMask(), rightRete.e2);
					rightIndexer.setMask(rightMask);
				}
			}
			else if(reuseLeft) {
				leftIndexer = reuseableIndexers.get(new Tuple<Node<PatternPartition, Boolean>,Node<PatternPartition, Boolean>>(root, leftChild));
				joinNodes = getJoinNodeOrder(leftIndexer);
				leftMask = reorderMaskForReuse(leftIndexer.getMask(), leftIndexer.getTableMask(), leftRete.e2);
				leftIndexer.setMask(leftMask);
			}
			else if(reuseRight) {
				rightIndexer = reuseableIndexers.get(new Tuple<Node<PatternPartition, Boolean>,Node<PatternPartition, Boolean>>(root, rightChild));
				joinNodes = getJoinNodeOrder(rightIndexer);
				rightMask = reorderMaskForReuse(rightIndexer.getMask(), rightIndexer.getTableMask(), rightRete.e2);
				rightIndexer.setMask(rightMask);
			}
			else {
				joinNodes = new ArrayList<AbstractStoryPatternObject>(ReteUtil.intersect(ReteUtil.collectNodeValues(leftChild.value), ReteUtil.collectNodeValues(rightChild.value)));
			}
			
			if(leftIndexer == null) {
				leftMask = transformMaskForJoin(leftRete.e2, ReteUtil.collectNodeValues(leftChild.value), joinNodes);
				leftIndexer = new ReteIndexer(leftMask, joinNodes.size(), ReteUtil.getNamesForIndices(searchModel, leftRete.e2).toString());
				
				int[] indexerTableMask = creatIndexerTableMask(leftMask, leftChild.value, leftRete.e2);
				leftIndexer.setTableMask(indexerTableMask);
			}
			if(rightIndexer == null) {
				rightMask = transformMaskForJoin(rightRete.e2, ReteUtil.collectNodeValues(rightChild.value), joinNodes);
				rightIndexer = new ReteIndexer(rightMask, joinNodes.size(), ReteUtil.getNamesForIndices(searchModel, rightRete.e2).toString());
				
				int[] indexerTableMask = creatIndexerTableMask(rightMask, rightChild.value, rightRete.e2);
				rightIndexer.setTableMask(indexerTableMask);
			}
			
			leftRete.e1.getRoot().addSuccessor(leftIndexer);
			net.addNode(leftIndexer);
			rightRete.e1.getRoot().addSuccessor(rightIndexer);
			net.addNode(rightIndexer);
			
			reteRoot = new ReteJoin(leftIndexer, rightIndexer, joinNodes.size());

			//add left nodes to mask
			for(Node<AbstractStoryPatternObject, StoryPatternLink> node:leftChild.value.nodes) {
				int id = spo2PatternNode.get(node.value).getId();
				int indexInLeftChild = leftRete.e2[id];
				int indexInLeftIndex;
				for(indexInLeftIndex = 0; indexInLeftIndex < leftMask.length; indexInLeftIndex++) {
					if(leftMask[indexInLeftIndex] == indexInLeftChild) {
						break;
					}
				}
				tableMask[id] = indexInLeftIndex;
			}
			
			//add right nodes to mask
			for(Node<AbstractStoryPatternObject, StoryPatternLink> node:rightChild.value.nodes) {
				if(joinNodes.contains(node.value)) {
					continue;
				}
				int id = spo2PatternNode.get(node.value).getId();
				int indexInRightChild = rightRete.e2[id];
				int indexInRightIndex;
				for(indexInRightIndex = 0; indexInRightIndex < rightMask.length; indexInRightIndex++) {
					if(rightMask[indexInRightIndex] == indexInRightChild) {
						break;
					}
				}
				tableMask[id] = indexInRightIndex + (leftChild.value.nodes.size() - joinNodes.size());
			}
			
			for(Node<AbstractStoryPatternObject, StoryPatternLink> leftNode:leftChild.value.nodes) {
				if(joinNodes.contains(leftNode.value)) {
					continue;
				}
				
				for(Node<AbstractStoryPatternObject, StoryPatternLink> rightNode:rightChild.value.nodes) {
					if(joinNodes.contains(rightNode.value)) {
						continue;
					}
					
					if(ReteUtil.typesMatch(leftNode.value.getType(), rightNode.value.getType())) {
						isomorphyChecks.add(new Tuple<Node<AbstractStoryPatternObject, StoryPatternLink>, Node<AbstractStoryPatternObject, StoryPatternLink>>(leftNode, rightNode));
					}
				}
			}
		}
		reteRoot.setTableMask(tableMask);
		net.setRoot(reteRoot);
		net.addNode(root, reteRoot);
		
		//add pure filters
		for(Node<AbstractStoryPatternObject, StoryPatternLink> newNode:newNodes) {
			ReteDomainFilter domainFilter = new ReteDomainFilter(newNode.value.getType(), tableMask[spo2PatternNode.get(newNode.value).getId()]);
			reteRoot.addSuccessor(domainFilter);
			reteRoot = domainFilter;
			reteRoot.setTableMask(tableMask);
			net.setRoot(reteRoot);
			net.addNode(reteRoot);
		}
		if(CHECK_ISOMORPHISM) {
			for(Tuple<Node<AbstractStoryPatternObject, StoryPatternLink>, Node<AbstractStoryPatternObject, StoryPatternLink>> isomorphyCheck:isomorphyChecks) {
				ReteIsomorphyFilter isomorphyFilter = new ReteIsomorphyFilter(tableMask[spo2PatternNode.get(isomorphyCheck.e1.value).getId()],
				tableMask[spo2PatternNode.get(isomorphyCheck.e2.value).getId()]);
				reteRoot.addSuccessor(isomorphyFilter);
				reteRoot = isomorphyFilter;
				reteRoot.setTableMask(tableMask);
				net.setRoot(reteRoot);
				net.addNode(reteRoot);
			}
		}
		Tuple<ReteNet, int[]> rete = new Tuple<ReteNet, int[]>(net, tableMask);

		//add transforming filters
		for(StoryPattern nac:pattern.nacs) {
			Collection<AbstractStoryPatternObject> movedSpos = pullDownContext(nac);
			DiameterBasedReteBuilder nacBuilder = new DiameterBasedReteBuilder(nac, modelIndex);
			Tuple<ReteNet, int[]> nacRete = nacBuilder.createReteNet();
			net.addNodes(nacRete.e1.getNodes());
			rete = addNAC(rete, nacRete, nacBuilder.getSpo2PatternNode(), nac);
			nac.getContainingPattern().getStoryPatternObjects().addAll(movedSpos);
		}
		
		return rete;
	}

	private List<AbstractStoryPatternObject> getJoinNodeOrder(ReteIndexer indexer) {
		int joinSize = indexer.getKeySize();
		AbstractStoryPatternObject[] order = new AbstractStoryPatternObject[joinSize];
		int[] tableMask = indexer.getTableMask();
		for(int i = 0; i < tableMask.length; i++) {
			int tupleIndex = tableMask[i];
			if(tupleIndex != ReteContinuationDiscriminator.NO_MAPPING && tupleIndex < joinSize) {
				order[tupleIndex] = searchModel.getNodeForId(i).getSpo();
			}
		}
		return Arrays.asList(order);
	}

	private int[] creatIndexerTableMask(int[] mask, PatternPartition partition, int[] partitionMask) {
		int[] tableMask = createEmptyTableMask();
		for(Node<AbstractStoryPatternObject, StoryPatternLink> node:partition.nodes) {
			int id = spo2PatternNode.get(node.value).getId();
			int indexInPartition = partitionMask[id];
			int indexInIndexer;
			for(indexInIndexer = 0; indexInIndexer < partitionMask.length; indexInIndexer++) {
				if(mask[indexInIndexer] == indexInPartition) {
					break;
				}
			}
			tableMask[id] = indexInIndexer;
		}
		return tableMask;
	}
	
	private int[] createEmptyTableMask() {
		int[] tableMask = new int[searchModel.getPatternNodes().size()];
		for(int i = 0; i < searchModel.getPatternNodes().size(); i++) {
			tableMask[i] = ReteContinuationDiscriminator.NO_MAPPING;
		}
		return tableMask;
	}


	private int[] reorderMaskForReuse(int[] previousMask, int[] targetTableMask, int[] sourceTableMask) {
		int[] newMask = new int[previousMask.length];
		for(int i = 0; i < targetTableMask.length; i++) {
			int targetPosition = targetTableMask[i];
			if(targetPosition != ReteContinuationDiscriminator.NO_MAPPING) {
				newMask[targetPosition] = sourceTableMask[i];
			}
		}
		return newMask;
	}

	private Tuple<StoryPattern, Map<AbstractStoryPatternObject, AbstractStoryPatternObject>> createStoryPatternCopy(WeightedGraph<AbstractStoryPatternObject, StoryPatternLink> pattern) {
		Map<AbstractStoryPatternObject, AbstractStoryPatternObject> newSpo2OldSpo = new LinkedHashMap<AbstractStoryPatternObject, AbstractStoryPatternObject>();
		Map<AbstractStoryPatternObject, AbstractStoryPatternObject> oldSpo2NewSpo = new LinkedHashMap<AbstractStoryPatternObject, AbstractStoryPatternObject>();
		StoryPattern storyPattern = MlstorypatternsFactory.eINSTANCE.createStoryPattern();
		for(Node<AbstractStoryPatternObject, StoryPatternLink> node:pattern.nodes) {
			AbstractStoryPatternObject spo = MlstorypatternsFactory.eINSTANCE.createStoryPatternObject();
			spo.setName(node.value.getName());
			spo.setType(node.value.getType());
			storyPattern.getStoryPatternObjects().add(spo);
			newSpo2OldSpo.put(spo, node.value);
			oldSpo2NewSpo.put(node.value, spo);
		}
		for(Edge<AbstractStoryPatternObject, StoryPatternLink> edge:pattern.edges) {
			StoryPatternLink link = MlstorypatternsFactory.eINSTANCE.createStoryPatternLink();
			link.setSource(oldSpo2NewSpo.get(edge.source.value));
			link.setTarget(oldSpo2NewSpo.get(edge.target.value));
			link.setFeature(edge.value.getFeature());
			storyPattern.getStoryPatternLinks().add(link);
		}
		return new Tuple<StoryPattern, Map<AbstractStoryPatternObject, AbstractStoryPatternObject>>(storyPattern, newSpo2OldSpo);
	}

	private int[] transformMaskForJoin(int[] nodeMask, List<AbstractStoryPatternObject> nodes, List<AbstractStoryPatternObject> joinNodes) {
		int[] result = new int[nodes.size()];
		
		int i = 0;
		for(AbstractStoryPatternObject joinNode:joinNodes) {
			result[i] = nodeMask[spo2PatternNode.get(joinNode).getId()];
			i++;
		}
		for(AbstractStoryPatternObject nonJoinNode:nodes) {
			if(joinNodes.contains(nonJoinNode)) {
				continue;
			}
			else {
				result[i] = nodeMask[spo2PatternNode.get(nonJoinNode).getId()];
				i++;
			}
		}
		
		return result;
	}

	protected Tuple<PatternPartition, PatternPartition> partitionGraph (
			PatternPartition graph, PatternPartition initialGraph) {
		Tuple<PatternPartition, PatternPartition> currentPartitioning = createTrivialPartitioning(graph);
		currentPartitioning = refinePartitioning(currentPartitioning, graph.allConstraints);
		
		if(currentPartitioning.e2.edges.isEmpty() && currentPartitioning.e2.nodes.isEmpty()) {
			//nothing was moved, force move and recompute
			currentPartitioning = forcePartitionElementMovement(currentPartitioning, initialGraph, graph.allConstraints);
			currentPartitioning = refinePartitioning(currentPartitioning, graph.allConstraints);
		}
		
		return currentPartitioning;
	}

	private Tuple<PatternPartition, PatternPartition> refinePartitioning(Tuple<PatternPartition, PatternPartition> currentPartitioning, Collection<PartitionConstraint> availableConstraints) {
		
		double bestTotalCost = currentPartitioning.e1.cost + currentPartitioning.e2.cost;
		double oldTotalCost = Double.POSITIVE_INFINITY;
		
		while(bestTotalCost < oldTotalCost) {
			oldTotalCost = bestTotalCost;

			PatternPartition partition1 = currentPartitioning.e1;
			PatternPartition partition2 = currentPartitioning.e2;

			Collection<DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink>> bestSelection = Collections.emptyList();
			
			for(int selectedEdgeNumber = 1; selectedEdgeNumber <= lookAhead + 1 && selectedEdgeNumber < partition1.edges.size(); selectedEdgeNumber++) {
				int[] selectedIndices = new int[selectedEdgeNumber];
				for(int i = 0; i < selectedIndices.length; i++) {
					selectedIndices[i] = i;
				}
				
				while(selectedIndices[0] <= partition1.edges.size() - selectedEdgeNumber) {
					Collection<DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink>> selection = new ArrayList<DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink>>();
					for(int i = 0; i < selectedIndices.length; i++) {
						selection.add(partition1.edges.get(selectedIndices[i]));
					}

					double newPartition1Cost = computeCostAfterRemoval(partition1, selection);
					double newPartition2Cost = computeCostAfterAddition(partition2, selection, availableConstraints);
					double costAfterMove = newPartition1Cost + newPartition2Cost;
					
					if(costAfterMove < bestTotalCost) {
						bestTotalCost = costAfterMove;
						bestSelection = selection;
					}
					
					int currentLevel = selectedEdgeNumber - 1;
					selectedIndices[currentLevel]++;
					while(currentLevel > 0 && selectedIndices[currentLevel] > partition1.edges.size() - (selectedEdgeNumber - currentLevel)) {
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
			
			moveEdges(partition1, partition2, bestSelection, availableConstraints);
		}
		return currentPartitioning;
	}

	private Tuple<PatternPartition, PatternPartition> createTrivialPartitioning(
			PatternPartition graph) {
		PatternPartition partition1 = new PatternPartition();
		partition1.nodes.addAll(graph.nodes);
		partition1.edges.addAll(graph.edges);
		partition1.nacs.addAll(graph.nacs);
		partition1.allConstraints.addAll(graph.allConstraints);
		PatternPartition partition2 = new PatternPartition();

		double partition1Domain = computePartitionCost(partition1);
		double partition1Filtering = computePartitionFiltering(partition1);
		double partition1Cost = partition1Domain * partition1Filtering;
		partition1.cost = partition1Cost;
		
		double partition2Domain = computePartitionCost(partition2);
		double partition2Filtering = computePartitionFiltering(partition2);
		double partition2Cost = partition2Domain * partition2Filtering;
		partition2.cost = partition2Cost;

		return new Tuple<PatternPartition, PatternPartition>(partition1, partition2);
	}

	private double computeCostAfterRemoval(PatternPartition partition,
			Collection<? extends Edge<AbstractStoryPatternObject, StoryPatternLink>> edges) {
		double cost = partition.cost;
		Set<Node<AbstractStoryPatternObject, StoryPatternLink>> affectedNodes = new LinkedHashSet<Node<AbstractStoryPatternObject, StoryPatternLink>>();
		for(Edge<AbstractStoryPatternObject, StoryPatternLink> edge:edges) {
			cost /= getFiltering(edge.value);
			affectedNodes.add(edge.source);
			affectedNodes.add(edge.target);
		}
		Set<Node<AbstractStoryPatternObject, StoryPatternLink>> removedNodes = new LinkedHashSet<Node<AbstractStoryPatternObject, StoryPatternLink>>();
		for(Node<AbstractStoryPatternObject, StoryPatternLink> node:affectedNodes) {
			if(!hasEdgeInPartition(node, partition, edges)) {
				cost /= getDomainSize(node.value);
				removedNodes.add(node);
			}
		}
		
		for(PartitionConstraint constraint:partition.allConstraints) {
			for(Node<AbstractStoryPatternObject, StoryPatternLink> node:constraint.requiredNodes) {
				if(removedNodes.contains(node)) {
					cost /= getFiltering(constraint);
					break;
				}
			}
		}
		
		return cost;
	}

	private double computeCostAfterAddition(PatternPartition partition,
			Collection<? extends Edge<AbstractStoryPatternObject, StoryPatternLink>> edges, Collection<PartitionConstraint> availableConstraints) {
		double cost = partition.cost;
		Set<Node<AbstractStoryPatternObject, StoryPatternLink>> affectedNodes = new LinkedHashSet<Node<AbstractStoryPatternObject, StoryPatternLink>>();
		for(Edge<AbstractStoryPatternObject, StoryPatternLink> edge:edges) {
			cost *= getFiltering(edge.value);
			
			affectedNodes.add(edge.source);
			affectedNodes.add(edge.target);
		}
		Set<Node<AbstractStoryPatternObject, StoryPatternLink>> allNodesAfterAddition = new LinkedHashSet<Node<AbstractStoryPatternObject, StoryPatternLink>>();
		allNodesAfterAddition.addAll(partition.nodes);
		for(Node<AbstractStoryPatternObject, StoryPatternLink> node:affectedNodes) {
			if(!partition.nodes.contains(node)) {
				cost *= getDomainSize(node.value);
				allNodesAfterAddition.add(node);
			}
		}
		
		for(PartitionConstraint constraint:availableConstraints) {
			if(!partition.nodes.containsAll(constraint.requiredNodes)
					&& allNodesAfterAddition.containsAll(constraint.requiredNodes)) {
				cost *= getFiltering(constraint);
			}
		}
		
		return cost;
	}

	protected void moveEdges(PatternPartition partition1, PatternPartition partition2,
			Collection<DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink>> edges,
			Collection<PartitionConstraint> availableConstraints) {
		for(DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink> edge:edges) {
			//remove from partition1
			partition1.edges.remove(edge);
			partition1.cost /= getFiltering(edge.value);
		
			if(partition1.nodes.contains(edge.source) && !hasEdgeInPartition(edge.source, partition1, edges)) {
				partition1.nodes.remove(edge.source);
				partition1.cost /= getDomainSize(edge.source.value);
			}
			if(partition1.nodes.contains(edge.target) && !hasEdgeInPartition(edge.target, partition1, edges)) {
				partition1.nodes.remove(edge.target);
				partition1.cost /= getDomainSize(edge.target.value);
			}
			
			//add to partition2
			partition2.edges.add(edge);
			partition2.cost *= getFiltering(edge.value);
		
			if(!partition2.nodes.contains(edge.source)) {
				partition2.nodes.add(edge.source);
				partition2.cost *= getDomainSize(edge.source.value);
			}
			if(!partition2.nodes.contains(edge.target)) {
				partition2.nodes.add(edge.target);
				partition2.cost *= getDomainSize(edge.target.value);
			}
		}
		removeInvalidConstraints(partition1);
		addValidConstraints(partition2, availableConstraints);
	}

	private void removeInvalidConstraints(PatternPartition partition) {
		for(Iterator<PartitionConstraint> it = partition.allConstraints.iterator(); it.hasNext();) {
			PartitionConstraint constraint = it.next();
			if(!partition.nodes.containsAll(constraint.requiredNodes)) {
				partition.cost /= getFiltering(constraint);
				it.remove();
			}
		}
	}
	
	private void addValidConstraints(PatternPartition partition, Collection<PartitionConstraint> availableConstraints) {
		for(PartitionConstraint constraint:availableConstraints) {
			if(!partition.allConstraints.contains(constraint)
					&& partition.nodes.containsAll(constraint.requiredNodes)) {
				partition.cost *= getFiltering(constraint);
				partition.allConstraints.add(constraint);
			}
		}
	}
	
	private Tuple<PatternPartition, PatternPartition> forcePartitionElementMovement(Tuple<PatternPartition, PatternPartition> currentPartitioning,
			PatternPartition initialGraph, Collection<PartitionConstraint> availableConstraints) {
		PatternPartition partition1 = currentPartitioning.e1;
		PatternPartition partition2 = currentPartitioning.e2;
		
		Tuple<Tuple<Node<AbstractStoryPatternObject, StoryPatternLink>, Node<AbstractStoryPatternObject, StoryPatternLink>>, Edge<AbstractStoryPatternObject, StoryPatternLink>> diameterAndLastEdge =
				findDiameter(partition1, initialGraph);
		Tuple<Node<AbstractStoryPatternObject, StoryPatternLink>, Node<AbstractStoryPatternObject, StoryPatternLink>> diameter = diameterAndLastEdge.e1;
		partition2.nodes.add(diameter.e2);
		
		if(diameter.e2.out.isEmpty() && diameter.e2.in.isEmpty()) {
			partition1.nodes.remove(diameter.e2);
		}
		else {
			DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink> initialEdge = selectInitialEdge(partition1, partition2, diameter.e2, diameterAndLastEdge.e2);

			partition1.edges.remove(initialEdge);
			partition2.edges.add(initialEdge);
			
			if(initialEdge.source == diameter.e2) {
				partition2.nodes.add(initialEdge.target);
			}
			else {
				partition2.nodes.add(initialEdge.source);
			}
			
			if(!hasEdgeInPartition(initialEdge.source, partition1, Collections.singleton(initialEdge))) {
				partition1.nodes.remove(initialEdge.source);
			}
			if(!hasEdgeInPartition(initialEdge.target, partition1, Collections.singleton(initialEdge))) {
				partition1.nodes.remove(initialEdge.target);
			}
		}
		
		removeInvalidConstraints(partition1);
		addValidConstraints(partition2, availableConstraints);
		
		double partition1Domain = computePartitionCost(partition1);
		double partition1Filtering = computePartitionFiltering(partition1);
		double partition1Cost = partition1Domain * partition1Filtering;
		partition1.cost = partition1Cost;
		
		double partition2Domain = computePartitionCost(partition2);
		double partition2Filtering = computePartitionFiltering(partition2);
		double partition2Cost = partition2Domain * partition2Filtering;
		partition2.cost = partition2Cost;
		
		return new Tuple<PatternPartition, PatternPartition>(partition1, partition2);
	}

	private DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink> selectInitialEdge(PatternPartition partition1,
			PatternPartition partition2, Node<AbstractStoryPatternObject, StoryPatternLink> node, Edge<AbstractStoryPatternObject, StoryPatternLink> diameterEdge) {
		Set<Edge<AbstractStoryPatternObject, StoryPatternLink>> candidateEdges = new LinkedHashSet<Edge<AbstractStoryPatternObject, StoryPatternLink>>();
		candidateEdges.addAll(ReteUtil.intersect(node.out, partition1.edges));
		candidateEdges.addAll(ReteUtil.intersect(node.in, partition1.edges));
		DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink> edge = null;
		double maxWeight = Double.NEGATIVE_INFINITY;
		for(Edge<AbstractStoryPatternObject, StoryPatternLink> candidate:candidateEdges) {
			DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink> weightedEdge = (DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink>) candidate;
			if(edge == null) {
				double weight = weightedEdge.source == node ? weightedEdge.backwardWeight : weightedEdge.forwardWeight;
				edge = weightedEdge;
				maxWeight = weight;
			}
			else if(edge != diameterEdge) {
				double weight = weightedEdge.source == node ? weightedEdge.backwardWeight : weightedEdge.forwardWeight;
				if(weight > maxWeight) {
					edge = weightedEdge;
					maxWeight = weight;
				}
			}
		}
		return edge;
	}

	private boolean hasEdgeInPartition(Node<AbstractStoryPatternObject, StoryPatternLink> node,
			WeightedGraph<AbstractStoryPatternObject, StoryPatternLink> partition,
			Collection<? extends Edge<AbstractStoryPatternObject, StoryPatternLink>> exceptions) {
		for(Edge<AbstractStoryPatternObject, StoryPatternLink> in:node.in) {
			if(partition.edges.contains(in) && !exceptions.contains(in)) {
				return true;
			}
		}
		for(Edge<AbstractStoryPatternObject, StoryPatternLink> out:node.out) {
			if(partition.edges.contains(out) && !exceptions.contains(out)) {
				return true;
			}
		}
		return false;
	}

	public double computePartitionCost(PatternPartition partition) {
		double partitionCardinality = 1;
		for(Node<AbstractStoryPatternObject, StoryPatternLink> node:partition.nodes) {
			partitionCardinality *= getDomainSize(node.value);
		}
		return partitionCardinality;
	}

	public double computePartitionFiltering(PatternPartition partition) {
		double filtering = 1;
		Set<StoryPatternLink> coveredLinks = new LinkedHashSet<StoryPatternLink>();
		for(Edge<AbstractStoryPatternObject, StoryPatternLink> edge:partition.edges) {
			if(!coveredLinks.contains(edge.value)) {
				coveredLinks.add(edge.value);
				filtering *= getFiltering(edge.value);
			}
		}
		for(PartitionConstraint constraint:partition.allConstraints) {
			filtering *= getFiltering(constraint);
		}
		return filtering;
	}

	protected double getFiltering(StoryPatternLink value) {
		double domainSize = modelIndex.getDomain(value.getSource()).size() *
								(double) modelIndex.getDomain(value.getTarget()).size();
		return domainSize == 0 ? 0 : modelIndex.getReferences(value).size() / domainSize;
	}

	protected double getFiltering(PartitionConstraint constraint) {
		if(constraintFilterings.containsKey(constraint.constraint)) {
			return constraintFilterings.get(constraint.constraint);
		}
		else {
			return 1;
		}
	}

	public void setFiltering(Object constraint, double filtering) {
		constraintFilterings.put(constraint, filtering);
	}

	protected double getDomainSize(AbstractStoryPatternObject value) {
		return modelIndex.getDomain(value).size();
	}

	@SuppressWarnings("unchecked")
	private Tuple<Tuple<Node<AbstractStoryPatternObject, StoryPatternLink>, Node<AbstractStoryPatternObject, StoryPatternLink>>, Edge<AbstractStoryPatternObject, StoryPatternLink>> findDiameter(
			WeightedGraph<AbstractStoryPatternObject, StoryPatternLink> graph, WeightedGraph<AbstractStoryPatternObject, StoryPatternLink> initialGraph) {
		//Floyd-Warshall
		
		//initialize matrix
		double[][] sp = new double[initialGraph.nodes.size()][initialGraph.nodes.size()];
		Edge<AbstractStoryPatternObject, StoryPatternLink>[][] lastEdges = (Edge<AbstractStoryPatternObject, StoryPatternLink>[][]) new Edge[initialGraph.nodes.size()][initialGraph.nodes.size()];
		for(int i = 0; i < graph.nodes.size(); i++) {
			int node1Id = graph.nodes.get(i).id;
			for(int j = 0; j < graph.nodes.size(); j++) {
				int node2Id = graph.nodes.get(j).id;
				lastEdges[node1Id][node2Id] = null;
				if(node1Id == node2Id) {
					sp[node1Id][node2Id] = 0;
				}
				else {
					sp[node1Id][node2Id] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for(DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink> edge:graph.edges) {
			sp[edge.source.id][edge.target.id] = edge.forwardWeight > 0 ? edge.forwardWeight : 0;
			sp[edge.target.id][edge.source.id] = edge.backwardWeight > 0 ? edge.backwardWeight : 0;
			lastEdges[edge.source.id][edge.target.id] = edge;
			lastEdges[edge.target.id][edge.source.id] = edge;
		}
		
		//find paths
		for(int k = 0; k < graph.nodes.size(); k++) {
			int intermediateId = graph.nodes.get(k).id;
			for(int i = 0; i < graph.nodes.size(); i++) {
				int node1Id = graph.nodes.get(i).id;
				for(int j = 0; j < graph.nodes.size(); j++) {
					int node2Id = graph.nodes.get(j).id;
					if(sp[node1Id][intermediateId] + sp[intermediateId][node2Id] < sp[node1Id][node2Id]) {
						sp[node1Id][node2Id] = sp[node1Id][intermediateId] + sp[intermediateId][node2Id];
						lastEdges[node1Id][node2Id] = lastEdges[intermediateId][node2Id];
					}
				}
			}
		}

		int maxSource = -1;
		int maxTarget = -1;
		double maxPathWeight = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < graph.nodes.size(); i++) {
			int node1Id = graph.nodes.get(i).id;
			for(int j = 0; j < graph.nodes.size(); j++) {
				int node2Id = graph.nodes.get(j).id;
				if(sp[node1Id][node2Id] > maxPathWeight) {
					maxSource = node1Id;
					maxTarget = node2Id;
					maxPathWeight = sp[node1Id][node2Id];
				}
			}
		}
		
		Tuple<Node<AbstractStoryPatternObject, StoryPatternLink>, Node<AbstractStoryPatternObject, StoryPatternLink>> diameter =
				new Tuple<Node<AbstractStoryPatternObject, StoryPatternLink>, Node<AbstractStoryPatternObject, StoryPatternLink>>(graph.getNode(maxSource), graph.getNode(maxTarget));
		Edge<AbstractStoryPatternObject, StoryPatternLink> lastEdge = lastEdges[maxSource][maxTarget];
		return new Tuple<Tuple<Node<AbstractStoryPatternObject, StoryPatternLink>, Node<AbstractStoryPatternObject, StoryPatternLink>>, Edge<AbstractStoryPatternObject, StoryPatternLink>>(diameter, lastEdge);
	}

	protected boolean isTerminal(WeightedGraph<AbstractStoryPatternObject, StoryPatternLink> graph) {
		//handle triangles
		return graph.edges.size() <= TERMINAL_EDGES && (graph.nodes.size() == 1 || graph.edges.size() == 1 || graph.nodes.size() <= graph.edges.size());
	}

	protected PatternPartition createWeightedGraph(StoryPattern pattern) {
		PatternPartition graph = new PatternPartition();
		
		Map<AbstractStoryPatternObject, Node<AbstractStoryPatternObject, StoryPatternLink>> spo2Node = new LinkedHashMap<AbstractStoryPatternObject, Node<AbstractStoryPatternObject, StoryPatternLink>>();
		for(int i = 0; i < pattern.getStoryPatternObjects().size(); i++) {
			AbstractStoryPatternObject spo = pattern.getStoryPatternObjects().get(i);
			Node<AbstractStoryPatternObject, StoryPatternLink> node = new Node<AbstractStoryPatternObject, StoryPatternLink>(i, spo);
			graph.nodes.add(node);
			spo2Node.put(spo, node);
		}
		
		for(AbstractStoryPatternLink abstractLink:pattern.getStoryPatternLinks()) {
			if(!(abstractLink instanceof StoryPatternLink)) {
				continue;
			}
			
			StoryPatternLink link = (StoryPatternLink) abstractLink;
			Node<AbstractStoryPatternObject, StoryPatternLink> source = spo2Node.get(link.getSource());
			Node<AbstractStoryPatternObject, StoryPatternLink> target = spo2Node.get(link.getTarget());

			double forwardWeight = getForwardWeight(link);
			double backwardWeight = getBackwardWeight(link);

			DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink> edge = new DoublyWeightedEdge<AbstractStoryPatternObject, StoryPatternLink>(source, target, link, forwardWeight, backwardWeight);
			
			source.out.add(edge);
			target.in.add(edge);
			graph.edges.add(edge);
		}
		
		for(StoryPattern nac:pattern.getNegativeApplicationConditions()) {
			PartitionConstraint constraint = new PartitionConstraint();
			constraint.constraint = nac;
			for(AbstractStoryPatternLink link:nac.getStoryPatternLinks()) {
				Node<AbstractStoryPatternObject, StoryPatternLink> source = spo2Node.get(link.getSource());
				if(source != null) {
					constraint.requiredNodes.add(source);
				}
				
				Node<AbstractStoryPatternObject, StoryPatternLink> target = spo2Node.get(link.getTarget());
				if(target != null) {
					constraint.requiredNodes.add(target);
				}
			}
			graph.allConstraints.add(constraint);
		}
		
		return graph;
	}

	protected double getForwardWeight(StoryPatternLink link) {
		double domainSize = modelIndex.getDomain(link.getSource()).size();
		return Math.log(modelIndex.getReferences(link).size() / Math.max(domainSize, 1));
	}

	protected double getBackwardWeight(StoryPatternLink link) {
		double domainSize = modelIndex.getDomain(link.getTarget()).size();
		return Math.log(modelIndex.getReferences(link).size() / Math.max(domainSize, 1));
	}

	private Map<AbstractStoryPatternObject, PatternNode<?, AbstractStoryPatternObject, ?, ?, ?, ?>> getSpo2PatternNode() {
		return spo2PatternNode;
	}

}
