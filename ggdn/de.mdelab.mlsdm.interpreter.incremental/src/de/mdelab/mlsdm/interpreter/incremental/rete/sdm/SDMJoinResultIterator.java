package de.mdelab.mlsdm.interpreter.incremental.rete.sdm;

import java.util.Iterator;
import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.mlsdm.mlindices.MlindicesFactory;

public class SDMJoinResultIterator implements Iterator<SDMQueryMatch> {

	protected SDMJoinNode joinNode;
	
	protected Iterator<IndexEntry> leftIt;
	protected Iterator<IndexEntry> rightIt = null;
	
	protected IndexEntry currentLeft = null;
	protected IndexEntry currentRight = null;
	protected IndexEntry next = null;

	public SDMJoinResultIterator(Iterator<IndexEntry> leftIt, SDMJoinNode joinNode) {
		this.leftIt = leftIt;
		this.joinNode = joinNode;
	}
	
	@Override
	public boolean hasNext() {
		if(next == null) {
			next = computeNext();
		}
		return next != null;
	}

	private IndexEntry computeNext() {
		while((rightIt != null && rightIt.hasNext()) || leftIt.hasNext()) {
			while((rightIt == null || !rightIt.hasNext()) && leftIt.hasNext()) {
				currentLeft = leftIt.next();
				List<Object> key = currentLeft.getKey();
				List<Object> query = joinNode.createPointQuery(key.subList(0, joinNode.getCardinality()));
				rightIt = joinNode.getRightInterface().getEntries(joinNode.completeQuery(query, joinNode.getRightIndexQuery()));
			}
			
			while(rightIt != null && rightIt.hasNext()) {
				IndexEntry rightCandidate = rightIt.next();
				IndexEntry nextCandidate = constructJoinTuple(rightCandidate);
				if(joinNode.passesUniquenessChecks(nextCandidate)) {
					currentRight = rightCandidate;
					return nextCandidate;
				}
			}
		}
		return null;
	}

	@Override
	public SDMQueryMatch next() {
		if(next == null) {
			next = computeNext();
		}
		
		if(next != null) {
			IndexEntry returnValue = next;
			next = null;
			return new SDMQueryMatch(returnValue, joinNode.getSPONameToIndex());
		}
		else {
			return null;
		}
	}

	private IndexEntry constructJoinTuple(IndexEntry nextRight) {
		IndexEntry joined = MlindicesFactory.eINSTANCE.createIndexEntry();
		List<Object> left = currentLeft.getKey();
		List<Object> right = nextRight.getKey().subList(joinNode.getCardinality(), nextRight.getKey().size());
		joined.getKey().addAll(left);
		joined.getKey().addAll(right);
		return joined;
	}
	
	public IndexEntry getCurrentLeft() {
		return currentLeft;
	}
	
	public IndexEntry getCurrentRight() {
		return currentRight;
	}
}
