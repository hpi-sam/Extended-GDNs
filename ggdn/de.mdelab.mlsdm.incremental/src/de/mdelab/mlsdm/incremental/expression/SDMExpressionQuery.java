package de.mdelab.mlsdm.incremental.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import de.mdelab.expressions.interpreter.core.ExpressionInterpreter;
import de.mdelab.expressions.interpreter.core.ExpressionInterpreterManager;
import de.mdelab.expressions.interpreter.core.ExpressionsInterpreterException;
import de.mdelab.expressions.interpreter.core.variables.Variable;
import de.mdelab.expressions.interpreter.core.variables.VariablesScope;
import de.mdelab.expressions.interpreter.ocl.OCLExpressionInterpreter;
import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.Activity;
import de.mdelab.mlsdm.ActivityEdge;
import de.mdelab.mlsdm.ActivityNode;
import de.mdelab.mlsdm.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMIndexChange;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent.SDMChangeEnum;
import de.mdelab.mlsdm.interpreter.facade.MLSDMMetamodelFacadeFactory;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.expressions.MLSDMOCLExpressionAnalyzer;
import de.mdelab.mlsdm.mlindices.Index;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.mlsdm.mlindices.MlindicesFactory;
import de.mdelab.mlstorypatterns.AbstractStoryPatternLink;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.icl.iCL.ICLConstraint;
import de.mdelab.sdm.icl.iCL.ICLParameter;
import de.mdelab.sdm.icl.iCL.ICLVariable;
import de.mdelab.sdm.interpreter.core.notifications.NotificationEmitter;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.expressions.ExpressionAnalyzer;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.expressions.FeatureAccess;
import de.mdelab.sdm.interpreter.core.variables.NotifierVariablesScope;

public class SDMExpressionQuery extends SDMQuery {

	protected ICLConstraint inConstraint;
	protected MLExpression valueExpression;
	protected List<Variable<EClassifier>> variables;
	
	protected SDMInterfaceIndex inIndex;
	protected Index outIndex;
	
	protected NotifierVariablesScope<?, ?, ?, ?, ?, ?, EClassifier, EStructuralFeature, MLExpression> variablesScope;
	protected ExpressionInterpreter<MLExpression, EClassifier> expressionInterpreter;
	protected ExpressionAnalyzer<EClassifier, EStructuralFeature, MLExpression> expressionAnalyzer;

	protected List<Object> undefinedQuery;
	protected List<Object> undefinedQueryOut;
	protected Map<String, Integer> parameterToIndex;
	private Map<EStructuralFeature, Set<FeatureAccess<EStructuralFeature>>> relevantFeatures;
	
	public SDMExpressionQuery(SDMQueryManager manager, ICLConstraint inConstraint, List<Variable<EClassifier>> variables, MLExpression valueExpression, SDMInterfaceIndex inIndex) {
		super(manager);
		this.inConstraint = inConstraint;
		this.valueExpression = valueExpression;
		this.variables = variables;
		this.inIndex = inIndex;
		this.variablesScope = createDummyVariablesScope();
		this.undefinedQuery = createUndefinedQuery(variables.size());
		this.undefinedQueryOut = createUndefinedQuery(variables.size() + 1);
		this.parameterToIndex = createParameterToIndexMap();
		this.expressionInterpreter = createExpressionInterpreter();
		this.expressionAnalyzer = createExpressionAnalyzer();
		this.relevantFeatures = getRelevantFeatures();
		this.outIndex = createOutIndex();
	}
	
	private NotifierVariablesScope<?, ?, ?, ?, ?, ?, EClassifier, EStructuralFeature, MLExpression> createDummyVariablesScope() {
		NotifierVariablesScope<?, ?, ?, ?, ?, ?, EClassifier, EStructuralFeature, MLExpression> dummyScope = new NotifierVariablesScope<Activity, ActivityNode, ActivityEdge, StoryPattern, AbstractStoryPatternObject, AbstractStoryPatternLink, EClassifier, EStructuralFeature, MLExpression>(
											new NotificationEmitter<>());
		for(Variable<EClassifier> var:variables) {
			dummyScope.createVariable(var.getName(), var.getClassifier(), null);
		}
		return dummyScope;
	}

	private Map<EStructuralFeature, Set<FeatureAccess<EStructuralFeature>>> getRelevantFeatures() {
		Map<EStructuralFeature, Set<FeatureAccess<EStructuralFeature>>> relevantFeatures = new LinkedHashMap<EStructuralFeature, Set<FeatureAccess<EStructuralFeature>>>();
		for(FeatureAccess<EStructuralFeature> access:expressionAnalyzer.getFeatureAccesses(valueExpression)) {
			if(!relevantFeatures.containsKey(access.getFeature())) {
				relevantFeatures.put(access.getFeature(), new LinkedHashSet<FeatureAccess<EStructuralFeature>>());
			}
			relevantFeatures.get(access.getFeature()).add(access);
		}
		return relevantFeatures;
	}

	//TODO this should probably be a static function somewhere
	private ExpressionInterpreter<MLExpression, EClassifier> createExpressionInterpreter() {
		ExpressionInterpreterManager<EClassifier, EStructuralFeature, MLExpression> manager =
				new ExpressionInterpreterManager<EClassifier, EStructuralFeature, MLExpression>(MLSDMMetamodelFacadeFactory.INSTANCE.getExpressionFacade(), SDMExpressionQuery.class.getClassLoader());
		ExpressionInterpreter<MLExpression, EClassifier> interpreter = null;
		switch(valueExpression.getExpressionLanguage()) {
			case "OCL":
				interpreter = createOCLExpressionInterpreter();
				break;
		}
		manager.registerExpressionInterpreter(interpreter, valueExpression.getExpressionLanguage(), "1.0");
		return interpreter;
	}

