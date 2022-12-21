package de.mdelab.mlsdm.interpreter.incremental.rete.sdm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mdelab.mlsdm.interpreter.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.interpreter.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.mlindices.NotifyingIndex;

public class SDMReteInterfaceIndex extends SDMInterfaceIndex {
	
	public SDMReteInterfaceIndex(NotifyingIndex index,
			Map<String, Integer> parameterPositions, int interfaceSize) {
		super(index, parameterPositions, interfaceSize);
	}
	
	protected List<Object> getEntryList(SDMQueryMatch match) {
		Object[] entry = new Object[parameterPositions.size()];
		for(Entry<String, Integer> parameter:parameterPositions.entrySet()) {
			entry[parameter.getValue()] = match.get(parameter.getKey());
		}
		List<Object> entryList = Arrays.asList(entry);
		return entryList;
	}

}
