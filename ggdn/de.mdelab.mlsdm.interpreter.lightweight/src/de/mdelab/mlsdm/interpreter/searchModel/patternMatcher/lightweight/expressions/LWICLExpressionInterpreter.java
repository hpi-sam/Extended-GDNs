package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.expressions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.serializer.impl.Serializer;

import com.google.inject.Injector;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlexpressions.MLStringExpression;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.LWExpressionInterpreterManager;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLWExpressionInterpreter;
import de.mdelab.mlsdm.mlindices.Index;
import de.mdelab.sdm.icl.ICLStandaloneSetup;
import de.mdelab.sdm.icl.iCL.ICLConstraint;
import de.mdelab.sdm.icl.iCL.ICLExpression;
import de.mdelab.sdm.icl.iCL.ICLPackage;
import de.mdelab.sdm.icl.iCL.ICLParameter;
import de.mdelab.sdm.icl.iCL.ICLRange;
import de.mdelab.sdm.icl.iCL.ICLStringExpression;
import de.mdelab.sdm.icl.iCL.ICLValue;
import de.mdelab.sdm.icl.iCL.ICLVariable;
import de.mdelab.sdm.icl.iCL.ICLVariableAttribute;
import de.mdelab.sdm.interpreter.core.SDMException;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.expressions.FeatureAccess;
import de.mdelab.expressions.interpreter.core.variables.Variable;

