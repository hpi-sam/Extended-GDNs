package de.mdelab.emf.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import de.mdelab.emf.util.delta.EAttributeChange;
import de.mdelab.emf.util.delta.EMFDelta;
import de.mdelab.emf.util.delta.EObjectCreation;
import de.mdelab.emf.util.delta.EObjectDeletion;
import de.mdelab.emf.util.delta.EReferenceCreation;
import de.mdelab.emf.util.delta.EReferenceDeletion;

public class EMFUtil {

	public static final char SEPARATOR = ';';
	public static final char ATTRIBUTE_SEPARATOR = '#';
	public static final String END_OF_OBJECTS = "===========";
	public static final String OBJECT_CREATIONS_START = "---OBJECT CREATIONS---";
	public static final String OBJECT_DELETIONS_START = "---OBJECT DELETIONS---";
	public static final String REFERENCE_CREATIONS_START = "---REFERENCE CREATIONS---";
	public static final String REFERENCE_DELETIONS_START = "---REFERENCE DELETIONS---";
	public static final String ATTRIBUTE_CHANGES_START = "---ATTRIBUTE CHANGES---";

	public static final String DELTA_SEPARATOR = "---DELTA---";

	public static void writeMDELabModel(EObject model, String path) {
		writeMDELabModel(Collections.singleton(model), path);
	}
	
