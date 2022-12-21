package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight;

import java.util.ArrayList;
import java.util.List;

import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.PatternConstraint;

public abstract class MLSDMLightweightMA {

	/*
	 * Pattern nodes that are required to be bound in order for this
	 * matching action to be executable.
	 */
	private List<MLSDMLightweightPN> requiredBindings;
	
	/*
	 * Cached cardinality estimate of the result of this matching action's
	 * execution.
	 */
	private double cardinality;
	
	/*
	 * Pattern constraint which this matching action executes an explicit or
	 * implicit check for.
	 */
	protected MLSDMLightweightPC patternConstraint;

	protected MLSDMLightweightMatcher matcher;
	
	public MLSDMLightweightMA(MLSDMLightweightPC patternConstraint, MLSDMLightweightMatcher matcher) {
		this.patternConstraint = patternConstraint;
		this.matcher = matcher;
		this.requiredBindings = new ArrayList<MLSDMLightweightPN>();
		this.cardinality = PatternConstraint.MATCHING_NOT_POSSIBLE;
	}

	public List<MLSDMLightweightPN> getRequiredBindings() {
		return requiredBindings;
	}

	public void addRequiredBinding(MLSDMLightweightPN requiredBinding) {
		this.requiredBindings.add(requiredBinding);
	}
	
	public double getCardinality() {
		return cardinality;
	}

	public void setCardinality(double cardinality) {
		this.cardinality = cardinality;
	}

	public MLSDMLightweightPC getPatternConstraint() {
		return patternConstraint;
	}

	public void setPatternConstraint(MLSDMLightweightPC patternConstraint) {
		this.patternConstraint = patternConstraint;
	}

	/**
	 * Recalculate the cached cardinality estimate.
	 * 
	 * @return	The result of the estimation.
	 */
	public double updateCardinalityEstimate() {
		this.cardinality = doEstimateCardinality();
		return this.cardinality;
	}
	
	/**
	 * Perform the cardinality computation as appropriate for
	 * the type of the matching action.
	 * 
	 * @return The result of the estimation.
	 */
	protected abstract double doEstimateCardinality();

	public boolean apply() {
		return doApply();
	}

	protected abstract boolean doApply();

	public void rollBack() {
		revertMapping();
		doRollBack();
	}
	
	protected abstract void doRollBack();

	public abstract void revertMapping();

}
