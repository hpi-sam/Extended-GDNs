package de.mdelab.migmm.sample.git.delta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.modisco.java.emf.JavaPackage;

import de.mdelab.emf.util.EMFUtil;
import de.mdelab.emf.util.delta.EMFDelta;

import java.io.FileReader;

public class MoDiscoDeltaReader {

	private Map<Long, EObject> objects;
	private Map<EObject, Long> ids;
	private List<EObject> modelRoots;
	private List<EMFDelta> deltas;
	
	public MoDiscoDeltaReader(String path) {
		ensurePackageRegistration();
		readModelAndDeltas(path);
	}

	private void ensurePackageRegistration() {
		JavaPackage.eINSTANCE.getName();
	}

	private void readModelAndDeltas(String path) {
		try {
			FileReader fr = new FileReader(path);
			StringBuffer sb = new StringBuffer();
			
			int c;
			while((c = fr.read()) != -1) {
				if(c != '\r') {
					sb.append(String.valueOf((char) c));
				}
			}
			
			fr.close();
			
			objects = new LinkedHashMap<Long, EObject>();
			
			int deltaIndex = sb.indexOf(EMFUtil.DELTA_SEPARATOR);
			String modelString = sb.substring(0, deltaIndex);
			modelRoots = EMFUtil.deserializeMDELabModel(modelString, objects);
			
			ids = new LinkedHashMap<EObject, Long>();
			for(Entry<Long, EObject> entry:objects.entrySet()) {
				ids.put(entry.getValue(), entry.getKey());
			}

			String remainingDeltasString = sb.toString();
			deltas = new ArrayList<EMFDelta>();
			
			while(deltaIndex != -1) {
				remainingDeltasString = remainingDeltasString.substring(deltaIndex + EMFUtil.DELTA_SEPARATOR.length());
				deltaIndex = remainingDeltasString.indexOf(EMFUtil.DELTA_SEPARATOR);
				String deltaString = remainingDeltasString.substring(0, (deltaIndex != -1 ? deltaIndex : remainingDeltasString.length()));
				EMFDelta delta = EMFUtil.deserializeDelta(deltaString);
				deltas.add(delta);
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<EObject> getModelRoots() {
		return modelRoots;
	}

	public List<EMFDelta> getDeltas() {
		return deltas;
	}
	
	public Map<Long, EObject> getObjects() {
		return objects;
	}
	
	public Map<EObject, Long> getIds() {
		return ids;
	}

	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("1 argument: path");
			return;
		}
				
		String path = args[0];
		
		JavaPackage.eINSTANCE.getName();
		
		MoDiscoDeltaReader reader = new MoDiscoDeltaReader(path);
		System.out.println(reader.getModelRoots());
		System.out.println(reader.getDeltas());
	}

}
