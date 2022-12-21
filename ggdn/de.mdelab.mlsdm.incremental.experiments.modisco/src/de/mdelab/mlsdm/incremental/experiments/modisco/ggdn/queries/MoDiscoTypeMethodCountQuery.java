package de.mdelab.mlsdm.incremental.experiments.modisco.ggdn.queries;

import java.io.IOException;
import java.util.Collections;

import de.mdelab.mlsdm.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.incremental.SDMLogger;
import de.mdelab.mlsdm.incremental.SDMPatternQuery;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.incremental.aggregation.SDMGroupingCountQuery;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.ExperimentsUtil;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.icl.iCL.ICLConstraint;
import de.mdelab.sdm.interpreter.core.SDMException;

public class MoDiscoTypeMethodCountQuery extends MoDiscoGGDNQuery {

	@Override
	public SDMQuery createQuery(SDMQueryManager queryManager) throws SDMException, IOException {
		SDMQuery patternQuery = new SDMPatternQuery(queryManager, getPattern(),
										Collections.emptyMap(), Collections.emptySet(),
										new SDMLogger() {
												public void print(String s) {}
										});
		queryManager.registerQuery(patternQuery);
		
		ICLConstraint groupingQueryInConstraint = createICLConstraint("i CONTAINS (type, method)");
		
		SDMInterfaceIndex interfaceIndex = queryManager.createInterfaceIndex(groupingQueryInConstraint);
		patternQuery.registerInterfaceIndex(interfaceIndex);

		int[] groupingIndices = {interfaceIndex.getParameterPositions().get("type")};
		String[] parameterNames = {"type"};
		
		SDMGroupingCountQuery countQuery = new SDMGroupingCountQuery(queryManager, interfaceIndex, groupingIndices, parameterNames);
		countQuery.registerDependency(patternQuery);
		queryManager.registerQuery(countQuery);
		
		return countQuery;
	}

	private StoryPattern getPattern() {
		return ExperimentsUtil.readPattern(PATTERN_DIR + "/type_method.mlsp");
	}
	
}
