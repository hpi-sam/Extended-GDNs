package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMatcher;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPC;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;
import de.mdelab.mlsdm.mlindices.Index;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.sdm.icl.iCL.ICLConstraint;

public class MLSDMIndexMatchMA extends MLSDMLightweightMA {

	/*
	 * Collection storing all pattern nodes for which this matching action creates
	 * new mappings and the corresponding index in the entries of the associated index.
	 */
	private List<Tuple<MLSDMLightweightPN, Integer>> createdMappings;
	
	private ICLConstraint iclConstraint;
	private Index index;
	private LWIndexQueryTemplate queryTemplate;
	private Iterator<IndexEntry> it;
	
	public MLSDMIndexMatchMA(MLSDMLightweightPC patternConstraint,
			MLSDMLightweightMatcher matcher, LWIndexQueryTemplate queryTemplate,
			ICLConstraint iclConstraint, List<Tuple<MLSDMLightweightPN, Integer>> createdMappings, Set<MLSDMLightweightPN> requiredBindings) {
		super(patternConstraint, matcher);
		
		this.iclConstraint = iclConstraint;
		this.index = (Index) matcher.getContextVariable(iclConstraint.getIndexID()).getValue();
		this.queryTemplate = queryTemplate;
		this.createdMappings = createdMappings;
		
		for(MLSDMLightweightPN pn:requiredBindings) {
			addRequiredBinding(pn);
		}
	}

	@Override
	protected double doEstimateCardinality() {
		queryTemplate.updateValues(matcher.getCurrentMatch());
		return index.estimateCardinality(queryTemplate);
	}

	@Override
	protected boolean doApply() {
		if(it == null) {
			queryTemplate.updateValues(matcher.getCurrentMatch());
			it = index.getEntries(queryTemplate);
		}
		
		while(it.hasNext()) {
			IndexEntry entry = it.next();
			boolean valid = true;

			for(int i = 0; i < createdMappings.size(); i++) {
				Tuple<MLSDMLightweightPN, Integer> createdMapping = createdMappings.get(i);
				EObject mapping = (EObject) entry.getKey().get(createdMapping.e2);
				
				if(!matcher.matchPatternNode(createdMapping.e1, (EObject) mapping)) {
					for(int j = i; j >= 0; j --) {
						Tuple<MLSDMLightweightPN, Integer> invalidMapping = createdMappings.get(j);
						matcher.unmatchPatternNode(invalidMapping.e1);
					}
					
					valid = false;
					break;
				}
			}
			
			if(valid) {
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
		for(int i = 0; i < createdMappings.size(); i++) {
			Tuple<MLSDMLightweightPN, Integer> createdMapping = createdMappings.get(i);
			matcher.unmatchPatternNode(createdMapping.e1);
		}
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(iclConstraint.getIndexID());
		sb.append(" -> ");
		for(int i = 0; i < createdMappings.size(); i++) {
			if(i > 0) {
				sb.append(", ");
			}
			Tuple<MLSDMLightweightPN, Integer> mapping = createdMappings.get(i);
			sb.append(mapping.e1.getName());
		}
		return sb.toString();
	}

}
