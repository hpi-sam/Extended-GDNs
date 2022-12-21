package de.mdelab.mlsdm.incremental.fragment;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.expressions.interpreter.core.variables.Variable;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMNodeChange;

public class SDMNodeFragment implements SDMFragment {

	private AbstractStoryPatternObject spo;
	private boolean isNegative;
	
	public SDMNodeFragment(AbstractStoryPatternObject spo, boolean isNegative) {
		this.spo = spo;
		this.isNegative = isNegative;
	}
	
	@Override
	public Collection<Variable<EClassifier>> createCandidateBinding(
			SDMChangeEvent change) {
		if(!(change instanceof SDMNodeChange)) {
			return null;
		}
		EObject object = ((SDMNodeChange) change).getObject();
		
		Collection<Variable<EClassifier>> binding = new ArrayList<Variable<EClassifier>>();
		binding.add(new Variable<EClassifier>(spo.getName(), spo.getType(), object));
		return binding;
	}
	
	public boolean isNegative() {
		return isNegative;
	}

}
