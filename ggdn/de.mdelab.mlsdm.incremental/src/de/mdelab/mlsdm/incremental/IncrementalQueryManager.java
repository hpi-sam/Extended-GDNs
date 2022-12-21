package de.mdelab.mlsdm.incremental;

import de.mdelab.mlsdm.incremental.change.SDMAttributeChange;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMEdgeChange;
import de.mdelab.mlsdm.incremental.change.SDMNodeChange;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent.SDMChangeEnum;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class IncrementalQueryManager extends MLSDMReferenceAdapter {

  public enum FlushStatus {
    FLUSH,
    DELAY,
    IGNORE
  }

  protected Collection<ChangeListener> changeListeners;
  protected SDMLogger logger;

  protected List<SDMChangeEvent> unhandledChanges;
  protected FlushStatus flushing;

  protected List<QueryManagerNotificationReceiver> notificationReceivers;

  public IncrementalQueryManager() {
    this(
        new SDMLogger() {

          @Override
          public void print(String s) {}
        });
  }

  public IncrementalQueryManager(SDMLogger logger) {
    this.changeListeners = new ArrayList<ChangeListener>();
    this.notificationReceivers = new ArrayList<QueryManagerNotificationReceiver>();
    this.logger = logger;
    this.unhandledChanges = new ArrayList<SDMChangeEvent>();
    this.flushing = FlushStatus.DELAY;
  }

  public void setFlushing(FlushStatus flushing) {
    this.flushing = flushing;
  }

  @Override
  public void notifyChanged(Notification notification) {
    super.notifyChanged(notification);
    
    Object source = notification.getNotifier();
    if (source instanceof EObject) {
      if (notification.getFeature() instanceof EAttribute) {
        registerChange(
            new SDMAttributeChange((EObject) source, (EAttribute) notification.getFeature()));
      }
    }
  }

  @Override
  protected void doRemoveEObject(EObject eObject) {
	super.doRemoveEObject(eObject);
	registerChange(new SDMNodeChange(eObject, SDMChangeEnum.DELETE));
	
    for(EReference reference:eObject.eClass().getEAllReferences()) {
  	  for(EObject target:getReferenceTargets(eObject, reference)) {
  		  removeReference(eObject, reference, target);
  		  
  		  if(reference.getEOpposite() != null && !reference.getEOpposite().isContainment()) {
  			  removeReference(target, reference.getEOpposite(), eObject);
  		  }
  	  }
    }
  }

  @Override
  protected void doAddEObject(EObject eObject) {
	super.doAddEObject(eObject);
	registerChange(new SDMNodeChange(eObject, SDMChangeEnum.CREATE));

	for(EReference reference:eObject.eClass().getEAllReferences()) {
	  for(EObject target:getReferenceTargets(eObject, reference)) {
		  addReference(eObject, reference, target);
		  
		  if(reference.getEOpposite() != null && !reference.getEOpposite().isContainment()) {
			  addReference(target, reference.getEOpposite(), eObject);
		  }
	  }
	}
  }

  @Override
  protected void doAddReference(EObject source, EReference reference, EObject target) {
    super.doAddReference(source, reference, target);
    registerChange(new SDMEdgeChange(source, reference, target, SDMChangeEnum.CREATE));

    if(reference.isContainment()) {
    	addEObject(target);
    }
  }

  @Override
  protected void doRemoveReference(EObject source, EReference reference, EObject target) {
    super.doRemoveReference(source, reference, target);
    registerChange(new SDMEdgeChange(source, reference, target, SDMChangeEnum.DELETE));
    
    if(reference.isContainment()) {
    	removeEObject(target);
    }
  }

  protected void registerChange(SDMChangeEvent change) {
    switch (flushing) {
      case DELAY:
        unhandledChanges.add(change);
        break;
      case FLUSH:
        flushEvent(change);
        break;
      case IGNORE:
        break;
      default:
        break;
    }
  }

  protected void flushEvent(SDMChangeEvent change) {
    notifyListeners(change);
  }

  protected void notifyListeners(SDMChangeEvent change) {
    for (ChangeListener changeListener : changeListeners) {
      changeListener.registerChange(change);
    }
  }

  public void flushUnhandledEvents() {
    FlushStatus previousFlushing = flushing;
    flushing = FlushStatus.FLUSH;
    for (SDMChangeEvent change : unhandledChanges) {
      notifyUpdateStart();
      flushEvent(change);
      notifyUpdateEnd();
    }
    unhandledChanges.clear();
    flushing = previousFlushing;
  }

  protected void notifyUpdateStart() {
    for (QueryManagerNotificationReceiver receiver : notificationReceivers) {
      receiver.notifyStartUpdate();
    }
  }

  protected void notifyUpdateEnd() {
    for (QueryManagerNotificationReceiver receiver : notificationReceivers) {
      receiver.notifyEndUpdate();
    }
  }

  public void addChangeListener(ChangeListener listener) {
    changeListeners.add(listener);
  }

  public void removeChangeListener(ChangeListener listener) {
    changeListeners.remove(listener);
  }

  public void clearChangeListeners() {
    changeListeners.clear();
  }

  public void clearUnhandledChanges() {
    unhandledChanges.clear();
  }

  public void registerNotificationReceiver(QueryManagerNotificationReceiver receiver) {
    this.notificationReceivers.add(receiver);
  }

  public void removeNotificationReceiver(QueryManagerNotificationReceiver receiver) {
    this.notificationReceivers.remove(receiver);
  }

  public void updateAllQueryMatches() {
	for(ChangeListener listener:changeListeners) {
		if(listener instanceof SDMQuery) {
			((SDMQuery) listener).getMatches();
		}
	}
  }
  
}
