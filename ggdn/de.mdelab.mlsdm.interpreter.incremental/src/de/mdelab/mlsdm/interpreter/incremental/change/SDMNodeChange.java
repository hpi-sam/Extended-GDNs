package de.mdelab.mlsdm.interpreter.incremental.change;

import org.eclipse.emf.ecore.EObject;

public class SDMNodeChange implements SDMChangeEvent {

	private EObject object;
	private SDMChangeEnum modifier;

	public SDMNodeChange(EObject object, SDMChangeEnum modifier) {
		this.object = object;
		this.modifier = modifier;
	}
	
	@Override
	public SDMChangeEnum getModifier() {
		return modifier;
	}
	
	@Override
	public Object getType() {
		return object.eClass();
	}

	public EObject getObject() {
		return object;
	}

}
