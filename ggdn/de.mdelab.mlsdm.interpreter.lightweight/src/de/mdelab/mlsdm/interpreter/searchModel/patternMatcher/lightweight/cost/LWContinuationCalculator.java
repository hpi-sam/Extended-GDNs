package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.cost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMMetadataIndex;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightCheckMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPC;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightPN;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.MLSDMLightweightSM;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.LWSPOExpressionCheckMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMDomainMatchMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMLinkCheckMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMLinkLookupMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMLinkReverseMA;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.actions.MLSDMLinkTraverseMA;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.sdm.icl.iCL.ICLConstraint;

public class LWContinuationCalculator {

	public static enum CostModel {AVERAGE_CASE, WORST_CASE};
	
	public class Adornment {

		protected boolean[] bindings;
		protected Collection<MLSDMLightweightPC> unhandledConstraints;
		protected double totalCost;
		protected List<MLSDMLightweightPC> matchingOrder;
		protected List<MLSDMLightweightMA> actions;
		protected double appliedFilters;
		
		protected double front = 1;
		
		public Adornment() {
			this.bindings = new boolean[searchModel.getPatternNodes().size()];
			for(int i = 0; i < bindings.length; i++) {
				this.bindings[i] = true;
			}
			
			this.unhandledConstraints = new ArrayList<MLSDMLightweightPC>(searchModel.getPatternConstraints());
			this.totalCost = 0;
			this.matchingOrder = new ArrayList<MLSDMLightweightPC>();
			this.actions = new ArrayList<MLSDMLightweightMA>();
			this.appliedFilters = 1;
		}

		public Adornment(	boolean[] bindings,
							Collection<MLSDMLightweightPC> checkedConstraints,
							double remainingCost,
							List<MLSDMLightweightPC> matchingOrder,
							List<MLSDMLightweightMA> actions,
							double appliedFilters) {
			this(bindings, checkedConstraints, remainingCost, matchingOrder, actions, appliedFilters, 1.0);
		}
		
		public Adornment(	boolean[] bindings,
				Collection<MLSDMLightweightPC> checkedConstraints,
				double remainingCost,
				List<MLSDMLightweightPC> matchingOrder,
				List<MLSDMLightweightMA> actions,
				double appliedFilters,
				double front) {
			this.bindings = bindings;
			this.unhandledConstraints = checkedConstraints;
			this.totalCost = remainingCost;
			this.matchingOrder = matchingOrder;
			this.actions = actions;
			this.appliedFilters = appliedFilters;
			this.front = front;
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(totalCost + "\t-\t[");
			boolean separate = false;
			for(int j = 0; j < bindings.length; j++) {
				if(!bindings[j]) {
					continue;
				}
				if(separate) {
					sb.append(", ");
				}
				sb.append(((AbstractStoryPatternObject)searchModel.getPatternNodes().get(j)).getName());
				separate = true;
			}
			sb.append("]");
			return sb.toString();
		}
		
	}

	private static final double DEFAULT_FILTERING = 0.1;
	
	protected MLSDMLightweightSM searchModel;
	protected MLSDMMetadataIndex metadataAdapter;
	protected CostModel costModel;
	protected Map<String, MLSDMLightweightPN> nameMappings;
	private Map<ICLConstraint, Double> indexFilters;

	public LWContinuationCalculator(MLSDMLightweightSM searchModel,
			MLSDMMetadataIndex metadataAdapter, CostModel costModel) {
		this.searchModel = searchModel;
		this.metadataAdapter = metadataAdapter;
		this.costModel = costModel;
		
		this.nameMappings = new HashMap<String, MLSDMLightweightPN>();
		for(MLSDMLightweightPN node:this.searchModel.getPatternNodes()) {
			nameMappings.put(node.getName(), node);
		}
		this.indexFilters = new HashMap<ICLConstraint, Double>();
	}
	
	public Adornment[] calculateCotinuationCosts() {
		Adornment[] table = new Adornment[(int) Math.pow(2, searchModel.getPatternNodes().size())];
		Adornment initial = new Adornment();
		initial.appliedFilters = getAppliedFilters(initial.bindings);
		table[(int) Math.pow(2, searchModel.getPatternNodes().size()) - 1] = initial;
		Collection<Adornment> adornments = new ArrayList<Adornment>();
		adornments.add(initial);
		
		while(!adornments.isEmpty()) {
			Collection<Adornment> next = new ArrayList<Adornment>();
			for(Adornment a:adornments) {
				Collection<Adornment> predecessors = computePredecessors(a);
				for(Adornment predecessor:predecessors) {
					int bindingCode = getBindingCode(predecessor);
					if(table[bindingCode] == null || table[bindingCode].totalCost > predecessor.totalCost) {
						table[bindingCode] = predecessor;
						next.add(predecessor);
					}
				}
			}
			
			adornments = next;
		}
		return table;
	}

	protected int getBindingCode(Adornment adornment) {
		int code = 0;
		for(int i  = 0; i < adornment.bindings.length; i++) {
			if(adornment.bindings[i]) {
				code = code | (1 << i);
			}
		}
		return code;
	}

