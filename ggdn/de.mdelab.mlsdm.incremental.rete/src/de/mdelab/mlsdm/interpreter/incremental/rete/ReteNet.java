package de.mdelab.mlsdm.interpreter.incremental.rete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.mdelab.mlsdm.interpreter.incremental.rete.diameter.PatternPartition;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Node;

public class ReteNet {

	private ReteNode root;
	private Map<Object, ReteNode> nodes;
	
	public ReteNet() {
		this.nodes = new LinkedHashMap<Object, ReteNode>();
	}

	public void addNode(Object key, ReteNode node) {
		if(node.getNet() != null) {
			node.getNet().removeNode(node);
		}
		nodes.put(key, node);
		node.setNet(this);
	}

	public void removeNode(ReteNode node) {
		Object key = null;
		for(Entry<Object, ReteNode> entry:nodes.entrySet()) {
			if(entry.getValue() == node) {
				key = entry.getKey();
				break;
			}
		}

		node.setNet(null);
		nodes.remove(key);
	}
	
	public void addNode(ReteNode node) {
		addNode(new Object(), node);
	}

	public void addNodes(Map<Object, ReteNode> nodes) {
		for(Entry<Object, ReteNode> entry:new ArrayList<Entry<Object, ReteNode>>(nodes.entrySet())) {
			addNode(entry.getKey(), entry.getValue());
		}
	}

	public void addNodes(Collection<? extends ReteNode> nodes) {
		for(ReteNode node:nodes) {
			addNode(node);
		}
	}

	public ReteNode getRoot() {
		return root;
	}

	public void setRoot(ReteNode root) {
		this.root = root;
	}

	public Map<Object, ReteNode> getNodes() {
		return nodes;
	}

	public ReteNode getNode(Node<PatternPartition, Boolean> partition) {
		return nodes.get(partition);
	}

}
