package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.FeatureSpecification;

public class ModelIndexManager {

	public enum EdgeIndexType {CHECK, TRAVERSE, REVERSE, LOOKUP};
	public enum NodeIndexType {CHECK, LOOKUP};

	public Map<FeatureSpecification, Map<EdgeIndexType, ReteIndexer>> edgeIndices;
	public Map<EClassifier, Map<NodeIndexType, ReteIndexer>> nodeIndices;
	
	public ModelIndexManager() {
		this.edgeIndices = new HashMap<FeatureSpecification, Map<EdgeIndexType, ReteIndexer>>();
		this.nodeIndices = new HashMap<EClassifier, Map<NodeIndexType, ReteIndexer>>();
	}
	
	public void registerEdgeIndex(FeatureSpecification featureSpec, EdgeIndexType type, ReteIndexer indexer) {
		if(!edgeIndices.containsKey(featureSpec)) {
			edgeIndices.put(featureSpec, new HashMap<EdgeIndexType, ReteIndexer>());
		}
		edgeIndices.get(featureSpec).put(type, indexer);
	}
	
	public ReteIndexer getEdgeIndex(FeatureSpecification feature, EdgeIndexType type) {
		if(edgeIndices.containsKey(feature) && edgeIndices.get(feature).containsKey(type)) {
			return edgeIndices.get(feature).get(type);
		}
		return null;
	}

	public void registerNodeIndex(EClassifier classifier, NodeIndexType type, ReteIndexer indexer) {
		if(!nodeIndices.containsKey(classifier)) {
			nodeIndices.put(classifier, new HashMap<NodeIndexType, ReteIndexer>());
		}
		nodeIndices.get(classifier).put(type, indexer);
	}
	
	public ReteIndexer getNodeIndex(EClassifier classifier, NodeIndexType type) {
		if(nodeIndices.containsKey(classifier) && nodeIndices.get(classifier).containsKey(type)) {
			return nodeIndices.get(classifier).get(type);
		}
		return null;
	}

}
