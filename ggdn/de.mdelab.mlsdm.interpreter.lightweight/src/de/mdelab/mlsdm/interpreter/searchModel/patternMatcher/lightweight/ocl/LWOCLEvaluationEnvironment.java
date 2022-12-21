package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.ocl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ocl.ecore.EcoreEvaluationEnvironment;
import org.eclipse.ocl.parser.AbstractOCLAnalyzer;

import de.mdelab.expressions.interpreter.core.variables.Variable;

public class LWOCLEvaluationEnvironment extends EcoreEvaluationEnvironment {
	
	private final Map<String, Variable<EClassifier>> contextVariables;

	private final Map<String, EObject> match;
	
	/*
	 * This map stores temporary variables like 'self'.
	 */
	private final Map<String, Object> localValues;

	/**
	 * 
	 * @param variablesScope
	 *            The adapter of the variables manager from where variables are
	 *            retrieved.
	 */
	@SuppressWarnings("deprecation")
	public LWOCLEvaluationEnvironment(Map<String, Variable<EClassifier>> contextVariables, Map<String, EObject> match)
	{
		this.localValues = new HashMap<String, Object>();
		
		this.contextVariables = contextVariables;
		this.match = match;
	}

	@Override
	public void add(String name, Object value)
	{
		this.localValues.put(name, value);
	}

	@Override
	public Object remove(String name)
	{
		return this.localValues.remove(name);
	}

	@Override
	public Object getValueOf(String name)
	{
		if (this.localValues.containsKey(name))
		{
			return this.localValues.get(name);
		}

		/*
		 * Delegate the lookup to the variables manager.
		 */

		String actualName = name;
		
		Object value = match.get(actualName);

		if (value == null && AbstractOCLAnalyzer.isEscaped(name))
		{
			actualName = AbstractOCLAnalyzer.unescape(name);
			value = match.get(actualName);
		}

		if(value == null) {
			actualName = name;
			Variable<EClassifier> variable = contextVariables.get(actualName);

			if (variable == null && AbstractOCLAnalyzer.isEscaped(name))
			{
				actualName = AbstractOCLAnalyzer.unescape(name);
				variable = contextVariables.get(actualName);
			}
			if(variable != null) {
				value = variable.getValue();
			}
		}

		if (value == null)
		{
			return null;
		}
		else
		{
			return value;
		}
	}

	@Override
	public void replace(String name, Object value)
	{
		this.localValues.put(name, value);
	}
}
