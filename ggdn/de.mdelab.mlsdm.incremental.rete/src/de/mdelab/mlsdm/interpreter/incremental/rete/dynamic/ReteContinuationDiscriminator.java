package de.mdelab.mlsdm.interpreter.incremental.rete.dynamic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation.ReteContinuation;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.continuation.ReteImmutablePropertyContinuation;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteImmutableFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIsomorphyFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteJoin;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteTableFitter;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteUtil;
import de.mdelab.mlsdm.interpreter.searchModel.model.MLSDMSearchModel;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.strategy.hybrid.MLSDMContinuationCostCalculator.Adornment;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.PatternNode;

public class ReteContinuationDiscriminator extends ReteDiscriminator {

	public static final int NO_MAPPING = -1;
	
	private ReteNode[] consumers;
	private Collection<ReteContinuation> continuations;
	private Adornment[] continuationTable;
	private int tableCode;
	private double oldFiltering;
	private int[] tableMask;
	private ReteTableFitter resultProvider;
	private MLSDMSearchModel searchModel;
	
	public ReteContinuationDiscriminator(Collection<ReteContinuation> continuations,
			Adornment[] continuationTable, int tableCode, int[] tableMapping, ReteTableFitter resultProvider,
			MLSDMSearchModel searchModel) {
		this.continuations = continuations;
		this.continuationTable = continuationTable;
		this.tableCode = tableCode;
		this.tableMask = tableMapping;
		this.consumers = new ReteNode[getLargestConstraintId() + 1];
		this.oldFiltering = continuationTable[tableCode].appliedFilters;
		this.resultProvider = resultProvider;
		this.searchModel = searchModel;
		if(continuations.isEmpty()) {
			addSuccessor(resultProvider);
		}
	}

	private int getLargestConstraintId() {
		int maxId = -1;
		for(ReteContinuation continuation:continuations) {
			maxId = continuation.getId() > maxId ? continuation.getId() : maxId;
		}
		return maxId;
	}

	@Override
	protected ReteNode selectConsumer(List<Object> tuple) {
		// no constraints left -> connect to result provider
		if(continuations.isEmpty()) {
			return resultProvider;
		}
		
		// pick and handle one continuation
		ReteContinuation cheapestContinuation = getCheapestContinuation(tuple);
		ReteNode consumer = consumers[cheapestContinuation.getId()];
		if(consumer == null) {
			consumer = constructConsumer(cheapestContinuation, tuple.size());
			consumers[cheapestContinuation.getId()] = consumer;
			addSuccessor(consumer);
		}
		return consumer;
	}

	private ReteNode constructConsumer(
			ReteContinuation continuation, int maskSize) {
		int newCode = tableCode | continuation.getTableCode();
		
		//set up initial join
		List<int[]> transformations = new ArrayList<int[]>();
		int[] indexerMask = completeMask(transformMask(continuation.getMask(), transformations), maskSize);
		transformations.add(indexerMask);
		ReteIndexer currentIndexer = new ReteIndexer(indexerMask, continuation.getMask().length, "DISCRIMINATOR_" + ReteUtil.getNamesForIndices(searchModel, tableMask).toString());
		currentIndexer.addNotificationReceiver(resultProvider);
		net.addNode(currentIndexer);
		
		ReteJoin join = new ReteJoin(currentIndexer, continuation.getIndexer(), continuation.getKeySize());
		net.addNode(join);
		
		ReteNode firstNode = currentIndexer;
		ReteNode lastNode = join;
		
		int[] newTableMask = deriveTableMask(continuation, transformMask(tableMask, transformations));
		int newMaskSize = getBoundNodeNumber(newTableMask);
		int[] currentTableMask = newTableMask;
		transformations.clear();

		for(ReteImmutableFilter filter:getRequiredFilters(newCode, currentTableMask)) {
			lastNode.addSuccessor(filter);
			lastNode = filter;
			net.addNode(filter);
		}

		// create nodes for subsequent joins
		for(ReteContinuation otherContinuation:continuations) {
			if(otherContinuation == continuation) {
				continue;
			}
			ReteContinuation newContinuation = otherContinuation.deriveContinuation(newCode, currentTableMask);
			
			if((newContinuation.getTableCode() & newCode) == newContinuation.getTableCode()) {
				if(newContinuation.isImmutableProperty()) {
					ReteImmutableFilter filter = ((ReteImmutablePropertyContinuation) newContinuation).createFilterNode(currentTableMask);
					lastNode.addSuccessor(filter);
					net.addNode(filter);
					lastNode = filter;
				}
				else {
					int[] newMask = completeMask(newContinuation.getMask(), newMaskSize);
					currentIndexer = new ReteIndexer(newMask, newContinuation.getMask().length, "DISCRIMINATOR_" + ReteUtil.getNamesForIndices(searchModel, tableMask).toString() + "_JOIN");
					currentIndexer.addNotificationReceiver(resultProvider);
					lastNode.addSuccessor(currentIndexer);
					net.addNode(currentIndexer);
					
					ReteJoin checkJoin = new ReteJoin(currentIndexer, newContinuation.getIndexer(), newContinuation.getKeySize());
					net.addNode(checkJoin);
					lastNode = checkJoin;
					transformations.add(newMask);
					currentTableMask = transformMask(newTableMask, transformations);
				}
			}
		}

		//add new continuations for discriminator
		Collection<ReteContinuation> newContinuations = new ArrayList<ReteContinuation>();
		for(ReteContinuation otherContinuation:continuations) {
			if(otherContinuation == continuation || (otherContinuation.getTableCode() & newCode) == otherContinuation.getTableCode()) {
				continue;
			}
			
			newContinuations.add(otherContinuation.deriveContinuation(newCode, currentTableMask));
		}

		ReteDiscriminator discriminator = new ReteContinuationDiscriminator(newContinuations, continuationTable, newCode, currentTableMask, resultProvider, searchModel);
		lastNode.addSuccessor(discriminator);
		net.addNode(discriminator);
		return firstNode;
	}

