package de.mdelab.mlsdm.incremental;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;

import de.mdelab.mlsdm.gdn.GDN;
import de.mdelab.mlsdm.gdn.GDNDependency;
import de.mdelab.mlsdm.gdn.GDNMapping;
import de.mdelab.mlsdm.gdn.GDNNode;
import de.mdelab.mlsdm.incremental.fragment.SDMIndexFragment;
import de.mdelab.mlsdm.incremental.rete.sdm.SDMAntiJoinQuery;
import de.mdelab.mlsdm.incremental.rete.sdm.SDMJoinQuery;
import de.mdelab.mlsdm.incremental.rete.sdm.SDMReteInterfaceIndex;
import de.mdelab.mlsdm.incremental.rete.sdm.SDMJoinInterfaceIndex;
import de.mdelab.mlsdm.incremental.rete.sdm.SDMReteResultProvider;
import de.mdelab.mlsdm.incremental.rete.sdm.SDMSingleEdgeInputQuery;
import de.mdelab.mlsdm.incremental.rete.sdm.SDMSingleVertexInputQuery;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex.MLSDMReferenceAdapterTuple;
import de.mdelab.mlsdm.mlindices.HashTableIndex;
import de.mdelab.mlsdm.mlindices.MlindicesFactory;
import de.mdelab.mlsdm.mlindices.MlindicesPackage;
import de.mdelab.mlsdm.mlindices.NotifyingIndex;
import de.mdelab.mlstorypatterns.AbstractStoryPatternLink;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.sdm.icl.iCL.ICLConstraint;
import de.mdelab.sdm.interpreter.core.SDMException;
import de.mdelab.expressions.interpreter.core.variables.Variable;

public class SDMGDNQueryManager extends SDMQueryManager {

	protected Map<GDNNode, SDMQuery> registeredNodes;

	public SDMGDNQueryManager() {
		super();
		this.registeredNodes = new HashMap<GDNNode, SDMQuery>();
	}

	public SDMGDNQueryManager(SDMLogger logger) {
		super(logger);
		this.registeredNodes = new HashMap<GDNNode, SDMQuery>();
	}

	public SDMQuery registerRETENetwork(GDN gdn, Collection<Variable<EClassifier>> inputParameters) {
		flushUnhandledEvents();
		FlushStatus previousFlushing = flushing;
		flushing = FlushStatus.IGNORE;
		SDMQuery query = registerRETENode(gdn.getRoot(), inputParameters);
		flushing = previousFlushing;
		SDMQuery resultProvider = createResultProvider(query);
		return resultProvider;
	}
	
	private SDMQuery registerRETENode(GDNNode node,
			Collection<Variable<EClassifier>> inputParameters) {
		if(registeredNodes.containsKey(node)) {
			return registeredNodes.get(node);
		}
		
		SDMQuery query;
		if(node.getDependencies().isEmpty()) {
			query = createRETEInputNode(node, inputParameters);
		}
		else {
			if(isAntiJoin(node)) {
				query = createRETEAntiJoinNode(node, inputParameters);
			}
			else {
				query = createRETEJoinNode(node, inputParameters);
			}
		}

		patternQueries.put(node.getPattern(), query);
		registeredNodes.put(node, query);	//TODO this won't work with recursion!
		
		return query;
	}

	private boolean isAntiJoin(GDNNode node) {
		for(GDNDependency dependency:node.getDependencies()) {
			if(dependency.isNegative()) {
				return true;
			}
		}
		return false;
	}

