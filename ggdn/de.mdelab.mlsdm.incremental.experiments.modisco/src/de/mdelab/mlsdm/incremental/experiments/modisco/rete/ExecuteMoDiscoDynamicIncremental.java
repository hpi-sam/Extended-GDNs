package de.mdelab.mlsdm.incremental.experiments.modisco.rete;

import java.io.IOException;
import java.util.Iterator;

import de.mdelab.migmm.sample.git.delta.MoDiscoDeltaReader;
import de.mdelab.mlsdm.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.ExperimentsUtil;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.IncrementalExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteSDMWrapper;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.DynamicReteQueryManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.NetSizeBasedQueryManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndex;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.interpreter.core.SDMException;

public class ExecuteMoDiscoDynamicIncremental {

	public static void main(String args[]) throws IOException, SDMException {
		if(args.length < 3) {
			System.out.println("3 arguments: dataFile, ruleFile, logMem");
			return;
		}
		
		final String dataFile = args[0];
		final String ruleFile = args[1];
		final boolean logMem = Boolean.parseBoolean(args[2]);
		
		ExperimentsUtil.registerEPackages();

		warmUp(dataFile, ruleFile);

		StoryPattern pattern = ExperimentsUtil.readPattern(ruleFile);
        
		MoDiscoDeltaReader deltaReader = new MoDiscoDeltaReader(dataFile);
		
		DynamicReteQueryManager queryManager = new NetSizeBasedQueryManager(pattern);
		
		IncrementalExecutor executor = new IncrementalExecutor();
		
		executor.executeIncremental(deltaReader, queryManager, logMem);
		
		long matchCount = 0;
		for(Iterator<SDMQueryMatch> it = new ReteSDMWrapper(((DynamicReteQueryManager) queryManager).getResultProvider()).getMatches(); it.hasNext();) {
			it.next();
			matchCount++;
		}
		System.out.println("matches: " + matchCount);
	}

	private static void warmUp(String dataFile, String ruleFile) throws IOException, SDMException {
		StoryPattern pattern = ExperimentsUtil.readPattern(ruleFile);

		MoDiscoDeltaReader deltaReader = new MoDiscoDeltaReader(dataFile);
		
		DynamicReteQueryManager reteNet = new NetSizeBasedQueryManager(pattern);
		reteNet.registerEObjects(deltaReader.getModelRoots());
		
		//find initial matches
		reteNet.flushUnhandledEvents();
		for(Iterator<SDMQueryMatch> it = new ReteSDMWrapper(reteNet.getResultProvider()).getMatches(); it.hasNext();) {
			it.next();
		}
		ReteIndex.TOTAL_TUPLES = 0;
	}
}
