package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;

public class LWIndexQueryVariableEntry extends LWIndexQueryTemplateEntry {

	private MLSDMLightweightPN referencedPN;

	public LWIndexQueryVariableEntry(MLSDMLightweightPN referencedPN) {
		this.referencedPN = referencedPN;
		this.dependencies.add(referencedPN);
	}
	
	@Override
	public void updateValue(Map<String, EObject> match) {
		this.value = referencedPN.getMapping();
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	public MLSDMLightweightPN getReferencedPN() {
		return referencedPN;
	}
	
}
