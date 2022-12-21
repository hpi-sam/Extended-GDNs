package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import java.util.Collection;

import org.eclipse.emf.ecore.EClassifier;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlexpressions.MLStringExpression;
import de.mdelab.mlexpressions.MlexpressionsPackage;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLWExpressionInterpreter;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightCheckMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPC;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;

public class LWExpressionCheckMA extends MLSDMLightweightCheckMA {

	private MLExpression expression;
	private Object parsedExpression;
	private EClassifier contextClassifier;
	private MLSDMLWExpressionInterpreter interpreter;

	public LWExpressionCheckMA(MLSDMLightweightPC patternConstraint,
			MLSDMLightweightMatcher matcher, Collection<MLSDMLightweightPN> requiredBindings, MLExpression expression, EClassifier contextClassifier) {
		super(patternConstraint, matcher);
		this.expression = expression;
		this.contextClassifier = contextClassifier;
		this.interpreter = matcher.getExpressionInterpreterManager().getInterpreter(expression.getExpressionLanguage());
		
		for(MLSDMLightweightPN pn:requiredBindings) {
			addRequiredBinding(pn);
		}
	}

	@Override
	public boolean check() {
		if(parsedExpression == null) {
			parsedExpression = interpreter.parseExpression(expression, contextClassifier, matcher.getCurrentMatch());
		}
		return (Boolean) interpreter.evaluateExpression(null, parsedExpression, matcher.getCurrentMatch());
	}
	
	@Override
	public String toString() {
		if(expression.eClass() == MlexpressionsPackage.Literals.ML_EXPRESSION) {
			return ((MLStringExpression) expression).getExpressionLanguage() + ": " + ((MLStringExpression) expression).getExpressionString();
		}
		else {
			return expression.toString();
		}
	}

}
