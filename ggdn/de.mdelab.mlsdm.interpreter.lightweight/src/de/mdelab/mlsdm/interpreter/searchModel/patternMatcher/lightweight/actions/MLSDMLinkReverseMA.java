package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPC;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;

public class MLSDMLinkReverseMA extends MLSDMLightweightMA {

	private MLSDMReferenceIndex ra;
	private MLSDMLightweightPN source;
	private MLSDMLightweightPN target;
	private EReference feature;
	private Iterator<? extends EObject> it;
	
	public MLSDMLinkReverseMA(MLSDMLightweightPC patternConstraint,
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
		
		addRequiredBinding(target);
	}

	@Override
	protected double doEstimateCardinality() {
		return ra.getInverseReferences(target.getMapping(), feature).size();
	}

	@Override
	protected boolean doApply() {
		if(it == null) {
			it = ra.getInverseReferences(target.getMapping(), feature).iterator();
		}
		
		while(it.hasNext()) {
			EObject mapping = it.next();
			if(matcher.matchPatternNode(source, mapping)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void doRollBack() {
		it = null;
	}

	@Override
	public void revertMapping() {
		matcher.unmatchPatternNode(source);
	}

	@Override
	public String toString() {
		return source.toString() + " -" + feature.getName() + "-> " + target.toString();
	}

}
