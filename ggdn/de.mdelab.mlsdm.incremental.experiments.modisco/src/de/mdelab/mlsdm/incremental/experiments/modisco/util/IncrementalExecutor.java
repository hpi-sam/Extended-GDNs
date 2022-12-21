package de.mdelab.mlsdm.incremental.experiments.modisco.util;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import de.mdelab.emf.util.delta.EMFDelta;
import de.mdelab.migmm.sample.git.delta.MoDiscoDeltaReader;
import de.mdelab.mlsdm.incremental.IncrementalQueryManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.StaticNetQueryManager;

public class IncrementalExecutor {
	
	public void executeIncremental(MoDiscoDeltaReader deltaReader, IncrementalQueryManager queryManager, boolean logMem) {
		int timeout;
		if(logMem) {
			System.out.println("MEMORY\n");
			timeout = 18000000;
		}
		else {
			System.out.println("UPDATES\n");
			timeout = 1800000;
		}
		
		if(queryManager instanceof StaticNetQueryManager) {
			((StaticNetQueryManager) queryManager).discardCurrentNet();
			((StaticNetQueryManager) queryManager).computeNewNetStructure();
		}
		
		Runtime runtime = Runtime.getRuntime();
		long gcTimeStart = getGarbageCollectionTime();
		
		//create initial elements
		long updateCount = 0;
		long updateTime = 0;
		long absoluteStart = System.currentTimeMillis();
		
		long start = System.nanoTime();
		queryManager.flushUnhandledEvents();
		long end = System.nanoTime();
		updateTime += (end - start);
		long gcTime = getGarbageCollectionTime() - gcTimeStart;
		
		String logEntry;
		if(logMem) {
	        runtime.gc();
	        long memory = runtime.totalMemory() - runtime.freeMemory();
	        logEntry = 1 + "\t" + memory;
		}
		else {
			logEntry = 1 + "\t" + updateTime;
			updateTime = 0;
		}
		System.out.println("UPDATE;" + logEntry);
		System.out.println("GC;" + updateCount + "\t" + gcTime);
		updateCount++;
		
		//create elements
		for(EMFDelta delta:deltaReader.getDeltas()) {
			delta.apply(deltaReader.getObjects(), deltaReader.getIds());
			start = System.nanoTime();
			queryManager.flushUnhandledEvents();
			queryManager.updateAllQueryMatches();
			end = System.nanoTime();
			updateTime += (end - start);
			updateCount++;
			gcTime = getGarbageCollectionTime() - gcTimeStart;
			
			if(logMem) {
		        runtime.gc();
		        long memory = runtime.totalMemory() - runtime.freeMemory();
		        logEntry = updateCount + "\t" + memory;
			}
			else {
				logEntry = updateCount + "\t" + updateTime;
				updateTime = 0;
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
