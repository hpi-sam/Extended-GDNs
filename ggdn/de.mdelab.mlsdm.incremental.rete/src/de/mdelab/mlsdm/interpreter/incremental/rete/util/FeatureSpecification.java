package de.mdelab.mlsdm.interpreter.incremental.rete.util;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;

public class FeatureSpecification {
		
	public EReference feature;
	public EClassifier sourceClassifier;
	public EClassifier targetClassifier;
	
	public FeatureSpecification(EClassifier sourceClassifier, EReference feature,
			EClassifier targetClassifier) {
		this.sourceClassifier = sourceClassifier;
		this.feature = feature;
		this.targetClassifier = targetClassifier;
	}

	@Override
	public int hashCode() {
		return feature.hashCode() ^ sourceClassifier.hashCode() ^ targetClassifier.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(!(o instanceof FeatureSpecification)) {
			return false;
		}
		else {
			FeatureSpecification other = (FeatureSpecification) o;
			return other.feature == this.feature &&
					other.sourceClassifier == this.sourceClassifier &&
					other.targetClassifier == this.targetClassifier;
		}
	}
	
	@Override
	public String toString() {
		return "(" + sourceClassifier.getName() + " -" + feature.getName() + "-> " + targetClassifier.getName() + ")";
	}
	
}
