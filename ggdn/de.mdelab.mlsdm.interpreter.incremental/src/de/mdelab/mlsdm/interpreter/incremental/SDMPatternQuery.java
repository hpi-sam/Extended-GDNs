package de.mdelab.mlsdm.interpreter.incremental;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.Activity;
import de.mdelab.mlsdm.ActivityEdge;
import de.mdelab.mlsdm.ActivityNode;
import de.mdelab.mlsdm.interpreter.MLSDMExpressionInterpreterManager;
import de.mdelab.mlsdm.interpreter.facade.MLSDMMetamodelFacadeFactory;
import de.mdelab.mlsdm.interpreter.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.interpreter.incremental.change.SDMChangeEvent.SDMChangeEnum;
import de.mdelab.mlsdm.interpreter.incremental.fragment.SDMEdgeFragment;
import de.mdelab.mlsdm.interpreter.incremental.fragment.SDMExpressionFragment;
import de.mdelab.mlsdm.interpreter.incremental.fragment.SDMFragment;
import de.mdelab.mlsdm.interpreter.incremental.fragment.SDMIndexFragment;
import de.mdelab.mlsdm.interpreter.incremental.fragment.SDMNACEdgeFragment;
import de.mdelab.mlsdm.interpreter.incremental.fragment.SDMNodeFragment;
import de.mdelab.mlsdm.interpreter.searchModel.MLSDMSearchModelBasedInterpreter;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMSearchModelBasedMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.expressions.MLSDMAdapterBackedOCLExpressionInterpreter;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.expressions.MLSDMCallActionsExpressionAnalyzer;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.expressions.MLSDMICLExpressionAnalyzer;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.expressions.MLSDMOCLExpressionAnalyzer;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.MLSDMStrategyFactory;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.hybrid.MLSDMContinuationCostCalculator.CostModel;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.hybrid.MLSDMHybridSelectionStrategy;
//import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.MLSDMHybridSelectionStrategy;
//import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.MLSDMContinuationCostCalculator.CostModel;
import de.mdelab.mlsdm.mlindices.Index;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.mlsdm.mlindices.MlindicesFactory;
import de.mdelab.mlsdm.mlindices.NotifyingIndex;
import de.mdelab.mlstorypatterns.AbstractStoryPatternLink;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.MlstorypatternsPackage;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.mlstorypatterns.StoryPatternObject;
import de.mdelab.sdm.icl.iCL.ICLExpression;
import de.mdelab.sdm.interpreter.core.SDMException;
import de.mdelab.sdm.interpreter.core.notifications.NotificationEmitter;
import de.mdelab.sdm.interpreter.core.patternmatcher.StoryPatternMatcher;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.expressions.FeatureAccess;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.strategy.PatternConstraintSelectionStrategy;
import de.mdelab.sdm.interpreter.core.variables.NotifierVariablesScope;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.expressions.ExpressionAnalyzerManager;
import de.mdelab.expressions.interpreter.callaction.CallActionInterpreter;
import de.mdelab.expressions.interpreter.core.variables.Variable;
import de.mdelab.sdm.interpreter.icl.ICLExpressionInterpreter;

public class SDMPatternQuery extends SDMQuery {

	public static long BINDING_CREATION = 0;
	public static long MATCH_SEARCH = 0;
	
	private static HashMap<String, Object> DEFAULT_MATCHER_PARAMETERS;
	
	protected StoryPatternMatcher<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> matcher;
	protected SDMLogger logger;
	protected SDMQueryManager queryManager;
	protected StoryPattern pattern;
	protected ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression> expressionAnalyzerManager;
	protected Index matches;
	protected Collection<Variable<EClassifier>> inputParameters;
	
	protected Map<String, Integer> parameterToIndex;
	private Map<Integer, AbstractStoryPatternObject> indexToParameter;
	private Collection<SDMChangeEvent> unhandledChanges;
	private Map<Object, Set<SDMFragment>> typeToFragment;
	private NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> variablesScope;
	
	private Collection<SDMQuery> dependencies;
	private List<Object> undefinedIndexQuery;
	
