package de.mdelab.mlsdm.interpreter.incremental.rete.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;

import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ReteContinuationDiscriminator;
import de.mdelab.mlsdm.interpreter.searchModel.model.MLSDMSearchModel;

public class ReteUtil {

	public static boolean typesMatch(EClassifier type, EClassifier type2) {
		return type == type2 ||
				(type.eClass() == EcorePackage.Literals.ECLASS && ((EClass) type).getEAllSuperTypes().contains(type2)) ||
				(type2.eClass() == EcorePackage.Literals.ECLASS && ((EClass) type2).getEAllSuperTypes().contains(type));
	}

	public static Collection<String> getNamesForIndices(MLSDMSearchModel searchModel, int[] nodeMask) {
		Collection<String> names = new ArrayList<String>();
		for(int id = 0; id < nodeMask.length; id++) {
			if(nodeMask[id] != ReteContinuationDiscriminator.NO_MAPPING) {
				names.add(searchModel.getNodeForId(id).getSpo().getName());
			}
		}
		return names;
	}

	public static <T> LinkedHashSet<T> intersect(
			Collection<? extends T> collection1,
			Collection<? extends T> collection2) {
		LinkedHashSet<T> intersection = new LinkedHashSet<T>();
		intersection.addAll(collection1);
		intersection.retainAll(collection2);
		return intersection;
	}

	public static <NodeType> List<NodeType> collectNodeValues (WeightedGraph<NodeType, ?> partition) {
		List<NodeType> values = new ArrayList<NodeType>();
		for(Node<NodeType, ?> node:partition.nodes) {
			values.add(node.value);
		}
		return values;
	}

}
