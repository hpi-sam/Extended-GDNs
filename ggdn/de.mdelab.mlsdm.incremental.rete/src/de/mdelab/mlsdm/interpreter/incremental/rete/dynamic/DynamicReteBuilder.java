package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.Activity;
import de.mdelab.mlsdm.ActivityEdge;
import de.mdelab.mlsdm.ActivityNode;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.interpreter.MLSDMExpressionInterpreterManager;
import de.mdelab.mlsdm.interpreter.facade.MLSDMMetamodelFacadeFactory;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNet;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteSDMWrapper;
import de.mdelab.mlsdm.interpreter.incremental.rete.StoryPatternModelIndex;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager.EdgeIndexType;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ModelIndexManager.NodeIndexType;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation.ReteContinuation;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation.ReteDomainCheck;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation.ReteDomainLookup;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation.ReteEdgeCheck;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation.ReteEdgeLookup;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation.ReteEdgeReversal;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation.ReteEdgeTraversal;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteDomainFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteEdgeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNodeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteResultProvider;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteTableFitter;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.FeatureSpecification;
import de.mdelab.mlsdm.interpreter.notifications.MLSDMNotificationEmitter;
import de.mdelab.mlsdm.interpreter.searchModel.model.MLSDMSearchModel;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMMetadataIndex;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMSearchModelBasedMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMSearchModelBuilder;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.hybrid.MLSDMConstraintSensitiveCostCalculator;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.hybrid.MLSDMContinuationCostCalculator;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.hybrid.MLSDMContinuationCostCalculator.Adornment;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.hybrid.MLSDMContinuationCostCalculator.CostModel;
import de.mdelab.mlstorypatterns.AbstractStoryPatternLink;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.MlstorypatternsPackage;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.sdm.interpreter.core.SDMException;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.PatternConstraint;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.PatternNode;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.expressions.ExpressionAnalyzerManager;
import de.mdelab.sdm.interpreter.core.variables.NotifierVariablesScope;

public class DynamicReteBuilder {

	protected static final int[] DOMAIN_CHECK_MASK = {0};
	protected static final int[] DOMAIN_LOOKUP_MASK = {0};
	
	protected static final int[] EDGE_CHECK_MASK = {0, 1};
	protected static final int[] EDGE_TRAVERSE_MASK = {0, 1};
	protected static final int[] EDGE_REVERSE_MASK = {1, 0};
	protected static final int[] EDGE_LOOKUP_MASK = {0, 1};
	
	private MLSDMContinuationCostCalculator costCalculator;
	private MLSDMSearchModel searchModel;
	private ModelIndexManager modelManager;
	protected StoryPatternModelIndex modelIndex;
	private MLSDMMetadataIndex metadataIndex;

	public SDMQuery buildReteSDMQuery(StoryPattern pattern, StoryPatternModelIndex modelIndex,
			NotifierVariablesScope<?, ?, ?, ?, ?, ?, ?, ?, MLExpression> variablesScope) throws SDMException {
		initializeDataStructures(pattern, modelIndex, variablesScope);
		
		Adornment[] continuationTable = costCalculator.calculateCotinuationCosts();
		PatternConstraint<StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> firstPC = continuationTable[0].matchingOrder.get(0);
		ReteIndexer firstIndexer = getLookupIndexer(firstPC);
		int initialTableCode = 0; 
		for(PatternNode<?, ?, ?, ?, ?, ?> pn:firstPC.getDependencies()) {
			initialTableCode = initialTableCode | (1 << pn.getId());
		}
		int[] initialTableMask = getInitialTableMask(firstPC, pattern.getStoryPatternObjects().size());
		Collection<ReteContinuation> continuations = constructContinuations(initialTableCode, initialTableMask, searchModel);

		ReteTableFitter tableFitter = new ReteTableFitter(costCalculator);
		ReteResultProvider resultProvider = new ReteResultProvider();
		tableFitter.addSuccessor(resultProvider);
		
		ReteDiscriminator initialDiscriminator = new ReteContinuationDiscriminator(continuations, continuationTable, initialTableCode, initialTableMask, tableFitter, searchModel);
		firstIndexer.addSuccessor(initialDiscriminator);
		
		return new ReteSDMWrapper(resultProvider);
	}
	
