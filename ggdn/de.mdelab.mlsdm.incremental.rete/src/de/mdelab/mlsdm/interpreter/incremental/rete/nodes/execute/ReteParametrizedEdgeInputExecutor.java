package de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteParametrizedEdgeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteParametrizedEdgeInputExecutor extends ReteNodeExecutor {

	private ReteParametrizedEdgeInput node;
	private ReteIndexer sourceIndexer;
	private ReteIndexer targetIndexer;
	private EStructuralFeature feature;
	private boolean isBackward;
	
	private Iterator<List<Object>> sourceIterator;
	private Iterator<? extends Object> targetIterator;
	private EObject currentSource;
	
	private ReteTuple next;
	
	public ReteParametrizedEdgeInputExecutor(
			ReteParametrizedEdgeInput node,
			ReteIndexer sourceIndexer, ReteIndexer targetIndexer,
			EStructuralFeature feature) {
		this.node = node;
		if(sourceIndexer != null) {
			this.sourceIndexer = sourceIndexer;
			this.targetIndexer = targetIndexer;
			this.feature = feature;
			this.isBackward = false;
		}
		else if(feature.eClass() == EcorePackage.Literals.EREFERENCE && ((EReference) feature).getEOpposite() != null && targetIndexer != null) {
			this.sourceIndexer = targetIndexer;
			this.targetIndexer = sourceIndexer;
			this.feature = ((EReference) feature).getEOpposite();
			this.isBackward = true;
		}
		else {
			throw new UnsupportedOperationException("Illegal parametrization");
		}
		
		this.sourceIterator = this.sourceIndexer.getAllTuples().keySet().iterator();
	}

	@Override
	public boolean hasNextTuple() {
		if(next == null) {
			next = computeNext();
		}
		return next != null;
	}

	private ReteTuple computeNext() {
		while(sourceIterator.hasNext() || targetIterator.hasNext()) {
			while(sourceIterator.hasNext() && !targetIterator.hasNext()) {
				currentSource = (EObject) sourceIterator.next().get(0);
				targetIterator = getFeatureIterator(currentSource);
			}
			
			while(targetIterator.hasNext()) {
				Object target = targetIterator.next();
				if(isValidTarget(target)) {
					List<Object> tuple = new ArrayList<Object>(2);
					if(isBackward) {
						tuple.add(target);
						tuple.add(currentSource);
					}
					else {
						tuple.add(currentSource);
						tuple.add(target);
					}
					return new ReteTuple(tuple);
				}
			}
		}
		return null;
	}

	private boolean isValidTarget(Object target) {
		return !targetIndexer.getTuples(Collections.singletonList(target)).isEmpty();
	}

	private Iterator<? extends Object> getFeatureIterator(EObject source) {
		if(feature.isMany()) {
			return ((Collection<?>) source.eGet(feature)).iterator();
		}
		else {
			Object target = source.eGet(feature);
			return target != null ? Collections.singleton(target).iterator() : Collections.emptyIterator();
		}
	}

	@Override
	public void generateNextTuple() {
		if(!hasNextTuple()) {
            throw new NoSuchElementException();
		}
		else {
			ReteTuple propagated = next;
			next = null;
			node.propagateAddition(propagated);
		}
	}

}
