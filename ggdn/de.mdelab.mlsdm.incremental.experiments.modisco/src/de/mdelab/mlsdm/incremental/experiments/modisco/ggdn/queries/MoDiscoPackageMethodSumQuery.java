package de.mdelab.mlsdm.incremental.experiments.modisco.ggdn.queries;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import de.mdelab.mlsdm.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.incremental.SDMLogger;
import de.mdelab.mlsdm.incremental.SDMPatternQuery;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.incremental.aggregation.SDMGroupingSumQuery;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.ExperimentsUtil;
import de.mdelab.mlsdm.incremental.rete.sdm.SDMJoinQuery;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.icl.iCL.ICLConstraint;
import de.mdelab.sdm.interpreter.core.SDMException;

public class MoDiscoPackageMethodSumQuery extends MoDiscoGGDNQuery {

	@Override
	public SDMQuery createQuery(SDMQueryManager queryManager) throws SDMException, IOException {
		//==========PATTERN==========
		
		SDMQuery patternQuery = new SDMPatternQuery(queryManager, getPattern(),
										Collections.emptyMap(), Collections.emptySet(),
										new SDMLogger() {
												public void print(String s) {}
										});
		queryManager.registerQuery(patternQuery);
		ICLConstraint leftConstraint = createICLConstraint("i CONTAINS (type, pkg)");
		SDMInterfaceIndex leftIndex = queryManager.createInterfaceIndex(leftConstraint);
		patternQuery.registerInterfaceIndex(leftIndex);

		//==========TYPE METHOD COUNT==========
		
		SDMQuery typeMethodCountQuery = new MoDiscoTypeMethodCountQuery().createQuery(queryManager);
		ICLConstraint rightConstraint = createICLConstraint("i CONTAINS (type, value)");
		SDMInterfaceIndex rightIndex = queryManager.createInterfaceIndex(rightConstraint);
		typeMethodCountQuery.registerInterfaceIndex(rightIndex);
		
		//==========JOIN==========
		
		Map<String, Integer> parameterToIndex = createJoinParameterToIndexMap();
		SDMJoinQuery joinQuery = new SDMJoinQuery(queryManager, leftIndex, rightIndex,
				parameterToIndex, 1,
				leftConstraint.getParameters().getList().size(), rightConstraint.getParameters().getList().size());
		joinQuery.registerDependency(patternQuery);
		joinQuery.registerDependency(typeMethodCountQuery);
		queryManager.registerQuery(joinQuery);
		ICLConstraint joinConstraint = createICLConstraint("i CONTAINS (type, pkg, value)");
		SDMInterfaceIndex joinIndex = queryManager.createJoinInterfaceIndex(joinConstraint);
		joinQuery.registerInterfaceIndex(joinIndex);

		//==========PKG METHOD SUM==========
		
		int[] groupingIndices = {joinIndex.getParameterPositions().get("pkg")};
		String[] parameterNames = {"pkg"};
		
		SDMGroupingSumQuery sumQuery = new SDMGroupingSumQuery(queryManager, joinIndex, groupingIndices,
				parameterNames, joinIndex.getParameterPositions().get("value"));
		sumQuery.registerDependency(joinQuery);
		queryManager.registerQuery(sumQuery);
		
		return sumQuery;
	}

	private Map<String, Integer> createJoinParameterToIndexMap() {
		Map<String, Integer> parameterToIndex = new LinkedHashMap<String, Integer>();
		parameterToIndex.put("type", 0);
		parameterToIndex.put("pkg", 1);
		parameterToIndex.put("value", 2);
		return parameterToIndex;
	}

	private StoryPattern getPattern() {
		return ExperimentsUtil.readPattern(PATTERN_DIR + "/pkg_type.mlsp");
	}
	
}
