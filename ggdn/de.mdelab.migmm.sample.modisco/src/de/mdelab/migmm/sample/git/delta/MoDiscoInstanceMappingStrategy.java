package de.mdelab.migmm.sample.git.delta;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.modisco.java.NamedElement;

import de.mdelab.migmm.history.timing.folding.ContainmentInstanceMappingStrategy;

public class MoDiscoInstanceMappingStrategy extends ContainmentInstanceMappingStrategy {

	@Override
	protected boolean matches(EObject source, EObject target) {
		if(source.eClass() != target.eClass()) {
			return false;
		}
		
		if(source instanceof NamedElement) {
			String sourceName = ((NamedElement) source).getName();
			String targetName = ((NamedElement) target).getName();
			return (sourceName == null && targetName == null) ||
					(sourceName != null && targetName != null && sourceName.equals(targetName));
		}
		
		return true;
	}

}
