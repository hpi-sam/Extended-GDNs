package de.mdelab.mlsdm.interpreter.incremental.change;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

public class SDMAttributeChange implements SDMChangeEvent {

	private EObject object;
	private EAttribute attribute;

	public SDMAttributeChange(EObject object, EAttribute attribute) {
		this.object = object;
		this.attribute = attribute;
	}
	
	@Override
	public SDMChangeEnum getModifier() {
		return SDMChangeEnum.MODIFY;
	}

	@Override
	public Object getType() {
		return attribute;
	}

	public EObject getObject() {
		return object;
	}

}
