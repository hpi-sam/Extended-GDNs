package de.mdelab.mlsdm.interpreter.incremental.rete.diameter;

import java.util.ArrayList;
import java.util.Collection;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.WeightedGraph;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;

public class PatternPartition extends WeightedGraph<AbstractStoryPatternObject, StoryPatternLink> {

	public double cost;
	public Collection<StoryPattern> nacs;
	public Collection<MLExpression> expressionConstraints;
	public Collection<PartitionConstraint> allConstraints;

	public PatternPartition() {
		super();
		this.nacs = new ArrayList<StoryPattern>();
		this.expressionConstraints = new ArrayList<MLExpression>();
		this.allConstraints = new ArrayList<PartitionConstraint>();
	}
	
	public PatternPartition shallowCopy() {
		PatternPartition copy = new PatternPartition();
		copy.nodes.addAll(nodes);
		copy.edges.addAll(edges);
		copy.cost = cost;
		copy.nacs.addAll(nacs);
		copy.expressionConstraints.addAll(expressionConstraints);
		copy.allConstraints.addAll(allConstraints);
		return copy;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for(int i = 0; i < nodes.size(); i++) {
			if(i != 0) {
				sb.append(", ");
			}
			sb.append(nodes.get(i).value.getName());
		}
		sb.append(")");
		return sb.toString();
	}
	
}
