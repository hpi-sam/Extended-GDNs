package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MLSDMLightweightPC {

	/*
	 * Constant indicating that the pattern constraint cannot
	 * currently be checked.
	 */
	public static final double MATCHING_NOT_POSSIBLE = Double.POSITIVE_INFINITY;
	
	/*
	 * Constant indicating that only an explicit check has to be
	 * performed for this pattern constraint.
	 */
	public static final double CHECK_ONLY = 0.5;

	/*
	 * Constant indicating that the pattern constraint should be
	 * checked as late as possible.
	 */
	public static final double ALWAYS_MATCH_LAST = Double.MAX_VALUE;
	
	/*
	 * List of pattern node dependencies of this pattern constraint.
	 */
	private List<MLSDMLightweightPN> dependencies;
	
	/*
	 * List of pattern node dependencies of this pattern constraint.
	 */
	private int[] dependencyIndices;
	
	/*
	 * List of matching actions which can be used to perform a check
	 * for this pattern constraint.
	 */
	private MLSDMLightweightMA[] registeredActions;
	
	/*
	 * Indicates whether the pattern constraint has already been checked.
	 */
	private boolean active;
	
	/*
	 * Matching action that can be executed to perform an explicit check of this
	 * pattern constraint.
	 */
	private MLSDMLightweightCheckMA explicitCheckAction;
	
	private int bindingCode;
	
	private Object constraint;
	
	public MLSDMLightweightPC(Object constraint){
		this.constraint = constraint;
		this.active = false;
		this.dependencies = new ArrayList<MLSDMLightweightPN>();
		this.registeredActions = new MLSDMLightweightMA[16];
		this.dependencyIndices = new int[4];
		this.bindingCode = 0;
	}

	public List<MLSDMLightweightPN> getDependencies() {
		return dependencies;
	}

	public void addDependency(MLSDMLightweightPN dependency) {
		this.dependencies.add(dependency);
		dependency.addDependantPatternConstraint(this);
		
		if(dependencyIndices.length < (dependency.getId() + 1)) {
			int[] newIndices = new int[dependency.getId() + 1];
			System.arraycopy(dependencyIndices, 0, newIndices, 0, dependencyIndices.length);
			dependencyIndices = newIndices;
		}
		dependencyIndices[dependency.getId()] = dependencies.size() - 1;
	}
	
	public void addDependencies(
			Collection<? extends MLSDMLightweightPN> dependencies) {
		for(MLSDMLightweightPN dependency:dependencies) {
			addDependency(dependency);
		}
	}
	
	public MLSDMLightweightMA[] getRegisteredActions() {
		return registeredActions;
	}
	
	public void registerAction(MLSDMLightweightMA matchingAction) {
		matchingAction.setPatternConstraint(this);
		
		int requirementsCode = getRequirementsBindingCode(matchingAction);
		if(registeredActions.length < (requirementsCode + 1)) {
			MLSDMLightweightMA[] newActions = new MLSDMLightweightMA[requirementsCode + 1];
			System.arraycopy(registeredActions, 0, newActions, 0, registeredActions.length);
			registeredActions = newActions;
		}
		registeredActions[requirementsCode] = matchingAction;
	}
	
	private int getRequirementsBindingCode(MLSDMLightweightMA matchingAction) {
		int requirementsCode = 0;
		for(MLSDMLightweightPN pn:matchingAction.getRequiredBindings()) {
			int index = dependencyIndices[pn.getId()];
			requirementsCode = requirementsCode | (1 << index);
		}
		return requirementsCode;
	}

	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public MLSDMLightweightMA getActiveAction() {
		return registeredActions[bindingCode];
	}
	
	public MLSDMLightweightCheckMA getExplicitCheckAction() {
		return explicitCheckAction;
	}

	public void setExplicitCheckAction(MLSDMLightweightCheckMA explicitCheckAction) {
		explicitCheckAction.setPatternConstraint(this);
		this.explicitCheckAction = explicitCheckAction;
	}

	public void patternNodeBound(int id) {
		int index = dependencyIndices[id];
		bindingCode = bindingCode | (1 << index);
		if(getActiveAction() != null) {
			getActiveAction().updateCardinalityEstimate();
		}
	}
	
	public void patternNodeUnbound(int id) {
		int index = dependencyIndices[id];
		bindingCode = bindingCode & (~(1 << index));
		if(getActiveAction() != null) {
			getActiveAction().updateCardinalityEstimate();
		}
	}

	@Override
	public String toString() {
		return explicitCheckAction.toString();
	}

	public Object getConstraint() {
		return constraint;
	}
}
