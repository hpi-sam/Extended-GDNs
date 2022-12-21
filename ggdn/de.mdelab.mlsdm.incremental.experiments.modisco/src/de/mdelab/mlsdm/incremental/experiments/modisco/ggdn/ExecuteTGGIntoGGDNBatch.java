package de.mdelab.mlsdm.incremental.experiments.modisco.ggdn;

import java.io.IOException;

import org.eclipse.emf.ecore.EPackage;

import de.mdelab.emf.util.EMFUtil;
import de.mdelab.mlsdm.incremental.experiments.modisco.ggdn.queries.MoDiscoGGDNQuery;
import de.mdelab.mlsdm.incremental.experiments.modisco.ggdn.queries.MoDiscoGGDNQuery.GGDNQueryType;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.BatchCombinedExecutor;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.ExperimentsUtil;
import de.mdelab.mltgg.mote2.sdm.SdmOperationalTGG;
import de.mdelab.sdm.interpreter.core.SDMException;

public class ExecuteTGGIntoGGDNBatch {

	public static void main(String[] args) throws SDMException, IOException {
		if(args.length < 5) {
			System.out.println("5 arguments: dataFile, tggPath, patternDir, rule, logMem");
			return;
		}
		
		String dataFile = args[0];
		String tggPath = args[1];
		String patternDir = args[2];
		GGDNQueryType queryType = GGDNQueryType.valueOf(args[3]);
		boolean logMem = Boolean.parseBoolean(args[4]);
		
		MoDiscoGGDNQuery.PATTERN_DIR = patternDir;
		
		ExperimentsUtil.registerEPackages();

		warmUp(dataFile, queryType);

		EPackage pkg = (EPackage) EMFUtil.loadXmi(dataFile);
		
		SdmOperationalTGG tgg = (SdmOperationalTGG) EMFUtil.loadXmi(tggPath);
		
		BatchCombinedExecutor executor = new BatchCombinedExecutor();
		
		executor.executeBatch(pkg, tgg, queryType, logMem);
	}

	private static void warmUp(String dataFile, GGDNQueryType queryType) throws SDMException, IOException {
		
	}

}
