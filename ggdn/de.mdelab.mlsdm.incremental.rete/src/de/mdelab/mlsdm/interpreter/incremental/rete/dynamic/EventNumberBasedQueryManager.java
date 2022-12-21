package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.interpreter.core.SDMException;

public class EventNumberBasedQueryManager extends CostAwareDynamicQueryManager {

	private static final long DEFAULT_RECOMPUTE_THRESHOLD = 10000;
	
	private long eventsSinceRecompute;
	private long recomputeThreshold;

	public EventNumberBasedQueryManager(StoryPattern storyPattern, EObject... eObjects) throws SDMException {
		this(storyPattern, DEFAULT_RECOMPUTE_THRESHOLD, eObjects);
	}

	public EventNumberBasedQueryManager(StoryPattern storyPattern, long recomputeThreshold, EObject... eObjects) throws SDMException {
		super(storyPattern, eObjects);
		this.recomputeThreshold = recomputeThreshold;
		this.eventsSinceRecompute = 0;
	}

	@Override
	protected void initializeReteNet() {
		super.initializeReteNet();
		updateAfterRecompute();
	}
	
	@Override
	public boolean triggerRecomputation() {
		return eventsSinceRecompute >= recomputeThreshold;
	}

	@Override
	protected void updateAfterRecompute() {
		super.updateAfterRecompute();
		eventsSinceRecompute = 0;
	}

	@Override
	protected void flushEvent(SDMChangeEvent change) {
		super.flushEvent(change);
		eventsSinceRecompute++;
	}
	
}