	private int getBoundNodeNumber(int[] newTableMask) {
		int count = 0;
		for(int index:newTableMask) {
			if(index != NO_MAPPING) {
				count++;
			}
		}
		return count;
	}

	private Collection<ReteImmutableFilter> getRequiredFilters(int newCode, int[] newTableMask) {
		List<ReteImmutableFilter> filters = new ArrayList<ReteImmutableFilter>();
		
		//add isomorphy filters
		for(PatternNode<?, AbstractStoryPatternObject, ?, ?, ?, ?> pn: searchModel.getPatternNodes()) {
			if(tableMask[pn.getId()] == NO_MAPPING && newTableMask[pn.getId()] != NO_MAPPING) {
				//new mapping for pn
				for(PatternNode<?, AbstractStoryPatternObject, ?, ?, ?, ?> pn2: searchModel.getPatternNodes()) {
					if(pn == pn2 || !ReteUtil.typesMatch(pn.getSpo().getType(), pn2.getSpo().getType())) {
						continue;
					}
					
					if(newTableMask[pn2.getId()] != NO_MAPPING) {
						//if mapping for pn2 is also new, ensure only one filter is added
						if(tableMask[pn2.getId()] == NO_MAPPING && pn.getId() < pn2.getId()) {
							filters.add(new ReteIsomorphyFilter(newTableMask[pn.getId()], newTableMask[pn2.getId()]));
						}
						else {
							filters.add(new ReteIsomorphyFilter(newTableMask[pn.getId()], newTableMask[pn2.getId()]));
						}
					}
				}
			}
		}
		return filters;
	}

	private int[] deriveTableMask(ReteContinuation continuation, int[] currentTableMask) {
		int joinSize = continuation.getKeySize();
		
		int maxLeft = getMaxValue(currentTableMask);
		
		int[] derivedMask = new int[currentTableMask.length];
		for(int i = 0; i < derivedMask.length; i++) {
			int value = NO_MAPPING;
			
			int leftValue = currentTableMask[i];
			if(leftValue != NO_MAPPING) {
				value = leftValue;
			}
			else {
				int rightValue = continuation.getTableMappingIntoIndexer(i);
				if(rightValue != NO_MAPPING && rightValue >= joinSize) {
					value = maxLeft + (rightValue - joinSize) + 1;
				}
			}
			derivedMask[i] = value;
		}
		return derivedMask;
	}

	private int getMaxValue(int[] integers) {
		int max = -1;
		for(int i:integers) {
			if(i > max) {
				max = i;
			}
		}
		return max;
	}

	private int[] transformMask(int[] mask, List<int[]> transformations) {
		int[] transformed = new int[mask.length];
		System.arraycopy(mask, 0, transformed, 0, mask.length);
		for(int[] transformation:transformations) {
			for(int i = 0; i < transformed.length; i++) {
				int expectedPosition = transformed[i];
				for(int j = 0; j < transformation.length; j++) {
					if(transformation[j] == expectedPosition) {
						transformed[i] = j;
						break;
					}
				}
			}
		}
		return transformed;
	}

	private int[] completeMask(int[] baseMask, int maskSize) {
		int[] mask = new int[maskSize];
		
		//copy baseMask
		int i;
		for(i = 0; i < baseMask.length; i++) {
			mask[i] = baseMask[i];
		}
		
		//complete mask
		int j = 0;
		for(; i < maskSize; i++) {
			while(inMask(j, baseMask)) {
				j++;
			}
			mask[i] = j;
			j++;
		}
		return mask;
	}

	private boolean inMask(int j, int[] baseMask) {
		for(int i = 0; i < baseMask.length; i++) {
			//check if candidate index is in baseMask
			if(j == baseMask[i]) {
				return true;
			}
		}
		return false;
	}

	private ReteContinuation getCheapestContinuation(
			List<Object> tuple) {
		ReteContinuation cheapestContinuation = null;
		double cheapestCost = Double.MAX_VALUE;
		for(ReteContinuation continuation:continuations) {
			double continuationCost = computeContinuationCost(continuation, tuple);
			if(continuationCost < cheapestCost) {
				cheapestCost = continuationCost;
				cheapestContinuation = continuation;
			}
		}
		return cheapestContinuation;
	}

	private double computeContinuationCost(
			ReteContinuation continuation, List<Object> tuple) {
		int newCode = tableCode | continuation.getTableCode();
		double constraintFiltering = continuation.getConstraintFiltering();
		double filtering = (constraintFiltering == 0 || oldFiltering == 0) ? 0 : continuationTable[newCode].appliedFilters / (constraintFiltering * oldFiltering);
		double continuationCardinality = continuation.getContinuationCost(tuple);
		double remainingCost = continuationTable[newCode].totalCost;
		return continuationCardinality + continuationCardinality * filtering * remainingCost;	//TODO consider RETE-case in computation!
	}

	public int[] getTableMask() {
		return tableMask;
	}

}
