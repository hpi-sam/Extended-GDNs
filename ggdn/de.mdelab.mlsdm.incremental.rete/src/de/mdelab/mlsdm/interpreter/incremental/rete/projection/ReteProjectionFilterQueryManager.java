package de.mdelab.mlsdm.interpreter.incremental.rete.projection;

import de.mdelab.mlsdm.incremental.ChangeListener;
import de.mdelab.mlsdm.incremental.QueryManagerNotificationReceiver;
import de.mdelab.mlsdm.incremental.SDMLogger;
import de.mdelab.mlsdm.incremental.change.SDMAttributeChange;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMEdgeChange;
import de.mdelab.mlsdm.incremental.change.SDMNodeChange;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent.SDMChangeEnum;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNet;
import de.mdelab.mlsdm.interpreter.incremental.rete.StoryPatternModelIndex;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteEdgeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteJoin;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteResultProvider;
import de.mdelab.mlsdm.interpreter.incremental.rete.projection.RetePushPullAnalyser.PushSideEnum;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.FeatureSpecification;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Tuple;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import de.mdelab.sdm.interpreter.core.SDMException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public class ReteProjectionFilterQueryManager implements Adapter, SubscriptionAdapter {

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

  protected Map<EObject, Set<Tuple<EStructuralFeature, Object>>> subscriptions;

  public ReteProjectionFilterQueryManager() {
    this(
        new SDMLogger() {

          @Override
          public void print(String s) {}
        });
  }

  public ReteProjectionFilterQueryManager(SDMLogger logger) {
    this.changeListeners = new ArrayList<ChangeListener>();
    this.notificationReceivers = new ArrayList<QueryManagerNotificationReceiver>();
    this.logger = logger;
    this.unhandledChanges = new ArrayList<SDMChangeEvent>();
    this.subscriptions = new HashMap<EObject, Set<Tuple<EStructuralFeature, Object>>>();
    this.flushing = FlushStatus.DELAY;
  }

  public void setFlushStatus(FlushStatus flushing) {
    this.flushing = flushing;
  }

  public Collection<EObject> getRegisteredObjects() {
    return subscriptions.keySet();
  }

  public ReteNet createReteNet(
      StoryPattern pattern, Collection<? extends EObject> searchRoots, AbstractStoryPatternObject... anchors)
      throws SDMException {
    StoryPatternModelIndex modelIndex = buildModelIndex(pattern, searchRoots);
    StaticRETEBuilder reteBuilder = new StaticRETEBuilder(pattern, modelIndex);
    Tuple<ReteNet, int[]> result = reteBuilder.createReteNet();
    ReteNet net = result.e1;
    RetePushPullAnalyser analyser = new RetePushPullAnalyser();
    Map<ReteJoin, PushSideEnum> pushDirections = analyser.analyzeReteNet(net, anchors);
    ReteFilterInserter filterInserter = new ReteFilterInserter(reteBuilder.getSearchModel());
    filterInserter.insertFilters(net, pushDirections);
    ReteResultProvider resultProvider = new ReteResultProvider();
    resultProvider.setTableMask(result.e2);
    net.getRoot().addSuccessor(resultProvider);
    net.setRoot(resultProvider);
    registerReteNet(net);
    modelIndex.clear();	//clearing the model index here prevents effective usage of node executors
    return net;
  }

  public ReteNet createAndPopulateReteNet(
      StoryPattern pattern, Collection<? extends EObject> searchRoots, AbstractStoryPatternObject... anchors)
      throws SDMException {
    ReteNet net = createReteNet(pattern, searchRoots, anchors);
    subscribeAndCreateRequiredNotifications(searchRoots);
    flushUnhandledEvents();
    return net;
  }

  private StoryPatternModelIndex buildModelIndex(StoryPattern pattern, Collection<? extends EObject> graph) {
    StoryPatternModelIndex modelIndex = new StoryPatternModelIndex();
//    modelIndex.observePatternStatistics(pattern);	//TODO this makes it so that a bad RETE net is built...
//    for (EObject object : graph) {
//      modelIndex.addEObject(object);
//
//      for (EReference reference : object.eClass().getEAllReferences()) {
//        for (EObject target : getReferenceTargets(object, reference)) {
//          modelIndex.addReference(object, reference, target);
//        }
//      }
//    }
    return modelIndex;
  }

  public void registerReteNet(ReteNet net) {
    for (ReteNode node : net.getNodes().values()) {
      if (node instanceof ChangeListener) {
        changeListeners.add((ChangeListener) node);
      }

      if (node.isFeatureSubscriber()) {
        ((FeatureSubscriber) node).setSubscriptionAdapter(this);
      }
    }
  }

  public void subscribeAndCreateRequiredNotifications(Collection<? extends EObject> objects) {
    Map<EClassifier, Collection<FeatureSpecification>> featureSpecs = collectRequiredFeatures();
    for (EObject object : objects) {
      // TODO how about single nodes?
      if (featureSpecs.containsKey(object.eClass())) {
        for (FeatureSpecification featureSpec : featureSpecs.get(object.eClass())) {
          subscribeToNotifications(object, featureSpec.feature, this);

          for (EObject target : getReferenceTargets(object, featureSpec.feature)) {
            registerEdgeAddition(object, target, featureSpec.feature);
          }
        }
      }

      for (EClass superClass : object.eClass().getEAllSuperTypes()) {
        if (featureSpecs.containsKey(superClass)) {
          for (FeatureSpecification featureSpec : featureSpecs.get(superClass)) {
            subscribeToNotifications(object, featureSpec.feature, this);

            for (EObject target : getReferenceTargets(object, featureSpec.feature)) {
              registerEdgeAddition(object, target, featureSpec.feature);
            }
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Collection<? extends EObject> getReferenceTargets(EObject source, EReference feature) {
    if (feature.isMany()) {
      return (Collection<? extends EObject>) source.eGet(feature);
    } else {
      return source.eGet(feature) != null
          ? Collections.singleton((EObject) source.eGet(feature))
          : Collections.emptyList();
    }
  }

  private Map<EClassifier, Collection<FeatureSpecification>> collectRequiredFeatures() {
    Map<EClassifier, Collection<FeatureSpecification>> features =
        new HashMap<EClassifier, Collection<FeatureSpecification>>();
    for (ChangeListener changeListener : changeListeners) {
      if (changeListener instanceof ReteEdgeInput) {
        StoryPatternLink link = ((ReteEdgeInput) changeListener).getLink();
        EClassifier sourceClassifier = link.getSource().getType();
        EClassifier targetClassifier = link.getTarget().getType();
        EReference feature = (EReference) link.getFeature();
        if (!features.containsKey(sourceClassifier)) {
          features.put(sourceClassifier, new HashSet<FeatureSpecification>());
        }
        features
            .get(sourceClassifier)
            .add(new FeatureSpecification(sourceClassifier, feature, targetClassifier));
      }
    }
    return features;
  }

  @Override
  public void subscribeToNotifications(
      EObject object, EStructuralFeature feature, Object subscriber) {
    if (!subscriptions.containsKey(object)) {
      object.eAdapters().add(this);
      subscriptions.put(object, new HashSet<Tuple<EStructuralFeature, Object>>());
    }
    subscriptions.get(object).add(new Tuple<EStructuralFeature, Object>(feature, subscriber));
  }

  @Override
  public void unsubscribe(EObject object, EStructuralFeature feature, Object subscriber) {
    Collection<Tuple<EStructuralFeature, Object>> featureSubscriptions =
        getFeatureSubscriptions(object);
    featureSubscriptions.remove(new Tuple<EStructuralFeature, Object>(feature, subscriber));
    if (featureSubscriptions.isEmpty()) {
      subscriptions.remove(object);
      object.eAdapters().remove(this);
    }
  }

  protected Collection<Tuple<EStructuralFeature, Object>> getFeatureSubscriptions(EObject object) {
    Collection<Tuple<EStructuralFeature, Object>> featureSubscriptions = subscriptions.get(object);
    return featureSubscriptions != null ? featureSubscriptions : Collections.emptySet();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void notifyChanged(Notification notification) {
    Object source = notification.getNotifier();
    if (source instanceof EObject) {
      if (notification.getFeature() instanceof EReference) {
        Object oldValue = notification.getOldValue();
        Object newValue = notification.getNewValue();

        switch (notification.getEventType()) {
          case Notification.ADD:
            if (newValue instanceof EObject) {
              registerEdgeAddition(
                  (EObject) source, (EObject) newValue, (EReference) notification.getFeature());
            }
            break;
          case Notification.ADD_MANY:
            for (EObject target : (Collection<EObject>) newValue) {
              registerEdgeAddition(
                  (EObject) source, target, (EReference) notification.getFeature());
            }
            break;
          case Notification.REMOVE:
            if (oldValue instanceof EObject) {
              registerEdgeRemoval(
                  (EObject) source, (EObject) oldValue, (EReference) notification.getFeature());
            }
            break;
          case Notification.REMOVE_MANY:
            for (EObject target : (Collection<EObject>) oldValue) {
              registerEdgeRemoval((EObject) source, target, (EReference) notification.getFeature());
            }
            break;
          case Notification.SET:
            if (oldValue instanceof EObject) {
              registerEdgeRemoval(
                  (EObject) source, (EObject) oldValue, (EReference) notification.getFeature());
            }
            if (newValue instanceof EObject) {
              registerEdgeAddition(
                  (EObject) source, (EObject) newValue, (EReference) notification.getFeature());
            }
        }
      } else if (notification.getFeature() instanceof EAttribute) {
        registerChange(
            new SDMAttributeChange((EObject) source, (EAttribute) notification.getFeature()));
      }
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.emf.common.notify.Adapter#getTarget()
   */
  @Override
  public Notifier getTarget() {
    return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common.notify.Notifier)
   */
  @Override
  public void setTarget(Notifier newTarget) {}

  /* (non-Javadoc)
   * @see org.eclipse.emf.common.notify.Adapter#isAdapterForType(java.lang.Object)
   */
  @Override
  public boolean isAdapterForType(Object type) {
    return type instanceof EClass;
  }

  private void registerEdgeRemoval(EObject source, EObject target, EReference feature) {
    if (feature.isContainment()) {
      //interpret as deletion
      registerNodeRemoval(target);
    }

    registerChange(new SDMEdgeChange(source, feature, target, SDMChangeEnum.DELETE));
  }

  private void registerNodeRemoval(EObject node) {
      registerChange(new SDMNodeChange(node, SDMChangeEnum.DELETE));
      
      for(EReference reference:node.eClass().getEAllReferences()) {
    	  if(reference.isContainer()) {
    		  continue;	//TODO figure out what should actually be done here.
    	  }
    	  for(EObject target:getReferenceTargets(node, reference)) {
    		  registerEdgeRemoval(node, target, reference);
    		  
    		  if(reference.getEOpposite() != null && !reference.getEOpposite().isContainment()) {
    			  registerEdgeRemoval(target, node, reference.getEOpposite());
    		  }
    	  }
      }
  }

  private void registerEdgeAddition(EObject source, EObject target, EReference feature) {
//    if (feature.isContainment()) {
//      registerNodeAddition(target);
//    }

    registerChange(new SDMEdgeChange(source, feature, target, SDMChangeEnum.CREATE));
  }

  private void registerNodeAddition(EObject node) {
	  registerChange(new SDMNodeChange(node, SDMChangeEnum.CREATE));
	  
      for(EReference reference:node.eClass().getEAllReferences()) {
    	  if(reference.isContainer()) {
    		  continue;	//TODO figure out what should actually be done here.
    	  }
    	  for(EObject target:getReferenceTargets(node, reference)) {
    		  registerEdgeAddition(node, target, reference);

    		  if(reference.getEOpposite() != null && !reference.getEOpposite().isContainment()) {
    			  registerEdgeAddition(target, node, reference.getEOpposite());
    		  }
    	  }
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
}
