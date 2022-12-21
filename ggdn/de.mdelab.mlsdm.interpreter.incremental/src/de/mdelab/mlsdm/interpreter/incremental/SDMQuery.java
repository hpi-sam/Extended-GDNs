package de.mdelab.mlsdm.interpreter.incremental;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class SDMQuery implements ChangeListener {

	protected Collection<SDMInterfaceIndex> interfaceIndices;
	protected SDMQueryManager manager;
	
	public SDMQuery(SDMQueryManager manager) {
		this.interfaceIndices = new ArrayList<SDMInterfaceIndex>();
		this.manager = manager;
	}
	
	public abstract void findInitialMatches();
	
	public abstract Iterator<SDMQueryMatch> getMatches();
	
	public void updateMatches() {
		manager.flushUnhandledEvents();
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
	
}
