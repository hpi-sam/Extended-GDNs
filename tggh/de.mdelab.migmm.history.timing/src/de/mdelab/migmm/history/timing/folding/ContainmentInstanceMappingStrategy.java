package de.mdelab.migmm.history.timing.folding;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public abstract class ContainmentInstanceMappingStrategy implements InstanceMappingStrategy {

	@Override
	public InstanceMapping createInstanceMapping(EObject source, EObject target) {
		InstanceMapping mapping = new InstanceMapping();
		if(matches(source, target)) {
			createInstanceMapping(source, target, mapping);

			Collection<EObject> uncontainedSourceObjects = collectUncontainedObjects(source);
			mapping.addUnmappedSources(uncontainedSourceObjects);
			Collection<EObject> uncontainedTargetObjects = collectUncontainedObjects(target);
			mapping.addUnmappedTargets(uncontainedTargetObjects);
		}
		else {
			mapping.addUnmappedSource(source);
			mapping.addUnmappedTarget(target);
		}
		return mapping;
	}

	protected void createInstanceMapping(EObject source, EObject target, InstanceMapping mapping) {
		mapping.addMapping(source, target);
		for(EReference reference:source.eClass().getEAllReferences()) {
			if(!reference.isContainment()) {
				continue;
			}

			List<EObject> sourceLinks = new LinkedList<>(getReferenceValues(source, reference));
			List<EObject> targetLinks = new LinkedList<>(getReferenceValues(target, reference));
			
			for(Iterator<EObject> sourceIt = sourceLinks.iterator(); sourceIt.hasNext();) {
				EObject sourceCandidate = sourceIt.next();
				for(Iterator<EObject> targetIt = targetLinks.iterator(); targetIt.hasNext();) {
					EObject targetCandidate = targetIt.next();
					if(matches(sourceCandidate, targetCandidate)) {
						createInstanceMapping(sourceCandidate, targetCandidate, mapping);
						sourceIt.remove();
						targetIt.remove();
						break;
					}
				}
			}

			mapping.addUnmappedSources(sourceLinks);
			mapping.addUnmappedTargets(targetLinks);
		}
	}

	protected Collection<EObject> collectUncontainedObjects(EObject root) {
		Set<EObject> seenObjects = new LinkedHashSet<EObject>();
		Queue<EObject> queue = new LinkedList<EObject>();
		queue.add(root);
		
		while(!queue.isEmpty()) {
			EObject current = queue.poll();
			
			for(EReference reference:current.eClass().getEAllReferences()) {
				Collection<EObject> referencedObjects = getReferenceValues(current, reference);
				for(EObject referencedObject:referencedObjects) {
					if(!seenObjects.contains(referencedObject)) {
						queue.add(referencedObject);
						seenObjects.add(referencedObject);
					}
				}
			}
		}
		
		Set<EObject> containedObjects = new LinkedHashSet<EObject>();		
		containedObjects.add(root);
		for(Iterator<EObject> it = root.eAllContents(); it.hasNext();) {
			containedObjects.add(it.next());
		}
		
		seenObjects.removeAll(containedObjects);
		
		return seenObjects;
	}

	@SuppressWarnings("unchecked")
	private Collection<EObject> getReferenceValues(EObject original, EReference reference) {
		Object value = original.eGet(reference);
		if(reference.isMany()) {
			return (Collection<EObject>) value;
		}
		else if(value != null) {
			return Collections.singleton((EObject) value);
		}
		else {
			return Collections.emptySet();
		}
	}

	protected abstract boolean matches(EObject source, EObject target);

}
