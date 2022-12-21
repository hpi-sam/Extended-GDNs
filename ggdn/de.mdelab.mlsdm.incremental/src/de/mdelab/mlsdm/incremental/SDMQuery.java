package de.mdelab.mlsdm.incremental;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;

public abstract class SDMQuery implements ChangeListener {

	protected Collection<SDMInterfaceIndex> interfaceIndices;
	protected SDMQueryManager manager;

	protected Collection<SDMChangeEvent> unhandledChanges;
	
	private Collection<SDMQuery> dependencies;
	
	public SDMQuery(SDMQueryManager manager) {
		this.interfaceIndices = new ArrayList<SDMInterfaceIndex>();
		this.manager = manager;
		this.dependencies = new ArrayList<SDMQuery>();
		this.unhandledChanges = new ArrayList<SDMChangeEvent>();
	}
	
	public void updateMatches() {
		manager.flushUnhandledEvents();
		
		for(SDMQuery dependency:dependencies) {
			dependency.updateMatches();
		}
		
		doUpdateMatches();
		
		unhandledChanges.clear();
	}
	
	public void registerInterfaceIndex(SDMInterfaceIndex interfaceIndex) {
		populateInterfaceIndex(interfaceIndex);
		interfaceIndices.add(interfaceIndex);
	}

	protected void populateInterfaceIndex(SDMInterfaceIndex interfaceIndex) {
		for(Iterator<SDMQueryMatch> it = getMatches(); it.hasNext();) {
			SDMQueryMatch match = it.next();
			interfaceIndex.addEntry(match);
		}
	}

	public void registerDependency(SDMQuery sdmQuery) {
		dependencies.add(sdmQuery);
	}

	@Override
	public void registerChange(SDMChangeEvent change) {
		if(isRelevant(change)) {
			unhandledChanges.add(change);
		}
	}

	public abstract void findInitialMatches();
	
	public abstract Iterator<SDMQueryMatch> getMatches();
	
	protected abstract void doUpdateMatches();
	
	protected abstract boolean isRelevant(SDMChangeEvent change);
}
