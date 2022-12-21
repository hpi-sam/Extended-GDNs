package de.mdelab.mlsdm.interpreter.incremental.rete.sdm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.interpreter.incremental.SDMQuery;
import de.mdelab.mlsdm.interpreter.incremental.SDMQueryManager;
import de.mdelab.mlsdm.interpreter.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.interpreter.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.interpreter.incremental.change.SDMNodeChange;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMReferenceIndex;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.mlsdm.mlindices.MlindicesFactory;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;

public class SDMSingleVertexInputNode extends SDMQuery {

	private final EClass type;
	private final MLSDMReferenceIndex ra;
	
	private final Map<String, Integer> parameterToIndex;
	
	public SDMSingleVertexInputNode(
			SDMQueryManager manager,
			AbstractStoryPatternObject spo,
			MLSDMReferenceIndex ra) {
		super(manager);
		this.type = (EClass) spo.getType();
		this.ra = ra;
		this.parameterToIndex = new HashMap<String, Integer>();
		this.parameterToIndex.put(spo.getName(), 0);
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
		final Iterator<EObject> it = ra.getDomain(type).iterator();
		return new Iterator<SDMQueryMatch>() {

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public SDMQueryMatch next() {
				return new SDMQueryMatch(constructIndexEntry(it.next()), parameterToIndex);
			}

		};
	}

	private IndexEntry constructIndexEntry(EObject next) {
		IndexEntry entry = MlindicesFactory.eINSTANCE.createIndexEntry();
		entry.getKey().add(parameterToIndex);
		return entry;
	}
	
	@Override
	public void registerChange(SDMChangeEvent change) {
		if(change.getType() == type) {
			IndexEntry entry = constructIndexEntry(((SDMNodeChange) change).getObject());
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

}
