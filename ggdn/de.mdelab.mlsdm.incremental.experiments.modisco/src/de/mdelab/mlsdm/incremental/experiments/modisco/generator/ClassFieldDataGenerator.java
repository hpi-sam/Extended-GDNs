package de.mdelab.mlsdm.incremental.experiments.modisco.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.modisco.java.ClassDeclaration;
import org.eclipse.modisco.java.emf.JavaFactory;
import org.eclipse.modisco.java.emf.JavaPackage;

import de.mdelab.emf.util.EMFUtil;
import de.mdelab.emf.util.delta.EMFDelta;
import de.mdelab.emf.util.delta.EObjectCreation;
import de.mdelab.emf.util.delta.EReferenceCreation;

public class ClassFieldDataGenerator {

	public static void main(String[] args) {
		if(args.length < 3) {
			System.out.println("3 arguments: classes, fields, outputFile");
			return;
		}

		int c = Integer.parseInt(args[0]);
		int f = Integer.parseInt(args[1]);
		String outputFile = args[2];
		
		StringBuffer sb = new StringBuffer();
		
		long nextID = 0;
		
		Map<EObject, Long> model = new HashMap<EObject, Long>();
		for(int i = 0; i < c; i++) {
			ClassDeclaration clazz = JavaFactory.eINSTANCE.createClassDeclaration();
			model.put(clazz, nextID);
			nextID++;
		}
		sb.append(EMFUtil.serializeToMDELabModel(model));
		

		for(int j = 0; j < f; j++) {
			for(Entry<EObject, Long> entry:model.entrySet()) {
				EMFDelta delta = new EMFDelta();
				delta.getObjectCreations().add(new EObjectCreation(JavaPackage.eINSTANCE.getFieldDeclaration(), nextID));
				delta.getReferenceCreations().add(new EReferenceCreation(
						(EReference) JavaPackage.eINSTANCE.getClassDeclaration().getEStructuralFeature("bodyDeclarations"),
						entry.getValue(),
						nextID));
				sb.append(EMFUtil.DELTA_SEPARATOR);
				sb.append("\n");
				sb.append(EMFUtil.serializeDelta(delta));
				nextID++;
			}
		}
		

		FileWriter fw;
		try {
			fw = new FileWriter(outputFile);
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
