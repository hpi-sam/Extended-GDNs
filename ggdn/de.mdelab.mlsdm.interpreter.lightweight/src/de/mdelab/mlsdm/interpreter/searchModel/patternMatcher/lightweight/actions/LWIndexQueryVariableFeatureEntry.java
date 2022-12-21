package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;

public class LWIndexQueryVariableFeatureEntry extends LWIndexQueryTemplateEntry {

	private MLSDMLightweightPN referencedPN;
	private EStructuralFeature feature;

	public LWIndexQueryVariableFeatureEntry(MLSDMLightweightPN referencedPN, EStructuralFeature feature) {
		this.referencedPN = referencedPN;
		this.feature = feature;
		this.dependencies.add(referencedPN);
	}
	
	@Override
	public void updateValue(Map<String, EObject> match) {
		this.value = referencedPN.getMapping().eGet(feature);
	}

}
