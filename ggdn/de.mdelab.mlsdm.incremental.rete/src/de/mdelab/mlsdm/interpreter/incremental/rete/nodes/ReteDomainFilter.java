package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteDomainFilter extends ReteImmutableFilter {

	private EClassifier classifier;
	private int index;

	public ReteDomainFilter(EClassifier classifier, int index) {
		this.classifier = classifier;
		this.index = index;
	}

	@Override
	protected boolean filter(ReteTuple tuple) {
		Object o = tuple.get(index);
		return !(o instanceof EObject) ||
				(((EObject) o).eClass() != classifier && !((EObject) o).eClass().getEAllSuperTypes().contains(classifier));
	}

}
