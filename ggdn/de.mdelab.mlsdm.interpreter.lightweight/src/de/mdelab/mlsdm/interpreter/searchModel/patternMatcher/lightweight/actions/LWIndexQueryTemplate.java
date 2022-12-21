package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import java.util.AbstractList;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

public class LWIndexQueryTemplate extends AbstractList<Object> {

	private LWIndexQueryTemplateEntry[] entries;
	
	public LWIndexQueryTemplate(LWIndexQueryTemplateEntry[] entries) {
		this.entries = entries;
	}
	
	public void updateValues(Map<String, EObject> match) {
		for(int i = 0; i < entries.length; i++) {
			entries[i].updateValue(match);
		}
	}
	
	@Override
	public Object get(int index) {
		return entries[index].getValue();
	}

	@Override
	public int size() {
		return entries.length;
	}

}
