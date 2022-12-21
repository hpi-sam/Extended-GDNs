package de.mdelab.mlsdm.incremental.rete.sdm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.mdelab.mlsdm.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.mlsdm.mlindices.NotifyingIndex;

public class SDMJoinInterfaceIndex extends SDMReteInterfaceIndex {

	protected Map<IndexEntry, Collection<IndexEntry>> childEntries;
	
	public SDMJoinInterfaceIndex(NotifyingIndex index,
			Map<String, Integer> parameterPositions) {
		super(index, parameterPositions);
		this.childEntries = new HashMap<IndexEntry, Collection<IndexEntry>>();
	}
	
	public void addEntry(SDMQueryMatch match, IndexEntry leftParent, IndexEntry rightParent) {
		IndexEntry entry = super.addEntry(match);
		addChildEntry(leftParent, entry);
		addChildEntry(rightParent, entry);
	}

	private void addChildEntry(IndexEntry parent, IndexEntry child) {
		if(parent == null || child == null) {
			return;
		}
		
		if(!childEntries.containsKey(parent)) {
			childEntries.put(parent, new ArrayList<IndexEntry>());
		}
		childEntries.get(parent).add(child);
	}

	public void removeChildEntries(IndexEntry parent) {
		if(childEntries.containsKey(parent)) {
			for(IndexEntry child:childEntries.get(parent)) {
				super.remove(child.getKey());
			}
		}
	}
	
}
