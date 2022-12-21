/**
 */
package de.mdelab.mlsdm.gdn;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see de.mdelab.mlsdm.gdn.GdnFactory
 * @model kind="package"
 * @generated
 */
public interface GdnPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "gdn";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.mdelab.de/mlsdm/gdn/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "gdn";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GdnPackage eINSTANCE = de.mdelab.mlsdm.gdn.impl.GdnPackageImpl.init();

	/**
	 * The meta object id for the '{@link de.mdelab.mlsdm.gdn.impl.GDNNodeImpl <em>GDN Node</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.mdelab.mlsdm.gdn.impl.GDNNodeImpl
	 * @see de.mdelab.mlsdm.gdn.impl.GdnPackageImpl#getGDNNode()
	 * @generated
	 */
	int GDN_NODE = 0;

	/**
	 * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_NODE__DEPENDENCIES = 0;

	/**
	 * The feature id for the '<em><b>Pattern</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_NODE__PATTERN = 1;

	/**
	 * The number of structural features of the '<em>GDN Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_NODE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>GDN Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_NODE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link de.mdelab.mlsdm.gdn.impl.GDNDependencyImpl <em>GDN Dependency</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.mdelab.mlsdm.gdn.impl.GDNDependencyImpl
	 * @see de.mdelab.mlsdm.gdn.impl.GdnPackageImpl#getGDNDependency()
	 * @generated
	 */
	int GDN_DEPENDENCY = 1;

	/**
	 * The feature id for the '<em><b>Node</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_DEPENDENCY__NODE = 0;

	/**
	 * The feature id for the '<em><b>Index Constraint</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_DEPENDENCY__INDEX_CONSTRAINT = 1;

	/**
	 * The feature id for the '<em><b>Mappings</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_DEPENDENCY__MAPPINGS = 2;

	/**
	 * The feature id for the '<em><b>Negative</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_DEPENDENCY__NEGATIVE = 3;

	/**
	 * The number of structural features of the '<em>GDN Dependency</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_DEPENDENCY_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>GDN Dependency</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_DEPENDENCY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link de.mdelab.mlsdm.gdn.impl.GDNMappingImpl <em>GDN Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.mdelab.mlsdm.gdn.impl.GDNMappingImpl
	 * @see de.mdelab.mlsdm.gdn.impl.GdnPackageImpl#getGDNMapping()
	 * @generated
	 */
	int GDN_MAPPING = 2;

	/**
	 * The feature id for the '<em><b>Parent Vertex</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_MAPPING__PARENT_VERTEX = 0;

	/**
	 * The feature id for the '<em><b>Child Vertex</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_MAPPING__CHILD_VERTEX = 1;

	/**
	 * The feature id for the '<em><b>Index Position</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_MAPPING__INDEX_POSITION = 2;

	/**
	 * The number of structural features of the '<em>GDN Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_MAPPING_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>GDN Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_MAPPING_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link de.mdelab.mlsdm.gdn.impl.GDNImpl <em>GDN</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see de.mdelab.mlsdm.gdn.impl.GDNImpl
	 * @see de.mdelab.mlsdm.gdn.impl.GdnPackageImpl#getGDN()
	 * @generated
	 */
	int GDN = 3;

	/**
	 * The feature id for the '<em><b>Owned Nodes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN__OWNED_NODES = 0;

	/**
	 * The feature id for the '<em><b>Root</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN__ROOT = 1;

	/**
	 * The number of structural features of the '<em>GDN</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>GDN</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GDN_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link de.mdelab.mlsdm.gdn.GDNNode <em>GDN Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>GDN Node</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNNode
	 * @generated
	 */
	EClass getGDNNode();

	/**
	 * Returns the meta object for the containment reference list '{@link de.mdelab.mlsdm.gdn.GDNNode#getDependencies <em>Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Dependencies</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNNode#getDependencies()
	 * @see #getGDNNode()
	 * @generated
	 */
	EReference getGDNNode_Dependencies();

	/**
	 * Returns the meta object for the containment reference '{@link de.mdelab.mlsdm.gdn.GDNNode#getPattern <em>Pattern</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Pattern</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNNode#getPattern()
	 * @see #getGDNNode()
	 * @generated
	 */
	EReference getGDNNode_Pattern();

	/**
	 * Returns the meta object for class '{@link de.mdelab.mlsdm.gdn.GDNDependency <em>GDN Dependency</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>GDN Dependency</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNDependency
	 * @generated
	 */
	EClass getGDNDependency();

	/**
	 * Returns the meta object for the reference '{@link de.mdelab.mlsdm.gdn.GDNDependency#getNode <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Node</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNDependency#getNode()
	 * @see #getGDNDependency()
	 * @generated
	 */
	EReference getGDNDependency_Node();

	/**
	 * Returns the meta object for the reference '{@link de.mdelab.mlsdm.gdn.GDNDependency#getIndexConstraint <em>Index Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Index Constraint</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNDependency#getIndexConstraint()
	 * @see #getGDNDependency()
	 * @generated
	 */
	EReference getGDNDependency_IndexConstraint();

	/**
	 * Returns the meta object for the containment reference list '{@link de.mdelab.mlsdm.gdn.GDNDependency#getMappings <em>Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Mappings</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNDependency#getMappings()
	 * @see #getGDNDependency()
	 * @generated
	 */
	EReference getGDNDependency_Mappings();

	/**
	 * Returns the meta object for the attribute '{@link de.mdelab.mlsdm.gdn.GDNDependency#isNegative <em>Negative</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Negative</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNDependency#isNegative()
	 * @see #getGDNDependency()
	 * @generated
	 */
	EAttribute getGDNDependency_Negative();

	/**
	 * Returns the meta object for class '{@link de.mdelab.mlsdm.gdn.GDNMapping <em>GDN Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>GDN Mapping</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNMapping
	 * @generated
	 */
	EClass getGDNMapping();

	/**
	 * Returns the meta object for the reference '{@link de.mdelab.mlsdm.gdn.GDNMapping#getParentVertex <em>Parent Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent Vertex</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNMapping#getParentVertex()
	 * @see #getGDNMapping()
	 * @generated
	 */
	EReference getGDNMapping_ParentVertex();

	/**
	 * Returns the meta object for the reference '{@link de.mdelab.mlsdm.gdn.GDNMapping#getChildVertex <em>Child Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Child Vertex</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNMapping#getChildVertex()
	 * @see #getGDNMapping()
	 * @generated
	 */
	EReference getGDNMapping_ChildVertex();

	/**
	 * Returns the meta object for the attribute '{@link de.mdelab.mlsdm.gdn.GDNMapping#getIndexPosition <em>Index Position</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Index Position</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDNMapping#getIndexPosition()
	 * @see #getGDNMapping()
	 * @generated
	 */
	EAttribute getGDNMapping_IndexPosition();

	/**
	 * Returns the meta object for class '{@link de.mdelab.mlsdm.gdn.GDN <em>GDN</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>GDN</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDN
	 * @generated
	 */
	EClass getGDN();

	/**
	 * Returns the meta object for the containment reference list '{@link de.mdelab.mlsdm.gdn.GDN#getOwnedNodes <em>Owned Nodes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Owned Nodes</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDN#getOwnedNodes()
	 * @see #getGDN()
	 * @generated
	 */
	EReference getGDN_OwnedNodes();

	/**
	 * Returns the meta object for the reference '{@link de.mdelab.mlsdm.gdn.GDN#getRoot <em>Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Root</em>'.
	 * @see de.mdelab.mlsdm.gdn.GDN#getRoot()
	 * @see #getGDN()
	 * @generated
	 */
	EReference getGDN_Root();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	GdnFactory getGdnFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link de.mdelab.mlsdm.gdn.impl.GDNNodeImpl <em>GDN Node</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.mdelab.mlsdm.gdn.impl.GDNNodeImpl
		 * @see de.mdelab.mlsdm.gdn.impl.GdnPackageImpl#getGDNNode()
		 * @generated
		 */
		EClass GDN_NODE = eINSTANCE.getGDNNode();

		/**
		 * The meta object literal for the '<em><b>Dependencies</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GDN_NODE__DEPENDENCIES = eINSTANCE.getGDNNode_Dependencies();

		/**
		 * The meta object literal for the '<em><b>Pattern</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GDN_NODE__PATTERN = eINSTANCE.getGDNNode_Pattern();

		/**
		 * The meta object literal for the '{@link de.mdelab.mlsdm.gdn.impl.GDNDependencyImpl <em>GDN Dependency</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.mdelab.mlsdm.gdn.impl.GDNDependencyImpl
		 * @see de.mdelab.mlsdm.gdn.impl.GdnPackageImpl#getGDNDependency()
		 * @generated
		 */
		EClass GDN_DEPENDENCY = eINSTANCE.getGDNDependency();

		/**
		 * The meta object literal for the '<em><b>Node</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GDN_DEPENDENCY__NODE = eINSTANCE.getGDNDependency_Node();

		/**
		 * The meta object literal for the '<em><b>Index Constraint</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GDN_DEPENDENCY__INDEX_CONSTRAINT = eINSTANCE.getGDNDependency_IndexConstraint();

		/**
		 * The meta object literal for the '<em><b>Mappings</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GDN_DEPENDENCY__MAPPINGS = eINSTANCE.getGDNDependency_Mappings();

		/**
		 * The meta object literal for the '<em><b>Negative</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GDN_DEPENDENCY__NEGATIVE = eINSTANCE.getGDNDependency_Negative();

		/**
		 * The meta object literal for the '{@link de.mdelab.mlsdm.gdn.impl.GDNMappingImpl <em>GDN Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.mdelab.mlsdm.gdn.impl.GDNMappingImpl
		 * @see de.mdelab.mlsdm.gdn.impl.GdnPackageImpl#getGDNMapping()
		 * @generated
		 */
		EClass GDN_MAPPING = eINSTANCE.getGDNMapping();

		/**
		 * The meta object literal for the '<em><b>Parent Vertex</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GDN_MAPPING__PARENT_VERTEX = eINSTANCE.getGDNMapping_ParentVertex();

		/**
		 * The meta object literal for the '<em><b>Child Vertex</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GDN_MAPPING__CHILD_VERTEX = eINSTANCE.getGDNMapping_ChildVertex();

		/**
		 * The meta object literal for the '<em><b>Index Position</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GDN_MAPPING__INDEX_POSITION = eINSTANCE.getGDNMapping_IndexPosition();

		/**
		 * The meta object literal for the '{@link de.mdelab.mlsdm.gdn.impl.GDNImpl <em>GDN</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see de.mdelab.mlsdm.gdn.impl.GDNImpl
		 * @see de.mdelab.mlsdm.gdn.impl.GdnPackageImpl#getGDN()
		 * @generated
		 */
		EClass GDN = eINSTANCE.getGDN();

		/**
		 * The meta object literal for the '<em><b>Owned Nodes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GDN__OWNED_NODES = eINSTANCE.getGDN_OwnedNodes();

		/**
		 * The meta object literal for the '<em><b>Root</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GDN__ROOT = eINSTANCE.getGDN_Root();

	}

} //GdnPackage
