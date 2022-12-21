package de.mdelab.mlsdm.incremental.rete.sdm;

import java.util.Iterator;

import de.mdelab.mlsdm.mlindices.IndexEntry;

public class SDMAntiJoinResultIterator extends SDMJoinResultIterator {

	public SDMAntiJoinResultIterator(Iterator<IndexEntry> leftIt, SDMAntiJoinQuery joinNode) {
		super(leftIt, joinNode);
	}
	
	@Override
	public boolean hasNext() {
		if(next == null) {
			next = computeNext();
		}
		return next != null;
	}

	private IndexEntry computeNext() {
		while(leftIt.hasNext()) {
			IndexEntry candidate = leftIt.next();
			if(!((SDMAntiJoinQuery) joinNode).hasRightOpposite(candidate)) {
				currentLeft = candidate;
				return currentLeft;
			}
		}
		return null;
	}
	
}
