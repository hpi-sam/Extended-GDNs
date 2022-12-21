package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.Activity;
import de.mdelab.mlsdm.ActivityEdge;
import de.mdelab.mlsdm.ActivityNode;
import de.mdelab.mlsdm.interpreter.MLSDMExpressionInterpreterManager;
import de.mdelab.mlsdm.interpreter.facade.MLSDMMetamodelFacadeFactory;
import de.mdelab.mlsdm.interpreter.notifications.MLSDMNotificationEmitter;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost.LWCostFunction;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost.LWCostFunctionFactory;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost.LWDynamicCostFunction;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.expressions.LWICLExpressionInterpreter;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.ocl.LWOCLExpressionInterpreter;
import de.mdelab.mlstorypatterns.AbstractStoryPatternLink;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.interpreter.core.SDMException;
import de.mdelab.sdm.interpreter.core.patternmatcher.StoryPatternMatcher;
import de.mdelab.sdm.interpreter.core.variables.NotifierVariablesScope;
import de.mdelab.expressions.interpreter.core.variables.Variable;

public class MLSDMLightweightMatcher extends StoryPatternMatcher<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression>{
	
	public static int MATCHING_ATTEMPTS = 0;
	
	private static final MLSDMMatch NO_MATCH = new MLSDMMatch();
	
	private LWExpressionInterpreterManager expressionInterpreterManager;
	
	private Map<String, Variable<EClassifier>> contextVariables;
	private Map<String, MLSDMLightweightPN> name2PN;
	
	private LWCostFunction costFunction;
	
	private MLSDMLightweightSM sm;
	private Set<Object> matchedInstances;
	private MLSDMMatch currentMatch;
	private List<MLSDMLightweightPC> matchingStack;
	private boolean newMatch;
	
	public MLSDMLightweightMatcher(StoryPattern pattern, MLSDMReferenceIndex ra, Map<String, Variable<EClassifier>> contextVariables) {
		this(pattern, ra, contextVariables, new LWDynamicCostFunction());
	}

	public MLSDMLightweightMatcher(StoryPattern pattern, MLSDMReferenceIndex ra, Map<String, Variable<EClassifier>> contextVariables, LWCostFunctionFactory cfFactory) {
		this(pattern, ra, contextVariables, (LWCostFunction) null);
		costFunction = cfFactory.createCostFunction(this);
	}

	private MLSDMLightweightMatcher(StoryPattern pattern, MLSDMReferenceIndex ra, Map<String, Variable<EClassifier>> contextVariables, LWCostFunction costFunction) {
		//TODO we only call this super constructor for compliance with the interface - none of this is actually used...
		super(pattern, new NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression>(new MLSDMNotificationEmitter()), MLSDMMetamodelFacadeFactory.INSTANCE, new MLSDMExpressionInterpreterManager(MLSDMLightweightMatcher.class.getClassLoader()), new MLSDMNotificationEmitter());
		
		this.matchedInstances = new HashSet<Object>();
		this.contextVariables = new HashMap<String, Variable<EClassifier>>(contextVariables);
		this.costFunction = costFunction;
		this.newMatch = true;
		
		initializeDefaultExpressionInterpreterManager();

		initializeSM(pattern, ra);
		
		this.currentMatch = new MLSDMMatch(sm);
		this.matchingStack = new ArrayList<MLSDMLightweightPC>(sm.getPatternConstraints().size());
		this.name2PN = new HashMap<String, MLSDMLightweightPN>();
		for(MLSDMLightweightPN pn:sm.getPatternNodes()) {
			name2PN.put(pn.getName(), pn);
		}
	}

	private void initializeDefaultExpressionInterpreterManager() {
		expressionInterpreterManager = new LWExpressionInterpreterManager();
		expressionInterpreterManager.registerExpressionInterpreter("OCL", new LWOCLExpressionInterpreter(contextVariables));
		expressionInterpreterManager.registerExpressionInterpreter("ICL", new LWICLExpressionInterpreter(contextVariables, expressionInterpreterManager));
	}

	public LWExpressionInterpreterManager getExpressionInterpreterManager() {
		return expressionInterpreterManager;
	}
	
	private void initializeSM(StoryPattern pattern, MLSDMReferenceIndex ra) {
		MLSDMLightweightSMBuilder builder = new MLSDMLightweightSMBuilder(this, ra);
		
		sm = builder.createSearchModel(pattern);
		for(MLSDMLightweightPC pc:sm.getPatternConstraints()) {
			if(pc.getActiveAction() != null) {
				pc.getActiveAction().updateCardinalityEstimate();
			}
		}
	}

	public boolean findNextMatch() {
		return getNextMatch() != NO_MATCH;
	}

