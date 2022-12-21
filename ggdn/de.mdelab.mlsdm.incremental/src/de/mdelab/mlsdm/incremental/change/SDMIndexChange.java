package de.mdelab.mlsdm.incremental.change;

import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.mlsdm.mlindices.NotifyingIndex;

public class SDMIndexChange implements SDMChangeEvent {

	private SDMChangeEnum modifier;
	private IndexEntry entry;
	private NotifyingIndex index;

	public SDMIndexChange(NotifyingIndex index, IndexEntry entry,
			SDMChangeEnum modifier) {
		this.index = index;
		this.entry = entry;
		this.modifier = modifier;
	}

	@Override
	public SDMChangeEnum getModifier() {
		return modifier;
	}

	@Override
	public Object getType() {
		return index;
	}

	public IndexEntry getEntry() {
		return entry;
	}

}
