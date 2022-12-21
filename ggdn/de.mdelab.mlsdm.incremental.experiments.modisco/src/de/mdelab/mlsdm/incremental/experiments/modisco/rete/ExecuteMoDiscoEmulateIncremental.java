package de.mdelab.mlsdm.incremental.experiments.modisco.rete;

import java.io.IOException;
import java.util.Iterator;

import de.mdelab.migmm.sample.git.delta.MoDiscoDeltaReader;
import de.mdelab.mlsdm.gdn.GDN;
import de.mdelab.mlsdm.incremental.QueryManagerNotificationReceiver;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.ExperimentsUtil;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.IncrementalExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.GDNReteBuilder;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNet;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteQueryManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteSDMWrapper;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndex;
import de.mdelab.sdm.interpreter.core.SDMException;

public class ExecuteMoDiscoEmulateIncremental {

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

		GDN gdn = ExperimentsUtil.readGDN(ruleFile);
        
		MoDiscoDeltaReader deltaReader = new MoDiscoDeltaReader(dataFile);

		ReteQueryManager queryManager = new ReteQueryManager() {

			@Override
			public void flushUnhandledEvents() {
				FlushStatus previousFlushing = flushing;
				flushing = FlushStatus.FLUSH;
				for(SDMChangeEvent change:unhandledChanges) {
					notifyUpdateStart();
					flushEvent(change);
					notifyUpdateEnd();
					notifyCustom();
				}
				unhandledChanges.clear();
				flushing = previousFlushing;
			}

			protected void notifyCustom() {
				for(QueryManagerNotificationReceiver receiver:notificationReceivers) {
					receiver.notifyCustom();
				}
			}

		};
		ReteNet reteNet = ((ReteSDMWrapper)new GDNReteBuilder().buildReteQuery(gdn, queryManager)).getResultProvider().getNet();
		queryManager.registerReteNet(reteNet);
		
		IncrementalExecutor executor = new IncrementalExecutor();
		
		executor.executeIncremental(deltaReader, queryManager, logMem);
	}

	private static void warmUp(String dataFile, String ruleFile) throws IOException, SDMException {
		GDN gdn = ExperimentsUtil.readGDN(ruleFile);
        
		MoDiscoDeltaReader deltaReader = new MoDiscoDeltaReader(dataFile);

		ReteQueryManager queryManager = new ReteQueryManager();
		queryManager.registerEObjects(deltaReader.getModelRoots());
		
		//find initial matches
		SDMQuery query = new GDNReteBuilder().buildReteQuery(gdn, queryManager);
		queryManager.flushUnhandledEvents();

		for(Iterator<SDMQueryMatch> it = query.getMatches(); it.hasNext();) {
			it.next();
		}
		ReteIndex.TOTAL_TUPLES = 0;
	}
}
