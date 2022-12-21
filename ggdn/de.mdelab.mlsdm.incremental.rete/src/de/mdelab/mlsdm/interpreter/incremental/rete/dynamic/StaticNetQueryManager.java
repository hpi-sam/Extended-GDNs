package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.interpreter.core.SDMException;

public class StaticNetQueryManager extends DynamicReteQueryManager {

	public StaticNetQueryManager(StoryPattern storyPattern, EObject... eObjects) throws SDMException {
		super(storyPattern, eObjects);
	}

	@Override
	public boolean triggerRecomputation() {
		return false;
	}

	protected boolean recomputeDynamicNodes() {
		return false;
	}

}