	public MLSDMMatch getNextMatch() {
		if(matchingStack.isEmpty()) {
			return extendMapping();
		}
		else {
			return continueCurrentExtension();
		}
	}

	private MLSDMMatch extendMapping() {
		if(sm.getBoundPatternNodeNumber() == sm.getPatternNodes().size()) {
			if(newMatch && checkRemainingConstraints()) {
				return emitMatch();
			}
			else {
				return NO_MATCH;
			}
		}
		
		MLSDMLightweightPC pc = selectNextConstraint();
		pc.setActive(true);
		MLSDMLightweightMA activeMA = pc.getActiveAction();
		matchingStack.add(pc);
		while(activeMA.apply()) {
			MLSDMMatch match = extendMapping();
			if(match != NO_MATCH) {
				return match;
			}
			else {
				activeMA.revertMapping();
			}
		}
		activeMA.rollBack();
		pc.setActive(false);
		matchingStack.remove(matchingStack.size() - 1);
		return NO_MATCH;
	}
	
	private MLSDMMatch continueCurrentExtension() {
		if(matchingStack.size() == 0) {
			return NO_MATCH;
		}
		MLSDMLightweightPC pc = matchingStack.get(matchingStack.size() - 1);
		MLSDMLightweightMA activeMA = pc.getActiveAction();
		activeMA.revertMapping();
		while(activeMA.apply()) {
			MLSDMMatch match = extendMapping();
			if(match != NO_MATCH) {
				return match;
			}
			else {
				activeMA.revertMapping();
			}
		}
		activeMA.rollBack();
		pc.setActive(false);
		matchingStack.remove(matchingStack.size() - 1);
		return continueCurrentExtension();
	}

	private MLSDMLightweightPC selectNextConstraint() {
		double minCost = MLSDMLightweightPC.MATCHING_NOT_POSSIBLE;
		MLSDMLightweightPC cheapestPC = null;
		for(MLSDMLightweightPC pc:sm.getPatternConstraints()) {
			if(!pc.isActive() && pc.getActiveAction() != null) {
				double cost = costFunction.computeCost(pc.getActiveAction());
				if(cost < minCost) {
					cheapestPC = pc;
					minCost = cost;
				}
			}
		}
		return cheapestPC;
	}

	private boolean checkRemainingConstraints() {
		for(MLSDMLightweightPC pc:sm.getPatternConstraints()) {
			if(pc.isActive()) {
				continue;
			}
			
			if(!pc.getExplicitCheckAction().check()) {
				return false;
			}
		}
		return true;
	}

	protected MLSDMMatch emitMatch() {
		newMatch = false;
		MLSDMMatch emittedMatch = currentMatch.clone();
		return emittedMatch;
	}
	
	public boolean matchPatternNode(MLSDMLightweightPN pn, EObject mapping) {
		if(matchedInstances.contains(mapping)) {
			return false;
		}
		
		MATCHING_ATTEMPTS++;
		
		if(pn.getMapping() != null) {
			matchedInstances.remove(pn.getMapping());
		}
		matchedInstances.add(mapping);
		
		pn.setBound(true);
		pn.setMapping((EObject) mapping);
		pn.updateDependentPCs();
		
		currentMatch.setMapping(pn.getId(), mapping);
		newMatch = true;
		return true;
	}
	
	public void unmatchPatternNode(MLSDMLightweightPN pn) {
		matchedInstances.remove(pn.getMapping());
		
		pn.setBound(false);
		pn.setMapping(null);
		pn.updateDependentPCs();
		
		currentMatch.setMapping(pn.getId(), null);
	}

	public Map<String, EObject> getCurrentMatch() {
		return currentMatch;
	}
	
	public void addContextVariable(Variable<EClassifier> variable) {
		contextVariables.put(variable.getName(), variable);
	}

	public Variable<EClassifier> getContextVariable(String name) {
		return contextVariables.get(name);
	}

	public boolean addMapping(String name, EObject mapping) {
		return matchPatternNode(name2PN.get(name), mapping);
	}
	
	@Override
	public void applyMatch() throws SDMException {
		// TODO Auto-generated method stub
		
	}

	public MLSDMLightweightSM getSM() {
		return sm;
	}
	
	public Set<Object> getMatchedInstances() {
		return matchedInstances;
	}
	
	@Override
	public void reset() {
		matchedInstances.clear();
		matchingStack.clear();
		for(MLSDMLightweightPN pn:sm.getPatternNodes()) {
			if(pn.getMapping() != null) {
				unmatchPatternNode(pn);
			}
		}
		currentMatch = new MLSDMMatch(sm);
	}

}