	public SDMPatternQuery(SDMQueryManager queryManager, StoryPattern pattern, Map<NotifyingIndex, SDMIndexFragment> dependencyIndexFragments, Collection<Variable<EClassifier>> inputParameters, SDMLogger logger) throws SDMException {
		super(queryManager);
		this.queryManager = queryManager;
		this.pattern = pattern;
		this.parameterToIndex = new HashMap<String, Integer>();
		this.indexToParameter = new HashMap<Integer, AbstractStoryPatternObject>();
		this.undefinedIndexQuery = new ArrayList<Object>();
		this.matches = MlindicesFactory.eINSTANCE.createStagedHashIndex();
		this.unhandledChanges = new ArrayList<SDMChangeEvent>();
		this.typeToFragment = new HashMap<Object, Set<SDMFragment>>();
		this.inputParameters = inputParameters;
		this.dependencies = new ArrayList<SDMQuery>();
		this.logger = logger;
		this.variablesScope = new NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression>(new NotificationEmitter<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression>());
		
		this.expressionAnalyzerManager = createExpressionAnalyzerManager();
		this.matcher = createMatcher();

		initializeParameterMaps();
		initializeFragmentMap(dependencyIndexFragments);
	}
	
	private ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression> createExpressionAnalyzerManager() {
		ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression> expressionAnalyzerManager = new ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression>(MLSDMMetamodelFacadeFactory.INSTANCE.getExpressionFacade());
		expressionAnalyzerManager.registerExpressionAnalyzer("OCL", new MLSDMOCLExpressionAnalyzer(expressionAnalyzerManager));
		expressionAnalyzerManager.registerExpressionAnalyzer("CallActions", new MLSDMCallActionsExpressionAnalyzer(expressionAnalyzerManager));
		expressionAnalyzerManager.registerExpressionAnalyzer("ICL", new MLSDMICLExpressionAnalyzer(expressionAnalyzerManager));
		expressionAnalyzerManager.setVariablesScope(variablesScope);
		
		return expressionAnalyzerManager;
	}

	protected StoryPatternMatcher<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> createMatcher() throws SDMException {
		MLSDMExpressionInterpreterManager expressionInterpreterManager =
				new MLSDMExpressionInterpreterManager(	SDMPatternQuery.class.getClassLoader(),
														variablesScope.getNotificationEmitter());
		expressionInterpreterManager.registerExpressionInterpreter(new MLSDMAdapterBackedOCLExpressionInterpreter(queryManager.getMetadataIndex()), "OCL", "1.0");
		expressionInterpreterManager.registerExpressionInterpreter(new ICLExpressionInterpreter<MLExpression>(), "ICL", "1.0");
		expressionInterpreterManager.registerExpressionInterpreter(new CallActionInterpreter(), "CallActions", "1.0");
		
		Map<String, Object> parameters = new HashMap<String, Object>(getDefaultMatcherParameters());
		parameters.put(MLSDMSearchModelBasedInterpreter.STRATEGY_FACTORY, new MLSDMStrategyFactory() {

			public PatternConstraintSelectionStrategy<StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> createMatchingStrategy(
					StoryPattern storyPattern, MLSDMReferenceIndex referenceAdapter,
					NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> variablesScope) {
				return new MLSDMHybridSelectionStrategy(queryManager.getMetadataIndex(), CostModel.AVERAGE_CASE, variablesScope);
			}
			
		});
		return new MLSDMSearchModelBasedMatcher(pattern, variablesScope, MLSDMMetamodelFacadeFactory.INSTANCE, expressionInterpreterManager, expressionAnalyzerManager, this.queryManager.getMetadataIndex(), variablesScope.getNotificationEmitter(), parameters);
//		return new MLSDMSearchModelBasedMatcher(pattern, variablesScope, MLSDMMetamodelFacadeFactory.INSTANCE, expressionInterpreterManager, expressionAnalyzerManager, this.queryManager, variablesScope.getNotificationEmitter(), getDefaultMatcherParameters());	
	}

