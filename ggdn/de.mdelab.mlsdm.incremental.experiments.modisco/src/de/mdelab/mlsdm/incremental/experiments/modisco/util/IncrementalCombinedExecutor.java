package de.mdelab.mlsdm.incremental.experiments.modisco.util;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import de.mdelab.mlsdm.incremental.IncrementalQueryManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.StaticNetQueryManager;
import de.mdelab.mltgg.mote2.TransformationDirectionEnum;
import de.mdelab.mltgg.mote2.sdm.MoTE2Sdm;

public class IncrementalCombinedExecutor {
	
	public void executeIncremental(EPackage pkg, MoTE2Sdm tggEngine, IncrementalQueryManager queryManager, boolean logMem) {
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
		if(queryManager instanceof StaticNetQueryManager) {
			((StaticNetQueryManager) queryManager).discardCurrentNet();
			((StaticNetQueryManager) queryManager).computeNewNetStructure();
		}
		queryManager.flushUnhandledEvents();
		queryManager.updateAllQueryMatches();
		
		long end = System.nanoTime();
		updateTime += (end - start);
		gcTime += getGarbageCollectionTime() - gcStart;
		
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
			
			start = System.nanoTime();
			gcStart = getGarbageCollectionTime();
			tggEngine.transform(TransformationDirectionEnum.FORWARD, true, false, false, false, null);
			queryManager.flushUnhandledEvents();
			queryManager.updateAllQueryMatches();
			end = System.nanoTime();
			updateTime += (end - start);
			updateCount++;
			gcTime += getGarbageCollectionTime() - gcStart;
			
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
	}

	private static long getGarbageCollectionTime() {
	    long collectionTime = 0;
	    for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
	        collectionTime += garbageCollectorMXBean.getCollectionTime();
	    }
	    return collectionTime;
	}
}
