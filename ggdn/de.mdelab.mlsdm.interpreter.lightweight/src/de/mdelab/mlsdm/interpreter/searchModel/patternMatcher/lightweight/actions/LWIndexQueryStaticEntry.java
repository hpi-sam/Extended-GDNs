package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

public class LWIndexQueryStaticEntry extends LWIndexQueryTemplateEntry {

	public LWIndexQueryStaticEntry(Object value) {
		this.value = value;
	}
	
	@Override
	public void updateValue(Map<String, EObject> match) {
		
	}

	@Override
	public boolean isStatic() {
		return true;
	}
	
}
