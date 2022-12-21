package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.interpreter.facade.MLSDMMetamodelFacadeFactory;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.expressions.MLSDMCallActionsExpressionAnalyzer;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.expressions.MLSDMICLExpressionAnalyzer;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.expressions.MLSDMOCLExpressionAnalyzer;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.LWExpressionCheckMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.LWIndexQueryExpressionEntry;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.LWIndexQueryStaticEntry;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.LWIndexQueryTemplate;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.LWIndexQueryTemplateEntry;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.LWIndexQueryVariableEntry;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.LWIndexQueryVariableFeatureEntry;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.LWSPOExpressionCheckMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMDomainCheckMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMDomainMatchMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMIndexMatchMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMLinkCheckMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMLinkLookupMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMLinkReverseMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMLinkTraverseMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.Tuple;
import de.mdelab.mlsdm.mlindices.Index;
import de.mdelab.mlsdm.mlindices.MlindicesPackage;
import de.mdelab.mlstorypatterns.AbstractStoryPatternLink;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.mlstorypatterns.StoryPatternObject;
import de.mdelab.sdm.icl.iCL.ICLConstraint;
import de.mdelab.sdm.icl.iCL.ICLExpression;
import de.mdelab.sdm.icl.iCL.ICLPackage;
import de.mdelab.sdm.icl.iCL.ICLParameter;
import de.mdelab.sdm.icl.iCL.ICLRange;
import de.mdelab.sdm.icl.iCL.ICLStringExpression;
import de.mdelab.sdm.icl.iCL.ICLValue;
import de.mdelab.sdm.icl.iCL.ICLVariable;
import de.mdelab.sdm.icl.iCL.ICLVariableAttribute;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.expressions.ExpressionAnalyzerManager;

public class MLSDMLightweightSMBuilder {

	private MLSDMReferenceIndex ra;
	private ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression> expressionAnalyzerManager;
	private MLSDMLightweightMatcher matcher;
	private Map<AbstractStoryPatternObject, MLSDMLightweightPN> spo2PN;
	private Map<String, MLSDMLightweightPN> name2PN;

	public MLSDMLightweightSMBuilder(MLSDMLightweightMatcher matcher, MLSDMReferenceIndex ra) {
		this.matcher = matcher;
		this.ra = ra;
		this.expressionAnalyzerManager = createExpressionAnalyzerManager();
	}
	
	private ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression> createExpressionAnalyzerManager() {
		ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression> analyzerManager =
				new ExpressionAnalyzerManager<EClassifier, EStructuralFeature, MLExpression>(MLSDMMetamodelFacadeFactory.INSTANCE.getExpressionFacade());
		analyzerManager.registerExpressionAnalyzer("OCL", new MLSDMOCLExpressionAnalyzer(analyzerManager));
		analyzerManager.registerExpressionAnalyzer("ICL", new MLSDMICLExpressionAnalyzer(analyzerManager));
		analyzerManager.registerExpressionAnalyzer("CallActions", new MLSDMCallActionsExpressionAnalyzer(analyzerManager));
		return analyzerManager;
	}

	public MLSDMLightweightSM createSearchModel(StoryPattern pattern) {
		MLSDMLightweightSM sm = new MLSDMLightweightSM();
		createPatternNodes(sm, pattern);
		createPatternConstraints(sm, pattern);
		return sm;
	}

	private void createPatternNodes(MLSDMLightweightSM sm,
			StoryPattern pattern) {
		this.spo2PN = new HashMap<AbstractStoryPatternObject, MLSDMLightweightPN>();
		this.name2PN = new HashMap<String, MLSDMLightweightPN>();
		
		int count = 0;
		for(AbstractStoryPatternObject spo:pattern.getStoryPatternObjects()) {
			MLSDMLightweightPN pn = new MLSDMLightweightPN(count, sm, spo.getName(), (EClass) spo.getType());
			sm.addPatternNode(pn);
			spo2PN.put(spo, pn);
			name2PN.put(spo.getName(), pn);
			count++;
		}
	}

	private void createPatternConstraints(MLSDMLightweightSM sm,
			StoryPattern pattern) {
		createDomainConstraints(sm, pattern);
		createLinkConstraints(sm, pattern);
		createExpressionConstraints(sm, pattern);
	}

