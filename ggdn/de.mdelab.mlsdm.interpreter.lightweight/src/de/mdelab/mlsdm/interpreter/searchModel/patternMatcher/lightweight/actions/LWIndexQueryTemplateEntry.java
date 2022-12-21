package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;

public abstract class LWIndexQueryTemplateEntry {

	protected Object value;
	protected Collection<MLSDMLightweightPN> dependencies;
	
	public LWIndexQueryTemplateEntry() {
		this.value = null;
		this.dependencies = new ArrayList<MLSDMLightweightPN>();
	}
	
	public Object getValue() {
		return value;
	}
	
	public abstract void updateValue(Map<String, EObject> match);
	
	public boolean isStatic() {
		return false;
	}

	public boolean isVariable() {
		return false;
	}

	public Collection<? extends MLSDMLightweightPN> getDependencies() {
		return dependencies;
	}
	
}
