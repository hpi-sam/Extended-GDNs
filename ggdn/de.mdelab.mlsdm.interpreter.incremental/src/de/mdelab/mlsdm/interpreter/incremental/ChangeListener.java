package de.mdelab.mlsdm.interpreter.incremental;

import de.mdelab.mlsdm.interpreter.incremental.change.SDMChangeEvent;

public interface ChangeListener {

	public void registerChange(SDMChangeEvent change);

}
