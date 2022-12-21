package de.mdelab.mlsdm.interpreter.incremental.fragment;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.incremental.change.SDMAttributeChange;
import de.mdelab.mlsdm.interpreter.incremental.change.SDMChangeEvent;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.expressions.interpreter.core.variables.Variable;

public class SDMExpressionFragment implements SDMFragment {

	private AbstractStoryPatternObject spo;
	
	public SDMExpressionFragment(AbstractStoryPatternObject spo) {
		this.spo = spo;
	}

	@Override
	public Collection<Variable<EClassifier>> createCandidateBinding(
			SDMChangeEvent change) {
		if(!(change instanceof SDMAttributeChange)) {
			return null;
		}

		EObject object = ((SDMAttributeChange) change).getObject();
		
		Collection<Variable<EClassifier>> binding = new ArrayList<Variable<EClassifier>>();
		binding.add(new Variable<EClassifier>(spo.getName(), spo.getType(), object));
		return binding;
	}

	public boolean isNegative() {
		return false;
	}
}