	private void createExpressionConstraints(MLSDMLightweightSM sm,
			StoryPattern pattern) {
		for(MLExpression expression:pattern.getConstraints()) {
			createExpressionConstraints(sm, expression);
		}
		for(AbstractStoryPatternObject spo:pattern.getStoryPatternObjects()) {
			for(MLExpression expression:((StoryPatternObject) spo).getConstraints()) {
				createSPOExpressionConstraint(sm, expression, spo);
			}
		}
	}

	private void createExpressionConstraints(MLSDMLightweightSM sm,
			MLExpression expression) {
		if(expression.getExpressionLanguage().equals("ICL")) {
			createIndexExpressionConstraints(sm, expression);
		}
		else {
			createGenericExpressionConstraints(sm, expression);
		}
	}

	private void createIndexExpressionConstraints(MLSDMLightweightSM sm,
			MLExpression expression) {
		ICLExpression iclExpression = (ICLExpression) expressionAnalyzerManager.parseAST(expression, null);
		if(iclExpression.eClass() == ICLPackage.Literals.ICL_CONSTRAINT) {
			ICLConstraint iclConstraint = (ICLConstraint) iclExpression;
			if(iclConstraint.getOperation().getName().equals("CONTAINS")) {
				Index index = (Index) matcher.getContextVariable(iclExpression.getIndexID()).getValue();
				
				switch(index.getAccessType()) {
					case ARBITRARY_KEY :
						createIndexArbitraryKeyConstraints(sm, iclConstraint);
						break;
					case FULL_KEY :
						createIndexFullKeyConstraints(sm, iclConstraint);
						break;
					case STAGED_KEY :
						createIndexStagedKeyConstraints(sm, iclConstraint, expression);
						break;
					default :
						break;
				}
			}
			else {
				createGenericExpressionConstraints(sm, expression);
			}
		}
	}

	private void createIndexStagedKeyConstraints(MLSDMLightweightSM sm,
			ICLConstraint iclConstraint, MLExpression expression) {
		MLSDMLightweightPC pc = new MLSDMLightweightPC(iclConstraint);
		
		List<ICLParameter> parameters = iclConstraint.getParameters().getList();
		LWIndexQueryTemplateEntry[] templateEntries = new LWIndexQueryTemplateEntry[parameters.size() * 2];
		Set<MLSDMLightweightPN> dependencies = new HashSet<MLSDMLightweightPN>();
		Set<MLSDMLightweightPN> minimumDependencies = new HashSet<MLSDMLightweightPN>();
		List<Tuple<MLSDMLightweightPN, Integer>> createdMappings = new ArrayList<Tuple<MLSDMLightweightPN, Integer>>();
		for(int i = 0; i < parameters.size(); i++) {
			ICLParameter parameter = parameters.get(i);
			if(parameter instanceof ICLRange) {
				ICLValue min = ((ICLRange) parameter).getMin();
				LWIndexQueryTemplateEntry minEntry = createTemplateEntry(min);
				templateEntries[2 * i] = minEntry;
				
				ICLValue max = ((ICLRange) parameter).getMax();
				LWIndexQueryTemplateEntry maxEntry = createTemplateEntry(max);
				templateEntries[(2 * i) + 1] = maxEntry;

				dependencies.addAll(getDependencies(maxEntry));
				dependencies.addAll(getDependencies(minEntry));
				minimumDependencies.addAll(getDependencies(maxEntry));
				minimumDependencies.addAll(getDependencies(minEntry));
			}
			else {
				LWIndexQueryTemplateEntry entry = createTemplateEntry((ICLValue) parameter);
				templateEntries[2 * i] = entry;
				templateEntries[(2 * i) + 1] = entry;

				dependencies.addAll(getDependencies(entry));
				if(entry.isVariable()) {
					MLSDMLightweightPN pn = ((LWIndexQueryVariableEntry) entry).getReferencedPN();
					createdMappings.add(new Tuple<MLSDMLightweightPN, Integer>(pn, i));
				}
				else {
					minimumDependencies.addAll(getDependencies(entry));
				}
			}
		}
		pc.addDependencies(dependencies);
		
		Set<MLSDMLightweightPN> maDependencies = new HashSet<MLSDMLightweightPN>(minimumDependencies);
		while(!createdMappings.isEmpty()) {
			LWIndexQueryTemplate template = createPartialIndexQueryTemplate(templateEntries, createdMappings);
			MLSDMLightweightMA ma = new MLSDMIndexMatchMA(pc, matcher, template, iclConstraint, new ArrayList<Tuple<MLSDMLightweightPN, Integer>>(createdMappings), maDependencies);
			pc.registerAction(ma);
			Tuple<MLSDMLightweightPN, Integer> newMapping = createdMappings.remove(0);
			maDependencies.add(newMapping.e1);
		}
		
		MLSDMLightweightCheckMA explicitCheck = new LWExpressionCheckMA(pc, matcher, dependencies, expression, MlindicesPackage.Literals.INDEX);
		pc.registerAction(explicitCheck);
		pc.setExplicitCheckAction(explicitCheck);
		
		sm.addPatternConstraint(pc);
	}