	public static void writeMDELabModel(Collection<EObject> model, String path) {
		Collection<EObject> allObjects = collectReachableObjects(model);
		
		//assign object IDs
		long id = 0;
		Map<EObject, Long> ids = new LinkedHashMap<EObject, Long>();
		for(EObject o:allObjects) {
			ids.put(o, id++);
		}
		
		try {
			FileWriter fw = new FileWriter(path);
			fw.write(serializeToMDELabModel(ids));
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static String serializeToMDELabModel(Map<EObject, Long> ids) {
		StringBuffer sb = new StringBuffer();
		serializeObjects(ids, sb);
		
		sb.append(END_OF_OBJECTS);
		sb.append('\n');
		
		serializeEdges(ids, sb);
		return sb.toString();
	}
	
	private static void serializeObjects(Map<EObject, Long> ids, StringBuffer sb) {
		for(EObject o:ids.keySet()) {
			serializeObject(o, ids, sb);
		}
	}

	private static void serializeObject(EObject o, Map<EObject, Long> ids, StringBuffer sb) {
		sb.append(Long.toString(ids.get(o)));
		sb.append(SEPARATOR);
		serializeEClass(o.eClass(), sb);
		sb.append(SEPARATOR);
		
		//write attributes
		for(EAttribute attribute:o.eClass().getEAllAttributes()) {
			Collection<Object> values = getAttributeValues(o, attribute);
			if(!values.isEmpty()) {
				sb.append(attribute.getName());
				sb.append(SEPARATOR);
				for(Iterator<Object> it = values.iterator(); it.hasNext();) {
					sb.append(escape(valueToString(it.next(), attribute.getEType())));
					if(it.hasNext()) {
						sb.append(SEPARATOR);
					}
				}
				sb.append(ATTRIBUTE_SEPARATOR);
			}
		}
		
		sb.append('\n');
	}

	private static void serializeEdges(Map<EObject, Long> ids, StringBuffer sb) {
		for(EObject o:ids.keySet()) {
			serializeEdges(o, ids, sb);
		}
	}

	private static void serializeEdges(EObject o, Map<EObject, Long> ids, StringBuffer sb) {
		Long id = ids.get(o);
		for(EReference reference:o.eClass().getEAllReferences()) {
			Collection<EObject> values = new ArrayList<EObject>(getReferenceValues(o, reference));
			
			//remove bidirectional refs that have already been written
			if(reference.getEOpposite() != null) {
				for(Iterator<EObject> it = values.iterator(); it.hasNext();) {
					EObject target = it.next();
					if(ids.get(target) < id) {
						it.remove();
					}
				}
			}
			
			for(EObject target:values) {
				sb.append(id.toString());
				sb.append(SEPARATOR);
				sb.append(reference.getName());
				sb.append(SEPARATOR);
				sb.append(ids.get(target).toString());
				sb.append('\n');
			}
		}
	}

	public static Collection<EObject> collectReachableObjects(EObject model) {
		return collectReachableObjects(Collections.singleton(model));
	}
	
	public static Collection<EObject> collectReachableObjects(Collection<EObject> model) {
		Set<EObject> seenObjects = new LinkedHashSet<EObject>();
		seenObjects.addAll(model);
		
		Queue<EObject> queue = new LinkedList<EObject>();
		queue.addAll(model);
		
		while(!queue.isEmpty()) {
			EObject current = queue.poll();
			
			for(EReference reference:current.eClass().getEAllReferences()) {
				Collection<EObject> referencedObjects = getReferenceValues(current, reference);
				for(EObject referencedObject:referencedObjects) {
					if(!seenObjects.contains(referencedObject)) {
						queue.add(referencedObject);
						seenObjects.add(referencedObject);
					}
				}
			}
		}
		
		return seenObjects;
	}

	@SuppressWarnings("unchecked")
	public static void addReferenceValue(EObject object, EReference reference, EObject value) {
		if(reference.isMany()) {
			((Collection<EObject>) object.eGet(reference)).add(value);
		}
		else {
			object.eSet(reference, value);
		}
	}


	@SuppressWarnings("unchecked")
	public static void removeReferenceValue(EObject object, EReference reference, EObject value) {
		if(reference.isMany()) {
			((Collection<EObject>) object.eGet(reference)).remove(value);
		}
		else {
			if(object.eGet(reference) == value) {
				object.eSet(reference, null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Collection<EObject> getReferenceValues(EObject eObject, EReference reference) {
		Object value = eObject.eGet(reference);
		if(reference.isMany()) {
			return (Collection<EObject>) value;
		}
		else if(value != null) {
			return Collections.singleton((EObject) value);
		}
		else {
			return Collections.emptySet();
		}
	}

	@SuppressWarnings("unchecked")
	public static Collection<Object> getAttributeValues(EObject eObject, EAttribute attribute) {
		Object value = eObject.eGet(attribute);
		if(attribute.isMany()) {
			return (Collection<Object>) value;
		}
		else if(value != null) {
			return Collections.singleton(value);
		}
		else {
			return Collections.emptySet();
		}
	}
	
	public static List<EObject> loadMDELabModel(String path) {
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
			
			return deserializeMDELabModel(sb.toString());
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static List<EObject> deserializeMDELabModel(String string) {
		return deserializeMDELabModel(string, new LinkedHashMap<Long, EObject>());
	}

	public static List<EObject> deserializeMDELabModel(String string, Map<Long, EObject> objects) {
		Set<EObject> roots = new LinkedHashSet<EObject>();
		
		String[] lines = string.split(Pattern.quote("\n"));
		int currentIndex = 0;
		for(; currentIndex < lines.length && !lines[currentIndex].equals(END_OF_OBJECTS); currentIndex++) {
			String line = lines[currentIndex];

			int separatorIndex = line.indexOf(SEPARATOR);
			Long id = Long.parseLong(line.substring(0, separatorIndex));
			EObject object = parseObject(line.substring(separatorIndex + 1));
			objects.put(id, object);
		}
		currentIndex++;

		for(; currentIndex < lines.length; currentIndex++) {
			String line = lines[currentIndex];
			createEdge(line, objects);
		}
		
		for(EObject object:objects.values()) {
			if(object.eContainer() == null) {
				roots.add(object);
			}
		}
		
		return new ArrayList<EObject>(roots);
	}
	
	private static EObject parseObject(String string) {
		int nextSeparatorIndex = string.indexOf(SEPARATOR);
		String packageURI = string.substring(0, nextSeparatorIndex);
		string = string.substring(nextSeparatorIndex + 1);
		
		nextSeparatorIndex = string.indexOf(SEPARATOR);
		String className = string.substring(0, nextSeparatorIndex);
		string = string.substring(nextSeparatorIndex + 1);
		
		EClass eClass = getEClass(packageURI, className);
		
		EObject object = eClass.getEPackage().getEFactoryInstance().create(eClass);
		String[] attributeStrings = string.split(Pattern.quote(Character.toString(ATTRIBUTE_SEPARATOR)));
		
		for(String attributeString:attributeStrings) {
			String[] tokens = attributeString.split(Pattern.quote(Character.toString(SEPARATOR)));
			EAttribute attribute = (EAttribute) eClass.getEStructuralFeature(tokens[0]);
			for(int i = 1; i < tokens.length; i++) {
				Object value = parseValue(tokens[i], attribute.getEType());
				addAttributeValue(object, attribute, value);
			}
		}
		
		return object;
	}

	@SuppressWarnings("unchecked")
	public static void addAttributeValue(EObject object, EAttribute attribute, Object value) {
		if(attribute.isMany()) {
			((Collection<Object>) object.eGet(attribute)).add(value);
		}
		else {
			object.eSet(attribute, value);
		}
	}

	private static void createEdge(String line, Map<Long, EObject> objects) {
		String[] tokens = line.split(Pattern.quote(Character.toString(SEPARATOR)));
		
		Long sourceId = Long.parseLong(tokens[0]);
		String referenceName = tokens[1];
		Long targetId = Long.parseLong(tokens[2]);
		
		EObject source = objects.get(sourceId);
		EReference reference = getReference(source.eClass(), referenceName);
		EObject target = objects.get(targetId);
		
		addReferenceValue(source, reference, target);
	}

	public static String serializeDelta(EMFDelta delta) {
		StringBuffer sb = new StringBuffer();

		sb.append(OBJECT_CREATIONS_START);
		sb.append("\n");
		serializeObjectCreations(delta, sb);

		sb.append(OBJECT_DELETIONS_START);
		sb.append("\n");
		serializeObjectDeletions(delta, sb);

		sb.append(REFERENCE_CREATIONS_START);
		sb.append("\n");
		serializeReferenceCreations(delta, sb);

		sb.append(REFERENCE_DELETIONS_START);
		sb.append("\n");
		serializeReferenceDeletions(delta, sb);

		sb.append(ATTRIBUTE_CHANGES_START);
		sb.append("\n");
		serializeAttributeChanges(delta, sb);
		
		return sb.toString();
	}

	private static void serializeObjectCreations(EMFDelta delta, StringBuffer sb) {
		for(EObjectCreation objectCreation:delta.getObjectCreations()) {
			serializeObjectCreation(objectCreation, sb);
		}
	}

	private static void serializeObjectCreation(EObjectCreation objectCreation, StringBuffer sb) {
		sb.append(Long.toString(objectCreation.getID()));
		sb.append(SEPARATOR);
		serializeEClass(objectCreation.getEClass(), sb);
		sb.append("\n");
	}

	private static void serializeObjectDeletions(EMFDelta delta, StringBuffer sb) {
		for(EObjectDeletion objectDeletion:delta.getObjectDeletions()) {
			serializeObjectDeletion(objectDeletion, sb);
		}
	}

	private static void serializeObjectDeletion(EObjectDeletion objectDeletion, StringBuffer sb) {
		sb.append(Long.toString(objectDeletion.getID()));
		sb.append("\n");
	}

	private static void serializeReferenceCreations(EMFDelta delta, StringBuffer sb) {
		for(EReferenceCreation referenceCreation:delta.getReferenceCreations()) {
			serializeReferenceCreation(referenceCreation, sb);
		}
	}

	private static void serializeReferenceCreation(EReferenceCreation referenceCreation, StringBuffer sb) {
		sb.append(Long.toString(referenceCreation.getSourceID()));
		sb.append(SEPARATOR);
		serializeEStructuralFeature(referenceCreation.getEReference(), sb);
		sb.append(SEPARATOR);
		sb.append(Long.toString(referenceCreation.getTargetID()));
		sb.append("\n");
	}

	private static void serializeReferenceDeletions(EMFDelta delta, StringBuffer sb) {
		for(EReferenceDeletion referenceDeletion:delta.getReferenceDeletions()) {
			serializeReferenceDeletions(referenceDeletion, sb);
		}
	}

	private static void serializeReferenceDeletions(EReferenceDeletion referenceDeletion, StringBuffer sb) {
		sb.append(Long.toString(referenceDeletion.getSourceID()));
		sb.append(SEPARATOR);
		serializeEStructuralFeature(referenceDeletion.getEReference(), sb);
		sb.append(SEPARATOR);
		sb.append(Long.toString(referenceDeletion.getTargetID()));
		sb.append("\n");
	}

	private static void serializeAttributeChanges(EMFDelta delta, StringBuffer sb) {
		for(EAttributeChange attributeChange:delta.getAttributeChanges()) {
			serializeAttributeChange(attributeChange, sb);
		}
	}

	private static void serializeAttributeChange(EAttributeChange attributeChange, StringBuffer sb) {
		sb.append(Long.toString(attributeChange.getID()));
		sb.append(SEPARATOR);
		serializeEStructuralFeature(attributeChange.getEAttribute(), sb);
		sb.append(SEPARATOR);
		sb.append(escape(valueToString(attributeChange.getNewValue(), attributeChange.getEAttribute().getEType())));
		sb.append("\n");
	}

	private static void serializeEStructuralFeature(EStructuralFeature feature, StringBuffer sb) {
		serializeEClass(feature.getEContainingClass(), sb);
		sb.append(SEPARATOR);
		sb.append(feature.getName());
	}

	private static void serializeEClass(EClass eClass, StringBuffer sb) {
		sb.append(eClass.getEPackage().getNsURI());
		sb.append(SEPARATOR);
		sb.append(eClass.getName());
	}

	public static EMFDelta deserializeDelta(String string) {
		EMFDelta delta = new EMFDelta();

		String[] lines = string.split("\n");
		int currentIndex = 0;

		for(; currentIndex < lines.length && !lines[currentIndex].equals(OBJECT_CREATIONS_START); currentIndex++) {}
		currentIndex++;

		for(; currentIndex < lines.length && !lines[currentIndex].equals(OBJECT_DELETIONS_START); currentIndex++) {
			EObjectCreation objectCreation = deserializeObjectCreation(lines[currentIndex]);
			delta.getObjectCreations().add(objectCreation);
		}
		currentIndex++;

		for(; currentIndex < lines.length && !lines[currentIndex].equals(REFERENCE_CREATIONS_START); currentIndex++) {
			EObjectDeletion objectDeletion = deserializeObjectDeletion(lines[currentIndex]);
			delta.getObjectDeletions().add(objectDeletion);
		}
		currentIndex++;

		for(; currentIndex < lines.length && !lines[currentIndex].equals(REFERENCE_DELETIONS_START); currentIndex++) {
			EReferenceCreation referenceCreation = deserializeReferenceCreation(lines[currentIndex]);
			delta.getReferenceCreations().add(referenceCreation);
		}
		currentIndex++;
		for(; currentIndex < lines.length && !lines[currentIndex].equals(ATTRIBUTE_CHANGES_START); currentIndex++) {
			EReferenceDeletion referenceDeletion = deserializeReferenceDeletion(lines[currentIndex]);
			delta.getReferenceDeletions().add(referenceDeletion);
		}
		currentIndex++;
		
		for(; currentIndex < lines.length && !lines[currentIndex].equals(""); currentIndex++) {
			EAttributeChange attributeChange = deserializeAttributeChange(lines[currentIndex]);
			delta.getAttributeChanges().add(attributeChange);
		}
		
		return delta;
	}

	private static EObjectCreation deserializeObjectCreation(String string) {
		String[] tokens = string.split(Character.toString(SEPARATOR));
		long id = Long.parseLong(tokens[0]);
		EClass eClass = getEClass(tokens[1], tokens[2]);
		return new EObjectCreation(eClass, id);
	}

	private static EObjectDeletion deserializeObjectDeletion(String string) {
		long id = Long.parseLong(string);
		return new EObjectDeletion(id);
	}

	private static EReferenceCreation deserializeReferenceCreation(String string) {
		String[] tokens = string.split(Character.toString(SEPARATOR));
		long sourceID = Long.parseLong(tokens[0]);
		EClass eClass = getEClass(tokens[1], tokens[2]);
		EReference reference = getReference(eClass, tokens[3]);
		long targetID = Long.parseLong(tokens[4]);
		return new EReferenceCreation(reference, sourceID, targetID);
	}

	private static EReferenceDeletion deserializeReferenceDeletion(String string) {
		String[] tokens = string.split(Character.toString(SEPARATOR));
		long sourceID = Long.parseLong(tokens[0]);
		EClass eClass = getEClass(tokens[1], tokens[2]);
		EReference reference = getReference(eClass, tokens[3]);
		long targetID = Long.parseLong(tokens[4]);
		return new EReferenceDeletion(reference, sourceID, targetID);
	}

	private static EAttributeChange deserializeAttributeChange(String string) {
		String[] tokens = string.split(Character.toString(SEPARATOR));
		long id = Long.parseLong(tokens[0]);
		EClass eClass = getEClass(tokens[1], tokens[2]);
		EAttribute attribute = getAttribute(eClass, tokens[3]);
		Object value = tokens.length > 4 ? parseValue(tokens[4], attribute.getEType()) : parseValue("", attribute.getEType());
		return new EAttributeChange(attribute, id, value);
	}

	public static EClass getEClass(String packageURI, String className) {
		EPackage pkg = EPackage.Registry.INSTANCE.getEPackage(packageURI);
		return (EClass) pkg.getEClassifier(className);
	}

	public static EReference getReference(EClass eClass, String referenceName) {
		return (EReference) getStructuralFeature(eClass, referenceName);
	}

	public static EAttribute getAttribute(EClass eClass, String attributeName) {
		return (EAttribute) getStructuralFeature(eClass, attributeName);
	}

	public static EStructuralFeature getStructuralFeature(EClass eClass, String featureName) {
		EStructuralFeature feature = eClass.getEStructuralFeature(featureName);
		if(feature != null) {
			return feature;
		}
		else if(!eClass.getESuperTypes().isEmpty()) {
			for(EClass superClass:eClass.getESuperTypes()) {
				feature = getStructuralFeature(superClass, featureName);
				if(feature != null) {
					return feature;
				}
			}
		}
		return null;
	}

	public static String valueToString(Object value, EClassifier eType) {
		if(value == null) {
			return "null";
		}
		else if(eType.eClass() == EcorePackage.Literals.EENUM) {
			return ((Enumerator) value).getName();
		}
		else {
			return value.toString();
		}
	}

	private static String escape(String string) {
		string = string.replace('\n', ' ');
		string = string.replace(SEPARATOR, ' ');
		string = string.replace(ATTRIBUTE_SEPARATOR, ' ');
		return string;
	}

	public static Object parseValue(String string, EClassifier eType) {
		if(string.equals("null")) {
			return null;
		}
		else if(eType == EcorePackage.Literals.ELONG) {
			return Long.parseLong(string);
		}
		else if(eType == EcorePackage.Literals.EINT) {
			return Integer.parseInt(string);
		}
		else if(eType == EcorePackage.Literals.ESTRING) {
			return string;
		}
		else if(eType == EcorePackage.Literals.EBOOLEAN) {
			return Boolean.parseBoolean(string);
		}
		else if(eType == EcorePackage.Literals.ECHAR) {
			return string.charAt(0);
		}
		else if (eType.eClass() == EcorePackage.Literals.EENUM) {
			return ((EEnum) eType).getEEnumLiteral(string).getInstance();
		}
		else {
			return null;
		}
	}

	public static void writeXMI(EObject o, String path) {		
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(o.eClass().getEPackage().getName(), new XMIResourceFactoryImpl());
		
		Resource r = rs.createResource(URI.createFileURI(path));
		r.getContents().add(o);
		try {
			r.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static EObject loadXmi(String modelFile) {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(modelFile.substring(modelFile.lastIndexOf('.') + 1), new XMIResourceFactoryImpl());
		
		Resource r = rs.createResource(URI.createFileURI(modelFile));
		try {
			r.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return r.getContents().get(0);
	}

}
