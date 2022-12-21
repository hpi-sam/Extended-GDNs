package de.mdelab.mlsdm.incremental.aggregation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.mdelab.mlsdm.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMIndexChange;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent.SDMChangeEnum;
import de.mdelab.mlsdm.mlindices.Index;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.mlsdm.mlindices.MlindicesFactory;

public abstract class SDMGroupingQuery extends SDMQuery {

	protected int[] groupingIndices;
	protected Map<List<Object>, Set<List<Object>>> groups;
	
	protected SDMInterfaceIndex inIndex;
	protected List<Object> undefinedInQuery;

	protected Index outIndex;
	protected List<Object> undefinedQueryOut;
	protected Map<String, Integer> parameterToIndex;
	
	public SDMGroupingQuery(SDMQueryManager manager, SDMInterfaceIndex inIndex, int[] groupingIndices, String[] variableNames) {
		super(manager);
		this.groups = new LinkedHashMap<List<Object>, Set<List<Object>>>();
		this.inIndex = inIndex;
		this.groupingIndices = groupingIndices;
		this.undefinedInQuery = createUndefinedQuery(inIndex.getParameterPositions().size());
		this.outIndex = createOutIndex();
		this.undefinedQueryOut = createUndefinedQuery(groupingIndices.length + 1);
		this.parameterToIndex = createParameterMap(groupingIndices, variableNames);
	}

	protected Index createOutIndex() {
		Index index = MlindicesFactory.eINSTANCE.createStagedHashIndex();
		return index;
	}

	private Map<String, Integer> createParameterMap(int[] groupingIndices, String[] variableNames) {
		Map<String, Integer> parameterToIndex = new LinkedHashMap<String, Integer>();
		int i;
		for(i = 0; i < groupingIndices.length; i++) {
			parameterToIndex.put(variableNames[i], i);
		}
		parameterToIndex.put("value", i);
		return parameterToIndex;
	}

	@Override
	public void findInitialMatches() {
		createGrouping(inIndex.getEntries(undefinedInQuery));
	}
	
	private void createGrouping(Iterator<IndexEntry> entries) {
		while(entries.hasNext()) {
			IndexEntry entry = entries.next();
			addToGrouping(entry);
		}
		updateAllGroupValues();
	}

	protected void addToGrouping(IndexEntry entry) {
		List<Object> key = entry.getKey();
		List<Object> groupBy = getGroupBy(key);
		if(!groups.containsKey(groupBy)) {
			groups.put(groupBy, new LinkedHashSet<List<Object>>());
		}
		groups.get(groupBy).add(key);
	}

	protected void removeFromGrouping(IndexEntry entry) {
		List<Object> key = entry.getKey();
		List<Object> groupBy = getGroupBy(key);
		if(!groups.containsKey(groupBy)) {
			return;
		}
		else {
			Set<List<Object>> groupMembers = getGroupMembers(groupBy);
			groupMembers.remove(entry.getKey());
			if(groupMembers.isEmpty()) {
				groups.remove(groupBy);
			}
		}
	}

	protected List<Object> getGroupBy(List<Object> key) {
		List<Object> groupBy = new ArrayList<Object>(groupingIndices.length);
		for(int i:groupingIndices) {
			groupBy.add(key.get(i));
		}
		return groupBy;
	}

	protected void updateAllGroupValues() {
		for(Entry<List<Object>, Set<List<Object>>> group:groups.entrySet()) {
			updateGroupValue(group.getKey(), group.getValue());
		}
	}

	protected void updateGroupValue(List<Object> groupBy) {
		updateGroupValue(groupBy, getGroupMembers(groupBy));
	}

	protected Set<List<Object>> getGroupMembers(List<Object> groupBy) {
		if(!groups.containsKey(groupBy)) {
			return Collections.emptySet();
		}
		else {
			return groups.get(groupBy);
		}
	}

	protected void updateGroupValue(List<Object> groupBy, Set<List<Object>> groupMembers) {
		outIndex.remove(groupBy);
		for(SDMInterfaceIndex index:interfaceIndices) {
			index.remove(groupBy);
		}
		
		Object groupValue = getGroupValue(groupBy, groupMembers);
		List<Object> groupEntry = createGroupEntry(groupBy, groupValue);
		outIndex.addEntry(groupEntry);
		for(SDMInterfaceIndex index:interfaceIndices) {
			index.addEntry(groupEntry);
		}
	}

	protected List<Object> createGroupEntry(List<Object> groupBy, Object groupValue) {
		List<Object> outEntry = new ArrayList<Object>(groupBy);
		outEntry.add(groupValue);
		return outEntry;
	}

	protected abstract Object getGroupValue(List<Object> groupBy, Set<List<Object>> entry);

	@Override
	public Iterator<SDMQueryMatch> getMatches() {
		updateMatches();
		
		return new Iterator<SDMQueryMatch>() {

			Iterator<IndexEntry> indexIterator = outIndex.getEntries(undefinedQueryOut);
			
			@Override
			public boolean hasNext() {
				return indexIterator.hasNext();
			}

			@Override
			public SDMQueryMatch next() {
				IndexEntry indexEntry = indexIterator.next();
				return new SDMQueryMatch(indexEntry, parameterToIndex);
			}
			
		};
	}
	
	protected void doUpdateMatches() {
		for(SDMChangeEvent change:unhandledChanges) {
			if(change.getType() == inIndex.getWrappedIndex()) {
				if(change.getModifier() == SDMChangeEnum.DELETE) {
					removeInIndexEntry(((SDMIndexChange) change).getEntry());
				}
				else {
					addInIndexEntry(((SDMIndexChange) change).getEntry());
				}
			}
		}
	}
	
	protected void addInIndexEntry(IndexEntry entry) {
		addToGrouping(entry);
		List<Object> groupBy = getGroupBy(entry.getKey());
		updateGroupValue(groupBy);
	}

	protected void removeInIndexEntry(IndexEntry entry) {
		removeFromGrouping(entry);
		List<Object> groupBy = getGroupBy(entry.getKey());
		if(!groups.containsKey(groupBy)) {
			outIndex.remove(groupBy);
		}
		else {
			updateGroupValue(groupBy);
		}
	}

	protected boolean isRelevant(SDMChangeEvent change) {
		return change.getType() == inIndex.getWrappedIndex();
	}

	protected List<Object> createUndefinedQuery(int size) {
		List<Object> query = new ArrayList<Object>();
		for(int i = 0; i < size; i++) {
			query.add(Index.UNDEFINED_PARAMETER);
			query.add(Index.UNDEFINED_PARAMETER);
		}	
		return query;
	}

}