	private SDMQuery createRETEJoinNode(GDNNode node,
			Collection<Variable<EClassifier>> inputParameters) {
		GDNDependency leftDependency = node.getDependencies().get(0);
		SDMQuery leftQuery = registerRETENode(leftDependency.getNode(), inputParameters);
		Set<AbstractStoryPatternObject> leftChildVertices = new HashSet<AbstractStoryPatternObject>();
		for(GDNMapping mapping:leftDependency.getMappings()) {
			leftChildVertices.add(mapping.getChildVertex());
		}
		
		GDNDependency rightDependency = node.getDependencies().get(1);
		Set<AbstractStoryPatternObject> rightChildVertices = new HashSet<AbstractStoryPatternObject>();
		for(GDNMapping mapping:rightDependency.getMappings()) {
			rightChildVertices.add(mapping.getChildVertex());
		}
		SDMQuery rightQuery = registerRETENode(rightDependency.getNode(), inputParameters);

		Set<AbstractStoryPatternObject> joinVertices = intersect(rightChildVertices, leftChildVertices);
		
		Map<AbstractStoryPatternObject, Integer> joinPositions = new HashMap<AbstractStoryPatternObject, Integer>();
		int joinIndex = 0;
		for(AbstractStoryPatternObject joinVertex:joinVertices) {
			joinPositions.put(joinVertex, joinIndex);
			joinIndex++;
		}

		Map<GDNMapping, Integer> leftPositionMap = createPositionMap(leftDependency.getMappings(), joinPositions);
		Map<GDNMapping, Integer> rightPositionMap = createPositionMap(rightDependency.getMappings(), joinPositions);

		SDMInterfaceIndex leftIndex = createRETEInterfaceIndex(leftQuery, computeParameterPositions(leftPositionMap), joinVertices.size());
		SDMInterfaceIndex rightIndex = createRETEInterfaceIndex(rightQuery, computeParameterPositions(rightPositionMap), joinVertices.size());
		
		
		Map<String, Integer> spoNameToIndex = computeNameToIndexMap(leftPositionMap, rightPositionMap, joinPositions.size());
		
		SDMJoinQuery joinNode = new SDMJoinQuery(this, leftIndex, rightIndex,
				spoNameToIndex, joinPositions.size(),
				leftDependency.getMappings().size(), rightDependency.getMappings().size());
		Collection<MLSDMReferenceAdapterTuple<Integer>> uniquenessChecks = computeRequiredUniquenessChecks(leftPositionMap, rightPositionMap, joinPositions.size());
		joinNode.addUniquenessChecks(uniquenessChecks);
		leftIndex.registerNotificationReceiver(joinNode);
		rightIndex.registerNotificationReceiver(joinNode);
		registeredNodes.put(node, joinNode);
		return joinNode;
	}

	private SDMQuery createRETEAntiJoinNode(GDNNode node,
			Collection<Variable<EClassifier>> inputParameters) {
		GDNDependency leftDependency = node.getDependencies().get(0);
		SDMQuery leftQuery = registerRETENode(leftDependency.getNode(), inputParameters);
		Set<AbstractStoryPatternObject> leftChildVertices = new HashSet<AbstractStoryPatternObject>();
		for(GDNMapping mapping:leftDependency.getMappings()) {
			leftChildVertices.add(mapping.getChildVertex());
		}
		
		GDNDependency rightDependency = node.getDependencies().get(1);
		Set<AbstractStoryPatternObject> rightChildVertices = new HashSet<AbstractStoryPatternObject>();
		for(GDNMapping mapping:rightDependency.getMappings()) {
			rightChildVertices.add(mapping.getChildVertex());
		}
		SDMQuery rightQuery = registerRETENode(rightDependency.getNode(), inputParameters);

		Set<AbstractStoryPatternObject> joinVertices = intersect(rightChildVertices, leftChildVertices);
		
		Map<AbstractStoryPatternObject, Integer> joinPositions = new HashMap<AbstractStoryPatternObject, Integer>();
		int joinIndex = 0;
		for(AbstractStoryPatternObject joinVertex:joinVertices) {
			joinPositions.put(joinVertex, joinIndex);
			joinIndex++;
		}

		Map<GDNMapping, Integer> leftPositionMap = createPositionMap(leftDependency.getMappings(), joinPositions);
		Map<GDNMapping, Integer> rightPositionMap = createPositionMap(rightDependency.getMappings(), joinPositions);

		SDMInterfaceIndex leftIndex = createRETEInterfaceIndex(leftQuery, computeParameterPositions(leftPositionMap), joinVertices.size());
		SDMInterfaceIndex rightIndex = createRETEInterfaceIndex(rightQuery, computeParameterPositions(rightPositionMap), joinVertices.size());
		
		Map<String, Integer> spoNameToIndex = computeNameToIndexMap(leftPositionMap, Collections.emptyMap(), joinPositions.size());
		
		SDMAntiJoinQuery joinNode = new SDMAntiJoinQuery(this, leftIndex, rightIndex,
				spoNameToIndex, joinPositions.size(),
				leftDependency.getMappings().size(), rightDependency.getMappings().size());
		leftIndex.registerNotificationReceiver(joinNode);
		rightIndex.registerNotificationReceiver(joinNode);
		registeredNodes.put(node, joinNode);
		return joinNode;
	}

	private Map<String, Integer> computeParameterPositions(
			Map<GDNMapping, Integer> positionMap) {
		Map<String, Integer> parameterPositions = new HashMap<String, Integer>();
		for(Entry<GDNMapping, Integer> position:positionMap.entrySet()) {
			parameterPositions.put(position.getKey().getParentVertex().getName(), position.getValue());
		}
		return parameterPositions;
	}