	public ReteNet buildReteQuery(StoryPattern pattern, StoryPatternModelIndex modelIndex,
			NotifierVariablesScope<?, ?, ?, ?, ?, ?, ?, ?, MLExpression> variablesScope) throws SDMException {
		initializeDataStructures(pattern, modelIndex, variablesScope);

		Adornment[] continuationTable = costCalculator.calculateCotinuationCosts();
		PatternConstraint<StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> firstPC = continuationTable[0].matchingOrder.get(0);
		ReteIndexer firstIndexer = getLookupIndexer(firstPC);
		int initialTableCode = 0; 
		for(PatternNode<?, ?, ?, ?, ?, ?> pn:firstPC.getDependencies()) {
			initialTableCode = initialTableCode | (1 << pn.getId());
		}
		int[] initialTableMask = getInitialTableMask(firstPC, pattern.getStoryPatternObjects().size());
		Collection<ReteContinuation> continuations = constructContinuations(initialTableCode, initialTableMask, searchModel);
		
		ReteTableFitter tableFitter = new ReteTableFitter(costCalculator);
		
		ReteDiscriminator initialDiscriminator = new ReteContinuationDiscriminator(continuations, continuationTable, initialTableCode, initialTableMask, tableFitter, searchModel);
		firstIndexer.addSuccessor(initialDiscriminator);
		
		ReteNet net = new ReteNet();
		net.setRoot(tableFitter);

		net.addNode(tableFitter);
		net.addNode(initialDiscriminator);
		net.addNodes(getModelManagerNodes());
		
		return net;
	}

	private Collection<ReteNode> getModelManagerNodes() {
		Collection<ReteNode> nodes = new HashSet<ReteNode>();
		for(Map<?, ReteIndexer> featureIndices:modelManager.edgeIndices.values()) {
			for(ReteIndexer indexer:featureIndices.values()) {
				nodes.addAll(getTree(indexer));
			}
		}
		for(Map<?, ReteIndexer> typeIndices:modelManager.nodeIndices.values()) {
			for(ReteIndexer indexer:typeIndices.values()) {
				nodes.addAll(getTree(indexer));
			}
		}
		return nodes;
	}
	
	private Collection<ReteNode> getTree(ReteNode node) {
		Collection<ReteNode> tree = new HashSet<ReteNode>();
		tree.add(node);
		for(ReteNode predecessor:node.getPredecessors()) {
			tree.addAll(getTree(predecessor));	
		}
		return tree;
	}
	
	private void initializeDataStructures(StoryPattern pattern, StoryPatternModelIndex modelIndex, NotifierVariablesScope<?, ?, ?, ?, ?, ?, ?, ?, MLExpression> variablesScope) throws SDMException {
		this.modelIndex = modelIndex;
		this.modelManager = createModelIndexManager(pattern);
		
		this.metadataIndex = new MLSDMMetadataIndex();
		for(Entry<EClassifier, Collection<EObject>> domain:modelIndex.getDomains().entrySet()) {
			metadataIndex.registerEObjects(domain.getValue());
		}
		
		MLSDMSearchModelBasedMatcher matcher = new MLSDMSearchModelBasedMatcher(pattern,
				new NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression>(new MLSDMNotificationEmitter()),
				new MLSDMExpressionInterpreterManager(DynamicReteBuilder.class.getClassLoader()),
				new ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression>(MLSDMMetamodelFacadeFactory.INSTANCE.getExpressionFacade()),
				metadataIndex,
				new MLSDMNotificationEmitter());
		MLSDMSearchModelBuilder builder = new MLSDMSearchModelBuilder();
		builder.setMatcher(matcher);
		this.searchModel = builder.createSearchModel(pattern);
		this.costCalculator = new MLSDMConstraintSensitiveCostCalculator(searchModel, metadataIndex, CostModel.AVERAGE_CASE, variablesScope);
	}

	private int[] getInitialTableMask(
			PatternConstraint<StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> pc, int maskSize) {
		int[] tableMask = createEmptyMask(maskSize);
		
		if(pc.getConstraint() instanceof StoryPatternLink) {
			tableMask[pc.getDependencies().get(0).getId()] = 0;
			tableMask[pc.getDependencies().get(1).getId()] = 1;
		}
		else if(pc.getConstraint() instanceof EClassifier) {
			tableMask[pc.getDependencies().get(0).getId()] = 0;
		}
		return tableMask;
	}

