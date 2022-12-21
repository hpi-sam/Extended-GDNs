package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMEdgeChange;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent.SDMChangeEnum;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteNodeExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteParametrizedEdgeInputExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.projection.FeatureSubscriber;
import de.mdelab.mlsdm.interpreter.incremental.rete.projection.SubscriptionAdapter;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;
import de.mdelab.mlstorypatterns.StoryPatternLink;

public class ReteParametrizedEdgeInput extends ReteInput implements FeatureSubscriber {

	private final ReteIndexer sourceIndexer;
	private final ReteIndexer targetIndexer;
	private final StoryPatternLink link;
	private final boolean lazyParameter;

	private SubscriptionAdapter adapter;

	public ReteParametrizedEdgeInput(StoryPatternLink link,
			ReteIndexer sourceIndexer, ReteIndexer targetIndexer) {
		this(link, sourceIndexer, targetIndexer, false);
	}

	public ReteParametrizedEdgeInput(StoryPatternLink link,
			ReteIndexer sourceIndexer, ReteIndexer targetIndexer, boolean lazy) {
		this.link = link;
		this.sourceIndexer = sourceIndexer;
		this.targetIndexer = targetIndexer;
		this.lazyParameter = lazy;
	}

	@Override
	public void doAddTuple(ReteTuple tuple, ReteNode source) {
		if(source == sourceIndexer) {	//TODO keep track of subscription to avoid redundant propagation?
			EObject sourceObject = (EObject) tuple.get(0);
			adapter.subscribeToNotifications(sourceObject, link.getFeature(), this);
			for(Object targetObject:getFeatureTargets(sourceObject, link.getFeature())) {
				if(isValidTarget(targetObject)) {
					propagateAddition(sourceObject, targetObject);
				}
			}
		}
		else {
			EObject targetObject = (EObject) tuple.get(0);
			adapter.subscribeToNotifications(targetObject, ((EReference)link.getFeature()).getEOpposite(), this);
			for(Object sourceObject:getFeatureTargets(targetObject, ((EReference) link.getFeature()).getEOpposite())) {
				if(isValidSource(sourceObject)) {
					propagateAddition(sourceObject, targetObject);
				}
			}
		}
	}

	@Override
	public void doRemoveTuple(ReteTuple tuple, ReteNode source) {
		if(!lazyParameter) {
			if(source == sourceIndexer) {
				EObject sourceObject = (EObject) tuple.get(0);
				adapter.unsubscribe(sourceObject, link.getFeature(), this);
				for(Object targetObject:getFeatureTargets(sourceObject, link.getFeature())) {
					if(isValidTarget(targetObject)) {
						propagateRemoval(sourceObject, targetObject);
					}
				}
			}
			else {
				EObject targetObject = (EObject) tuple.get(0);
				adapter.unsubscribe(targetObject, ((EReference)link.getFeature()).getEOpposite(), this);
				for(Object sourceObject:getFeatureTargets(targetObject, ((EReference) link.getFeature()).getEOpposite())) {
					if(isValidSource(sourceObject)) {
						propagateRemoval(sourceObject, targetObject);
					}
				}
			}
		}
	}

	private Collection<? extends Object> getFeatureTargets(EObject object, EStructuralFeature feature) {
		try {
			if(feature.isMany()) {
				return (Collection<?>) object.eGet(feature);
			}
			else {
				Object target = object.eGet(feature);
				return target != null ? Collections.singleton(target) : Collections.emptySet();
			}
		}
		catch(Exception e) {	//TODO be more specific (Thomas' exception types)
			return Collections.emptySet();
		}
	}

	@Override
	public void registerChange(SDMChangeEvent change) {
		if(change.getType() == link.getFeature() && change.getModifier() == SDMChangeEnum.CREATE) {
			EObject source = ((SDMEdgeChange)change).getSource();
			EObject target = ((SDMEdgeChange)change).getTarget();
			if(isValidSource(source) && isValidTarget(target)) {
				propagateAddition(source, target);
			}
		}
		else if(change.getType() == link.getFeature() && change.getModifier() == SDMChangeEnum.DELETE) {
			EObject source = ((SDMEdgeChange)change).getSource();
			EObject target = ((SDMEdgeChange)change).getTarget();
			if(isValidSource(source) && isValidTarget(target)) {
				propagateRemoval(source, target);
			}
		}
	}

	private void propagateAddition(Object source, Object target) {
		List<Object> tuple = new ArrayList<Object>(2);
		tuple.add(source);
		tuple.add(target);
		propagateAddition(new ReteTuple(tuple));
	}

	private void propagateRemoval(Object source, Object target) {
		List<Object> tuple = new ArrayList<Object>(2);
		tuple.add(source);
		tuple.add(target);
		propagateRemoval(new ReteTuple(tuple));	//TODO retrieve old tuple!!!
	}

	private boolean isValidSource(Object source) {
		return sourceIndexer == null || !sourceIndexer.getTuples(Collections.singletonList(source)).isEmpty();
	}

	private boolean isValidTarget(Object target) {
		return targetIndexer == null || !targetIndexer.getTuples(Collections.singletonList(target)).isEmpty();
	}

	@Override
	public ReteNodeExecutor createExecutor() {
		return new ReteParametrizedEdgeInputExecutor(this, sourceIndexer, targetIndexer, (EReference) link.getFeature());
	}
	
	public boolean isFeatureSubscriber() {
		return true;
	}

	@Override
	public void setSubscriptionAdapter(SubscriptionAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public String toString() {
		return "PARAMETRIZED INPUT <" + link.getSource().getName() + " -" + link.getFeature().getName() + "-> " + link.getTarget().getName() + ">";
	}
}
