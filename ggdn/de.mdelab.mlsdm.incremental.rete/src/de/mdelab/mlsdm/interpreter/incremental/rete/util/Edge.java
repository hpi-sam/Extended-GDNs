package de.mdelab.mlsdm.interpreter.incremental.rete.util;

public class Edge<NodeType, EdgeType> {

	public Node<NodeType, EdgeType> source;
	public Node<NodeType, EdgeType> target;
	public EdgeType value;
	
	public Edge(Node<NodeType, EdgeType> source, Node<NodeType, EdgeType> target, EdgeType value) {
		this.source = source;
		this.target = target;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "[" + value.toString() + "]";
	}
}