	private ReteIndexer getLookupIndexer(
			PatternConstraint<StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> pc) {
		if(pc.getConstraint() instanceof StoryPatternLink) {
			return modelManager.getEdgeIndex(getFeatureSpec((StoryPatternLink) pc.getConstraint()), EdgeIndexType.LOOKUP);
		}
		else if(pc.getConstraint() instanceof EClassifier) {
			return modelManager.getNodeIndex((EClassifier) pc.getConstraint(), NodeIndexType.LOOKUP);
		}
		else {
			return null;
		}
	}

	private FeatureSpecification getFeatureSpec(StoryPatternLink constraint) {
		FeatureSpecification featureSpec = new FeatureSpecification(constraint.getSource().getType(),
				(EReference) constraint.getFeature(), constraint.getTarget().getType());
		return featureSpec;
	}
	private Collection<ReteContinuation> constructContinuations(int bindingCode,
			int[] tableMask, MLSDMSearchModel searchModel) {
		Collection<ReteContinuation> continuations = new ArrayList<ReteContinuation>();
		for(PatternConstraint<StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> pc:searchModel.getPatternConstraints()) {
			continuations.addAll(createContinuations(bindingCode, tableMask, pc));
		}
		return continuations;
	}

	private Collection<ReteContinuation> createContinuations(int bindingCode,
			int[] tableMask, PatternConstraint<StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> pc) {
		Collection<ReteContinuation> continuations = new ArrayList<ReteContinuation>();
		if(pc.getConstraint() instanceof StoryPatternLink) {
			StoryPatternLink link = (StoryPatternLink) pc.getConstraint();
			EReference feature = (EReference) link.getFeature();
			FeatureSpecification featureSpec = new FeatureSpecification(link.getSource().getType(), feature, link.getTarget().getType());
			
			int sourceId = pc.getDependencies().get(0).getId();
			int targetId = pc.getDependencies().get(1).getId();
			int sourceCode = 1 << sourceId;
			int targetCode = 1 << targetId;
			if((sourceCode | targetCode) == (bindingCode & (sourceCode | targetCode))) {
				continuations.add(new ReteEdgeCheck(sourceId, targetId, costCalculator.getFiltering(pc.getExplicitCheckAction()),
						pc.getId(), tableMask, featureSpec, modelManager, metadataIndex));
			}
			else if(sourceCode == (bindingCode & sourceCode)) {
				continuations.add(new ReteEdgeTraversal(sourceId, targetId, costCalculator.getFiltering(pc.getExplicitCheckAction()),
						pc.getId(), tableMask, featureSpec, modelManager, metadataIndex));
			}
			else if(targetCode == (bindingCode & targetCode)) {
				continuations.add(new ReteEdgeReversal(sourceId, targetId, costCalculator.getFiltering(pc.getExplicitCheckAction()),
						pc.getId(), tableMask, featureSpec, modelManager, metadataIndex));
			}
			else {
				continuations.add(new ReteEdgeLookup(sourceId, targetId, costCalculator.getFiltering(pc.getExplicitCheckAction()),
						pc.getId(), tableMask, featureSpec, modelManager, metadataIndex));
			}
		}
		else if(pc.getConstraint() instanceof EClass) {
			int nodeId = pc.getDependencies().get(0).getId();
			int nodeCode = 1 << nodeId;
			if(nodeCode == (bindingCode & nodeCode)) {
				continuations.add(new ReteDomainCheck(costCalculator.getFiltering(pc.getExplicitCheckAction()),
						pc.getId(), tableMask, nodeId, (EClass) pc.getConstraint(), modelManager, metadataIndex));
			}
			else {
				continuations.add(new ReteDomainLookup(costCalculator.getFiltering(pc.getExplicitCheckAction()),
						pc.getId(), tableMask, nodeId, (EClass) pc.getConstraint(), modelManager, metadataIndex));
			}
		}
		return continuations;
	}

	protected int[] createEmptyMask(int size) {
		int[] mask = new int[size];
		for(int i = 0; i < size; i++) {
			mask[i] = ReteContinuationDiscriminator.NO_MAPPING;
		}
		return mask;
	}

