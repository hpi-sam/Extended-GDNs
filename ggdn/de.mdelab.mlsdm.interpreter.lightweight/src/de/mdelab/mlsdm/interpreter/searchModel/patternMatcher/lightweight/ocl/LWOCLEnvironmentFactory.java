package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.ocl;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.EvaluationEnvironment;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.SendSignalAction;

import de.mdelab.expressions.interpreter.core.variables.Variable;

public class LWOCLEnvironmentFactory extends EcoreEnvironmentFactory {

	private Map<String, Variable<EClassifier>> contextVariables;
	private Map<String, EObject> match;

	public LWOCLEnvironmentFactory(Map<String, Variable<EClassifier>> contextVariables,
			Map<String, EObject> match) {
		this.contextVariables = contextVariables;
		this.match = match;
	}

	@Override
	public Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> createEnvironment()
	{
		LWOCLEnvironment environment = new LWOCLEnvironment(contextVariables, match);
		environment.setFactory(this);
		return environment;
	}

	@Override
	public EvaluationEnvironment<EClassifier, EOperation, EStructuralFeature, EClass, EObject> createEvaluationEnvironment()
	{
		return new LWOCLEvaluationEnvironment(contextVariables, match);
	}

	@Override
	public Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> createEnvironment(
			Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> parent)
	{
		LWOCLEnvironment environment = new LWOCLEnvironment(contextVariables, match);
		environment.setParent(parent);
		environment.setFactory(this);

		return environment;
	}
}
