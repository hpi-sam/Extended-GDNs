package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLWExpressionInterpreter;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;

public class LWIndexQueryExpressionEntry extends LWIndexQueryTemplateEntry {

	private Object parsedExpression;
	private MLSDMLWExpressionInterpreter expressionInterpreter;

	public LWIndexQueryExpressionEntry(Object parsedExpression, MLSDMLWExpressionInterpreter expressionInterpreter,
			Collection<MLSDMLightweightPN> dependencies) {
		this.parsedExpression = parsedExpression;
		this.expressionInterpreter = expressionInterpreter;
		this.dependencies.addAll(dependencies);
	}
	
	@Override
	public void updateValue(Map<String, EObject> match) {
		this.value = expressionInterpreter.evaluateExpression(null, parsedExpression, match);
	}

}
