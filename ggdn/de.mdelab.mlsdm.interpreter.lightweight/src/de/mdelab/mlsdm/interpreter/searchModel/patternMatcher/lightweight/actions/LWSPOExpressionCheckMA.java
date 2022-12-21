package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import org.eclipse.emf.ecore.EClassifier;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlexpressions.MLStringExpression;
import de.mdelab.mlexpressions.MlexpressionsPackage;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLWExpressionInterpreter;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightCheckMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPC;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;

public class LWSPOExpressionCheckMA extends MLSDMLightweightCheckMA {

	private MLSDMLightweightPN pn;
	private MLExpression expression;
	private Object parsedExpression;
	private EClassifier contextClassifier;
	private MLSDMLWExpressionInterpreter interpreter;

	public LWSPOExpressionCheckMA(MLSDMLightweightPC patternConstraint,
			MLSDMLightweightMatcher matcher, MLSDMLightweightPN pn, MLExpression expression, EClassifier contextClassifier) {
		super(patternConstraint, matcher);
		this.pn = pn;
		this.expression = expression;
		this.contextClassifier = contextClassifier;
		this.interpreter = matcher.getExpressionInterpreterManager().getInterpreter(expression.getExpressionLanguage());
		
		addRequiredBinding(pn);
	}

	@Override
	public boolean check() {
		if(parsedExpression == null) {
			parsedExpression = interpreter.parseExpression(expression, contextClassifier, matcher.getCurrentMatch());
		}
		return (Boolean) interpreter.evaluateExpression(pn.getMapping(), parsedExpression, matcher.getCurrentMatch());
	}

	@Override
	public String toString() {
		String pnString = "[" + pn.getName() +"] ";
		if(expression.eClass() == MlexpressionsPackage.Literals.ML_EXPRESSION) {
			return pnString + ((MLStringExpression) expression).getExpressionLanguage() + ": " + ((MLStringExpression) expression).getExpressionString();
		}
		else {
			return pnString + expression.toString();
		}
	}

}
