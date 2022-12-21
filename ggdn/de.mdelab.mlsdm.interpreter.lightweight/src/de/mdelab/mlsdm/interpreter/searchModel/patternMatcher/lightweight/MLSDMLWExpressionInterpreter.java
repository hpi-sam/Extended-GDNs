package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.expressions.FeatureAccess;

public interface MLSDMLWExpressionInterpreter {

	public Object evaluateExpression(Object contextInstance, Object parsedExpression, Map<String, EObject> match);
	
	public Object parseExpression(MLExpression expression, EClassifier contextClassifier, Map<String, EObject> match);

	public Collection<FeatureAccess<EStructuralFeature>> getFeatureAccesses(Object parsedExpression, Map<String, EObject> match);
}
