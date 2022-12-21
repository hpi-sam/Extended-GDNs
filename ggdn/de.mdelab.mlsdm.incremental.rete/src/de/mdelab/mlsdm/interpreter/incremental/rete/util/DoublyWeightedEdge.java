package de.mdelab.mlsdm.interpreter.incremental.rete.util;

public class DoublyWeightedEdge<NodeType, EdgeType> extends Edge<NodeType, EdgeType> {

	public double forwardWeight;
	public double backwardWeight;
	
	public DoublyWeightedEdge(Node<NodeType, EdgeType> source, Node<NodeType, EdgeType> target, EdgeType value, double forwardWeight, double backwardWeight) {
		super(source, target, value);
		this.forwardWeight = forwardWeight;
		this.backwardWeight = backwardWeight;
	}

}
