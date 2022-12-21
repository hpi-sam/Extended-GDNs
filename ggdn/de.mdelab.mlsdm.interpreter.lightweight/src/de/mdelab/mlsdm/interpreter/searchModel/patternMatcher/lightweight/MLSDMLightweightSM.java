package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MLSDMLightweightSM {

	/*
	 * Counter for the overall number of pattern nodes which have
	 * already been matched.
	 */
	private int boundPatternNodeNumber;
	
	/*
	 * Bit-vector encoding of the current configuration.
	 */
	private int bindingCode;
	
	/*
	 * List of pattern constraints contained in this search model.
	 */
	private List<MLSDMLightweightPC> patternConstraints;
	
	/*
	 * List of pattern nodes contained in this search model.
	 */
	private List<MLSDMLightweightPN> patternNodes;

	public MLSDMLightweightSM() {
		this.boundPatternNodeNumber = 0;
		this.bindingCode = 0;
		this.patternConstraints = new ArrayList<MLSDMLightweightPC>();
		this.patternNodes = new ArrayList<MLSDMLightweightPN>();
	}

	public int getBindingCode() {
		return bindingCode;
	}
	
	public int getBoundPatternNodeNumber() {
		return boundPatternNodeNumber;
	}

	public List<MLSDMLightweightPC> getPatternConstraints() {
		return patternConstraints;
	}

	public void addPatternConstraint(MLSDMLightweightPC patternConstraint) {
		this.patternConstraints.add(patternConstraint);
	}

	public void addPatternConstraints(Collection<? extends MLSDMLightweightPC> patternConstraints) {
		for(MLSDMLightweightPC patternConstraint:patternConstraints) {
			addPatternConstraint(patternConstraint);
		}
	}

	public List<MLSDMLightweightPN> getPatternNodes() {
		return patternNodes;
	}

	public void addPatternNode(MLSDMLightweightPN patternNode) {
		this.patternNodes.add(patternNode);
	}
	
	public void patternNodeBound(int id) {
		boundPatternNodeNumber++;
		bindingCode = bindingCode | (1 << id);
	}
	
	public void patternNodeUnbound(int id) {
		boundPatternNodeNumber--;
		bindingCode = bindingCode & (~(1 << id));
	}

}
