package de.mdelab.mlsdm.incremental.lightweight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.Activity;
import de.mdelab.mlsdm.ActivityEdge;
import de.mdelab.mlsdm.ActivityNode;
import de.mdelab.mlsdm.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.incremental.SDMLogger;
import de.mdelab.mlsdm.incremental.SDMPatternQuery;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.incremental.fragment.SDMIndexFragment;
import de.mdelab.mlsdm.interpreter.MLSDMExpressionInterpreterManager;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost.LWCostFunction;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost.LWCostFunctionFactory;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost.LWHybridCostFunction;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.mlsdm.mlindices.MlindicesPackage;
import de.mdelab.mlsdm.mlindices.NotifyingIndex;
import de.mdelab.mlstorypatterns.AbstractStoryPatternLink;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.interpreter.core.SDMException;
import de.mdelab.sdm.interpreter.core.notifications.NotificationEmitter;
import de.mdelab.sdm.interpreter.core.patternmatcher.StoryPatternMatcher;
import de.mdelab.expressions.interpreter.callaction.CallActionInterpreter;
import de.mdelab.expressions.interpreter.core.variables.Variable;
import de.mdelab.expressions.interpreter.ocl.OCLExpressionInterpreter;
import de.mdelab.sdm.interpreter.icl.ICLExpressionInterpreter;

public class SDMQueryLightweight extends SDMPatternQuery {
	
	public SDMQueryLightweight(SDMQueryManager queryManager, StoryPattern pattern, Map<NotifyingIndex, SDMIndexFragment> dependencyIndexFragments, Collection<Variable<EClassifier>> inputParameters, SDMLogger logger) throws SDMException {
		super(queryManager, pattern, dependencyIndexFragments, inputParameters, logger);
	}

	@Override
	protected StoryPatternMatcher<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> createMatcher() throws SDMException {
		MLSDMExpressionInterpreterManager expressionInterpreterManager =
				new MLSDMExpressionInterpreterManager(	SDMQueryLightweight.class.getClassLoader(),
														new NotificationEmitter<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression>());
		expressionInterpreterManager.registerExpressionInterpreter(new OCLExpressionInterpreter<MLExpression>(), "OCL", "1.0");
		expressionInterpreterManager.registerExpressionInterpreter(new ICLExpressionInterpreter<MLExpression>(), "ICL", "1.0");
		expressionInterpreterManager.registerExpressionInterpreter(new CallActionInterpreter(), "CallActions", "1.0");

		Map<String, Variable<EClassifier>> contextVariables = new HashMap<String, Variable<EClassifier>>();
		
		for(Variable<EClassifier> parameter:this.inputParameters) {
			contextVariables.put(parameter.getName(), parameter);
		}
		
		return new MLSDMLightweightMatcher(pattern, queryManager.getMetadataIndex(), contextVariables, new LWCostFunctionFactory() {
			
			@Override
			public LWCostFunction createCostFunction(MLSDMLightweightMatcher matcher) {
				return new LWHybridCostFunction(matcher.getSM(), queryManager.getMetadataIndex());
			}
		});	
	}

	@Override
	public void findInitialMatches() {
		List<Variable<EClassifier>> variables = new ArrayList<Variable<EClassifier>>();

//		long start = System.nanoTime();
		findMatches(variables, true);
//		logger.print("initial_" + pattern.getName() + "=" + (System.nanoTime() - start) / 1000000);
	}

	@Override
	protected boolean findMatches(Collection<Variable<EClassifier>> variables, boolean notifyDependencies) {
		createVariables(variables);
		
		boolean newMatch = false;
		try {
			while(matcher.findNextMatch()) {
				registerCurrentMatch(notifyDependencies);
				newMatch = true;
			}
		} catch (SDMException e) {
			e.printStackTrace();
		}
		
		matcher.reset();
		
		return newMatch;
	}

	@Override
	protected void registerCurrentMatch(boolean notifyDependencies) {
		List<Object> entry = new ArrayList<Object>(pattern.getStoryPatternObjects().size());
		Map<String, EObject> match = ((MLSDMLightweightMatcher) matcher).getCurrentMatch();
		for(AbstractStoryPatternObject spo:pattern.getStoryPatternObjects()) {
			Object mapping = match.get(spo.getName());
			entry.add(mapping);
		}
		IndexEntry indexEntry = matches.addEntry(entry);
		
		if(notifyDependencies) {
			for(SDMInterfaceIndex index:interfaceIndices) {
				index.addEntry(new SDMQueryMatch(indexEntry, parameterToIndex));
			}
		}
	}

	@Override
	protected void createVariables(Collection<Variable<EClassifier>> variables) {
		super.createVariables(variables);
		//TODO (add parameters to matcher)?
		for(Variable<EClassifier> variable:variables) {
			if(variable.getClassifier() == MlindicesPackage.Literals.INDEX) {
				((MLSDMLightweightMatcher) matcher).addContextVariable(variable);
			}
			else if(variable.getValue() instanceof EObject) {
				((MLSDMLightweightMatcher) matcher).addMapping(variable.getName(), (EObject) variable.getValue());
				//TODO handle case where mapping is not isomorphic
			}
		}
	}

}