	private static Map<String, Object> getDefaultMatcherParameters() {
		if(DEFAULT_MATCHER_PARAMETERS == null) {
			DEFAULT_MATCHER_PARAMETERS = new HashMap<String, Object>();
			DEFAULT_MATCHER_PARAMETERS.put(MLSDMSearchModelBasedInterpreter.ENABLE_STAGED_INDEX_MATCHING, false);
		}
		return DEFAULT_MATCHER_PARAMETERS;
	}

	private void initializeFragmentMap(Map<NotifyingIndex, SDMIndexFragment> dependencyIndexFragments) throws SDMException {
		createVariables(inputParameters);
		
		Map<String, AbstractStoryPatternObject> spos = new HashMap<String, AbstractStoryPatternObject>();
		for(AbstractStoryPatternObject spo:pattern.getStoryPatternObjects()) {
			spos.put(spo.getName(), spo);
		}
		Set<String> usedSPONames = new HashSet<String>();
		
		createEdgeFragments(spos, usedSPONames);
		createExpressionFragments(spos, dependencyIndexFragments, usedSPONames);
		createNodeFragments(spos, usedSPONames);
		createNACFragments(spos, usedSPONames);
		
		for(AbstractStoryPatternObject spo:pattern.getStoryPatternObjects()) {
			if(usedSPONames.contains(spo.getName())) {
				continue;
			}
			addFragment(spo.getType(), new SDMNodeFragment(spo, false));
		}

		variablesScope.clear();
	}

	private void createNACFragments(
			Map<String, AbstractStoryPatternObject> spos,
			Set<String> usedSPONames) {
		for(StoryPattern nac:pattern.getNegativeApplicationConditions()) {
			for(AbstractStoryPatternLink link:nac.getStoryPatternLinks()) {
				if(link.getSource().getStoryPattern() == pattern || link.getTarget().getStoryPattern() == pattern) {
					addFragment(((StoryPatternLink) link).getFeature(), new SDMNACEdgeFragment((StoryPatternLink) link, pattern));
					usedSPONames.add(link.getSource().getName());
					usedSPONames.add(link.getTarget().getName());
				}
			}
			
			//TODO for now isolated spos in NACs are not supported
		}		
	}

	private void createNodeFragments(
			Map<String, AbstractStoryPatternObject> spos,
			Set<String> usedSPONames) {
		for(AbstractStoryPatternObject spo:pattern.getStoryPatternObjects()) {
			if(!((StoryPatternObject) spo).getConstraints().isEmpty()) {
				for(MLExpression expression:((StoryPatternObject) spo).getConstraints()) {
					for(FeatureAccess<EStructuralFeature> featureAccess:expressionAnalyzerManager.getFeatureAccesses(expression, spo.getType())) {
						AbstractStoryPatternObject spo2 = featureAccess.getVariableName().equals("self") ? 
															spo :
															spos.get(featureAccess.getVariableName());
						addFragment(featureAccess.getFeature(), new SDMExpressionFragment(spo2));
					}
				}
			}
		}
	}

	protected void createExpressionFragments(
			Map<String, AbstractStoryPatternObject> spos,
			Map<NotifyingIndex, SDMIndexFragment> dependencyIndexFragments, Set<String> usedSPONames) {
		NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> dummyScope = createDummyVariablesScope(pattern.getStoryPatternObjects());
		
		expressionAnalyzerManager.setVariablesScope(dummyScope);
		for(MLExpression expression:pattern.getConstraints()) {
			if(expression.getExpressionLanguage().equals("ICL")) {
				ICLExpression iclExpression = (ICLExpression) expressionAnalyzerManager.parseAST(expression, null);
				Object index = getVariable(iclExpression.getIndexID());
				if(dependencyIndexFragments.containsKey(index)) {
					addFragment(index, dependencyIndexFragments.get(index));
					for(String spoName:expressionAnalyzerManager.getVariableNames(expression)) {
						usedSPONames.add(spoName);
					}	
				}
			}
			else {
				for(FeatureAccess<EStructuralFeature> featureAccess:expressionAnalyzerManager.getFeatureAccesses(expression, EcorePackage.Literals.EOBJECT)) {
					AbstractStoryPatternObject spo = spos.get(featureAccess.getVariableName());
					addFragment(featureAccess.getFeature(), new SDMExpressionFragment(spo));
				}
			}
		}
		expressionAnalyzerManager.setVariablesScope(variablesScope);
	}

