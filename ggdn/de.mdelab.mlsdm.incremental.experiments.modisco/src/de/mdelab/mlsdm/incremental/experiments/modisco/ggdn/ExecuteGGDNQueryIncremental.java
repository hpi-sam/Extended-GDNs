package de.mdelab.mlsdm.incremental.experiments.modisco.ggdn;

import java.io.IOException;
import java.util.Iterator;

import de.mdelab.migmm.sample.git.delta.MoDiscoDeltaReader;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.incremental.experiments.modisco.ggdn.queries.MoDiscoGGDNQuery;
import de.mdelab.mlsdm.incremental.experiments.modisco.ggdn.queries.MoDiscoGGDNQuery.GGDNQueryType;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.ExperimentsUtil;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.IncrementalExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndex;
import de.mdelab.sdm.interpreter.core.SDMException;

public class ExecuteGGDNQueryIncremental {

	public static void main(String[] args) throws SDMException, IOException {
		if(args.length < 3) {
			System.out.println("3 arguments: dataFile, rule, logMem");
			return;
		}
		
		final String dataFile = args[0];
		GGDNQueryType queryType = GGDNQueryType.valueOf(args[1]);
		final boolean logMem = Boolean.parseBoolean(args[2]);
		
		ExperimentsUtil.registerEPackages();

		warmUp(dataFile, queryType);

		MoDiscoDeltaReader deltaReader = new MoDiscoDeltaReader(dataFile);

		SDMQueryManager queryManager = new SDMQueryManager();
		queryManager.registerEObjects(deltaReader.getModelRoots());
		
		SDMQuery query = MoDiscoGGDNQuery.createQuery(queryType, queryManager);
		
		IncrementalExecutor executor = new IncrementalExecutor();
		
		executor.executeIncremental(deltaReader, queryManager, logMem);
		
		long matchCount = 0;
		for(Iterator<SDMQueryMatch> it = query.getMatches(); it.hasNext();) {
//			it.next();
			System.out.println(it.next());
			matchCount++;
		}
		System.out.println("matches: " + matchCount);
	}

	private static void warmUp(String dataFile, GGDNQueryType queryType) throws SDMException, IOException {
		MoDiscoDeltaReader deltaReader = new MoDiscoDeltaReader(dataFile);

		SDMQueryManager queryManager = new SDMQueryManager();
		SDMQuery query = MoDiscoGGDNQuery.createQuery(queryType, queryManager);
		
		queryManager.registerEObjects(deltaReader.getModelRoots());
		
		//find initial matches
		queryManager.flushUnhandledEvents();
		for(Iterator<SDMQueryMatch> it = query.getMatches(); it.hasNext();) {
			it.next();
		}
		ReteIndex.TOTAL_TUPLES = 0;
	}

}
