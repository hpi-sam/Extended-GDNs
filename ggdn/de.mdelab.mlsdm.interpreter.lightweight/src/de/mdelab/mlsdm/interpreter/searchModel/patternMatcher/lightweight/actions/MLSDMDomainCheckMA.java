package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import org.eclipse.emf.ecore.EClass;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightCheckMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPC;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;

public class MLSDMDomainCheckMA extends MLSDMLightweightCheckMA {

	private MLSDMLightweightPN pn;
	private EClass type;

	public MLSDMDomainCheckMA(MLSDMLightweightPC patternConstraint, MLSDMLightweightMatcher matcher, MLSDMLightweightPN pn, EClass type) {
		super(patternConstraint, matcher);
		this.pn = pn;
		this.type = type;
		
		addRequiredBinding(pn);
	}

	@Override
	public boolean check() {
		return (type == pn.getMapping().eClass() || pn.getMapping().eClass().getEAllSuperTypes().contains(type));
	}

	@Override
	public String toString() {
		return type.getName() + ":" + pn.toString();
	}
}
