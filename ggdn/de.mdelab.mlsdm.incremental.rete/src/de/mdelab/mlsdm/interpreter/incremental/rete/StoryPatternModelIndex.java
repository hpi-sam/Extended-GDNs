package de.mdelab.mlsdm.interpreter.incremental.rete;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import de.mdelab.mlsdm.interpreter.incremental.rete.util.FeatureSpecification;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Tuple;
import de.mdelab.mlstorypatterns.AbstractStoryPatternLink;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.MlstorypatternsPackage;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;

public class StoryPatternModelIndex {

	/*
	 * model indices
	 */
	protected Map<StoryPatternLink, Collection<Tuple<EObject, EObject>>> link2references;
	protected Map<AbstractStoryPatternObject, Collection<EObject>> spo2domains;

	protected Map<FeatureSpecification, Collection<Tuple<EObject, EObject>>> references;
	protected Map<EClassifier, Collection<EObject>> domains;
	
	protected Map<EReference, Collection<FeatureSpecification>> featureSpecs;
	
	public StoryPatternModelIndex() {
		this.link2references = new HashMap<StoryPatternLink, Collection<Tuple<EObject, EObject>>>();
		this.spo2domains = new HashMap<AbstractStoryPatternObject, Collection<EObject>>();
		this.references = new HashMap<FeatureSpecification, Collection<Tuple<EObject, EObject>>>();
		this.domains = new HashMap<EClassifier, Collection<EObject>>();
		this.featureSpecs = new HashMap<EReference, Collection<FeatureSpecification>>();
	}

	public void addEObject(EObject eObject) {
		EClass type = eObject.eClass();
		if(domains.containsKey(type)) {
			domains.get(type).add(eObject);
		}
		for(EClassifier superType:type.getEAllSuperTypes()) {
			if(domains.containsKey(superType)) {
				domains.get(superType).add(eObject);
			}	
		}
	}

	public void removeEObject(EObject eObject) {
		EClass type = eObject.eClass();
		if(domains.containsKey(type)) {
			domains.get(type).remove(eObject);
		}
		for(EClassifier superType:type.getEAllSuperTypes()) {
			if(domains.containsKey(superType)) {
				domains.get(superType).remove(eObject);
			}	
		}
	}

	private boolean isSupertype(EClassifier superType, EClass subType) {
		return superType == subType || subType.getEAllSuperTypes().contains(superType);
	}

	public void addReference(EObject source, EReference reference,
			EObject target) {
		if(featureSpecs.containsKey(reference)) {
			for(FeatureSpecification featureSpec:featureSpecs.get(reference)) {
				if(isSupertype(featureSpec.sourceClassifier, source.eClass()) && isSupertype(featureSpec.targetClassifier, target.eClass())) {
					references.get(featureSpec).add(new Tuple<EObject, EObject>(source, target));
				}
			}
		}
	}

	public void removeReference(EObject source, EReference reference,
			EObject target) {
		if(featureSpecs.containsKey(reference)) {
			for(FeatureSpecification featureSpec:featureSpecs.get(reference)) {
				if(isSupertype(featureSpec.sourceClassifier, source.eClass()) && isSupertype(featureSpec.targetClassifier, target.eClass())) {
					references.get(featureSpec).remove(new Tuple<EObject, EObject>(source, target));
				}
			}
		}
	}

	public void observePatternStatistics(StoryPattern sp) {
		for(AbstractStoryPatternObject spo:sp.getStoryPatternObjects()) {
			if(!domains.containsKey(spo.getType())) {
				domains.put(spo.getType(), new HashSet<EObject>());
			}
			spo2domains.put(spo, domains.get(spo.getType()));
		}
		
		for(AbstractStoryPatternLink link:sp.getStoryPatternLinks()) {
			if(link.eClass() == MlstorypatternsPackage.Literals.STORY_PATTERN_LINK) {
				FeatureSpecification featureSpec = new FeatureSpecification(link.getSource().getType(),
						(EReference) ((StoryPatternLink) link).getFeature(), link.getTarget().getType());
				
				if(!references.containsKey(featureSpec)) {
					references.put(featureSpec, new HashSet<Tuple<EObject, EObject>>());
					
					if(!featureSpecs.containsKey(featureSpec.feature)) {
						featureSpecs.put(featureSpec.feature, new HashSet<FeatureSpecification>());
					}
					featureSpecs.get(featureSpec.feature).add(featureSpec);
				}
				
				link2references.put((StoryPatternLink) link, references.get(featureSpec));
			}
		}
		
		for(StoryPattern nac:sp.getNegativeApplicationConditions()) {
			observePatternStatistics(nac);
		}
	}

	public Map<EClassifier, Collection<EObject>> getDomains() {
		return domains;
	}
	
	public Map<FeatureSpecification, Collection<Tuple<EObject, EObject>>> getReferences() {
		return references;
	}

	public Collection<Tuple<EObject, EObject>> getReferences(StoryPatternLink link) {
		return link2references.get(link);
	}

	public Collection<EObject> getDomain(AbstractStoryPatternObject spo) {
		return spo2domains.get(spo);
	}

	public void clear() {
		references.clear();
		domains.clear();
	}
}