	private ExpressionInterpreter<MLExpression, EClassifier> createOCLExpressionInterpreter() {
		return new OCLExpressionInterpreter<MLExpression>();
	}

	//TODO this should probably be a static function somewhere
	private ExpressionAnalyzer<EClassifier, EStructuralFeature, MLExpression> createExpressionAnalyzer() {
		switch(valueExpression.getExpressionLanguage()) {
			case "OCL":
				return createOCLExpressionAnalyzer();
			default:
				return null;
		}
	}

	private ExpressionAnalyzer<EClassifier, EStructuralFeature, MLExpression> createOCLExpressionAnalyzer() {
		ExpressionAnalyzer<EClassifier, EStructuralFeature, MLExpression> analyzer = new MLSDMOCLExpressionAnalyzer();
		analyzer.setVariablesScope(variablesScope);
		return analyzer;
	}

	protected Index createOutIndex() {
		Index index = MlindicesFactory.eINSTANCE.createStagedHashIndex();
		return index;
	}

	protected List<Object> createUndefinedQuery(int size) {
		List<Object> query = new ArrayList<Object>();
		for(int i = 0; i < size; i++) {
			query.add(Index.UNDEFINED_PARAMETER);
			query.add(Index.UNDEFINED_PARAMETER);
		}	
		return query;
	}

	protected Map<String, Integer> createParameterToIndexMap() {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		int i;
		for(i = 0; i < inConstraint.getParameters().getList().size(); i++) {
			ICLParameter parameter = inConstraint.getParameters().getList().get(i);
			if(parameter instanceof ICLVariable) {
				map.put(((ICLVariable) parameter).getName(), i);
			}
		}
		map.put("value", i);
		return map;
	}

	protected void addInIndexEntry(IndexEntry sourceEntry) {
		createVariables(sourceEntry);
		try {
			Object value = expressionInterpreter.evaluateExpression(valueExpression, EcorePackage.Literals.EOBJECT, null, variablesScope).getValue();
			List<Object> outKey = new ArrayList<Object>(sourceEntry.getKey());
			outKey.add(value);
			outIndex.addEntry(outKey);
		} catch (ExpressionsInterpreterException e) {
			e.printStackTrace();
		}
	}
	
	protected void removeInIndexEntry(IndexEntry sourceEntry) {
		List<Object> query = createIndexQuery(sourceEntry);
		Collection<IndexEntry> affectedEntries = new ArrayList<IndexEntry>();
		for(Iterator<IndexEntry> it = inIndex.getEntries(query); it.hasNext();) {
			affectedEntries.add(it.next());
		}
		
		for(IndexEntry affectedEntry:affectedEntries) {
			outIndex.remove(affectedEntry.getKey());
		}
	}
	
	private List<Object> createIndexQuery(IndexEntry sourceEntry) {
		List<Object> query = new ArrayList<Object>();
		for(Object o:sourceEntry.getKey()) {
			query.add(o);
			query.add(o);
		}
		query.add(Index.UNDEFINED_PARAMETER);
		query.add(Index.UNDEFINED_PARAMETER);
		return query;
	}

	@Override
	public void findInitialMatches() {
		for(Iterator<IndexEntry> entryIterator = inIndex.getEntries(undefinedQuery); entryIterator.hasNext();) {
			IndexEntry inEntry = entryIterator.next();
			addInIndexEntry(inEntry);
		}
	}

	protected void createVariables(IndexEntry entry) {
		variablesScope.clear();
		for(int i = 0; i < inConstraint.getParameters().getList().size(); i++) {
			ICLParameter parameter = inConstraint.getParameters().getList().get(i);
			if(parameter instanceof ICLVariable) {
				Object value = entry.getKey().get(i);
				createVariable(((ICLVariable) parameter).getName(), value);
			}
		}
	}

	protected void createVariable(String name, Object value) {
		variablesScope.createVariable(name, getClassifier(value), value);
	}
	
	private EClassifier getClassifier(Object value) {
		if(value instanceof Collection) {
			return EcorePackage.Literals.EE_LIST; 
		}
		else if(value instanceof EObject) {
			return ((EObject) value).eClass();
		}
		else if(value instanceof Integer) {
			return EcorePackage.Literals.EINT;
		}
		else if(value instanceof Long) {
			return EcorePackage.Literals.ELONG;
		}
		else if(value instanceof String) {
			return EcorePackage.Literals.ESTRING;
		}
		else if(value instanceof Boolean) {
			return EcorePackage.Literals.EBOOLEAN;
		}
		return null;
	}

	@Override
	public Iterator<SDMQueryMatch> getMatches() {
		updateMatches();
		
		return new Iterator<SDMQueryMatch>() {

			Iterator<IndexEntry> indexIterator = outIndex.getEntries(undefinedQueryOut);
			
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
	protected void doUpdateMatches() {
		for(SDMChangeEvent change:unhandledChanges) {
			if(change.getType() == inIndex.getWrappedIndex()) {
				if(change.getModifier() == SDMChangeEnum.DELETE) {
					removeInIndexEntry(((SDMIndexChange) change).getEntry());
				}
				else {
					addInIndexEntry(((SDMIndexChange) change).getEntry());
				}
			}
		}
		//TODO consider feature changes
	}

	@Override
	protected boolean isRelevant(SDMChangeEvent change) {
		return change.getType() == inIndex.getWrappedIndex() || relevantFeatures.containsKey(change.getType());
	}

}
