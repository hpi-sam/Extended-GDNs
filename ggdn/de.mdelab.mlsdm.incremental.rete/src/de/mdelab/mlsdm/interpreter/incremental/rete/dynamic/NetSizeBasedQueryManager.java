package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNotifier;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.interpreter.core.SDMException;

public class NetSizeBasedQueryManager extends CostAwareDynamicQueryManager {

	private static final int RECOMPUTE_THRESHOLD = 1;
	
	private long changesSinceRecompute;
	private long currentThreshold;

	public NetSizeBasedQueryManager(StoryPattern storyPattern, EObject... eObjects) throws SDMException {
		super(storyPattern, eObjects);
	}

	@Override
	protected void initializeReteNet() {
		super.initializeReteNet();
		updateAfterRecompute();
	}
	
	@Override
	public boolean triggerRecomputation() {
		return changesSinceRecompute > currentThreshold * RECOMPUTE_THRESHOLD;
	}

	@Override
	protected void updateAfterRecompute() {
		super.updateAfterRecompute();
		long currentSize = computeCurrentNetSize();
		currentThreshold = currentSize != 0 ? currentSize : 1;
		changesSinceRecompute = 0;
	}

	@Override
	public void notifyAddition(ReteTuple tuple, ReteNotifier notifier) {
		super.notifyAddition(tuple, notifier);
		changesSinceRecompute++;
	}

	@Override
	public void notifyRemoval(ReteTuple tuple, ReteNotifier notifier) {
		super.notifyRemoval(tuple, notifier);
		changesSinceRecompute++;
	}
	
}
