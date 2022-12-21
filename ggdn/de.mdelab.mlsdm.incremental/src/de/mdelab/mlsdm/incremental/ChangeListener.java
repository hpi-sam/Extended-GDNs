package de.mdelab.mlsdm.incremental;

import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;

public interface ChangeListener {

	public void registerChange(SDMChangeEvent change);

}
