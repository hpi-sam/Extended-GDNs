package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight;

import java.util.Collection;

import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.PatternNode;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.SearchModelUpdateTransaction;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.strategy.OrderProducingSelectionStrategy;

public abstract class LightWeightSelectionStrategy<StoryPattern, StoryPatternObject, StoryPatternLink, Classifier, Feature, Expression> extends OrderProducingSelectionStrategy<StoryPattern, StoryPatternObject, StoryPatternLink, Classifier, Feature, Expression>{

	protected void updateSearchModel(
			Collection<PatternNode<StoryPattern, StoryPatternObject, StoryPatternLink, Classifier, Feature, Expression>> newlyBoundPatternNodes,
			boolean updateMatchingCosts) {
		SearchModelUpdateTransaction<StoryPattern, StoryPatternObject, StoryPatternLink, Classifier, Feature, Expression> searchModelUpdate
					= new SearchModelUpdateTransaction<StoryPattern, StoryPatternObject, StoryPatternLink, Classifier, Feature, Expression>();
		
		if(updateMatchingCosts) {
			for(PatternNode<StoryPattern, StoryPatternObject, StoryPatternLink, Classifier, Feature, Expression> patternNode:newlyBoundPatternNodes) {
				updateMatchingCosts(searchModelUpdate, patternNode.getDependantPatternConstraints());
			}
		}
	
		getSearchModelUpdates().push(searchModelUpdate);
		searchModelUpdate.commit();
	}

}