	private LWIndexQueryTemplate createPartialIndexQueryTemplate(
			LWIndexQueryTemplateEntry[] templateEntries,
			List<Tuple<MLSDMLightweightPN, Integer>> createdMappings) {
		LWIndexQueryTemplateEntry[] partialEntries = Arrays.copyOf(templateEntries, templateEntries.length);
		for(Tuple<MLSDMLightweightPN, Integer> mapping:createdMappings) {
			partialEntries[2 * mapping.e2] = new LWIndexQueryStaticEntry(Index.UNDEFINED_PARAMETER);
			partialEntries[(2 * mapping.e2) + 1] = new LWIndexQueryStaticEntry(Index.UNDEFINED_PARAMETER);
		}
		return new LWIndexQueryTemplate(partialEntries);
	}

	private Collection<? extends MLSDMLightweightPN> getDependencies(
			LWIndexQueryTemplateEntry entry) {
		return entry.getDependencies();
	}

	private LWIndexQueryTemplateEntry createTemplateEntry(ICLValue value) {
		if(value.eClass() == ICLPackage.Literals.ICL_VARIABLE) {
			ICLVariable variable = (ICLVariable) value;
			if(name2PN.containsKey(variable.getName())) {
				return new LWIndexQueryVariableEntry(name2PN.get(variable.getName()));
			}
			else {
				return new LWIndexQueryStaticEntry(matcher.getContextVariable(variable.getName()));
			}
		}
		else if (value.eClass() == ICLPackage.Literals.ICL_VARIABLE_ATTRIBUTE) {
			ICLVariableAttribute variableAttribute = (ICLVariableAttribute) value;
			if(name2PN.containsKey(variableAttribute.getVariable().getName())) {
				MLSDMLightweightPN pn = name2PN.get(variableAttribute.getVariable().getName());
				return new LWIndexQueryVariableFeatureEntry(pn, pn.getType().getEStructuralFeature(variableAttribute.getAttribute()));
			}
			else {
				EObject eObject = (EObject) matcher.getContextVariable(variableAttribute.getVariable().getName()).getValue();
				return new LWIndexQueryStaticEntry(eObject.eGet(eObject.eClass().getEStructuralFeature(variableAttribute.getAttribute())));
			}
		}
		else if(value.eClass() == ICLPackage.Literals.ICL_STRING_EXPRESSION) {
			ICLStringExpression stringExpression = (ICLStringExpression) value;
			Collection<String> varNames = expressionAnalyzerManager.getVariableNames(stringExpression.getExpression());
			boolean isStaticValue = true;
			Collection<MLSDMLightweightPN> dependencies = new ArrayList<MLSDMLightweightPN>();
			for(String varName:varNames) {
				if(name2PN.containsKey(varName)) {
					dependencies.add(name2PN.get(varName));
					isStaticValue = false;
				}
			}
			String language = stringExpression.getExpression().getExpressionLanguage();
			Object parsedExpression = expressionAnalyzerManager.parseAST(stringExpression.getExpression(), null);
			if(!isStaticValue) {
				return new LWIndexQueryExpressionEntry(parsedExpression, matcher.getExpressionInterpreterManager().getInterpreter(language), dependencies);
			}
			else {
				return new LWIndexQueryStaticEntry(matcher.getExpressionInterpreterManager().getInterpreter(language).evaluateExpression(null, parsedExpression, new MLSDMMatch()));
			}
		}
		return new LWIndexQueryStaticEntry(Index.UNDEFINED_PARAMETER);
	}

	private void createIndexFullKeyConstraints(MLSDMLightweightSM sm,
			ICLConstraint iclExpression) {
		// TODO Auto-generated method stub
		
	}

