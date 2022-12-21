package de.mdelab.mlsdm.interpreter.incremental.change;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class SDMEdgeChange implements SDMChangeEvent {

	private EObject source;
	private EReference feature;
	private EObject target;
	private SDMChangeEnum modifier;
	
	public SDMEdgeChange(EObject source, EReference feature, EObject target, SDMChangeEnum type) {
		this.source = source;
		this.feature = feature;
		this.target = target;
		this.modifier = type;
	}
	
	@Override
	public SDMChangeEnum getModifier() {
		return modifier;
	}

	@Override
	public Object getType() {
		return feature;
	}
	
	public EReference getFeature() {
		return feature;
	}

	public EObject getSource() {
		return source;
	}

	public EObject getTarget() {
		return target;
	}
}
