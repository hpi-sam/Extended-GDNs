package de.mdelab.mlsdm.incremental.lightweight;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;

import de.mdelab.mlsdm.incremental.SDMGDNQueryManager;
import de.mdelab.mlsdm.incremental.SDMLogger;
import de.mdelab.mlsdm.incremental.SDMPatternQuery;
import de.mdelab.mlsdm.incremental.fragment.SDMIndexFragment;
import de.mdelab.mlsdm.mlindices.NotifyingIndex;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.interpreter.core.SDMException;
import de.mdelab.expressions.interpreter.core.variables.Variable;

public class SDMQueryManagerLightweight extends SDMGDNQueryManager {

	public SDMQueryManagerLightweight() {
		super();
	}

	public SDMQueryManagerLightweight(SDMLogger logger) {
		super(logger);
	}

	@Override
	protected SDMPatternQuery createQuery(StoryPattern storyPattern, Map<NotifyingIndex, SDMIndexFragment> dependencyIndexFragments, Collection<Variable<EClassifier>> inputParameters) throws SDMException {
		return new SDMQueryLightweight(this, storyPattern, dependencyIndexFragments, inputParameters, logger);
	}

}
