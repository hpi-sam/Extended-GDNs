package de.mdelab.mlsdm.interpreter.incremental.rete.util;

import java.util.ArrayList;
import java.util.List;

public class WeightedGraph<NodeType, EdgeType> {

	public List<Node<NodeType, EdgeType>> nodes = new ArrayList<Node<NodeType, EdgeType>>();
	public List<DoublyWeightedEdge<NodeType, EdgeType>> edges = new ArrayList<DoublyWeightedEdge<NodeType, EdgeType>>();
	
	public Node<NodeType, EdgeType> getNode(int id) {
		for(Node<NodeType, EdgeType> node:nodes) {
			if(node.id == id) {
				return node;
			}
		}
		return null;
	}

}
