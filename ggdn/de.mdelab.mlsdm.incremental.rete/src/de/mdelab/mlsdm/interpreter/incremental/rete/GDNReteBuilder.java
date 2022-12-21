package de.mdelab.mlsdm.interpreter.incremental.rete;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.util.EList;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.gdn.GDN;
import de.mdelab.mlsdm.gdn.GDNDependency;
import de.mdelab.mlsdm.gdn.GDNMapping;
import de.mdelab.mlsdm.gdn.GDNNode;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteAntiJoin;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteDomainFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteEdgeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteExpressionFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
//import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIsomorphyFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteJoin;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNodeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteResultProvider;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteUtil;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.ocl.LWOCLExpressionInterpreter;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;

public class GDNReteBuilder {

	private Map<ReteNet, Map<AbstractStoryPatternObject, Integer>> spoPositions;
	private Map<GDNNode, ReteNet> reteNets;
	
	public SDMQuery buildReteQuery(GDN gdn, ReteQueryManager queryManager) {
		return buildReteQuery(gdn, queryManager.getModelIndex());
	}
	
	public SDMQuery buildReteQuery(GDN gdn, StoryPatternModelIndex modelIndex) {
		ReteNet net = buildReteNet(gdn, modelIndex);
		ReteResultProvider resultProvider = new ReteResultProvider();
		int resultSize = gdn.getRoot().getPattern().getStoryPatternObjects().size();
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
	
	public ReteNet buildReteNet(GDN gdn, StoryPatternModelIndex modelIndex) {
		this.reteNets = new LinkedHashMap<GDNNode, ReteNet>();
		this.spoPositions = new LinkedHashMap<ReteNet, Map<AbstractStoryPatternObject, Integer>>();
		ReteNet net = getReteNet(gdn.getRoot(), modelIndex);
		return net;
	}

	private ReteNet getReteNet(GDNNode root, StoryPatternModelIndex modelIndex) {
		if(reteNets.containsKey(root)) {
			return reteNets.get(root);
		}
		
		ReteNet net = new ReteNet();
		Map<AbstractStoryPatternObject, Integer> positions = new LinkedHashMap<AbstractStoryPatternObject, Integer>();
		ReteNode reteRoot;
		
		if(root.getDependencies().isEmpty()) {
			//build input
			StoryPattern pattern = root.getPattern();
			if(pattern.getStoryPatternLinks().isEmpty()) {
				reteRoot = buildNodeInput(root, net, positions, modelIndex);
			}
			else {
				reteRoot = buildEdgeInput(root, net, positions, modelIndex);
			}
		}
		else {
			//build join
			reteRoot = buildJoin(root, net, positions, modelIndex);
		}

		net.addNode(root, reteRoot);
		net.setRoot(reteRoot);
		modelIndex.observePatternStatistics(root.getPattern());
		reteNets.put(root, net);
		spoPositions.put(net, positions);
		return net;
	}

	private ReteNode buildJoin(GDNNode root, ReteNet net,
			Map<AbstractStoryPatternObject, Integer> positions, StoryPatternModelIndex modelIndex) {
		ReteNode reteRoot;
		
		GDNDependency leftDependency = root.getDependencies().get(0);
		GDNDependency rightDependency = root.getDependencies().get(1);
		if(leftDependency.isNegative()) {
			GDNDependency tmp = leftDependency;
			leftDependency = rightDependency;
			rightDependency = tmp;
		}

		LinkedHashSet<AbstractStoryPatternObject> joinSpos = ReteUtil.intersect(getMappedSpos(leftDependency), getMappedSpos(rightDependency));

		Map<Object, ReteNode> subNetNodes = new LinkedHashMap<Object, ReteNode>();
		
		ReteNet leftSubNet = getReteNet(leftDependency.getNode(), modelIndex);
		subNetNodes.putAll(leftSubNet.getNodes());
		Map<AbstractStoryPatternObject, Integer> leftPositions = spoPositions.get(leftSubNet);
		Map<GDNMapping, Integer> leftMaskPositions = computeJoinPositions(leftDependency, leftPositions, joinSpos);
		int[] leftMask = createMask(leftMaskPositions, leftPositions);
		ReteIndexer leftIndexer = new ReteIndexer(leftMask, joinSpos.size(), getIndexerName(leftDependency.getNode().getPattern().getStoryPatternObjects()));
		leftSubNet.getRoot().addSuccessor(leftIndexer);
		net.addNode(leftIndexer);
		
		ReteNet rightSubNet = getReteNet(rightDependency.getNode(), modelIndex);
		subNetNodes.putAll(rightSubNet.getNodes());
		Map<AbstractStoryPatternObject, Integer> rightPositions = spoPositions.get(rightSubNet);
		Map<GDNMapping, Integer> rightMaskPositions = computeJoinPositions(rightDependency, rightPositions, joinSpos);
		int[] rightMask = createMask(rightMaskPositions, rightPositions);
		ReteIndexer rightIndexer = new ReteIndexer(rightMask, joinSpos.size(), getIndexerName(rightDependency.getNode().getPattern().getStoryPatternObjects()));
		rightSubNet.getRoot().addSuccessor(rightIndexer);
		net.addNode(rightIndexer);
		
		net.addNodes(subNetNodes);
		
		if(rightDependency.isNegative()) {
			//NAC
			reteRoot = new ReteAntiJoin(leftIndexer, rightIndexer, joinSpos.size());
			
			for(GDNMapping mapping:leftDependency.getMappings()) {
				int position = leftMaskPositions.get(mapping);
				positions.put(mapping.getChildVertex(), position);
			}
		}
		else {
			reteRoot = new ReteJoin(leftIndexer, rightIndexer, joinSpos.size());
			
			for(GDNMapping mapping:leftDependency.getMappings()) {
				int position = leftMaskPositions.get(mapping);
				positions.put(mapping.getChildVertex(), position);
			}
			for(GDNMapping mapping:rightDependency.getMappings()) {
				if(joinSpos.contains(mapping.getChildVertex())) {
					continue;
				}
				
				int position = rightMaskPositions.get(mapping) + leftDependency.getMappings().size() - joinSpos.size();
				positions.put(mapping.getChildVertex(), position);
			}

//			for(GDNMapping leftMapping:leftDependency.getMappings()) {
//				AbstractStoryPatternObject leftSpo = leftMapping.getChildVertex();
//				if(joinSpos.contains(leftSpo)) {
//					continue;
//				}
//				
//				for(GDNMapping rightMapping:rightDependency.getMappings()) {	//TODO enable isomorphy again
//					AbstractStoryPatternObject rightSpo = rightMapping.getChildVertex();
//					if(joinSpos.contains(rightSpo)) {
//						continue;
//					}
//					
//					if(ReteUtil.typesMatch(leftSpo.getType(), rightSpo.getType())) {
//						net.addNode(reteRoot);
//						ReteIsomorphyFilter isomorphyFilter = new ReteIsomorphyFilter(positions.get(leftSpo), positions.get(rightSpo));
//						reteRoot.addSuccessor(isomorphyFilter);
//						reteRoot = isomorphyFilter;
//					}
//				}
//			}
		}
//		printPositions(positions);
		return reteRoot;
	}

	private void printPositions(
			Map<AbstractStoryPatternObject, Integer> positions) {
		for(Entry<AbstractStoryPatternObject, Integer> position:positions.entrySet()) {
			System.out.println(position.getKey() + "\t" + position.getValue());
		}
		System.out.println("------------------------------------------");
	}

	private String getIndexerName(
			EList<AbstractStoryPatternObject> spos) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < spos.size(); i++) {
			if(i > 0) {
				sb.append(", ");
			}
			sb.append(spos.get(i).getType().getName());
		}
		return sb.toString();
	}

	private ReteNode buildEdgeInput(GDNNode node, ReteNet net,
			Map<AbstractStoryPatternObject, Integer> positions, StoryPatternModelIndex modelIndex) {
		StoryPattern pattern = node.getPattern();
		StoryPatternLink link = (StoryPatternLink) pattern.getStoryPatternLinks().get(0);
		ReteNode reteRoot = new ReteEdgeInput(link, modelIndex);
		positions.put(link.getSource(), 0);
		positions.put(link.getTarget(), 1);
		if(link.getSource().getType() != link.getFeature().getEContainingClass()) {
			net.addNode(reteRoot);
			ReteDomainFilter domainFilter = new ReteDomainFilter(link.getSource().getType(), 0);
			reteRoot.addSuccessor(domainFilter);
			reteRoot = domainFilter;
		}
		if(link.getTarget().getType() != link.getFeature().getEType()) {
			net.addNode(reteRoot);
			ReteDomainFilter domainFilter = new ReteDomainFilter(link.getTarget().getType(), 1);
			reteRoot.addSuccessor(domainFilter);
			reteRoot = domainFilter;
		}
//		if(ReteUtil.typesMatch(link.getSource().getType(), link.getTarget().getType())) {	//TODO enable isomorphy again
//			net.addNode(reteRoot);
//			ReteIsomorphyFilter isomorphyFilter = new ReteIsomorphyFilter(0, 1);
//			reteRoot.addSuccessor(isomorphyFilter);
//			reteRoot = isomorphyFilter;
//		}
		return reteRoot;
	}

	private ReteNode buildNodeInput(GDNNode node, ReteNet net,
			Map<AbstractStoryPatternObject, Integer> positions, StoryPatternModelIndex modelIndex) {
		StoryPattern pattern = node.getPattern();
		AbstractStoryPatternObject spo = pattern.getStoryPatternObjects().get(0);
		ReteNode reteRoot = new ReteNodeInput(spo, modelIndex);
		positions.put(spo, 0);
		net.addNode(reteRoot);
		net.setRoot(reteRoot);

		Map<String, Integer> namePositions = new LinkedHashMap<String, Integer>();
		namePositions.put(spo.getName(), 0);
		for(MLExpression constraint:pattern.getConstraints()) {
			ReteExpressionFilter filter = buildFilter(constraint, namePositions);
			reteRoot.addSuccessor(filter);
			reteRoot = filter;
		}
		
		return reteRoot;
	}

	private ReteExpressionFilter buildFilter(MLExpression constraint, Map<String, Integer> namePositions) {
		ReteExpressionFilter filter = new ReteExpressionFilter(constraint, new LWOCLExpressionInterpreter(Collections.emptyMap()), namePositions);
		return filter;
	}

	private int[] createMask(
			Map<GDNMapping, Integer> maskPositions, Map<AbstractStoryPatternObject, Integer> positions) {
		int[] mask = new int[maskPositions.size()];
		for(Entry<GDNMapping, Integer> entry:maskPositions.entrySet()) {
			mask[entry.getValue()] = positions.get(entry.getKey().getParentVertex());
		}
		return mask;
	}

	private Map<GDNMapping, Integer> computeJoinPositions(GDNDependency dependency,
			Map<AbstractStoryPatternObject, Integer> positions,
			LinkedHashSet<AbstractStoryPatternObject> joinSpos) {
		Map<AbstractStoryPatternObject, GDNMapping> mappings = new LinkedHashMap<AbstractStoryPatternObject, GDNMapping>();
		for(GDNMapping mapping:dependency.getMappings()) {
			mappings.put(mapping.getChildVertex(), mapping);
		}
		
		Map<GDNMapping, Integer> joinPositions = new LinkedHashMap<GDNMapping, Integer>();
		int i = 0;
		for(AbstractStoryPatternObject joinSpo:joinSpos) {
			joinPositions.put(mappings.get(joinSpo), i);
			i++;
		}
		for(GDNMapping mapping:dependency.getMappings()) {
			AbstractStoryPatternObject nonJoinSpo = mapping.getChildVertex();
			if(joinSpos.contains(nonJoinSpo)) {
				continue;
			}

			joinPositions.put(mapping, i);
			i++;
		}
		return joinPositions;
	}

	private Collection<AbstractStoryPatternObject> getMappedSpos(GDNDependency dependency) {
		Set<AbstractStoryPatternObject> mappedSpos = new LinkedHashSet<AbstractStoryPatternObject>();
		
		for(GDNMapping mapping:dependency.getMappings()) {
			mappedSpos.add(mapping.getChildVertex());
		}
		
		return mappedSpos;
	}
	
}
