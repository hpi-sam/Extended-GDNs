package de.mdelab.mlsdm.incremental.aggregation;

import java.util.List;
import java.util.Set;

import de.mdelab.mlsdm.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.incremental.SDMQueryManager;

public class SDMGroupingCountQuery extends SDMGroupingQuery {

	public SDMGroupingCountQuery(SDMQueryManager manager, SDMInterfaceIndex inIndex, int[] groupingIndices,
			String[] variableNames) {
		super(manager, inIndex, groupingIndices, variableNames);
	}

	@Override
	protected Object getGroupValue(List<Object> groupBy, Set<List<Object>> groupMembers) {
		return groupMembers.size();
	}
}