	private void createIndexArbitraryKeyConstraints(MLSDMLightweightSM sm,
			ICLConstraint iclExpression) {
		// TODO Auto-generated method stub
		
	}

	private void createGenericExpressionConstraints(MLSDMLightweightSM sm,
			MLExpression expression) {
		MLSDMLightweightPC pc = new MLSDMLightweightPC(expression);
		
		Collection<MLSDMLightweightPN> dependencies = new ArrayList<MLSDMLightweightPN>();
		Collection<String> varNames = expressionAnalyzerManager.getVariableNames(expression);
		for(String varName:varNames) {
			dependencies.add(name2PN.get(varName));
		}
		
		pc.addDependencies(dependencies);
		
		MLSDMLightweightCheckMA checkMA = new LWExpressionCheckMA(pc, matcher, dependencies, expression, EcorePackage.Literals.EOBJECT);
		pc.registerAction(checkMA);
		pc.setExplicitCheckAction(checkMA);
		
		sm.addPatternConstraint(pc);
	}

	private void createSPOExpressionConstraint(MLSDMLightweightSM sm,
			MLExpression expression, AbstractStoryPatternObject spo) {
		MLSDMLightweightPC pc = new MLSDMLightweightPC(expression);
		
		MLSDMLightweightPN pn = spo2PN.get(spo);
		
		pc.addDependency(pn);
		
		MLSDMLightweightCheckMA checkMA = new LWSPOExpressionCheckMA(pc, matcher, pn, expression, spo.getType());
		pc.registerAction(checkMA);
		pc.setExplicitCheckAction(checkMA);
		
		sm.addPatternConstraint(pc);
	}

	private void createLinkConstraints(MLSDMLightweightSM sm,
			StoryPattern pattern) {
		for(AbstractStoryPatternLink link:pattern.getStoryPatternLinks()) {
			createLinkConstraint(sm, link);
		}
	}

	private void createLinkConstraint(MLSDMLightweightSM sm,
			AbstractStoryPatternLink link) {
		MLSDMLightweightPC pc = new MLSDMLightweightPC(link);

		MLSDMLightweightPN source = spo2PN.get(link.getSource());
		MLSDMLightweightPN target = spo2PN.get(link.getTarget());

		pc.addDependency(source);
		pc.addDependency(target);

		MLSDMLightweightMA lookupMA = new MLSDMLinkLookupMA(pc, matcher, source, target, (EReference) ((StoryPatternLink) link).getFeature(), ra);
		pc.registerAction(lookupMA);

		MLSDMLightweightMA traverseMA = new MLSDMLinkTraverseMA(pc, matcher, source, target, ((StoryPatternLink) link).getFeature());
		pc.registerAction(traverseMA);

		MLSDMLightweightMA reverseMA = new MLSDMLinkReverseMA(pc, matcher, source, target, (EReference) ((StoryPatternLink) link).getFeature(), ra);
		pc.registerAction(reverseMA);
		
		MLSDMLightweightCheckMA checkMA = new MLSDMLinkCheckMA(pc, matcher, source, target, (EReference) ((StoryPatternLink) link).getFeature(), ra);
		pc.registerAction(checkMA);
		pc.setExplicitCheckAction(checkMA);
		
		sm.addPatternConstraint(pc);
	}

	private void createDomainConstraints(MLSDMLightweightSM sm,
			StoryPattern pattern) {
		for(AbstractStoryPatternObject spo:pattern.getStoryPatternObjects()) {
			createDomainConstraint(sm, spo);
		}
	}

	private void createDomainConstraint(MLSDMLightweightSM sm,
			AbstractStoryPatternObject spo) {
		EClass type = (EClass) spo.getType();
		MLSDMLightweightPN pn = spo2PN.get(spo);
		
		MLSDMLightweightPC pc = new MLSDMLightweightPC(type);
		pc.addDependency(pn);
		
		MLSDMLightweightMA matchMA = new MLSDMDomainMatchMA(pc, matcher, pn, type, ra);
		pc.registerAction(matchMA);
		
		MLSDMLightweightCheckMA checkMA = new MLSDMDomainCheckMA(pc, matcher, pn, type);
		pc.registerAction(checkMA);
		pc.setExplicitCheckAction(checkMA);
		
		sm.addPatternConstraint(pc);
	}

}
