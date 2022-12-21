package de.mdelab.migmm.history.timing.folding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

public class InstanceMapping {

	private Map<EObject, EObject> mapping;
	private Map<EObject, EObject> inverseMapping;
	private Set<EObject> unmappedSourceObjects;
	private Set<EObject> unmappedTargetObjects;
	
	public InstanceMapping() {
		this.mapping = new HashMap<EObject, EObject>();
		this.inverseMapping = new HashMap<EObject, EObject>();
		this.unmappedSourceObjects = new HashSet<EObject>();
		this.unmappedTargetObjects = new HashSet<EObject>();
	}
	
	public void addMapping(EObject source, EObject target) {
		mapping.put(source, target);
		inverseMapping.put(target, source);
	}
	
	public void addUnmappedSource(EObject source) {
		unmappedSourceObjects.add(source);
	}

	public void addUnmappedSources(Iterable<EObject> sources) {
		for(EObject source:sources) {
			addUnmappedSource(source);
		}
	}
	
	public void addUnmappedTarget(EObject target) {
		unmappedTargetObjects.add(target);
	}

	public void addUnmappedTargets(Iterable<EObject> targets) {
		for(EObject target:targets) {
			addUnmappedTarget(target);
		}
	}

	public Map<EObject, EObject> getMapping() {
		return mapping;
	}

	public Map<EObject, EObject> getInverseMapping() {
		return inverseMapping;
	}
	
	public Set<EObject> getUnmappedSources() {
		return unmappedSourceObjects;
	}
	
	public Set<EObject> getUnmappedTargets() {
		return unmappedTargetObjects;
	}

	public EObject getTarget(EObject source) {
		return mapping.get(source);
	}
	
	public EObject getSource(EObject target) {
		return inverseMapping.get(target);
	}
}
