package de.mdelab.mlsdm.interpreter.incremental.rete.sdm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.mdelab.mlsdm.interpreter.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.interpreter.incremental.SDMQuery;
import de.mdelab.mlsdm.interpreter.incremental.SDMQueryManager;
import de.mdelab.mlsdm.interpreter.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.interpreter.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.mlindices.Index;
import de.mdelab.mlsdm.mlindices.IndexEntry;

public class SDMReteResultProvider extends SDMQuery {

	private SDMInterfaceIndex index;
	private List<Object> indexQuery;
	
	public SDMReteResultProvider(SDMQueryManager manager, SDMInterfaceIndex index) {
		super(manager);
		this.index = index;
		this.indexQuery = createUndefinedQuery(index.getParameterPositions().size() + 1);
	}

	private List<Object> createUndefinedQuery(int size) {
		List<Object> query = new ArrayList<Object>(size * 2);
		for(int i = 0; i < size; i++) {
			query.add(Index.UNDEFINED_PARAMETER);
			query.add(Index.UNDEFINED_PARAMETER);
		}
		return query;
	}

	@Override
	public void findInitialMatches() {

	}

	@Override
	public Iterator<SDMQueryMatch> getMatches() {
		return new Iterator<SDMQueryMatch>() {

			private Iterator<IndexEntry> it = index.getEntries(indexQuery);
			
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public SDMQueryMatch next() {
				return new SDMQueryMatch(it.next(), index.getParameterPositions());
			}
			
		};
	}

	@Override
	public void registerChange(SDMChangeEvent change) {
		
	}

}
