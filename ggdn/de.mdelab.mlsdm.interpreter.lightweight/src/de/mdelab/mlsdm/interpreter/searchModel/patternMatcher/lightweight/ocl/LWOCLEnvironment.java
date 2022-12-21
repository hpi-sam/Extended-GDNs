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
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.ocl.EnvironmentFactory;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironment;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.parser.AbstractOCLAnalyzer;

public class LWOCLEnvironment extends EcoreEnvironment {

	private Map<String, de.mdelab.expressions.interpreter.core.variables.Variable<EClassifier>> contextVariables;
	private Map<String, EObject> match;

	@SuppressWarnings("deprecation")
	public LWOCLEnvironment(Map<String, de.mdelab.expressions.interpreter.core.variables.Variable<EClassifier>> contextVariables,
			Map<String, EObject> match) {
		super(EPackage.Registry.INSTANCE);
		
		this.contextVariables = contextVariables;
		this.match = match;
	}
	
	@Override
	public Variable<EClassifier, EParameter> lookupLocal(String name)
	{
		/*
		 * Delegate the lookup to the variables manager.
		 */

		String actualName = name;
		
		Object variable = match.get(actualName);

		if (variable == null && AbstractOCLAnalyzer.isEscaped(name))
		{
			actualName = AbstractOCLAnalyzer.unescape(name);
			variable = match.get(actualName);
		}

		if(variable == null) {
			actualName = name;
			variable = contextVariables.get(actualName);

			if (variable == null && AbstractOCLAnalyzer.isEscaped(name))
			{
				actualName = AbstractOCLAnalyzer.unescape(name);
				variable = contextVariables.get(actualName);
			}
		}

		if (variable == null)
		{
			return super.lookupLocal(name);
		}
		else
		{
			Variable<EClassifier, EParameter> var = this.getOCLFactory().createVariable();
			var.setName(actualName);
			var.setType(this.getOclType(variable));

			return var;
		}
	}

	private EClassifier getOclType(Object variable) {
		if(variable instanceof EObject) {
			return ((EObject) variable).eClass();
		}
		else if(variable instanceof de.mdelab.expressions.interpreter.core.variables.Variable) {
			/*
			 * copied from de.mdelab.sdm.interpreter.ocl.environment.CustomOCLEnvironment
			 */
			@SuppressWarnings("unchecked")
			EClassifier classifier = ((de.mdelab.expressions.interpreter.core.variables.Variable<EClassifier>) variable).getClassifier();
			if (classifier.eClass() == EcorePackage.Literals.EDATA_TYPE)
			{
				// EInt, EIntegerObject, EBigInteger, EByte, EByteObject, EShort,
				// EShortObject
				if (EcorePackage.Literals.EINT.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.EINTEGER_OBJECT.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.EBIG_INTEGER.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.EBYTE.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.EBYTE_OBJECT.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.ESHORT.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.ESHORT_OBJECT.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.ELONG.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.ELONG_OBJECT.getInstanceTypeName().equals(classifier.getInstanceTypeName()))
				{
					return this.getOCLStandardLibrary().getInteger();
				}
				// EDouble, EDoubleObject, EFloat, EFloatObject, EBigDecimal
				else if (EcorePackage.Literals.EDOUBLE.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.EDOUBLE_OBJECT.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.EFLOAT.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.EFLOAT_OBJECT.getInstanceTypeName().equals(classifier.getInstanceTypeName()))
				{
					return this.getOCLStandardLibrary().getReal();
				}
				// EBoolean, EBooleanObject
				else if (EcorePackage.Literals.EBOOLEAN.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.EBOOLEAN_OBJECT.getInstanceTypeName().equals(classifier.getInstanceTypeName()))
				{
					return this.getOCLStandardLibrary().getBoolean();
				}
				// EChar, ECharacterObject, EString
				else if (EcorePackage.Literals.ECHAR.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.ECHARACTER_OBJECT.getInstanceTypeName().equals(classifier.getInstanceTypeName())
						|| EcorePackage.Literals.ESTRING.getInstanceTypeName().equals(classifier.getInstanceTypeName()))
				{
					return this.getOCLStandardLibrary().getString();
				}
				else
				{
					return classifier;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void setFactory(
			EnvironmentFactory<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> factory)
	{
		super.setFactory(factory);
	}
}
