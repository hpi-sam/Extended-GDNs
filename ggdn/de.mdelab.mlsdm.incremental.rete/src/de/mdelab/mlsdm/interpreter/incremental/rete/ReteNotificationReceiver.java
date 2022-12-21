package de.mdelab.mlsdm.interpreter.incremental.rete;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public interface ReteNotificationReceiver {

	public void notifyAddition(ReteTuple tuple, ReteNotifier notifier);
	public void notifyRemoval(ReteTuple tuple, ReteNotifier notifier);
	
}
