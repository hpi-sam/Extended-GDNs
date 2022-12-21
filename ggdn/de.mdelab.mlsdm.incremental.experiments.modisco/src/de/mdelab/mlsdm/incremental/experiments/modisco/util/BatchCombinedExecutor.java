package de.mdelab.mlsdm.incremental.experiments.modisco.util;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.incremental.experiments.modisco.ggdn.queries.MoDiscoGGDNQuery;
import de.mdelab.mlsdm.incremental.experiments.modisco.ggdn.queries.MoDiscoGGDNQuery.GGDNQueryType;
import de.mdelab.mltgg.mote2.TransformationDirectionEnum;
import de.mdelab.mltgg.mote2.sdm.MoTE2Sdm;
import de.mdelab.mltgg.mote2.sdm.SdmOperationalTGG;
import de.mdelab.sdm.interpreter.core.SDMException;

public class BatchCombinedExecutor {

	public void executeBatch(EPackage pkg, SdmOperationalTGG tgg, GGDNQueryType queryType, boolean logMem) throws SDMException, IOException {
		int timeout;
		if(logMem) {
			System.out.println("MEMORY\n");
			timeout = 18000000;
		}
		else {
			System.out.println("UPDATES\n");
			timeout = 1800000;
		}
		
		Runtime runtime = Runtime.getRuntime();

		MoTE2Sdm tggEngine = createTGGEngine(tgg, Collections.singletonList(pkg));
		
		SDMQueryManager queryManager = new SDMQueryManager();		
		SDMQuery query = MoDiscoGGDNQuery.createQuery(queryType, queryManager);
		
		long updateCount = 0;
		long updateTime = 0;
		long absoluteStart = System.currentTimeMillis();
		long start = System.nanoTime();
		long gcTime = 0;
		long gcStart = getGarbageCollectionTime();
		
		//perform initial transformation
		tggEngine.transform(TransformationDirectionEnum.FORWARD, false, false, false, false, null);

		//execute initial query
		queryManager.registerEObjects(tggEngine.getRightInputElements());
		queryManager.flushUnhandledEvents();
		queryManager.updateAllQueryMatches();
		
		long end = System.nanoTime();
		updateTime += (end - start);
		gcTime += getGarbageCollectionTime() - gcStart;
		
		tggEngine.detachChangeListeners();
		
		String logEntry;
		if(logMem) {
	        runtime.gc();
	        long memory = runtime.totalMemory() - runtime.freeMemory();
	        logEntry = 1 + "\t" + memory;
		}
		else {
			logEntry = 1 + "\t" + updateTime;
			updateTime = 0;
			gcTime = 0;
		}
		System.out.println("UPDATE;" + logEntry);
		System.out.println("GC;" + updateCount + "\t" + gcTime);
		updateCount++;
		
		//incrementally add 30 attributes to all classes
		for(int i = 0; i < 30; i++) {
			for(EClassifier c:pkg.getEClassifiers()) {
				if(c.eClass() == EcorePackage.Literals.ECLASS) {
					EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
					attribute.setName(c.getName() + i);
					((EClass) c).getEStructuralFeatures().add(attribute);
				}
			}

			tggEngine = createTGGEngine(tgg, Collections.singletonList(pkg));
			
			queryManager = new SDMQueryManager();		
			query = MoDiscoGGDNQuery.createQuery(queryType, queryManager);

			start = System.nanoTime();
			gcStart = getGarbageCollectionTime();
			
			//perform batch transformation
			tggEngine.transform(TransformationDirectionEnum.FORWARD, false, false, false, false, null);

			//execute batch query
			queryManager.registerEObjects(tggEngine.getRightInputElements());
			queryManager.flushUnhandledEvents();
			queryManager.updateAllQueryMatches();
			
			end = System.nanoTime();
			updateTime += (end - start);
			updateCount++;
			gcTime += getGarbageCollectionTime() - gcStart;

			tggEngine.detachChangeListeners();
			
			if(logMem) {
		        runtime.gc();
		        long memory = runtime.totalMemory() - runtime.freeMemory();
		        logEntry = updateCount + "\t" + memory;
			}
			else {
				logEntry = updateCount + "\t" + updateTime;
				updateTime = 0;
				gcTime = 0;
			}
			
			System.out.println("UPDATE;" + logEntry);
			System.out.println("GC;" + updateCount + "\t" + gcTime);
			
			if(System.currentTimeMillis() - absoluteStart >= timeout) {
				break;
			}
		}

		System.out.println("total time: " + (System.currentTimeMillis() - absoluteStart));	

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

	private static long getGarbageCollectionTime() {
	    long collectionTime = 0;
	    for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
	        collectionTime += garbageCollectorMXBean.getCollectionTime();
	    }
	    return collectionTime;
	}
}
