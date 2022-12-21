package de.mdelab.migmm.history.timing.folding;

import org.eclipse.emf.ecore.EObject;

public interface InstanceMappingStrategy {

	public InstanceMapping createInstanceMapping(EObject source, EObject target);

}
