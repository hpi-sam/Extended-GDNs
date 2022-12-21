package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.incremental.ChangeListener;
import de.mdelab.mlsdm.incremental.change.SDMAttributeChange;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.interpreter.incremental.rete.projection.FeatureSubscriber;
import de.mdelab.mlsdm.interpreter.incremental.rete.projection.SubscriptionAdapter;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteMatch;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLWExpressionInterpreter;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.expressions.FeatureAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class ReteExpressionFilter extends ReteDistributor implements ChangeListener, FeatureSubscriber {

	private MLExpression expression;
	private MLSDMLWExpressionInterpreter interpreter;
	private Object expressionAST;

	private ReteMatch match;

	private Map<EStructuralFeature, Collection<Integer>> featureAccesses;
	private Map<EStructuralFeature, Map<Object, Collection<ReteTuple>>> registeredTuples; // TODO make this an explicit
																							// (multi-)indexer?
	private SubscriptionAdapter adapter;
	private ReteTuple emptyMapping;

	public ReteExpressionFilter(MLExpression expression, MLSDMLWExpressionInterpreter interpreter,
			Map<String, Integer> indices) {
		this.expression = expression;
		this.interpreter = interpreter;
		this.match = new ReteMatch(indices);
		this.featureAccesses = new HashMap<EStructuralFeature, Collection<Integer>>();
		this.registeredTuples = new HashMap<EStructuralFeature, Map<Object, Collection<ReteTuple>>>();
		this.emptyMapping = createEmptyMapping();
	}

	private ReteTuple createEmptyMapping() {
		List<Object> tuple = new ArrayList<Object>(match.size());
		for (int i = 0; i < match.size(); i++) {
			tuple.add(null);
		}
		return new ReteTuple(tuple);
	}

	@Override
	public void doAddTuple(ReteTuple tuple, ReteNode source) {
		defaultValueAccessed();				//reset default value flag
		boolean filtered = filter(tuple);
		if (!filtered) {
			if(defaultValueAccessed()) {
				tuple = new ReteTuple(tuple);
				tuple.setIsPreliminary(true);
			}
			registerTuple(tuple);
			super.doAddTuple(tuple, source);
		}
		else {
			registerTuple(tuple);
		}
	}

	@Override
	public void doRemoveTuple(ReteTuple tuple, ReteNode source) {
		unregisterTuple(tuple);
		super.doRemoveTuple(tuple, source);
	}

	//TODO
	public static long FILTER_TIME = 0;
	
	private boolean filter(ReteTuple tuple) {
		long start = System.nanoTime();
		match.setMappings(tuple);
		if (expressionAST == null) {
			expressionAST = interpreter.parseExpression(expression, null, match);
			featureAccesses = analyzeFeatureAccesses(match);
			for (EStructuralFeature feature : featureAccesses.keySet()) {
				registeredTuples.put(feature, new HashMap<Object, Collection<ReteTuple>>());
			}
		}
		boolean result;
		try {
			result = ((boolean) interpreter.evaluateExpression(null, expressionAST, match));
		} catch (Exception e) { // TODO be more precise here!
			result = false;
		}
		match.setMappings(emptyMapping);
		long end = System.nanoTime();
		FILTER_TIME += (end - start);
		return !result;
	}

	private void registerTuple(ReteTuple tuple) {
		for (Entry<EStructuralFeature, Collection<Integer>> entry : featureAccesses.entrySet()) {
			for (int index : entry.getValue()) {
				registerTuple(entry.getKey(), tuple.get(index), tuple);
				if (adapter != null) {
					adapter.subscribeToNotifications((EObject) tuple.get(index), entry.getKey(), this);
				}
			}
		}
	}

	private void registerTuple(EStructuralFeature key, Object object, ReteTuple tuple) {
		Map<Object, Collection<ReteTuple>> tuples = registeredTuples.get(key);
		if (!tuples.containsKey(object)) {
			tuples.put(object, new HashSet<ReteTuple>());
		}
		tuples.get(object).add(tuple);
	}

	private void unregisterTuple(ReteTuple tuple) {
		for (Entry<EStructuralFeature, Collection<Integer>> entry : featureAccesses.entrySet()) {
			for (int index : entry.getValue()) {
				Object value = tuple.get(index);
				unregisterTuple(entry.getKey(), value, tuple);
			}
		}
	}

	private void unregisterTuple(EStructuralFeature key, Object object, ReteTuple tuple) {
		Map<Object, Collection<ReteTuple>> tuples = registeredTuples.get(key);
		Collection<ReteTuple> values = tuples.get(object);
		if (values != null) {
			values.remove(tuple);
			if (values.isEmpty()) {
				tuples.remove(object);
				if (adapter != null) {
					adapter.unsubscribe((EObject) object, key, this);
				}
			}
		}
	}

	private Map<EStructuralFeature, Collection<Integer>> analyzeFeatureAccesses(ReteMatch match) {
		Collection<FeatureAccess<EStructuralFeature>> accesses = interpreter.getFeatureAccesses(expressionAST, match);
		Map<EStructuralFeature, Collection<Integer>> featureAccesses = new HashMap<EStructuralFeature, Collection<Integer>>();
		for (FeatureAccess<EStructuralFeature> access : accesses) {
			if (!featureAccesses.containsKey(access.getFeature())) {
				featureAccesses.put(access.getFeature(), new HashSet<Integer>());
			}
			featureAccesses.get(access.getFeature()).add(match.getIndices().get(access.getVariableName()));
		}
		return featureAccesses;
	}

	public Map<EStructuralFeature, Collection<Integer>> getFeatureAccesses() {
		return featureAccesses;
	}
	
	@Override
	public void registerChange(SDMChangeEvent change) {
		if (featureAccesses.containsKey(change.getType())) {
			SDMAttributeChange attributeChange = (SDMAttributeChange) change; // TODO actually, we could get here with
																				// an edge change, too
			Collection<ReteTuple> relatedTuples = registeredTuples.get(attributeChange.getType())
					.get(attributeChange.getObject());

			if (relatedTuples != null) {
				for (ReteTuple tuple : relatedTuples) {
					boolean filtered = filter(tuple);
					if (filtered) {
						propagateRemoval(tuple); // TODO could be optimized by storing last decision regarding filtering
													// for
						// each tuple
					} else {
						propagateAddition(tuple);
					}
				}
			}
		}
	}

	@Override
	public boolean isFeatureSubscriber() {
		return true;
	}

	@Override
	public void setSubscriptionAdapter(SubscriptionAdapter adapter) {
		this.adapter = adapter;
	}

}