@SuppressWarnings("restriction")
public class LWICLExpressionInterpreter
		implements
			MLSDMLWExpressionInterpreter {
	
	/*
	 * Dummy resource set for storing parsed expressions. 
	 */
	private XtextResourceSet dummyResourceSet;
	
	/*
	 * Serializer for ICL constraints.
	 */
	private Serializer serializer;
	
	/*
	 * Number of parsed expressions. This is used to assign each expression
	 * a unique identifier.
	 */
	private int dummyCount;
	
	private Map<String, Variable<EClassifier>> contextVariables;

	private LWExpressionInterpreterManager expressionInterpreterManager;
	
	private Map<MLExpression, Object> parsedExpressions;
	
	public LWICLExpressionInterpreter(Map<String, Variable<EClassifier>> contextVariables, LWExpressionInterpreterManager expressionInterpreterManager) {
		this.contextVariables = contextVariables;
		this.dummyCount = 0;
		this.expressionInterpreterManager = expressionInterpreterManager;
		this.parsedExpressions = new HashMap<MLExpression, Object>();
		initializeResourceSet();
	}
	
	/**
	 * Create the dummy resource set and set up the Xtext parser for ICL.
	 */
	private void initializeResourceSet() {
		Injector injector = new ICLStandaloneSetup().createInjectorAndDoEMFRegistration();
		serializer = injector.getInstance(Serializer.class);
		dummyResourceSet = injector.getInstance(XtextResourceSet.class);
		dummyResourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
	}

	@Override
	public Object evaluateExpression(Object contextInstance, Object parsedExpression, Map<String, EObject> match) {
		ICLExpression iclExpression = (ICLExpression) parsedExpression;
		boolean result = evaluateICLExpression(iclExpression, match);
		return result;
	}
	
	/**
	 * Create the AST from an ICL expression in textual syntax.
	 * 
	 * @param expressionString
	 * 			The expression to parse.
	 * @return 	The AST of the given expression.
	 * @throws IOException
	 * 			If something goes wrong when parsing the expression.
	 */
	public ICLExpression parseExpression(String expressionString) throws IOException {
		/*
		 * assign each parsed expression a unique identifier in the dummy resource set
		 */
		Resource resource = dummyResourceSet.createResource(URI.createURI("dummy:/dummy" + dummyCount++ + ".icl"));
		InputStream in = new ByteArrayInputStream(expressionString.getBytes());
		resource.load(in, dummyResourceSet.getLoadOptions());
		ICLExpression expression = (ICLExpression) resource.getContents().get(0);
		return expression;
	}

	@Override
	public Object parseExpression(MLExpression expression,
			EClassifier contextClassifier, Map<String, EObject> match) {
		/*
		 * assign each parsed expression a unique identifier in the dummy resource set
		 */
		Resource resource = dummyResourceSet.createResource(URI.createURI("dummy:/dummy" + dummyCount++ + ".icl"));
		InputStream in = new ByteArrayInputStream(((MLStringExpression) expression).getExpressionString().getBytes());
		try {
			resource.load(in, dummyResourceSet.getLoadOptions());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ICLExpression iclExpression = (ICLExpression) resource.getContents().get(0);
		return iclExpression;
	}

	/**
	 * Evaluate the given expression and return the evaluation result.
	 * 
	 * @param iclExpression
	 * 			The expression to evaluate.
	 * @param expressionFacade
	 * 			The expression facade.
	 * @param variablesScope
	 * 			The context in which to evaluate the expression.
	 * @return	The result of the evaluation.
	 * @throws SDMException
	 * 			In case the expression could not be evaluated.
	 */
	private boolean evaluateICLExpression(ICLExpression iclExpression,
			Map<String, EObject> match) {
		if(iclExpression.eClass() == ICLPackage.Literals.ICL_CONSTRAINT) {
			ICLConstraint iclConstraint = (ICLConstraint) iclExpression;
			String operationName = iclConstraint.getOperation().getName();
			if(operationName.equals("CONTAINS")) {
				return evaluateContainsConstraint(iclConstraint, match);
			}
			else if(operationName.equals("EXCLUDES")) {
				return evaluateExcludesConstraint(iclConstraint, match);
			}
			else if(operationName.equals("ADD")) {
				return evaluateAddConstraint(iclConstraint, match);
			}
			else {
				return false;
			}
		}
		else if(iclExpression.eClass() == ICLPackage.Literals.ICL_DECLARATION) {
			throw new UnsupportedOperationException("ICL interpreter implementation for lightweight matcher does not support declaration");
		}
		return false;
	}

	/**
	 * Evaluate a containment constraint.
	 * 
	 * @param iclConstraint
	 * 			The containment constraint to evaluate.
	 * @param variablesScope
	 * 			The context in which to evaluate the constraint.
	 * @return	The result of the evaluation.
	 */
	private boolean evaluateContainsConstraint(ICLConstraint iclConstraint,
			Map<String, EObject> match) {
		Index index = (Index) contextVariables.get(iclConstraint.getIndexID()).getValue();
		List<ICLParameter> parameters = iclConstraint.getParameters().getList();
		/*
		 * each parameter is translated to two list entries in the query
		 * (lower and upper bound)
		 */
		List<Object> query = new ArrayList<Object>(parameters.size() * 2);
		
		for(ICLParameter parameter:parameters) {
			if(parameter.eClass() ==  ICLPackage.Literals.ICL_RANGE) {
				query.add(getValue(((ICLRange) parameter).getMin(), match));
				query.add(getValue(((ICLRange) parameter).getMax(), match));
			}
			else {
				/*
				 * in the case of a point query, lower and upper bound are equal
				 */
				query.add(getValue((ICLValue) parameter, match));
				query.add(getValue((ICLValue) parameter, match));
			}
		}
		
		return index.getEntries(query).hasNext();
	}

	/**
	 * Evaluate an exclusion constraint. This corresponds to a negated
	 * containment constraint.
	 * 
	 * @param iclConstraint
	 * 			The exclusion constraint to evaluate.
	 * @param variablesScope
	 * 			The context in which to evaluate the expression.
	 * @return	The result of the evaluation.
	 */
	private boolean evaluateExcludesConstraint(ICLConstraint iclConstraint,
			Map<String, EObject> match) {
		return !evaluateContainsConstraint(iclConstraint, match);
	}
	
	/**
	 * Evaluate an addition constraint. This adds a new entry to the index specified
	 * in the constraint.
	 * 
	 * @param iclConstraint
	 * 			The addition constraint to evaluate.
	 * @param expressionFacade
	 * 			The expression facade.
	 * @param variablesScope
	 * 			The context in which to evaluate the constraint.
	 * @return	The result of the evaluation. This is always true since the
	 * 			addition of a new entry is not actually a constraint over a pattern.
	 * @throws SDMException
	 * 			In case the entry to add is not well-formed.
	 */
	private boolean evaluateAddConstraint(ICLConstraint iclConstraint,
			Map<String, EObject> match) {
		Index index = (Index) contextVariables.get(iclConstraint.getIndexID()).getValue();
		List<ICLParameter> parameters = iclConstraint.getParameters().getList();
		List<Object> key = new ArrayList<Object>(parameters.size());
		
		for(ICLParameter parameter:parameters) {
			if(parameter.eClass() ==  ICLPackage.Literals.ICL_RANGE) {
				throw new UnsupportedOperationException("could not evaluate ICL ADD constraint: key for adding may not be a range");
			}
			else {
				key.add(getValue((ICLValue) parameter, match));
			}
		}
		index.addEntry(key);
		
		return true;
	}

	/**
	 * Retrieve the actual value represented by an ICL value in a given context.
	 * 
	 * @param value
	 * 			The ICL value representing the actual value to be retrieved.
	 * @param variablesScope
	 * 			The context from which to retrieve the actual value.
	 * @return	The actual value represented by the ICL value.
	 */
	private Object getValue(ICLValue value,
			Map<String, EObject> match) {
		if(value.eClass() == ICLPackage.Literals.ICL_VARIABLE_ATTRIBUTE) {
			/*
			 * the icl value represents an attribute of a value stored in
			 * a specified variable
			 */
			ICLVariableAttribute iclValueAttribute = (ICLVariableAttribute) value;
			String variableName = iclValueAttribute.getVariable().getName();
			EObject eObject = (EObject) match.get(variableName);
			
			return eObject.eGet(eObject.eClass().getEStructuralFeature(iclValueAttribute.getAttribute()));
		}
		else if (value.eClass() == ICLPackage.Literals.ICL_VARIABLE){
			/*
			 * the icl value represents a value stored in a specified variable
			 */
			String variableName = ((ICLVariable)value).getName();
			return match.get(variableName);
		}
		else if (value.eClass() == ICLPackage.Literals.ICL_STRING_EXPRESSION) {
			/*
			 * the icl value represents the evaluation result of an expression
			 * formulated in another expression language
			 */
			return expressionInterpreterManager.getInterpreter(((ICLStringExpression) value).getExpression().getExpressionLanguage()).evaluateExpression(null, getParsedExpression(((ICLStringExpression) value).getExpression(), match), match);
		}
		else {
			return Index.UNDEFINED_PARAMETER;
		}
	}

	private Object getParsedExpression(MLExpression expression, Map<String, EObject> match) {
		if(parsedExpressions.containsKey(expression)) {	//TODO avoid HashMap
			parsedExpressions.put(expression, expressionInterpreterManager.getInterpreter(expression.getExpressionLanguage()).parseExpression(expression, EcorePackage.Literals.EOBJECT, match));
		}
		return parsedExpressions.get(expression);
	}

	/**
	 * Serialize an ICL expression using the Xtext serializer to retrieve
	 * a representation in textual syntax.
	 * 
	 * @param expression
	 * 			The expression to be serialized.
	 * @return	A representation of the expression in textual syntax.
	 */
	public String getExpressionString(ICLExpression expression) {
		return serializer.serialize(expression);
	}

	@Override
	public Collection<FeatureAccess<EStructuralFeature>> getFeatureAccesses(
			Object parsedExpression, Map<String, EObject> match) {
		throw new UnsupportedOperationException();	//TODO implement later
	}

}