	private Collection<MLSDMReferenceAdapterTuple<Integer>> computeRequiredUniquenessChecks(
			Map<GDNMapping, Integer> leftPositionMap,
			Map<GDNMapping, Integer> rightPositionMap, int joinCardinality) {
		int rightOffset = leftPositionMap.size() - joinCardinality;
		Collection<MLSDMReferenceAdapterTuple<Integer>> uniquenessChecks = new ArrayList<MLSDMReferenceAdapterTuple<Integer>>();
		for(Entry<GDNMapping, Integer> leftPosition:leftPositionMap.entrySet()) {
			if(leftPosition.getValue() < joinCardinality) {
				continue;
			}
			
			for(Entry<GDNMapping, Integer> rightPosition:rightPositionMap.entrySet()) {
				if(rightPosition.getValue() < joinCardinality) {
					continue;
				}
				
				if(typeMatches(leftPosition.getKey().getChildVertex(), rightPosition.getKey().getChildVertex())) {
					uniquenessChecks.add(new MLSDMReferenceAdapterTuple<Integer>(leftPosition.getValue(), rightPosition.getValue() + rightOffset));
				}
			}
		}
		return uniquenessChecks;
	}

	private boolean typeMatches(AbstractStoryPatternObject spo1,
			AbstractStoryPatternObject spo2) {
		return spo1.getType() == spo2.getType() ||
				((EClass) spo1.getType()).getEAllSuperTypes().contains(spo2.getType()) ||
				((EClass) spo2.getType()).getEAllSuperTypes().contains(spo1.getType());
	}

	private Map<String, Integer> computeNameToIndexMap(
			Map<GDNMapping, Integer> leftPositionMap,
			Map<GDNMapping, Integer> rightPositionMap,
			int joinCardinality) {
		Map<String, Integer> nameToIndex = new HashMap<String, Integer>();
		for(Entry<GDNMapping, Integer> leftPosition:leftPositionMap.entrySet()) {
			nameToIndex.put(leftPosition.getKey().getChildVertex().getName(), leftPosition.getValue());
		}
		int rightOffset = leftPositionMap.size() - joinCardinality;
		for(Entry<GDNMapping, Integer> rightPosition:rightPositionMap.entrySet()) {
			if(nameToIndex.containsKey(rightPosition.getKey().getChildVertex().getName())) {
				continue;
			}
			nameToIndex.put(rightPosition.getKey().getChildVertex().getName(), rightPosition.getValue() + rightOffset);
		}
		
		return nameToIndex;
	}

	private Map<GDNMapping, Integer> createPositionMap(
			Collection<GDNMapping> mappings,
			Map<AbstractStoryPatternObject, Integer> joinPositions) {
		Map<GDNMapping, Integer> positionMap = new HashMap<GDNMapping, Integer>();
		int nonJoinCount = 0;
		for(GDNMapping mapping:mappings) {
			if(joinPositions.containsKey(mapping.getChildVertex())) {
				positionMap.put(mapping, joinPositions.get(mapping.getChildVertex()));
			}
			else {
				positionMap.put(mapping, joinPositions.size() + nonJoinCount);
				nonJoinCount++;
			}
		}
		return positionMap;
	}

	private <E> Set<E> intersect(Set<E> set1, Set<E> set2) {
		Set<E> intersection = new HashSet<E>();
		intersection.addAll(set1);
		intersection.retainAll(set2);
		return intersection;
	}

	private SDMQuery createResultProvider(SDMQuery query) {
		SDMInterfaceIndex index = createRETEInterfaceIndex(query, ((SDMJoinQuery) query).getSPONameToIndex(), 0);
		return new SDMReteResultProvider(this, index);
	}

	private SDMInterfaceIndex createRETEInterfaceIndex(
			SDMQuery query, Map<String, Integer> parameterPositions, int joinVertices) {
		HashTableIndex table = MlindicesFactory.eINSTANCE.createHashTableIndex();
		table.setKeySize(joinVertices);

		SDMInterfaceIndex interfaceIndex = query instanceof SDMJoinQuery ? 
												new SDMJoinInterfaceIndex(table, parameterPositions):
												new SDMReteInterfaceIndex(table, parameterPositions);
		query.registerInterfaceIndex(interfaceIndex);
		return interfaceIndex;
	}

	private SDMQuery createRETEInputNode(GDNNode node,
			Collection<Variable<EClassifier>> inputParameters) {
		StoryPattern pattern = node.getPattern();
		if(pattern.getStoryPatternLinks().isEmpty()) {
			return createSingleVertexInputNode(node, inputParameters);
		}
		else {
			return createEdgeInputNode(node, inputParameters);
		}
	}

