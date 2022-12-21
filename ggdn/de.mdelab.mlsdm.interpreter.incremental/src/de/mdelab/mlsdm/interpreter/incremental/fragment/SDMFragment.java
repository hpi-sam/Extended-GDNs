package de.mdelab.mlsdm.interpreter.incremental.fragment;

import java.util.Collection;

import org.eclipse.emf.ecore.EClassifier;

import de.mdelab.mlsdm.interpreter.incremental.change.SDMChangeEvent;
import de.mdelab.expressions.interpreter.core.variables.Variable;

public interface SDMFragment {

	public Collection<Variable<EClassifier>> createCandidateBinding(SDMChangeEvent change);
	
	public boolean isNegative();
	
}
