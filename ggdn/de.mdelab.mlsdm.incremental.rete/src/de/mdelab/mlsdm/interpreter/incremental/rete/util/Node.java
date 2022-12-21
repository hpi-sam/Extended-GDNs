package de.mdelab.mlsdm.interpreter.incremental.rete.util;

import java.util.ArrayList;
import java.util.List;

public class Node<NodeType, EdgeType> {

	public int id;
	public NodeType value;
	
	public List<Edge<NodeType, EdgeType>> out = new ArrayList<Edge<NodeType, EdgeType>>();
	public List<Edge<NodeType, EdgeType>> in = new ArrayList<Edge<NodeType, EdgeType>>();;

	public Node(int id, NodeType value) {
		this.id = id;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "[" + value.toString() + "]";
	}
}
