package de.mdelab.mlsdm.interpreter.incremental.rete.diameter;

import java.util.ArrayList;
import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.Node;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPatternLink;

public class PartitionConstraint {

	public List<Node<AbstractStoryPatternObject, StoryPatternLink>> requiredNodes = new ArrayList<Node<AbstractStoryPatternObject, StoryPatternLink>>();
	public Object constraint;
	
}
