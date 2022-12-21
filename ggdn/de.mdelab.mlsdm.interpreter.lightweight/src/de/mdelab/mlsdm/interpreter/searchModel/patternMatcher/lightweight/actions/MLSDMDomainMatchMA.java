package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import java.util.Iterator;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPC;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;

public class MLSDMDomainMatchMA extends MLSDMLightweightMA {

	private MLSDMReferenceIndex ra;
	private MLSDMLightweightPN pn;
	private EClass type;
	private Iterator<EObject> it;

	public MLSDMDomainMatchMA(MLSDMLightweightPC pc, MLSDMLightweightMatcher matcher, MLSDMLightweightPN pn, EClass type, MLSDMReferenceIndex ra) {
		super(pc, matcher);
		this.pn = pn;
		this.type = type;
		this.ra = ra;
	}
	
	@Override
	protected double doEstimateCardinality() {
		return ra.getDomain(type).size();
	}

	protected boolean doApply() {
		if(it == null) {
			it = ra.getDomain(type).iterator();
		}
		
		while(it.hasNext()) {
			EObject mapping = it.next();
			if(matcher.matchPatternNode(pn, mapping)) {
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
		matcher.unmatchPatternNode(pn);
	}
	
	@Override
	public String toString() {
		return type.getName() + ":" + pn.toString();
	}

}
