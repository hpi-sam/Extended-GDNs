package de.mdelab.mlsdm.incremental.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;

import de.mdelab.expressions.interpreter.core.ExpressionsInterpreterException;
import de.mdelab.expressions.interpreter.core.variables.Variable;
import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.mlindices.Index;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.sdm.icl.iCL.ICLConstraint;
import de.mdelab.sdm.icl.iCL.ICLParameter;
import de.mdelab.sdm.icl.iCL.ICLVariable;

public class SDMAggregationExpressionQuery extends SDMExpressionQuery {

	private List<String> groupingVariableNames;
	private int[] groupingIndices;
	
	protected Map<List<Object>, Map<List<Object>, Object>> allGroupValues;
	
	protected MLExpression aggregationExpression;
	
	public SDMAggregationExpressionQuery(SDMQueryManager manager, ICLConstraint inConstraint, List<Variable<EClassifier>> variables, List<Variable<EClassifier>> groupingVariables,
			MLExpression valueExpression, MLExpression aggregationExpression, SDMInterfaceIndex inIndex) {
		super(manager, inConstraint, variables, valueExpression, inIndex);
		this.undefinedQueryOut = createUndefinedQuery(groupingVariables.size() + 1);
		this.aggregationExpression = aggregationExpression;
		this.allGroupValues = new LinkedHashMap<List<Object>, Map<List<Object>, Object>>();
		this.groupingVariableNames = getGroupingVariableNames(groupingVariables);
		this.groupingIndices = getGroupingIndices();
		this.parameterToIndex = createAggregationParameterToIndexMap();
	}

	private List<String> getGroupingVariableNames(List<Variable<EClassifier>> groupingVariables) {
		List<String> names = new ArrayList<String>();
		for(Variable<EClassifier> var:groupingVariables) {
			names.add(var.getName());
		}
		return names;
	}

	private int[] getGroupingIndices() {
		int[] indices = new int[groupingVariableNames.size()];
		for(int i = 0; i < groupingVariableNames.size(); i++) {
			String groupingVariable = groupingVariableNames.get(i);
			for(int j = 0; j < inConstraint.getParameters().getList().size(); j++) {
				ICLParameter parameter = inConstraint.getParameters().getList().get(j);
				if(parameter instanceof ICLVariable) {
					String var = ((ICLVariable) parameter).getName();
					if(groupingVariable.equals(var)) {
						indices[i] = j;
					}
				}
			}
		}
		return indices;
	}

	protected Map<String, Integer> createAggregationParameterToIndexMap() {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		int i;
		for(i = 0; i < groupingVariableNames.size(); i++) {
			map.put(groupingVariableNames.get(i), i);
		}
		map.put("value", i);
		return map;
	}

	protected void addInIndexEntry(IndexEntry sourceEntry) {
		createVariables(sourceEntry);
		try {
			List<Object> group = getGroup(sourceEntry);
			addGroupValue(group, sourceEntry);
			removeOldOutEntry(group);
			Object aggregateValue = computeAggregateValue(group);
			addOutEntry(group, aggregateValue);
		} catch (ExpressionsInterpreterException e) {
			e.printStackTrace();
		}
	}
	
	protected void removeInIndexEntry(IndexEntry sourceEntry) {
		List<Object> group = getGroup(sourceEntry);
		removeGroupValue(group, sourceEntry);
		removeOldOutEntry(group);
		if(allGroupValues.containsKey(group)) {
			try {
				Object aggregateValue = computeAggregateValue(group);
				addOutEntry(group, aggregateValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private Object computeAggregateValue(List<Object> group) throws ExpressionsInterpreterException {
		EList<Object> values = getGroupValues(group);
		Object aggregateValue = expressionInterpreter.evaluateExpression(aggregationExpression, EcorePackage.Literals.EE_LIST, values, variablesScope).getValue();
		return aggregateValue;
	}

	private void addOutEntry(List<Object> group, Object aggregateValue) {
		List<Object> outEntry = new ArrayList<Object>(group);
		outEntry.add(aggregateValue);
		outIndex.addEntry(outEntry);
	}

	private void removeOldOutEntry(List<Object> group) {
		Collection<List<Object>> oldEntries = new ArrayList<List<Object>>();
		for(Iterator<IndexEntry> it = outIndex.getEntries(createOutGroupQuery(group)); it.hasNext();) {
			oldEntries.add(it.next().getKey());
		}
		for(List<Object> oldEntry:oldEntries) {
			outIndex.remove(oldEntry);
		}
	}

	private List<Object> createOutGroupQuery(List<Object> group) {
		List<Object> query = new ArrayList<Object>();
		for(Object o:group) {
			query.add(o);
			query.add(o);
		}
		query.add(Index.UNDEFINED_PARAMETER);
		query.add(Index.UNDEFINED_PARAMETER);
		return query;
	}

	private EList<Object> getGroupValues(Object group) {
		return new BasicEList<Object>(allGroupValues.get(group).values());
	}

	private void addGroupValue(List<Object> group, IndexEntry sourceEntry) throws ExpressionsInterpreterException {
		Object value = expressionInterpreter.evaluateExpression(valueExpression, EcorePackage.Literals.EOBJECT, null, variablesScope).getValue();
		addGroupValue(group, sourceEntry, value);
	}

	private void addGroupValue(List<Object> group, IndexEntry sourceEntry, Object value) {
		if(!allGroupValues.containsKey(group)) {
			allGroupValues.put(group, new LinkedHashMap<List<Object>, Object>());
		}
		allGroupValues.get(group).put(sourceEntry.getKey(), value);
	}

	private List<Object> getGroup(IndexEntry sourceEntry) {
		List<Object> group = new ArrayList<Object>();
		for(int i:groupingIndices) {
			group.add(sourceEntry.getKey().get(i));
		}
		return group;
	}

	private void removeGroupValue(List<Object> group, IndexEntry sourceEntry) {
		allGroupValues.get(group).remove(sourceEntry.getKey());
		if(allGroupValues.get(group).isEmpty()) {
			allGroupValues.remove(group);
		}
	}
	
}
