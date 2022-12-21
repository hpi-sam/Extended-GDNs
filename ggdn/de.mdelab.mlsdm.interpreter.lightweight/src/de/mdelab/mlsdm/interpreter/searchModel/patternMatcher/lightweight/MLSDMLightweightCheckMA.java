package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight;

public abstract class MLSDMLightweightCheckMA extends MLSDMLightweightMA {

	private boolean checked;
	
	public MLSDMLightweightCheckMA(MLSDMLightweightPC patternConstraint, MLSDMLightweightMatcher matcher) {
		super(patternConstraint, matcher);
		this.checked = false;
	}

	@Override
	protected boolean doApply() {
		boolean result = !checked && check();
		checked = true;
		return result;
	}

	@Override
	public void revertMapping() {
		
	}
	
	@Override
	protected void doRollBack() {
		checked = false;
	}

	@Override
	protected double doEstimateCardinality() {
		return MLSDMLightweightPC.CHECK_ONLY;
	}

	public abstract boolean check();
	
}