	protected Object getVariable(String name) {
		return variablesScope.getVariable(name).getValue();
	}

	private void createEdgeFragments(
			Map<String, AbstractStoryPatternObject> spos,
			Set<String> usedSPONames) {
		for(AbstractStoryPatternLink link:pattern.getStoryPatternLinks()) {
			if(link.eClass() == MlstorypatternsPackage.Literals.STORY_PATTERN_LINK) {
				addFragment(((StoryPatternLink) link).getFeature(), new SDMEdgeFragment((StoryPatternLink) link, false));
				usedSPONames.add(link.getSource().getName());
				usedSPONames.add(link.getTarget().getName());
			}
		}
	}

	private NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> createDummyVariablesScope(
			EList<AbstractStoryPatternObject> spos) {
		NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression> dummyScope =
					new NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression>(matcher.getNotificationEmitter());
		for(AbstractStoryPatternObject spo:spos) {
			dummyScope.createVariable(spo.getName(), spo.getType(), null);
		}
		return dummyScope;
	}

	public void addFragment(Object type, SDMFragment fragment) {
		if(!typeToFragment.containsKey(type)) {
			typeToFragment.put(type, new HashSet<SDMFragment>());
		}
		typeToFragment.get(type).add(fragment);
	}

	private void initializeParameterMaps() {
		int index = 0;
		for(AbstractStoryPatternObject spo:pattern.getStoryPatternObjects()) {
			parameterToIndex.put(spo.getName(), index);
			indexToParameter.put(index, spo);
			index++;
		}
		
		for(int i = 0; i < parameterToIndex.size(); i++) {
			undefinedIndexQuery.add(Index.UNDEFINED_PARAMETER);
			undefinedIndexQuery.add(Index.UNDEFINED_PARAMETER);
		}
	}

	@Override
	public void findInitialMatches() {
		List<Variable<EClassifier>> variables = new ArrayList<Variable<EClassifier>>();

//		long start = System.nanoTime();
		findMatches(variables, true);
//		logger.print("initial_" + pattern.getName() + "=" + (System.nanoTime() - start) / 1000000);
	}
	
	protected boolean findMatches(Collection<Variable<EClassifier>> variables, boolean notifyDependencies) {
		variablesScope.clear();
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
		matcher.setVariablesScope(variablesScope);
		
		return newMatch;
	}

	protected void registerCurrentMatch(boolean notifyDependencies) {
		List<Object> entry = new ArrayList<Object>(pattern.getStoryPatternObjects().size());
		for(AbstractStoryPatternObject spo:pattern.getStoryPatternObjects()) {
			Object mapping = variablesScope.getVariable(spo.getName()).getValue();
			entry.add(mapping);
		}
		IndexEntry indexEntry = matches.addEntry(entry);
		
		if(notifyDependencies) {
			for(SDMInterfaceIndex index:interfaceIndices) {
				index.addEntry(new SDMQueryMatch(indexEntry, parameterToIndex));
			}
		}
	}

	protected void createVariables(Collection<Variable<EClassifier>> variables) {
		for(Variable<EClassifier> variable:variables) {
			variablesScope.createVariable(variable.getName(), variable.getClassifier(), variable.getValue());
		}
		for(Variable<EClassifier> variable:inputParameters) {
			variablesScope.createVariable(variable.getName(), variable.getClassifier(), variable.getValue());
		}
	}

	@Override
	public Iterator<SDMQueryMatch> getMatches() {
		updateMatches();
		
		List<Object> indexQuery = undefinedIndexQuery;
		
		return new Iterator<SDMQueryMatch>() {

			Iterator<IndexEntry> indexIterator = matches.getEntries(indexQuery);
			
			@Override
			public boolean hasNext() {
				return indexIterator.hasNext();
			}

			@Override
			public SDMQueryMatch next() {
				IndexEntry indexEntry = indexIterator.next();
				return new SDMQueryMatch(indexEntry, parameterToIndex);
			}
			
		};
	}

