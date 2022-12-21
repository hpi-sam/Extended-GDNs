package de.mdelab.mlsdm.incremental.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMIndexChange;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.sdm.icl.iCL.ICLConstraint;
import de.mdelab.sdm.icl.iCL.ICLParameter;
import de.mdelab.sdm.icl.iCL.ICLVariable;
import de.mdelab.expressions.interpreter.core.variables.Variable;

public class SDMIndexFragment implements SDMFragment {

	private ICLConstraint constraint;
	private boolean isNegative;
	
	public SDMIndexFragment(ICLConstraint constraint) {
		this.constraint = constraint;
		this.isNegative = constraint.getOperation().getName().equals("EXCLUDES");
	}
	
	@Override
	public Collection<Variable<EClassifier>> createCandidateBinding(
			SDMChangeEvent change) {
		if(!(change instanceof SDMIndexChange)) {
			return null;
		}
		
		IndexEntry entry = ((SDMIndexChange) change).getEntry();
		
		List<Object> key = entry.getKey();
		List<ICLParameter> parameterList = constraint.getParameters().getList();
		
		if(parameterList.size() != key.size()) {
			return null;
		}

		Collection<Variable<EClassifier>> binding = new ArrayList<Variable<EClassifier>>();
		for(int i = 0; i < parameterList.size(); i++) {
			ICLParameter parameter = constraint.getParameters().getList().get(i);
			if(parameter instanceof ICLVariable) {
				Variable<EClassifier> variable = new Variable<EClassifier>(((ICLVariable) parameter).getName(),
																			((EObject) key.get(i)).eClass(),
																			key.get(i));
				binding.add(variable);
			}
		}
		return binding;	
	}

	public boolean isNegative() {
		return isNegative;
	}
}
