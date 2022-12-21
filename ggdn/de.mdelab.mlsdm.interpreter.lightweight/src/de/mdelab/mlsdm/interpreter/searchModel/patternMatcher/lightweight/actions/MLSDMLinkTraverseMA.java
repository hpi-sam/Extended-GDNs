package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPC;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;

public class MLSDMLinkTraverseMA extends MLSDMLightweightMA {

	private MLSDMLightweightPN source;
	private MLSDMLightweightPN target;
	private EStructuralFeature feature;
	private Iterator<? extends Object> it;

	public MLSDMLinkTraverseMA(MLSDMLightweightPC pc, MLSDMLightweightMatcher matcher, MLSDMLightweightPN source,
			MLSDMLightweightPN target, EStructuralFeature feature) {
		super(pc, matcher);
		this.source = source;
		this.target = target;
		this.feature = feature;
		
		addRequiredBinding(source);
	}

	@Override
	protected double doEstimateCardinality() {
		if(!feature.isMany()) {
			return source.getMapping().eGet(feature) != null ? 1 : 0;
		}
		else {
			return ((EList<?>) source.getMapping().eGet(feature)).size();
		}
	}

	@Override
	protected boolean doApply() {
		if(it == null) {
			if(!feature.isMany()) {
				it = Collections.singleton(source.getMapping().eGet(feature)).iterator();
			}
			else {
				it = ((EList<?>) source.getMapping().eGet(feature)).iterator();
			}
		}
		
		while(it.hasNext()) {
			Object mapping = it.next();
			if(matcher.matchPatternNode(target, (EObject) mapping)) {
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
		matcher.unmatchPatternNode(target);
	}

	@Override
	public String toString() {
		return source.toString() + " -" + feature.getName() + "-> " + target.toString();
	}
}