	private ModelIndexManager createModelIndexManager(StoryPattern pattern) {
		ModelIndexManager modelManager = new ModelIndexManager();
		//TODO non-link constraints?
		Map<EClassifier, AbstractStoryPatternObject> requiredClassifiers = new HashMap<EClassifier, AbstractStoryPatternObject>();
		for(AbstractStoryPatternObject spo:pattern.getStoryPatternObjects()) {
			requiredClassifiers.put(spo.getType(), spo);
		}
		for(Entry<EClassifier, AbstractStoryPatternObject> entry:requiredClassifiers.entrySet()) {
			EClassifier classifier = entry.getKey();
			ReteNodeInput inputNode = new ReteNodeInput(entry.getValue(), modelIndex);
			
			ReteIndexer checkIndexer = new ReteIndexer(DOMAIN_CHECK_MASK, 1, classifier.getName() + "_DOMAIN_CHECK");
			inputNode.addSuccessor(checkIndexer);
			modelManager.registerNodeIndex(classifier, NodeIndexType.CHECK, checkIndexer);
			
			ReteIndexer lookupIndexer = new ReteIndexer(DOMAIN_LOOKUP_MASK, 0, classifier.getName() + "_DOMAIN_LOOKUP");
			inputNode.addSuccessor(lookupIndexer);
			modelManager.registerNodeIndex(classifier, NodeIndexType.LOOKUP, lookupIndexer);
		}
		
		Map<FeatureSpecification, StoryPatternLink> requiredFeatures = new HashMap<FeatureSpecification, StoryPatternLink>();	//TODO construct per combination of required feature and source/target type
		for(AbstractStoryPatternLink link:pattern.getStoryPatternLinks()) {
			if(!(link.eClass() == MlstorypatternsPackage.Literals.STORY_PATTERN_LINK)) {
				continue;
			}
			
			FeatureSpecification featureSpec = new FeatureSpecification(link.getSource().getType(), (EReference) (((StoryPatternLink) link).getFeature()), link.getTarget().getType());
			requiredFeatures.put(featureSpec, (StoryPatternLink) link);
		}
		for(Entry<FeatureSpecification, StoryPatternLink> entry:requiredFeatures.entrySet()) {
			FeatureSpecification featureSpec = entry.getKey();
			ReteNode inputNode = new ReteEdgeInput(entry.getValue(), modelIndex);
			if(!(featureSpec.sourceClassifier == featureSpec.feature.getEContainingClass())) {
				ReteNode filter = new ReteDomainFilter(featureSpec.sourceClassifier, 0);
				inputNode.addSuccessor(filter);
				inputNode = filter;
			}
			if(!(featureSpec.targetClassifier == featureSpec.feature.getEContainingClass())) {
				ReteNode filter = new ReteDomainFilter(featureSpec.targetClassifier, 1);
				inputNode.addSuccessor(filter);
				inputNode = filter;
			}
			
			ReteIndexer checkIndexer = new ReteIndexer(EDGE_CHECK_MASK, 2, featureSpec + "_EDGE_CHECK");
			inputNode.addSuccessor(checkIndexer);
			modelManager.registerEdgeIndex(featureSpec, EdgeIndexType.CHECK, checkIndexer);
			
			ReteIndexer traverseIndexer = new ReteIndexer(EDGE_TRAVERSE_MASK, 1, featureSpec + "_EDGE_TRAVERSE");
			inputNode.addSuccessor(traverseIndexer);
			modelManager.registerEdgeIndex(featureSpec, EdgeIndexType.TRAVERSE, traverseIndexer);
			
			ReteIndexer reverseIndexer = new ReteIndexer(EDGE_REVERSE_MASK, 1, featureSpec + "_EDGE_REVERSE");
			inputNode.addSuccessor(reverseIndexer);
			modelManager.registerEdgeIndex(featureSpec, EdgeIndexType.REVERSE, reverseIndexer);
			
			ReteIndexer lookupIndexer = new ReteIndexer(EDGE_LOOKUP_MASK, 0, featureSpec + "_EDGE_LOOKUP");
			inputNode.addSuccessor(lookupIndexer);
			modelManager.registerEdgeIndex(featureSpec, EdgeIndexType.LOOKUP, lookupIndexer);
		}
		return modelManager;
	}

	public MLSDMSearchModel getCurrentSearchModel() {
		return searchModel;
	}
	
}
