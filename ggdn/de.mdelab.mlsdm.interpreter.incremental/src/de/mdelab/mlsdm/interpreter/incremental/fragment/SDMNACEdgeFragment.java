package de.mdelab.mlsdm.interpreter.incremental.fragment;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.interpreter.incremental.change.SDMEdgeChange;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.expressions.interpreter.core.variables.Variable;

public class SDMNACEdgeFragment extends SDMEdgeFragment {

	private StoryPattern parentPattern;

	public SDMNACEdgeFragment(StoryPatternLink link, StoryPattern parentPattern) {
		super(link, true);
		this.parentPattern = parentPattern;
	}

	@Override
	public Collection<Variable<EClassifier>> createCandidateBinding(
			SDMChangeEvent change) {
		if(!(change instanceof SDMEdgeChange)) {
			return null;
		}
		
		Collection<Variable<EClassifier>> binding = new ArrayList<Variable<EClassifier>>();
		
		if(link.getSource().getStoryPattern() == parentPattern) {
			EObject source = ((SDMEdgeChange) change).getSource();
			binding.add(new Variable<EClassifier>(link.getSource().getName(), link.getSource().getType(), source));
		}
		
		if(link.getTarget().getStoryPattern() == parentPattern) {
			EObject target = ((SDMEdgeChange) change).getTarget();
			binding.add(new Variable<EClassifier>(link.getTarget().getName(), link.getTarget().getType(), target));
		}
		
		return binding;
	}

}
