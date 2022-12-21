package de.mdelab.mlsdm.interpreter.incremental.rete;

import java.util.ArrayList;
import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteNotifier {

	private List<ReteNotificationReceiver> notificationReceivers;
	
	public ReteNotifier() {
		this.notificationReceivers = new ArrayList<ReteNotificationReceiver>();
	}
	
	public void addNotificationReceiver(ReteNotificationReceiver receiver) {
		this.notificationReceivers.add(receiver);
	}

	public void removeNotificationReceiver(ReteNotificationReceiver receiver) {
		this.notificationReceivers.remove(receiver);
	}
	
	public void notifyAddition(ReteTuple tuple) {
		for(ReteNotificationReceiver notificationReceiver:notificationReceivers) {
			notificationReceiver.notifyAddition(tuple, this);
		}
	}
	
	public void notifyRemoval(ReteTuple tuple) {
		for(ReteNotificationReceiver notificationReceiver:notificationReceivers) {
			notificationReceiver.notifyRemoval(tuple, this);
		}
	}

	public boolean isReteNode() {
		return false;
	}

}
