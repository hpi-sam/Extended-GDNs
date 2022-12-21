package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

public class MLSDMLightweightPN {

	/*
	 * The search model this pattern node is part of.
	 */
	private MLSDMLightweightSM searchModel;
	
	/*
	 * Whether the pattern node has been mapped in the course of a
	 * graph search.
	 */
	private boolean bound;

	/*
	 * Current mapping for this pattern node
	 */
	private EObject mapping;
	
	/*
	 * List of all dependent pattern constraints of this pattern
	 * node.
	 */
	private List<MLSDMLightweightPC> dependantPatternConstraints;
	
	/*
	 * Integer identifier for this pattern node
	 */
	private int id;

	private String name;

	private EClass type;
	
	public MLSDMLightweightPN(int id, MLSDMLightweightSM searchModel, String name, EClass type) {
		this.id = id;
		this.searchModel = searchModel;
		this.name = name;
		this.type = type;
		this.bound = false;
		this.dependantPatternConstraints = new ArrayList<MLSDMLightweightPC>();
	}

	public String getName() {
		return name;
	}

	public boolean isBound() {
		return bound;
	}

	/**
	 * Set whether this pattern node is bound and update the counter for
	 * bound pattern nodes of the search model accordingly.
	 * 
	 * @param bound
	 */
	public void setBound(boolean bound) {
		if(this.bound != bound) {
			if(bound) {
				searchModel.patternNodeBound(id);
			}
			else {
				searchModel.patternNodeUnbound(id);
			}
		}
		this.bound = bound;
	}

	public EObject getMapping() {
		return mapping;
	}

	public void setMapping(EObject mapping) {
		this.mapping = mapping;
	}

	public List<MLSDMLightweightPC> getDependantPatternConstraints() {
		return dependantPatternConstraints;
	}

	public void addDependantPatternConstraint(MLSDMLightweightPC patternConstraint) {
		this.dependantPatternConstraints.add(patternConstraint);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void updateDependentPCs() {
		for(MLSDMLightweightPC pc:dependantPatternConstraints) {
			if(!pc.isActive()) {
				if(bound) {
					pc.patternNodeBound(id);
				}
				else {
					pc.patternNodeUnbound(id);
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return name;
	}

	public EClass getType() {
		return type;
	}

}
