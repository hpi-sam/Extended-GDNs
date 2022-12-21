package de.mdelab.mlsdm.interpreter.incremental.rete.projection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public interface SubscriptionAdapter {

	public void subscribeToNotifications(EObject object, EStructuralFeature feature, Object subscriber);

	public void unsubscribe(EObject eObject, EStructuralFeature key, Object subscriber);
	
}
