package de.mdelab.mlsdm.incremental.experiments.modisco.ggdn;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import de.mdelab.emf.util.EMFUtil;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.incremental.experiments.modisco.ggdn.queries.MoDiscoGGDNQuery;
import de.mdelab.mlsdm.incremental.experiments.modisco.ggdn.queries.MoDiscoGGDNQuery.GGDNQueryType;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.ExperimentsUtil;
import de.mdelab.mlsdm.incremental.experiments.modisco.util.IncrementalCombinedExecutor;
import de.mdelab.mltgg.mote2.sdm.MoTE2Sdm;
import de.mdelab.mltgg.mote2.sdm.SdmOperationalTGG;
import de.mdelab.sdm.interpreter.core.SDMException;

public class ExecuteTGGIntoGGDNIncremental {

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
		MoTE2Sdm tggEngine = createTGGEngine(tgg, Collections.singletonList(pkg));
		
		SDMQueryManager queryManager = new SDMQueryManager();		
		SDMQuery query = MoDiscoGGDNQuery.createQuery(queryType, queryManager);
		
		IncrementalCombinedExecutor executor = new IncrementalCombinedExecutor();
		
		executor.executeIncremental(pkg, tggEngine, queryManager, logMem);
		
		long matchCount = 0;
		for(Iterator<SDMQueryMatch> it = query.getMatches(); it.hasNext();) {
			System.out.println(it.next());
			matchCount++;
		}
		System.out.println("matches: " + matchCount);
	}

	private static MoTE2Sdm createTGGEngine(SdmOperationalTGG tgg, List<EObject> modelRoots) {
		final MoTE2Sdm engine = new MoTE2Sdm();
		engine.initRules( tgg);
		EList<EObject> leftElements = new BasicEList<EObject>();
		EList<EObject> rightElements = new BasicEList<EObject>();
		leftElements.addAll(modelRoots);
		engine.initModels(leftElements, rightElements);
		return engine;
	}

	private static void warmUp(String dataFile, GGDNQueryType queryType) throws SDMException, IOException {
		
	}

}
