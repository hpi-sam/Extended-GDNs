package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.ocl;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.ecore.OCLExpression;
import org.eclipse.ocl.ecore.OCL.Helper;
import de.mdelab.expressions.interpreter.core.variables.Variable;
import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlexpressions.MLStringExpression;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.expressions.MLSDMOCLFeatureAccessCollectorVisitor;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLWExpressionInterpreter;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.expressions.FeatureAccess;

public class LWOCLExpressionInterpreter
		implements
			MLSDMLWExpressionInterpreter {
	
	private Map<String, Variable<EClassifier>> contextVariables;
	private Map<String, EObject> oldMatch;
	private OCL oldOcl;
	
	public LWOCLExpressionInterpreter(Map<String, Variable<EClassifier>> contextVariables) {
		this.contextVariables = contextVariables;
	}
	
	@Override
	public Object evaluateExpression(Object contextInstance, Object parsedExpression, Map<String, EObject> match) {
		OCL ocl = getOCL(match);
		return ocl.evaluate(contextInstance, (OCLExpression) parsedExpression);
	}

	@Override
	public Object parseExpression(MLExpression expression, EClassifier contextClassifier, Map<String, EObject> match) {		
		OCL ocl = getOCL(match);
		
		if (contextClassifier == null)
		{
			/*
			 * TODO: This is more of a hack. Actually, the context should be
			 * null if the constraint is to be evaluated without a context. But
			 * createQuery throws a NullPointerException in this case.
			 * Therefore, the context is set to EObject.
			 */
			contextClassifier = EcorePackage.Literals.EOBJECT;
		}

		final Helper helper = ocl.createOCLHelper();

		helper.setContext(contextClassifier);

		try {
			return helper.createQuery(((MLStringExpression) expression).getExpressionString());
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private OCL getOCL(Map<String, EObject> match) {
		if(oldOcl != null && match == oldMatch) {
			return oldOcl;
		}
		return OCL.newInstance(new LWOCLEnvironmentFactory(contextVariables, match));
	}

	@Override
	public Collection<FeatureAccess<EStructuralFeature>> getFeatureAccesses(
			Object parsedExpression, Map<String, EObject> match) {
		OCLExpression oclExpression = (OCLExpression) parsedExpression;
		
		MLSDMOCLFeatureAccessCollectorVisitor visitor = new MLSDMOCLFeatureAccessCollectorVisitor();
		oclExpression.accept(visitor);
		return visitor.getFeatureAccesses();
	}
	
}
