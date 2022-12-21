package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import org.eclipse.emf.ecore.EReference;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightCheckMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPC;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;

public class MLSDMLinkCheckMA extends MLSDMLightweightCheckMA {

	private MLSDMLightweightPN source;
	private MLSDMLightweightPN target;
	private EReference feature;
	private MLSDMReferenceIndex ra;
	
	public MLSDMLinkCheckMA(MLSDMLightweightPC patternConstraint,
			MLSDMLightweightMatcher matcher,
			MLSDMLightweightPN source,
			MLSDMLightweightPN target,
			EReference feature,
			MLSDMReferenceIndex ra) {
		super(patternConstraint, matcher);
		this.source = source;
		this.target = target;
		this.feature = feature;
		this.ra = ra;
		
		addRequiredBinding(source);
		addRequiredBinding(target);
	}

	@Override
	public boolean check() {
		if(!feature.isMany()) {
			return source.getMapping().eGet(feature) == target.getMapping();
		}
		else {
			return ra.checkReference(source.getMapping(), feature, target.getMapping());
		}
	}
	
	@Override
	public String toString() {
		return source.toString() + " -" + feature.getName() + "-> " + target.toString();
	}
}