	protected Collection<Adornment> computePredecessors(Adornment a) {
		Collection<Adornment> predecessors = new ArrayList<Adornment>();
		for(MLSDMLightweightPC pc:a.unhandledConstraints) {
			for(MLSDMLightweightMA ma:pc.getRegisteredActions()) {
				/*
				 * skip explicit checks
				 */
				if(ma == null || ma.getRequiredBindings().size() == ma.getPatternConstraint().getDependencies().size()) {
					continue;
				}
				
//				if(ma instanceof MLSDMIndexStagedMatchingAction) {
//					predecessors.addAll(getIndexStagedPredecessors(a, (MLSDMIndexStagedMatchingAction) ma));
//				}
//				else {
					Adornment predecessor = getPredecessor(a, ma);
					if(predecessor != null) {
						predecessors.add(predecessor);
					}
//				}
			}
		}
		return predecessors;
	}

//	private Collection<Adornment> getIndexStagedPredecessors(
//			Adornment a, MLSDMIndexStagedMatchingAction ma) {
//		Collection<Adornment> predecessors = new ArrayList<Adornment>();
//		
//		ICLConstraint constraint = ((ICLConstraint) ma.getPatternConstraint().getConstraint());
//		double domainSize = 1;
//		for(ICLParameter parameter:constraint.getParameters().getList()) {
//			if(!(parameter instanceof ICLVariable)) {			//TODO handle other kinds of index parameters
//				return predecessors;
//			}
//			
//			MLSDMLightweightPN node = nameMappings.get(((ICLVariable) parameter).getName());
//			domainSize *= metadataAdapter.getDomain((EClass) node.getSpo().getType()).size();
//		}
//		int parameterNumber = constraint.getParameters().getList().size();
//		Index index = (Index) variablesScope.getVariable(constraint.getIndexID()).getValue();
//		double filter = index.size() / domainSize;
//		
//		boolean[] currentBindings = new boolean[searchModel.getPatternNodes().size()];
//		System.arraycopy(a.bindings, 0, currentBindings, 0, currentBindings.length);
//		double unmatchedDomainSize = 1;
//		for(int i = parameterNumber - 1; i >= 0; i--) {
//			ICLVariable parameter = (ICLVariable) constraint.getParameters().getList().get(i);
//
//			MLSDMLightweightPN node = nameMappings.get(((ICLVariable) parameter).getName());
//			unmatchedDomainSize *= metadataAdapter.getDomain((EClass) node.getSpo().getType()).size();
//			
//			double cost = unmatchedDomainSize * filter;
//			boolean[] derivedBindings = new boolean[searchModel.getPatternNodes().size()];
//			System.arraycopy(currentBindings, 0, derivedBindings, 0, derivedBindings.length);
//			derivedBindings[node.getId()] = false;
//			
//			predecessors.add(createDerivedAdornment(a, derivedBindings, cost, parameterNumber - i, ma));
//			currentBindings = derivedBindings;
//		}
//		
//		return predecessors;
//	}


	protected Adornment createDerivedAdornment(Adornment a, boolean[] derivedBindings,
			double cost, int newBindings, MLSDMLightweightMA ma) {
		Collection<MLSDMLightweightPC> checkedConstraints = new ArrayList<MLSDMLightweightPC>(a.unhandledConstraints);
		LinkedList<MLSDMLightweightPC> matchingOrder = new LinkedList<MLSDMLightweightPC>(a.matchingOrder);
		LinkedList<MLSDMLightweightMA> actions = new LinkedList<MLSDMLightweightMA>(a.actions);
		double filtering = 1;
		for(Iterator<MLSDMLightweightPC> it = checkedConstraints.iterator(); it.hasNext();) {
			MLSDMLightweightPC pc2 = it.next();
			if(!isChecked(pc2, derivedBindings)) {
				it.remove();
				if(pc2 != ma.getPatternConstraint()) {
					matchingOrder.add(0, pc2);
					actions.add(0, pc2.getExplicitCheckAction());
					filtering *= getFiltering(pc2.getExplicitCheckAction());
				}
			}
		}
		matchingOrder.add(0, ma.getPatternConstraint());
		actions.add(0, ma);
		
		double remainingCost = (newBindings * cost) + (a.totalCost * cost * filtering);
		double appliedFiltering = getAppliedFilters(derivedBindings);
		
		return new Adornment(derivedBindings, checkedConstraints, remainingCost, matchingOrder, actions, appliedFiltering);
	}


	protected Adornment getPredecessor(Adornment a,
			MLSDMLightweightMA ma) {
		double cost = estimateCost(a, ma);
		
		if(cost != MLSDMLightweightPC.MATCHING_NOT_POSSIBLE) {
			boolean[] derivedBindings = new boolean[searchModel.getPatternNodes().size()];
			System.arraycopy(a.bindings, 0, derivedBindings, 0, derivedBindings.length);
			int newBindings = apply(ma, derivedBindings);

			return createDerivedAdornment(a, derivedBindings, cost, newBindings, ma);
		}
		
		return null;
	}