	@Override
	public void updateMatches() {
		super.updateMatches();
		
		for(SDMQuery dependency:dependencies) {
			dependency.updateMatches();
		}
		
		Collection<SDMChangeEvent> changes = new ArrayList<SDMChangeEvent>(unhandledChanges);
		unhandledChanges.clear();
//		long start = System.nanoTime();
//		int relevantChanges = changes.size();
		for(SDMChangeEvent change:changes) {
			updateMatches(change);
		}
//		logger.print("changes_" + pattern.getName() + "=" + relevantChanges);
//		logger.print("update_" + pattern.getName() + "=" + (System.nanoTime() - start) / 1000000);
//		logger.print("matches_" + pattern.getName() + "=" + matches.size());
	}
	
	@Override
	public void registerChange(SDMChangeEvent change) {
		Collection<SDMFragment> candidateFragments = getCandidateFragments(change);
		if(candidateFragments != null && !candidateFragments.isEmpty()) {
			unhandledChanges.add(change);
		}
	}

	private void updateMatches(SDMChangeEvent change) {
		for(SDMFragment fragment:getCandidateFragments(change)) {
			if(change.getModifier() == SDMChangeEnum.CREATE) {
				if(fragment.isNegative()) {
					checkExistingMatches(change, fragment);
				}
				else {
					findNewMatches(change, fragment);
				}
			}
			else if(change.getModifier() == SDMChangeEnum.DELETE){
				if(fragment.isNegative()) {
					findNewMatches(change, fragment);
				}
				else {
					checkExistingMatches(change, fragment);
				}
			}
			else if(change.getModifier() == SDMChangeEnum.MODIFY) {
				checkExistingMatches(change, fragment);
				findNewMatches(change, fragment);
			}
		}
	}
	
	private void checkExistingMatches(SDMChangeEvent change, SDMFragment fragment) {
		//find potentially obsolete matches
		List<Object> indexQuery = new ArrayList<Object>();
		for(int i = 0 ; i < parameterToIndex.size(); i++) {
			indexQuery.add(Index.UNDEFINED_PARAMETER);
			indexQuery.add(Index.UNDEFINED_PARAMETER);
		}
		Collection<Variable<EClassifier>> binding = fragment.createCandidateBinding(change);
		
		if(binding != null) {
			for(Variable<EClassifier> variable:binding) {
				int index = parameterToIndex.get(variable.getName());
				indexQuery.set(2 * index, variable.getValue());
				indexQuery.set(2 * index + 1, variable.getValue());
			}
			
			List<IndexEntry> matchingEntries = new ArrayList<IndexEntry>();
			for(Iterator<IndexEntry> it = matches.getEntries(indexQuery); it.hasNext();) {
				IndexEntry entry = it.next();
				matchingEntries.add(entry);
			}
			
			for(IndexEntry entry:matchingEntries) {
				//check if match is still valid
				List<Variable<EClassifier>> variables = new ArrayList<Variable<EClassifier>>();
				for(int i = 0; i < pattern.getStoryPatternObjects().size(); i++) {
					AbstractStoryPatternObject spo = pattern.getStoryPatternObjects().get(i);
					variables.add(new Variable<EClassifier>(spo.getName(), spo.getType(), entry.getKey().get(i)));
				}
				
				boolean valid = findMatches(variables, false);
				
				if(!valid) {
					matches.remove(entry.getKey());
					
					for(SDMInterfaceIndex index:interfaceIndices) {
						index.removeEntry(new SDMQueryMatch(entry, parameterToIndex));
					}
				}
			}
		}
	}

	private void findNewMatches(SDMChangeEvent change, SDMFragment fragment) {
		long start = System.nanoTime();
		Collection<Variable<EClassifier>> binding = fragment.createCandidateBinding(change);
		BINDING_CREATION += System.nanoTime() - start;

		start = System.nanoTime();
		if(binding != null) {
			findMatches(binding, true);
		}
		MATCH_SEARCH += System.nanoTime() - start;
	}

	private Collection<SDMFragment> getCandidateFragments(SDMChangeEvent change) {
		return typeToFragment.get(change.getType());
	}

	public void registerDependency(SDMQuery sdmQuery) {
		dependencies.add(sdmQuery);
	}

}
