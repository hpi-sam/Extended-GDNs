package de.mdelab.mlsdm.incremental.viatra.emitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.context.common.BaseInputKeyWrapper;
import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;
import org.eclipse.viatra.query.runtime.rete.boundary.ExternalInputEnumeratorNode;
import org.eclipse.viatra.query.runtime.rete.boundary.ExternalInputStatelessFilterNode;
import org.eclipse.viatra.query.runtime.rete.index.DualInputNode;
import org.eclipse.viatra.query.runtime.rete.index.ExistenceNode;
import org.eclipse.viatra.query.runtime.rete.index.Indexer;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteEngine;
import org.eclipse.viatra.query.runtime.rete.network.Node;
import org.eclipse.viatra.query.runtime.rete.network.ProductionNode;
import org.eclipse.viatra.query.runtime.rete.network.Receiver;
import org.eclipse.viatra.query.runtime.rete.network.StandardNode;
import org.eclipse.viatra.query.runtime.rete.network.Supplier;
import org.eclipse.viatra.query.runtime.rete.recipes.AntiJoinRecipe;
import org.eclipse.viatra.query.runtime.rete.recipes.InputFilterRecipe;
import org.eclipse.viatra.query.runtime.rete.single.DiscriminatorBucketNode;
import org.eclipse.viatra.query.runtime.rete.single.SingleInputNode;
import org.eclipse.viatra.query.runtime.rete.single.TrimmerNode;
import org.eclipse.viatra.query.runtime.rete.single.UniquenessEnforcerNode;
import org.eclipse.viatra.query.runtime.rete.traceability.CompiledSubPlan;
import org.eclipse.viatra.query.runtime.rete.traceability.TraceInfo;

import de.mdelab.mlexpressions.MLStringExpression;
import de.mdelab.mlexpressions.MlexpressionsFactory;
import de.mdelab.mlsdm.gdn.GDN;
import de.mdelab.mlsdm.gdn.GDNDependency;
import de.mdelab.mlsdm.gdn.GDNMapping;
import de.mdelab.mlsdm.gdn.GDNNode;
import de.mdelab.mlsdm.gdn.GdnFactory;
import de.mdelab.mlsdm.gdn.GdnPackage;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.BindingTypeEnum;
import de.mdelab.mlstorypatterns.MatchTypeEnum;
import de.mdelab.mlstorypatterns.MlstorypatternsFactory;
import de.mdelab.mlstorypatterns.MlstorypatternsPackage;
import de.mdelab.mlstorypatterns.ModifierEnum;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.mlstorypatterns.StoryPatternObject;

public abstract class ReteNetEmitter {
	
	public void emitRETENet() {
		registerEPackages();

		IQuerySpecification<?> querySpecification;
		AdvancedViatraQueryEngine engine;
		try {
			engine = (AdvancedViatraQueryEngine) createEngine();
			querySpecification = getQuerySpecification();
			ReteEngine reteEngine = (ReteEngine) engine.getQueryBackend(ReteBackendFactory.INSTANCE);
			ProductionNode reteNode = reteEngine.getBoundary().accessProductionNode(querySpecification.getInternalQueryRepresentation()).getNodeCache();
			GDN gdn = createGDN(reteNode, querySpecification.getSimpleName());
			saveGDN(gdn, "gdns");
			savePatterns(gdn, "patterns");
		} catch (ViatraQueryException | QueryProcessingException e1) {
			e1.printStackTrace();
		}
	}
	
	protected abstract IQuerySpecification<?> getQuerySpecification();

	public ViatraQueryEngine createEngine() throws ViatraQueryException {
		ViatraQueryEngineOptions.setSystemDefaultBackends(ReteBackendFactory.INSTANCE, ReteBackendFactory.INSTANCE, ReteBackendFactory.INSTANCE);
		EMFScope scope = new EMFScope(new ResourceSetImpl());
		ViatraQueryEngine engine = ViatraQueryEngine.on(scope, ViatraQueryEngineOptions.getDefault());
		return engine;
	}