	protected int apply(MLSDMLightweightMA ma, boolean[] bindings) {
		int newBindings = 0;
		for(MLSDMLightweightPN pn:ma.getPatternConstraint().getDependencies()) {
			if(ma.getRequiredBindings().contains(pn)) {
				continue;
			}
			int index = pn.getId();
			bindings[index] = false;
			newBindings++;
		}
		return newBindings;
	}

	protected boolean isChecked(MLSDMLightweightPC pc2, boolean[] bindings) {
		for(MLSDMLightweightPN pn:pc2.getDependencies()) {
			int index = pn.getId();
			if(!bindings[index]) {
				return false;
			}
		}
		return true;
	}

	protected double estimateCost(Adornment a, MLSDMLightweightMA ma) {
		return estimateAverageCost(a, ma);
	}
	
	protected double estimateAverageCost(Adornment a, MLSDMLightweightMA ma) {
		if(ma instanceof MLSDMDomainMatchMA) {
			return (double) metadataAdapter.getDomain((EClass) ma.getPatternConstraint().getConstraint()).size();
		}
		else if(ma instanceof MLSDMLinkTraverseMA) {
			StoryPatternLink link = (StoryPatternLink) ma.getPatternConstraint().getConstraint();
			EReference reference = (EReference) link.getFeature();
			EClass sourceClass = (EClass) link.getSource().getType();
			return 	(((double) metadataAdapter.getReferences(reference).size())
					/ metadataAdapter.getDomain(sourceClass).size());
		}
		else if(ma instanceof MLSDMLinkReverseMA) {
			StoryPatternLink link = (StoryPatternLink) ma.getPatternConstraint().getConstraint();
			EReference reference = (EReference) link.getFeature();
			EClass targetClass = (EClass) link.getTarget().getType();
			return 	(((double) metadataAdapter.getReferences(reference).size())
					/ metadataAdapter.getDomain(targetClass).size());
		}
		else if(ma instanceof MLSDMLinkLookupMA) {
			StoryPatternLink link = (StoryPatternLink) ma.getPatternConstraint().getConstraint();
			EReference reference = (EReference) link.getFeature();
			return 	metadataAdapter.getReferences(reference).size();
		}
		else {
			return MLSDMLightweightPC.MATCHING_NOT_POSSIBLE;
		}
	}

	private double getAppliedFilters(boolean[] bindings) {
		double appliedFilters = 1;
		for(MLSDMLightweightPC pc:searchModel.getPatternConstraints()) {
			if(isChecked(pc, bindings)) {
				appliedFilters *= getFiltering(pc.getExplicitCheckAction());
			}
		}
		return appliedFilters;
	}

	public double getFiltering(
			MLSDMLightweightCheckMA explicitCheckAction) {
		if(explicitCheckAction instanceof LWSPOExpressionCheckMA) {
			int validInstances = metadataAdapter.getValidInstanceNumbers(explicitCheckAction.getPatternConstraint().getConstraint());
			if(validInstances != MLSDMMetadataIndex.EXPRESSION_NOT_REGISTERED) {
				int allInstances = metadataAdapter.getDomain((EClass) explicitCheckAction.getRequiredBindings().get(0).getType()).size();
				return allInstances > 0 ? (double) validInstances / (double) allInstances : 0;
			}
//			else if (explicitCheckAction.getPatternConstraint().getConstraint() instanceof ICLConstraint) {
//				ICLConstraint constraint = (ICLConstraint) explicitCheckAction.getPatternConstraint().getConstraint();
//				if(!indexFilters.containsKey(constraint)) {
//					double domainSizes = 1;
//					for(MLSDMLightweightPN node:explicitCheckAction.getRequiredBindings()) {
//						domainSizes *= metadataAdapter.getDomain((EClass) node.getSpo().getType()).size();
//					}
//					String indexId = constraint.getIndexID();
//					Index index = (Index) variablesScope.getVariable(indexId).getValue();
//					if(constraint.getOperation().getName().equals("CONTAINS")) {
//						indexFilters.put(constraint, index.size() / domainSizes);
//					}
//					else if(constraint.getOperation().getName().equals("EXCLUDES")) {
//						indexFilters.put(constraint, 1.0 - (index.size() / domainSizes));
//					}
//					else {
//						indexFilters.put(constraint, DEFAULT_FILTERING);
//					}
//				}
//				return indexFilters.get(constraint);
//			}
			else {
				return DEFAULT_FILTERING;
			}
		}
		else if(explicitCheckAction instanceof MLSDMLinkCheckMA) {
			StoryPatternLink link = (StoryPatternLink) explicitCheckAction.getPatternConstraint().getConstraint();
			int allInstances = metadataAdapter.getDomain((EClass) link.getSource().getType()).size() * metadataAdapter.getDomain((EClass) link.getTarget().getType()).size();
			int validInstances = metadataAdapter.getReferences((EReference) link.getFeature()).size();
			return allInstances > 0 ? (double) validInstances / (double) allInstances : 0;	//TODO polymorphism?
		}
		else {
			return 1;
		}
	}

}
