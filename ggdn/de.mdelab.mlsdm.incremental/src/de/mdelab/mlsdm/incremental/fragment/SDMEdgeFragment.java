package de.mdelab.mlsdm.incremental.fragment;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.expressions.interpreter.core.variables.Variable;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMEdgeChange;

public class SDMEdgeFragment implements SDMFragment {

	protected StoryPatternLink link;
	protected boolean isNegative;
	
	public SDMEdgeFragment(StoryPatternLink link, boolean isNegative) {
		this.link = link;
		this.isNegative = isNegative;
	}
	
	@Override
	public Collection<Variable<EClassifier>> createCandidateBinding(
			SDMChangeEvent change) {
		if(!(change instanceof SDMEdgeChange)) {
			return null;
		}
		
		Collection<Variable<EClassifier>> binding = new ArrayList<Variable<EClassifier>>();
		EObject source = ((SDMEdgeChange) change).getSource();
		EObject target = ((SDMEdgeChange) change).getTarget();
		
		binding.add(new Variable<EClassifier>(link.getSource().getName(), link.getSource().getType(), source));
		binding.add(new Variable<EClassifier>(link.getTarget().getName(), link.getTarget().getType(), target));
		return binding;
	}
	
	public boolean isNegative() { 
		return isNegative;
	}

}
