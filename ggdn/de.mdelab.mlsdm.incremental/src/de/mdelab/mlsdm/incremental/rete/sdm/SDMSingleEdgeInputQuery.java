package de.mdelab.mlsdm.incremental.rete.sdm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import de.mdelab.mlsdm.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMEdgeChange;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex.MLSDMReferenceAdapterTuple;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.mlsdm.mlindices.MlindicesFactory;
import de.mdelab.mlstorypatterns.StoryPatternLink;

public class SDMSingleEdgeInputQuery extends SDMQuery {

	private final EClassifier sourceType;
	private final EClassifier targetType;
	
	private final EReference feature;
	private final MLSDMReferenceIndex ra;
	
	private final Map<String, Integer> parameterToIndex;

	public SDMSingleEdgeInputQuery(
			SDMQueryManager manager,
			StoryPatternLink link,
			MLSDMReferenceIndex ra) {
		super(manager);
		this.sourceType = link.getSource().getType();
		this.targetType = link.getTarget().getType();
		this.feature = (EReference) link.getFeature();
		this.ra = ra;
		this.parameterToIndex = new HashMap<String, Integer>();
		this.parameterToIndex.put(link.getSource().getName(), 0);
		this.parameterToIndex.put(link.getTarget().getName(), 1);
	}

	@Override
	public void findInitialMatches() {
		for(SDMInterfaceIndex index:interfaceIndices) {
			for(Iterator<SDMQueryMatch> it = getMatches(); it.hasNext();) {
			SDMQueryMatch match = it.next();
				index.addEntry(match);
			}
		}
	}

	@Override
	public Iterator<SDMQueryMatch> getMatches() {
		final Iterator<MLSDMReferenceAdapterTuple<EObject>> it = ra.getReferences(feature).iterator();
		return new Iterator<SDMQueryMatch>() {

			MLSDMReferenceAdapterTuple<EObject> next = null;
			
			@Override
			public boolean hasNext() {
				if(next == null) {
					next = computeNext();
				}
				return next != null;
			}

			private MLSDMReferenceAdapterTuple<EObject> computeNext() {
				while(it.hasNext()) {
					MLSDMReferenceAdapterTuple<EObject> candidate = it.next();
					if(matchesType(candidate.e1.eClass(), sourceType) && matchesType(candidate.e2.eClass(), targetType)) {
						return candidate;
					}
				}
				return null;
			}

			@Override
			public SDMQueryMatch next() {
				if(!hasNext()) {
					return null;
				}
				else {
					MLSDMReferenceAdapterTuple<EObject> returnValue = next;
					next = null;
					return new SDMQueryMatch(constructIndexEntry(returnValue), parameterToIndex);
				}
			}
			
		};
	}

	private IndexEntry constructIndexEntry(MLSDMReferenceAdapterTuple<EObject> link) {
		IndexEntry entry = MlindicesFactory.eINSTANCE.createIndexEntry();
		entry.getKey().add(link.e1);
		entry.getKey().add(link.e2);
		return entry;
	}
	
	@Override
	public void registerChange(SDMChangeEvent change) {
		if(change.getType() == feature &&
				matchesType(((SDMEdgeChange) change).getSource().eClass(), sourceType) &&
				matchesType(((SDMEdgeChange) change).getTarget().eClass(), targetType)) {
			IndexEntry entry = constructIndexEntry(new MLSDMReferenceAdapterTuple<EObject>(((SDMEdgeChange) change).getSource(), ((SDMEdgeChange) change).getTarget()));
			switch(change.getModifier()) {
				case CREATE :
					for(SDMInterfaceIndex index:interfaceIndices) {
						index.addEntry(new SDMQueryMatch(entry, parameterToIndex));
					}
					break;
				case DELETE :
					for(SDMInterfaceIndex index:interfaceIndices) {
						index.removeEntry(new SDMQueryMatch(entry, parameterToIndex));
					}
					break;
				case MODIFY :
					break;
				default :
					break;
			}
		}
	}

	private boolean matchesType(EClass type, EClassifier superType) {
		return type == superType || type.getEAllSuperTypes().contains(superType);
	}

	@Override
	protected void doUpdateMatches() {
		
	}

	@Override
	protected boolean isRelevant(SDMChangeEvent change) {
		return false;
	}

}
