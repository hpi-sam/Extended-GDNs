/**
 */
package de.mdelab.mlsdm.gdn.impl;

import de.mdelab.mlcore.MlcorePackage;

import de.mdelab.mlexpressions.MlexpressionsPackage;

import de.mdelab.mlsdm.gdn.GDNDependency;
import de.mdelab.mlsdm.gdn.GDNMapping;
import de.mdelab.mlsdm.gdn.GDNNode;
import de.mdelab.mlsdm.gdn.GdnFactory;
import de.mdelab.mlsdm.gdn.GdnPackage;

import de.mdelab.mlstorypatterns.MlstorypatternsPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class GdnPackageImpl extends EPackageImpl implements GdnPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass gdnNodeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass gdnDependencyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass gdnMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass gdnEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private GdnPackageImpl() {
		super(eNS_URI, GdnFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link GdnPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static GdnPackage init() {
		if (isInited) return (GdnPackage)EPackage.Registry.INSTANCE.getEPackage(GdnPackage.eNS_URI);

		// Obtain or create and register package
		GdnPackageImpl theGdnPackage = (GdnPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof GdnPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new GdnPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		MlcorePackage.eINSTANCE.eClass();
		MlexpressionsPackage.eINSTANCE.eClass();
		MlstorypatternsPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theGdnPackage.createPackageContents();

		// Initialize created meta-data
		theGdnPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theGdnPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(GdnPackage.eNS_URI, theGdnPackage);
		return theGdnPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGDNNode() {
		return gdnNodeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGDNNode_Dependencies() {
		return (EReference)gdnNodeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGDNNode_Pattern() {
		return (EReference)gdnNodeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGDNDependency() {
		return gdnDependencyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGDNDependency_Node() {
		return (EReference)gdnDependencyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGDNDependency_IndexConstraint() {
		return (EReference)gdnDependencyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGDNDependency_Mappings() {
		return (EReference)gdnDependencyEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGDNDependency_Negative() {
		return (EAttribute)gdnDependencyEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGDNMapping() {
		return gdnMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGDNMapping_ParentVertex() {
		return (EReference)gdnMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGDNMapping_ChildVertex() {
		return (EReference)gdnMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getGDNMapping_IndexPosition() {
		return (EAttribute)gdnMappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getGDN() {
		return gdnEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGDN_OwnedNodes() {
		return (EReference)gdnEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getGDN_Root() {
		return (EReference)gdnEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GdnFactory getGdnFactory() {
		return (GdnFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		gdnNodeEClass = createEClass(GDN_NODE);
		createEReference(gdnNodeEClass, GDN_NODE__DEPENDENCIES);
		createEReference(gdnNodeEClass, GDN_NODE__PATTERN);

		gdnDependencyEClass = createEClass(GDN_DEPENDENCY);
		createEReference(gdnDependencyEClass, GDN_DEPENDENCY__NODE);
		createEReference(gdnDependencyEClass, GDN_DEPENDENCY__INDEX_CONSTRAINT);
		createEReference(gdnDependencyEClass, GDN_DEPENDENCY__MAPPINGS);
		createEAttribute(gdnDependencyEClass, GDN_DEPENDENCY__NEGATIVE);

		gdnMappingEClass = createEClass(GDN_MAPPING);
		createEReference(gdnMappingEClass, GDN_MAPPING__PARENT_VERTEX);
		createEReference(gdnMappingEClass, GDN_MAPPING__CHILD_VERTEX);
		createEAttribute(gdnMappingEClass, GDN_MAPPING__INDEX_POSITION);

		gdnEClass = createEClass(GDN);
		createEReference(gdnEClass, GDN__OWNED_NODES);
		createEReference(gdnEClass, GDN__ROOT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		MlstorypatternsPackage theMlstorypatternsPackage = (MlstorypatternsPackage)EPackage.Registry.INSTANCE.getEPackage(MlstorypatternsPackage.eNS_URI);
		MlexpressionsPackage theMlexpressionsPackage = (MlexpressionsPackage)EPackage.Registry.INSTANCE.getEPackage(MlexpressionsPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(gdnNodeEClass, GDNNode.class, "GDNNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGDNNode_Dependencies(), this.getGDNDependency(), null, "dependencies", null, 0, -1, GDNNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getGDNNode_Pattern(), theMlstorypatternsPackage.getStoryPattern(), null, "pattern", null, 1, 1, GDNNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(gdnDependencyEClass, GDNDependency.class, "GDNDependency", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGDNDependency_Node(), this.getGDNNode(), null, "node", null, 1, 1, GDNDependency.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getGDNDependency_IndexConstraint(), theMlexpressionsPackage.getMLStringExpression(), null, "indexConstraint", null, 1, 1, GDNDependency.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getGDNDependency_Mappings(), this.getGDNMapping(), null, "mappings", null, 0, -1, GDNDependency.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGDNDependency_Negative(), ecorePackage.getEBoolean(), "negative", "false", 1, 1, GDNDependency.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(gdnMappingEClass, GDNMapping.class, "GDNMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGDNMapping_ParentVertex(), theMlstorypatternsPackage.getAbstractStoryPatternObject(), null, "parentVertex", null, 1, 1, GDNMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getGDNMapping_ChildVertex(), theMlstorypatternsPackage.getAbstractStoryPatternObject(), null, "childVertex", null, 0, 1, GDNMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGDNMapping_IndexPosition(), ecorePackage.getEInt(), "indexPosition", null, 1, 1, GDNMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(gdnEClass, de.mdelab.mlsdm.gdn.GDN.class, "GDN", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getGDN_OwnedNodes(), this.getGDNNode(), null, "ownedNodes", null, 0, -1, de.mdelab.mlsdm.gdn.GDN.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getGDN_Root(), this.getGDNNode(), null, "root", null, 1, 1, de.mdelab.mlsdm.gdn.GDN.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //GdnPackageImpl
