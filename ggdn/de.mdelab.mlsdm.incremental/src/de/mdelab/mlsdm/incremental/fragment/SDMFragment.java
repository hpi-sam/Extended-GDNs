package de.mdelab.mlsdm.incremental.fragment;

import java.util.Collection;

import org.eclipse.emf.ecore.EClassifier;

import de.mdelab.expressions.interpreter.core.variables.Variable;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;

public interface SDMFragment {

	public Collection<Variable<EClassifier>> createCandidateBinding(SDMChangeEvent change);
	
	public boolean isNegative();
	
}