	private SDMQuery createSingleVertexInputNode(GDNNode node,
			Collection<Variable<EClassifier>> inputParameters) {
		return new SDMSingleVertexInputQuery(this, node.getPattern().getStoryPatternObjects().get(0), this.getMetadataIndex());
	}

	private SDMQuery createEdgeInputNode(GDNNode node,
			Collection<Variable<EClassifier>> inputParameters) {
		return new SDMSingleEdgeInputQuery(this, (StoryPatternLink) node.getPattern().getStoryPatternLinks().get(0), this.getMetadataIndex());
	}

	public SDMPatternQuery registerGDN(GDN gdn, Collection<Variable<EClassifier>> inputParameters) throws SDMException, IOException {
		return registerGDNNode(gdn.getRoot(), inputParameters);
	}
	
	private SDMPatternQuery registerGDNNode(GDNNode node, Collection<Variable<EClassifier>> inputParameters) throws SDMException, IOException {
		Map<String, StoryPattern> dependencies = new HashMap<String, StoryPattern>();
		Collection<Variable<EClassifier>> allInputParameters = new ArrayList<Variable<EClassifier>>(inputParameters);
		Map<NotifyingIndex, SDMIndexFragment> dependencyIndexFragments = new HashMap<NotifyingIndex, SDMIndexFragment>();
		
		for(GDNDependency dependency:node.getDependencies()) {
			ICLConstraint indexConstraint = (ICLConstraint) getAST(dependency.getIndexConstraint());
			dependencies.put(indexConstraint.getIndexID(), dependency.getNode().getPattern());
			
			NotifyingIndex index = MlindicesFactory.eINSTANCE.createStagedHashIndex();
			
			Map<String, Integer> positions = new HashMap<String, Integer>();
			for(GDNMapping mapping:dependency.getMappings()) {
				positions.put(mapping.getParentVertex().getName(), mapping.getIndexPosition());
			}
			
			SDMInterfaceIndex interfaceIndex = new SDMInterfaceIndex(index, positions, dependency.getMappings().size());
			
			if(!registeredNodes.containsKey(dependency.getNode())) {
				registerGDNNode(dependency.getNode(), inputParameters);
			}
			SDMQuery dependencyQuery = registeredNodes.get(dependency.getNode());
			
			dependencyQuery.registerInterfaceIndex(interfaceIndex);
			interfaceIndex.registerNotificationReceiver(this);
			
			dependencyIndexFragments.put(index, new SDMIndexFragment(indexConstraint));
			allInputParameters.add(new Variable<EClassifier>(indexConstraint.getIndexID(), MlindicesPackage.Literals.INDEX, index));
		}
		
		SDMPatternQuery query = createQuery(node.getPattern(), dependencyIndexFragments, allInputParameters);
		for(Entry<String, StoryPattern> entry:dependencies.entrySet()) {
			query.registerDependency(patternQueries.get(entry.getValue()));
		}

		query.findInitialMatches();
		patternQueries.put(node.getPattern(), query);
		registeredNodes.put(node, query);	//TODO this won't work with recursion!
		return query;
	}

	public long computeGDNCost(GDN gdn) {
		long cost = 0;
		for(GDNNode node:gdn.getOwnedNodes()) {
			double domainSize = getDomainSize(node.getPattern());
			double filtering = getFiltering(node);
			cost += domainSize * filtering;
		}
		return cost;
	}

	private double getFiltering(GDNNode node) {
		double filtering = getFiltering(node.getPattern());
		for(GDNDependency dependency:node.getDependencies()) {
			if(!dependency.isNegative()) {
				filtering *= getFiltering(dependency.getNode());
			}
		}
		return filtering;
	}

	private double getFiltering(StoryPattern pattern) {
		double filtering = 1;
		for(AbstractStoryPatternLink link:pattern.getStoryPatternLinks()) {
			double domainSize = (double) getMetadataIndex().getDomain((EClass) link.getSource().getType()).size() * getMetadataIndex().getDomain((EClass) link.getTarget().getType()).size();
			filtering *= getMetadataIndex().getReferences((EReference) ((StoryPatternLink) link).getFeature()).size() / domainSize;
		}
		return filtering;
	}

	private double getDomainSize(StoryPattern pattern) {
		double domainSize = 1;
		for(AbstractStoryPatternObject spo:pattern.getStoryPatternObjects()) {
			domainSize *= getMetadataIndex().getDomain((EClass) spo.getType()).size();
		}
		return domainSize;
	}

}