	private void savePatterns(GDN gdn, String outputDir) {
		File dir = new File(outputDir);
		if(!dir.exists()) {
			try {
				Files.createDirectory(dir.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		storePattern(gdn.getRoot(), outputDir);
	}

	private void saveGDN(GDN gdn, String outputDir) {
		File dir = new File(outputDir);
		if(!dir.exists()) {
			try {
				Files.createDirectory(dir.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		saveEObject(gdn, outputDir + "/" + gdn.getRoot().getPattern().getName().replace('.', '-') + ".gdn");
	}

	private void storePattern(GDNNode node, String outputDir) {
		saveEObject(EcoreUtil.copy(node.getPattern()), outputDir + "/" + node.getPattern().getName().replace('.', '-') + ".mlsp");
		
		for(GDNDependency dependency:node.getDependencies()) {
			storePattern(dependency.getNode(), outputDir);
		}
	}

	protected void registerEPackages() {
		GdnPackage.eINSTANCE.getName();
		MlstorypatternsPackage.eINSTANCE.getName();
		
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("gdn", new XMIResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("mlsp", new XMIResourceFactoryImpl());
		
		registerDomainPackages();
	}

	protected abstract void registerDomainPackages();

	private void saveEObject(EObject object, String outputFile) {
		ResourceSet rs = new ResourceSetImpl();
		Resource resource = rs.createResource(URI.createFileURI(outputFile));
		resource.getContents().add(object);
		try {
			resource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private GDN createGDN(ProductionNode reteNode, String id) {
		GDN gdn = GdnFactory.eINSTANCE.createGDN();
		getOrCreateGDNNode(reteNode, gdn, id, true, new HashMap<Node, GDNNode>());
		return gdn;
	}

	private GDNNode getOrCreateGDNNode(Node reteNode, GDN gdn, String id, boolean isRoot, HashMap<Node, GDNNode> nodeMap) {
		if(nodeMap.containsKey(reteNode)) {
			return nodeMap.get(reteNode);
		}
		
		GDNNode node = null;
		if(reteNode instanceof DualInputNode) {
			node = createNodeForJoin((DualInputNode) reteNode, id, gdn, nodeMap);
			gdn.getOwnedNodes().add(node);
			nodeMap.put(reteNode, node);
		}
		else if(reteNode instanceof ExternalInputEnumeratorNode) {
			node = createNodeForInput((ExternalInputEnumeratorNode) reteNode, id, gdn);
			gdn.getOwnedNodes().add(node);
			nodeMap.put(reteNode, node);
		}
		else if(reteNode instanceof UniquenessEnforcerNode) {
			int i = 0;
			for(Supplier parent:((UniquenessEnforcerNode) reteNode).getParents()) {
				GDNNode newNode = getOrCreateGDNNode(parent, gdn, id + "." + ++i, isRoot, nodeMap);
				if(node == null) {
					node = newNode;
					isRoot = false;
				}
			}
		}
		else if(reteNode instanceof SingleInputNode) {
			node = getOrCreateGDNNode(((SingleInputNode) reteNode).getParents().iterator().next(), gdn, id, isRoot, nodeMap);
		}
		else if(reteNode instanceof Indexer) {
			node = getOrCreateGDNNode(((Indexer) reteNode).getParent(), gdn, id, isRoot, nodeMap);
		}
		else {
			System.out.println("Unhandled RETE node: " + reteNode);
		}
		
		if(node != null) {
			if(isRoot) {
				gdn.setRoot(node);
			}
		}
		return node;
	}

	private GDNNode createNodeForInput(ExternalInputEnumeratorNode reteNode, String id, GDN gdn) {
		StoryPattern pattern = MlstorypatternsFactory.eINSTANCE.createStoryPattern();
		pattern.setName(id);
		Object inputKey = ((BaseInputKeyWrapper<?>) reteNode.getInputKey()).getWrappedKey();
		
		if(inputKey instanceof EReference) {
			EReference reference = (EReference) inputKey;

			StoryPatternObject source = createDefaultSPO("source", getFilteredType(reference.getEContainingClass(), reteNode, 0));	//TODO handle ExternalInputStatelessFilterNode
			pattern.getStoryPatternObjects().add(source);
			
			StoryPatternObject target = createDefaultSPO("target", getFilteredType(reference.getEType(), reteNode, 1));
			pattern.getStoryPatternObjects().add(target);
			
			StoryPatternLink link = MlstorypatternsFactory.eINSTANCE.createStoryPatternLink();
			link.setFeature(reference);
			link.setMatchType(MatchTypeEnum.DEFAULT);
			link.setModifier(ModifierEnum.NONE);
			link.setSource(source);
			link.setTarget(target);
			pattern.getStoryPatternLinks().add(link);
		}
		else if(inputKey instanceof EAttribute) {
			StoryPatternObject spo = createDefaultSPO("o", getFilteredType(((EAttribute) inputKey).getEContainingClass(), reteNode, 0));
			pattern.getStoryPatternObjects().add(spo);
			
			String constraintValue = getAttributeConstraintValue(reteNode);
			String expressionString = spo.getName() + "." + ((EAttribute) inputKey).getName() + " = " + constraintValue;
			
			MLStringExpression constraint = MlexpressionsFactory.eINSTANCE.createMLStringExpression();
			constraint.setExpressionLanguageID("OCL");
			constraint.setExpressionString(expressionString);
			pattern.getConstraints().add(constraint);			
		}
		
		GDNNode node = GdnFactory.eINSTANCE.createGDNNode();
		node.setPattern(pattern);
		
		return node;
	}

	private String getAttributeConstraintValue(Node reteNode) {
		while(!(reteNode instanceof DiscriminatorBucketNode)) {
			reteNode = ((StandardNode) reteNode).getReceivers().iterator().next();
		}
		Object value = ((DiscriminatorBucketNode) reteNode).getBucketKey();
		return value instanceof String ? "'" + value + "'" : value.toString();
	}

	private EClassifier getFilteredType(EClassifier type, ExternalInputEnumeratorNode reteNode, int maskIndex) {
		Collection<ExternalInputStatelessFilterNode> parentFilters = getFilterNodes(reteNode.getReceivers());
		while(!parentFilters.isEmpty()) {
			Collection<ExternalInputStatelessFilterNode> newFilters = new HashSet<ExternalInputStatelessFilterNode>();
			for(ExternalInputStatelessFilterNode filter:parentFilters) {
				Set<TraceInfo> traceInfos = filter.getTraceInfos();
				for(TraceInfo trace:traceInfos) {
					if(!(trace instanceof CompiledSubPlan)) {
						continue;
					}
					
					CompiledSubPlan plan = (CompiledSubPlan) trace;
					InputFilterRecipe recipe = (InputFilterRecipe) plan.getRecipe();
					if(recipe.getMask().getSourceIndices().get(0) == maskIndex && ((BaseInputKeyWrapper<?>) recipe.getInputKey()).getWrappedKey() instanceof EClassifier) {
						type = (EClassifier) ((BaseInputKeyWrapper<?>) recipe.getInputKey()).getWrappedKey();
					}
				}
				
				newFilters.addAll(getFilterNodes(filter.getReceivers()));
			}
			
			parentFilters = newFilters;
		}
		return type;
	}

	private Collection<ExternalInputStatelessFilterNode> getFilterNodes(Collection<Receiver> collection) {
		Collection<ExternalInputStatelessFilterNode> filters = new ArrayList<ExternalInputStatelessFilterNode>();
		for(Receiver receiver:collection) {
			if(receiver instanceof ExternalInputStatelessFilterNode) {
				filters.add((ExternalInputStatelessFilterNode) receiver);
			}
		}
		return filters;
	}

	private GDNNode createNodeForJoin(DualInputNode reteNode, String id, GDN gdn, HashMap<Node, GDNNode> nodeMap) {
		boolean isNegative = isNegative(reteNode);
	
		GDNNode leftParent = getOrCreateGDNNode(reteNode.getPrimarySlot().getParent(), gdn, id + ".1", false, nodeMap);
		StoryPattern leftPattern = leftParent.getPattern();
		List<Integer> leftJoins = getJoins(reteNode.getPrimarySlot());

		GDNNode rightParent = getOrCreateGDNNode(reteNode.getSecondarySlot().getParent(), gdn, id + ".2", false, nodeMap);
		StoryPattern rightPattern = rightParent.getPattern();
		List<Integer> rightJoins = getJoins(reteNode.getSecondarySlot());

		GDNNode node = GdnFactory.eINSTANCE.createGDNNode();
		
		StoryPattern pattern = MlstorypatternsFactory.eINSTANCE.createStoryPattern();
		pattern.setName(id);
		node.setPattern(pattern);

		//left parent
		
		GDNDependency leftDependency = GdnFactory.eINSTANCE.createGDNDependency();
		leftDependency.setNode(leftParent);
		node.getDependencies().add(leftDependency);
		
		int i;
		int leftOverlapDelimiter = 0;
		Map<StoryPatternObject, GDNMapping> leftSPOMappings = new HashMap<StoryPatternObject, GDNMapping>();
		List<StoryPatternObject> leftIndexedSPOs = new ArrayList<StoryPatternObject>();
		for(i = 0; i < leftPattern.getStoryPatternObjects().size(); i++) {
			AbstractStoryPatternObject leftSPO = leftPattern.getStoryPatternObjects().get(i);
			
			StoryPatternObject spo = createDefaultSPO("node" + i, leftSPO.getType());
			pattern.getStoryPatternObjects().add(spo);
			
			if(leftJoins.contains(i)) {
				leftIndexedSPOs.add(leftOverlapDelimiter, spo);
				leftOverlapDelimiter++;
			}
			else {
				leftIndexedSPOs.add(spo);
			}

			GDNMapping mapping = GdnFactory.eINSTANCE.createGDNMapping();
			mapping.setParentVertex(leftSPO);
			mapping.setChildVertex(spo);
			leftSPOMappings.put(spo, mapping);
			leftDependency.getMappings().add(mapping);
		}

		MLStringExpression leftExpression = MlexpressionsFactory.eINSTANCE.createMLStringExpression();
		leftExpression.setExpressionLanguageID("ICL");
		leftExpression.setExpressionString(createConstraintString("left", leftIndexedSPOs, leftSPOMappings, false));
		leftDependency.setIndexConstraint(leftExpression);
		pattern.getConstraints().add(leftExpression);

		//right parent
		
		GDNDependency rightDependency = GdnFactory.eINSTANCE.createGDNDependency();
		rightDependency.setNode(rightParent);
		node.getDependencies().add(rightDependency);

		int rightOverlapDelimiter = 0;
		Map<StoryPatternObject, GDNMapping> rightSPOMappings = new HashMap<StoryPatternObject, GDNMapping>();
		List<StoryPatternObject> rightIndexedSPOs = new ArrayList<StoryPatternObject>();
		for(int j = 0; j < rightPattern.getStoryPatternObjects().size(); j++) {
			AbstractStoryPatternObject rightSPO = rightPattern.getStoryPatternObjects().get(j);
			StoryPatternObject spo;
			
			int rightJoinIndex = rightJoins.indexOf(j);
			if(rightJoinIndex != -1) {
				int leftJoin = leftJoins.get(rightJoinIndex);
				spo = (StoryPatternObject) pattern.getStoryPatternObjects().get(leftJoin);
				rightIndexedSPOs.add(rightOverlapDelimiter, spo);
				rightOverlapDelimiter++;
			}
			else if(!isNegative){
				spo = createDefaultSPO("node" + i, rightSPO.getType());
				pattern.getStoryPatternObjects().add(spo);
				rightIndexedSPOs.add(spo);
				i++;
			}
			else {
				spo = null;
			}

			GDNMapping mapping = GdnFactory.eINSTANCE.createGDNMapping();
			mapping.setParentVertex(rightSPO);
			mapping.setChildVertex(spo);
			mapping.setIndexPosition(j);
			rightSPOMappings.put(spo, mapping);
			rightDependency.getMappings().add(mapping);
		}
		
		MLStringExpression rightExpression = MlexpressionsFactory.eINSTANCE.createMLStringExpression();
		rightExpression.setExpressionLanguageID("ICL");
		rightExpression.setExpressionString(createConstraintString("right", rightIndexedSPOs, rightSPOMappings, isNegative));
		rightDependency.setIndexConstraint(rightExpression);
		rightDependency.setNegative(isNegative);
		pattern.getConstraints().add(rightExpression);

		return node;
	}

	private List<Integer> getJoins(Indexer indexer) {
		List<Integer> joins = asList(indexer.getMask().indices);
		Node parent = indexer.getParent();
		while(!(parent instanceof ExternalInputEnumeratorNode) && !(parent instanceof DualInputNode)) {
			if(parent instanceof Indexer) {
				List<Integer> newJoins = new ArrayList<>();
				for(int join:joins) {
					newJoins.add(((Indexer) parent).getMask().indices[join]);
				}
				joins = newJoins;
			}
			else if(parent instanceof TrimmerNode) {
				List<Integer> newJoins = new ArrayList<>();
				for(int join:joins) {
					newJoins.add(((TrimmerNode) parent).getMask().indices[join]);
				}
				joins = newJoins;
			}
			
			parent = getParent(parent);
		}
		return joins;
	}

	private Node getParent(Node parent) {
		if(parent instanceof DualInputNode) {
			return ((DualInputNode) parent).getPrimarySlot();
		}
		else if(parent instanceof ExternalInputEnumeratorNode) {
			return ((ExternalInputEnumeratorNode) parent).getParents().iterator().next();
		}
		else if(parent instanceof UniquenessEnforcerNode) {
			return ((UniquenessEnforcerNode) parent).getParents().iterator().next();
		}
		else if(parent instanceof SingleInputNode) {
			return ((SingleInputNode) parent).getParents().iterator().next();
		}
		else if(parent instanceof Indexer) {
			return ((Indexer) parent).getParent();
		}
		return null;
	}

	private boolean isNegative(Supplier node) {
		if(!(node instanceof ExistenceNode)) {
			return false;
		}
		else {
			CompiledSubPlan subPlan = (CompiledSubPlan) node.getTraceInfos().iterator().next();
			return subPlan.getRecipe() instanceof AntiJoinRecipe;
		}
	}

	private List<Integer> asList(int[] is) {
		List<Integer> list = new ArrayList<Integer>(is.length);
		for(int i:is) {
			list.add(i);
		}
		return list;
	}

	private StoryPatternObject createDefaultSPO(String name, EClassifier type) {
		StoryPatternObject spo = MlstorypatternsFactory.eINSTANCE.createStoryPatternObject();
		spo.setBindingType(BindingTypeEnum.CAN_BE_BOUND);
		spo.setMatchType(MatchTypeEnum.DEFAULT);
		spo.setModifier(ModifierEnum.NONE);
		spo.setName(name);
		spo.setType(type);
		return spo;
	}

	private String createConstraintString(String indexID, List<StoryPatternObject> spos, Map<StoryPatternObject, GDNMapping> spoMappings, boolean isNegative) {
		String constraintString = indexID + " ";
		constraintString += (isNegative ? "EXCLUDES" : "CONTAINS");
		constraintString += " (";
		for(int i = 0; i < spos.size(); i++) {
			StoryPatternObject spo = spos.get(i);
			GDNMapping mapping = spoMappings.get(spo);
			
			constraintString += spo.getName();
			mapping.setIndexPosition(i);
			if(i < spos.size() - 1) {
				constraintString += ", ";
			}
		}
		constraintString += ")";
		
		return constraintString;
	}

}
