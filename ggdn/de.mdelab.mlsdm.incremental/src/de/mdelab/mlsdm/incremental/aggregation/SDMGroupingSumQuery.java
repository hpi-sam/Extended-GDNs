package de.mdelab.mlsdm.incremental.aggregation;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mdelab.mlsdm.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.mlindices.IndexEntry;

public class SDMGroupingSumQuery extends SDMGroupingQuery {

	protected int valueIndex;
	
	protected Map<List<Object>, Long> groupSums;
	
	public SDMGroupingSumQuery(SDMQueryManager manager, SDMInterfaceIndex inIndex, int[] groupingIndices,
			String[] variableNames, int valueIndex) {
		super(manager, inIndex, groupingIndices, variableNames);
		this.valueIndex = valueIndex;
		this.groupSums = new LinkedHashMap<List<Object>, Long>();
	}

	@Override
	protected Object getGroupValue(List<Object> groupBy, Set<List<Object>> groupMembers) {
		return getGroupSum(groupBy);
	}
	
	protected long getGroupSum(List<Object> groupBy) {
		return groupSums.containsKey(groupBy) ? groupSums.get(groupBy) : 0;
	}
	
	protected void addToGrouping(IndexEntry entry) {
		List<Object> key = entry.getKey();
		List<Object> groupBy = getGroupBy(key);
		if(!groups.containsKey(groupBy)) {
			groups.put(groupBy, new LinkedHashSet<List<Object>>());
		}
		groups.get(groupBy).add(key);
		
		long currentSum = getGroupSum(groupBy);
		long newSum = currentSum + ((Number) key.get(valueIndex)).longValue();
		groupSums.put(groupBy, newSum);
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
				groupSums.remove(groupBy);
			}
			else {
				long currentSum = getGroupSum(groupBy);
				long newSum = currentSum - ((Number) key.get(valueIndex)).longValue();
				groupSums.put(groupBy, newSum);
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

}
