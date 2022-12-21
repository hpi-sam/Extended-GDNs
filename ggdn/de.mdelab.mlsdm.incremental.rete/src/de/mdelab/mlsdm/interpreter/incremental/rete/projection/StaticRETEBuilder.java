package de.mdelab.mlsdm.interpreter.incremental.rete.projection;

import de.mdelab.mlsdm.interpreter.incremental.rete.ReteQueryManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.StoryPatternModelIndex;
import de.mdelab.mlsdm.interpreter.incremental.rete.diameter.DiameterBasedReteBuilder;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.sdm.interpreter.core.SDMException;

public class StaticRETEBuilder extends DiameterBasedReteBuilder{

	private static final double DEFAULT_DOMAIN_SIZE = 100;
	private static final double DEFAULT_FILTERING = 0.1;
	private static final double DEFAULT_CARDINALITY = 10;

	public StaticRETEBuilder(StoryPattern storyPattern, ReteQueryManager queryManager) throws SDMException {
		super(storyPattern, queryManager);
	}

	public StaticRETEBuilder(StoryPattern storyPattern, StoryPatternModelIndex queryManager) throws SDMException {
		super(storyPattern, queryManager);
	}

	protected double getFiltering(StoryPatternLink value) {
		return DEFAULT_FILTERING;
	}

	protected double getDomainSize(AbstractStoryPatternObject value) {
		return DEFAULT_DOMAIN_SIZE;
	}

	protected double getForwardWeight(StoryPatternLink link) {
		return DEFAULT_CARDINALITY;
	}

	protected double getBackwardWeight(StoryPatternLink link) {
		return DEFAULT_CARDINALITY;
	}

}
