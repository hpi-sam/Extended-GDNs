/**
 */
package de.mdelab.migmm.sample.ecore2java.generated.impl;

import de.mdelab.migmm.sample.ecore2java.Ecore2javaPackage;

import de.mdelab.migmm.sample.ecore2java.generated.GeneratedFactory;
import de.mdelab.migmm.sample.ecore2java.generated.GeneratedPackage;
import de.mdelab.migmm.sample.ecore2java.generated.ecore2javaAttributeRule;
import de.mdelab.migmm.sample.ecore2java.generated.ecore2javaAttributeRule_r1;
import de.mdelab.migmm.sample.ecore2java.generated.ecore2javaAxiom;
import de.mdelab.migmm.sample.ecore2java.generated.ecore2javaAxiom_r1;
import de.mdelab.migmm.sample.ecore2java.generated.ecore2javaClassRule;
import de.mdelab.migmm.sample.ecore2java.generated.ecore2javaClassRule_r1;
import de.mdelab.migmm.sample.ecore2java.generated.ecore2javaOperationalTGG;

import de.mdelab.migmm.sample.ecore2java.impl.Ecore2javaPackageImpl;

import de.mdelab.mlcallactions.MlcallactionsPackage;

import de.mdelab.mlcore.MlcorePackage;

import de.mdelab.mlexpressions.MlexpressionsPackage;

import de.mdelab.mlsdm.MlsdmPackage;

import de.mdelab.mlstorypatterns.MlstorypatternsPackage;

import de.mdelab.mltgg.mote2.Mote2Package;

import de.mdelab.mltgg.mote2.helpers.HelpersPackage;

import de.mdelab.mltgg.mote2.operationalTGG.OperationalTGGPackage;

import de.mdelab.mltgg.mote2.sdm.SdmPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class GeneratedPackageImpl extends EPackageImpl implements GeneratedPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ecore2javaOperationalTGGEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ecore2javaAxiomEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ecore2javaClassRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ecore2javaAttributeRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ecore2javaAxiom_r1EClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ecore2javaClassRule_r1EClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ecore2javaAttributeRule_r1EClass = null;

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
	 * @see de.mdelab.migmm.sample.ecore2java.generated.GeneratedPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private GeneratedPackageImpl() {
		super(eNS_URI, GeneratedFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link GeneratedPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static GeneratedPackage init() {
		if (isInited)
			return (GeneratedPackage) EPackage.Registry.INSTANCE.getEPackage(GeneratedPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredGeneratedPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		GeneratedPackageImpl theGeneratedPackage = registeredGeneratedPackage instanceof GeneratedPackageImpl
				? (GeneratedPackageImpl) registeredGeneratedPackage
				: new GeneratedPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		Mote2Package.eINSTANCE.eClass();
		MlcorePackage.eINSTANCE.eClass();
		SdmPackage.eINSTANCE.eClass();
		MlsdmPackage.eINSTANCE.eClass();
		MlexpressionsPackage.eINSTANCE.eClass();
		MlstorypatternsPackage.eINSTANCE.eClass();
		MlcallactionsPackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(Ecore2javaPackage.eNS_URI);
		Ecore2javaPackageImpl theEcore2javaPackage = (Ecore2javaPackageImpl) (registeredPackage instanceof Ecore2javaPackageImpl
				? registeredPackage
				: Ecore2javaPackage.eINSTANCE);

		// Create package meta-data objects
		theGeneratedPackage.createPackageContents();
		theEcore2javaPackage.createPackageContents();

		// Initialize created meta-data
		theGeneratedPackage.initializePackageContents();
		theEcore2javaPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theGeneratedPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(GeneratedPackage.eNS_URI, theGeneratedPackage);
		return theGeneratedPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getecore2javaOperationalTGG() {
		return ecore2javaOperationalTGGEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getecore2javaAxiom() {
		return ecore2javaAxiomEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getecore2javaClassRule() {
		return ecore2javaClassRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getecore2javaAttributeRule() {
		return ecore2javaAttributeRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getecore2javaAxiom_r1() {
		return ecore2javaAxiom_r1EClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAxiom_r1_AddElementActivity() {
		return (EReference) ecore2javaAxiom_r1EClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAxiom_r1_MoveElementActivity() {
		return (EReference) ecore2javaAxiom_r1EClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAxiom_r1_ChangeAttributeActivity() {
		return (EReference) ecore2javaAxiom_r1EClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAxiom_r1_TransformForwardActivity() {
		return (EReference) ecore2javaAxiom_r1EClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAxiom_r1_TransformMappingActivity() {
		return (EReference) ecore2javaAxiom_r1EClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAxiom_r1_TransformBackwardActivity() {
		return (EReference) ecore2javaAxiom_r1EClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAxiom_r1_SynchronizeForwardActivity() {
		return (EReference) ecore2javaAxiom_r1EClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAxiom_r1_SynchronizeBackwardActivity() {
		return (EReference) ecore2javaAxiom_r1EClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAxiom_r1__AddElement__EMap_EList_EList() {
		return ecore2javaAxiom_r1EClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAxiom_r1__ChangeAttributeValues__TGGNode_EMap() {
		return ecore2javaAxiom_r1EClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAxiom_r1__TransformForward__EList_EList_boolean() {
		return ecore2javaAxiom_r1EClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAxiom_r1__TransformMapping__EList_EList_boolean() {
		return ecore2javaAxiom_r1EClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAxiom_r1__TransformBackward__EList_EList_boolean() {
		return ecore2javaAxiom_r1EClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAxiom_r1__SynchronizeForward__EList_EList_TGGNode_boolean() {
		return ecore2javaAxiom_r1EClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAxiom_r1__SynchronizeBackward__EList_EList_TGGNode_boolean() {
		return ecore2javaAxiom_r1EClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getecore2javaClassRule_r1() {
		return ecore2javaClassRule_r1EClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_AddElementActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_MoveElementActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_ChangeAttributeActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_TransformForwardActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_TransformMappingActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_TransformBackwardActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_ConflictCheckForwardActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_ConflictCheckMappingActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_ConflictCheckBackwardActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_SynchronizeForwardActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_SynchronizeBackwardActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_RepairForwardActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaClassRule_r1_RepairBackwardActivity() {
		return (EReference) ecore2javaClassRule_r1EClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__AddElement__EMap() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__ChangeAttributeValues__TGGNode_EMap() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__MoveElement__TGGNode_TGGNode_TGGNode() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__TransformForward__TGGNode_boolean_boolean() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__TransformMapping__TGGNode_boolean_boolean() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__TransformBackward__TGGNode_boolean_boolean() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__ConflictCheckForward__TGGNode() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__ConflictCheckMapping__TGGNode() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__ConflictCheckBackward__TGGNode() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__SynchronizeForward__TGGNode_boolean() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__SynchronizeBackward__TGGNode_boolean() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__RepairForward__TGGNode_boolean() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaClassRule_r1__RepairBackward__TGGNode_boolean() {
		return ecore2javaClassRule_r1EClass.getEOperations().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getecore2javaAttributeRule_r1() {
		return ecore2javaAttributeRule_r1EClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_AddElementActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_MoveElementActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_ChangeAttributeActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_TransformForwardActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_TransformMappingActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_TransformBackwardActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_ConflictCheckForwardActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_ConflictCheckMappingActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_ConflictCheckBackwardActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_SynchronizeForwardActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_SynchronizeBackwardActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_RepairForwardActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getecore2javaAttributeRule_r1_RepairBackwardActivity() {
		return (EReference) ecore2javaAttributeRule_r1EClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__AddElement__EMap() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__ChangeAttributeValues__TGGNode_EMap() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__MoveElement__TGGNode_TGGNode_TGGNode() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__TransformForward__TGGNode_boolean_boolean() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__TransformMapping__TGGNode_boolean_boolean() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__TransformBackward__TGGNode_boolean_boolean() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__ConflictCheckForward__TGGNode() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__ConflictCheckMapping__TGGNode() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__ConflictCheckBackward__TGGNode() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__SynchronizeForward__TGGNode_boolean() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__SynchronizeBackward__TGGNode_boolean() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__RepairForward__TGGNode_boolean() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getecore2javaAttributeRule_r1__RepairBackward__TGGNode_boolean() {
		return ecore2javaAttributeRule_r1EClass.getEOperations().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeneratedFactory getGeneratedFactory() {
		return (GeneratedFactory) getEFactoryInstance();
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
		if (isCreated)
			return;
		isCreated = true;

		// Create classes and their features
		ecore2javaOperationalTGGEClass = createEClass(ECORE2JAVA_OPERATIONAL_TGG);

		ecore2javaAxiomEClass = createEClass(ECORE2JAVA_AXIOM);

		ecore2javaClassRuleEClass = createEClass(ECORE2JAVA_CLASS_RULE);

		ecore2javaAttributeRuleEClass = createEClass(ECORE2JAVA_ATTRIBUTE_RULE);

		ecore2javaAxiom_r1EClass = createEClass(ECORE2JAVA_AXIOM_R1);
		createEReference(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1__ADD_ELEMENT_ACTIVITY);
		createEReference(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1__MOVE_ELEMENT_ACTIVITY);
		createEReference(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1__CHANGE_ATTRIBUTE_ACTIVITY);
		createEReference(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1__TRANSFORM_FORWARD_ACTIVITY);
		createEReference(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1__TRANSFORM_MAPPING_ACTIVITY);
		createEReference(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1__TRANSFORM_BACKWARD_ACTIVITY);
		createEReference(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1__SYNCHRONIZE_FORWARD_ACTIVITY);
		createEReference(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1__SYNCHRONIZE_BACKWARD_ACTIVITY);
		createEOperation(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1___ADD_ELEMENT__EMAP_ELIST_ELIST);
		createEOperation(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1___CHANGE_ATTRIBUTE_VALUES__TGGNODE_EMAP);
		createEOperation(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1___TRANSFORM_FORWARD__ELIST_ELIST_BOOLEAN);
		createEOperation(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1___TRANSFORM_MAPPING__ELIST_ELIST_BOOLEAN);
		createEOperation(ecore2javaAxiom_r1EClass, ECORE2JAVA_AXIOM_R1___TRANSFORM_BACKWARD__ELIST_ELIST_BOOLEAN);
		createEOperation(ecore2javaAxiom_r1EClass,
				ECORE2JAVA_AXIOM_R1___SYNCHRONIZE_FORWARD__ELIST_ELIST_TGGNODE_BOOLEAN);
		createEOperation(ecore2javaAxiom_r1EClass,
				ECORE2JAVA_AXIOM_R1___SYNCHRONIZE_BACKWARD__ELIST_ELIST_TGGNODE_BOOLEAN);

		ecore2javaClassRule_r1EClass = createEClass(ECORE2JAVA_CLASS_RULE_R1);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__ADD_ELEMENT_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__MOVE_ELEMENT_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__CHANGE_ATTRIBUTE_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__TRANSFORM_FORWARD_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__TRANSFORM_MAPPING_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__TRANSFORM_BACKWARD_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__CONFLICT_CHECK_FORWARD_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__CONFLICT_CHECK_MAPPING_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__CONFLICT_CHECK_BACKWARD_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__SYNCHRONIZE_FORWARD_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__SYNCHRONIZE_BACKWARD_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__REPAIR_FORWARD_ACTIVITY);
		createEReference(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1__REPAIR_BACKWARD_ACTIVITY);
		createEOperation(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1___ADD_ELEMENT__EMAP);
		createEOperation(ecore2javaClassRule_r1EClass,
				ECORE2JAVA_CLASS_RULE_R1___CHANGE_ATTRIBUTE_VALUES__TGGNODE_EMAP);
		createEOperation(ecore2javaClassRule_r1EClass,
				ECORE2JAVA_CLASS_RULE_R1___MOVE_ELEMENT__TGGNODE_TGGNODE_TGGNODE);
		createEOperation(ecore2javaClassRule_r1EClass,
				ECORE2JAVA_CLASS_RULE_R1___TRANSFORM_FORWARD__TGGNODE_BOOLEAN_BOOLEAN);
		createEOperation(ecore2javaClassRule_r1EClass,
				ECORE2JAVA_CLASS_RULE_R1___TRANSFORM_MAPPING__TGGNODE_BOOLEAN_BOOLEAN);
		createEOperation(ecore2javaClassRule_r1EClass,
				ECORE2JAVA_CLASS_RULE_R1___TRANSFORM_BACKWARD__TGGNODE_BOOLEAN_BOOLEAN);
		createEOperation(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1___CONFLICT_CHECK_FORWARD__TGGNODE);
		createEOperation(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1___CONFLICT_CHECK_MAPPING__TGGNODE);
		createEOperation(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1___CONFLICT_CHECK_BACKWARD__TGGNODE);
		createEOperation(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1___SYNCHRONIZE_FORWARD__TGGNODE_BOOLEAN);
		createEOperation(ecore2javaClassRule_r1EClass,
				ECORE2JAVA_CLASS_RULE_R1___SYNCHRONIZE_BACKWARD__TGGNODE_BOOLEAN);
		createEOperation(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1___REPAIR_FORWARD__TGGNODE_BOOLEAN);
		createEOperation(ecore2javaClassRule_r1EClass, ECORE2JAVA_CLASS_RULE_R1___REPAIR_BACKWARD__TGGNODE_BOOLEAN);

		ecore2javaAttributeRule_r1EClass = createEClass(ECORE2JAVA_ATTRIBUTE_RULE_R1);
		createEReference(ecore2javaAttributeRule_r1EClass, ECORE2JAVA_ATTRIBUTE_RULE_R1__ADD_ELEMENT_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass, ECORE2JAVA_ATTRIBUTE_RULE_R1__MOVE_ELEMENT_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass, ECORE2JAVA_ATTRIBUTE_RULE_R1__CHANGE_ATTRIBUTE_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass, ECORE2JAVA_ATTRIBUTE_RULE_R1__TRANSFORM_FORWARD_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass, ECORE2JAVA_ATTRIBUTE_RULE_R1__TRANSFORM_MAPPING_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass, ECORE2JAVA_ATTRIBUTE_RULE_R1__TRANSFORM_BACKWARD_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1__CONFLICT_CHECK_FORWARD_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1__CONFLICT_CHECK_MAPPING_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1__CONFLICT_CHECK_BACKWARD_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass, ECORE2JAVA_ATTRIBUTE_RULE_R1__SYNCHRONIZE_FORWARD_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass, ECORE2JAVA_ATTRIBUTE_RULE_R1__SYNCHRONIZE_BACKWARD_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass, ECORE2JAVA_ATTRIBUTE_RULE_R1__REPAIR_FORWARD_ACTIVITY);
		createEReference(ecore2javaAttributeRule_r1EClass, ECORE2JAVA_ATTRIBUTE_RULE_R1__REPAIR_BACKWARD_ACTIVITY);
		createEOperation(ecore2javaAttributeRule_r1EClass, ECORE2JAVA_ATTRIBUTE_RULE_R1___ADD_ELEMENT__EMAP);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___CHANGE_ATTRIBUTE_VALUES__TGGNODE_EMAP);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___MOVE_ELEMENT__TGGNODE_TGGNODE_TGGNODE);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___TRANSFORM_FORWARD__TGGNODE_BOOLEAN_BOOLEAN);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___TRANSFORM_MAPPING__TGGNODE_BOOLEAN_BOOLEAN);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___TRANSFORM_BACKWARD__TGGNODE_BOOLEAN_BOOLEAN);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___CONFLICT_CHECK_FORWARD__TGGNODE);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___CONFLICT_CHECK_MAPPING__TGGNODE);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___CONFLICT_CHECK_BACKWARD__TGGNODE);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___SYNCHRONIZE_FORWARD__TGGNODE_BOOLEAN);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___SYNCHRONIZE_BACKWARD__TGGNODE_BOOLEAN);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___REPAIR_FORWARD__TGGNODE_BOOLEAN);
		createEOperation(ecore2javaAttributeRule_r1EClass,
				ECORE2JAVA_ATTRIBUTE_RULE_R1___REPAIR_BACKWARD__TGGNODE_BOOLEAN);
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
		if (isInitialized)
			return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		SdmPackage theSdmPackage = (SdmPackage) EPackage.Registry.INSTANCE.getEPackage(SdmPackage.eNS_URI);
		OperationalTGGPackage theOperationalTGGPackage = (OperationalTGGPackage) EPackage.Registry.INSTANCE
				.getEPackage(OperationalTGGPackage.eNS_URI);
		MlsdmPackage theMlsdmPackage = (MlsdmPackage) EPackage.Registry.INSTANCE.getEPackage(MlsdmPackage.eNS_URI);
		Mote2Package theMote2Package = (Mote2Package) EPackage.Registry.INSTANCE.getEPackage(Mote2Package.eNS_URI);
		HelpersPackage theHelpersPackage = (HelpersPackage) EPackage.Registry.INSTANCE
				.getEPackage(HelpersPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		ecore2javaOperationalTGGEClass.getESuperTypes().add(theSdmPackage.getSdmOperationalTGG());
		ecore2javaAxiomEClass.getESuperTypes().add(theOperationalTGGPackage.getOperationalAxiomGroup());
		ecore2javaClassRuleEClass.getESuperTypes().add(theOperationalTGGPackage.getOperationalRuleGroup());
		ecore2javaAttributeRuleEClass.getESuperTypes().add(theOperationalTGGPackage.getOperationalRuleGroup());
		ecore2javaAxiom_r1EClass.getESuperTypes().add(theOperationalTGGPackage.getOperationalAxiom());
		ecore2javaClassRule_r1EClass.getESuperTypes().add(theOperationalTGGPackage.getOperationalRule());
		ecore2javaAttributeRule_r1EClass.getESuperTypes().add(theOperationalTGGPackage.getOperationalRule());

		// Initialize classes, features, and operations; add parameters
		initEClass(ecore2javaOperationalTGGEClass, ecore2javaOperationalTGG.class, "ecore2javaOperationalTGG",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(ecore2javaAxiomEClass, ecore2javaAxiom.class, "ecore2javaAxiom", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

		initEClass(ecore2javaClassRuleEClass, ecore2javaClassRule.class, "ecore2javaClassRule", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(ecore2javaAttributeRuleEClass, ecore2javaAttributeRule.class, "ecore2javaAttributeRule",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(ecore2javaAxiom_r1EClass, ecore2javaAxiom_r1.class, "ecore2javaAxiom_r1", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getecore2javaAxiom_r1_AddElementActivity(), theMlsdmPackage.getActivity(), null,
				"addElementActivity", null, 1, 1, ecore2javaAxiom_r1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAxiom_r1_MoveElementActivity(), theMlsdmPackage.getActivity(), null,
				"moveElementActivity", null, 1, 1, ecore2javaAxiom_r1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAxiom_r1_ChangeAttributeActivity(), theMlsdmPackage.getActivity(), null,
				"changeAttributeActivity", null, 1, 1, ecore2javaAxiom_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAxiom_r1_TransformForwardActivity(), theMlsdmPackage.getActivity(), null,
				"transformForwardActivity", null, 1, 1, ecore2javaAxiom_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAxiom_r1_TransformMappingActivity(), theMlsdmPackage.getActivity(), null,
				"transformMappingActivity", null, 1, 1, ecore2javaAxiom_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAxiom_r1_TransformBackwardActivity(), theMlsdmPackage.getActivity(), null,
				"transformBackwardActivity", null, 1, 1, ecore2javaAxiom_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAxiom_r1_SynchronizeForwardActivity(), theMlsdmPackage.getActivity(), null,
				"synchronizeForwardActivity", null, 1, 1, ecore2javaAxiom_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAxiom_r1_SynchronizeBackwardActivity(), theMlsdmPackage.getActivity(), null,
				"synchronizeBackwardActivity", null, 1, 1, ecore2javaAxiom_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = initEOperation(getecore2javaAxiom_r1__AddElement__EMap_EList_EList(),
				theMote2Package.getTGGNode(), "addElement", 1, 1, IS_UNIQUE, IS_ORDERED);
		EGenericType g1 = createEGenericType(theHelpersPackage.getMapEntry());
		EGenericType g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(ecorePackage.getEJavaObject());
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "parameters", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "leftInputElements", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "rightInputElements", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAxiom_r1__ChangeAttributeValues__TGGNode_EMap(), ecorePackage.getEBoolean(),
				"changeAttributeValues", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "correspondenceNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(theHelpersPackage.getMapEntry());
		g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(ecorePackage.getEJavaObject());
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "ruleParameters", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAxiom_r1__TransformForward__EList_EList_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "transformForward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "leftInputElements", 1, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "rightInputElements", 1, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAxiom_r1__TransformMapping__EList_EList_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "transformMapping", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "leftInputElements", 1, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "rightInputElements", 1, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAxiom_r1__TransformBackward__EList_EList_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "transformBackward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "leftInputElements", 1, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "rightInputElements", 1, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAxiom_r1__SynchronizeForward__EList_EList_TGGNode_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "synchronizeForward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "leftInputElements", 1, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "rightInputElements", 1, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAxiom_r1__SynchronizeBackward__EList_EList_TGGNode_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "synchronizeBackward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "leftInputElements", 1, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "rightInputElements", 1, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		initEClass(ecore2javaClassRule_r1EClass, ecore2javaClassRule_r1.class, "ecore2javaClassRule_r1", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getecore2javaClassRule_r1_AddElementActivity(), theMlsdmPackage.getActivity(), null,
				"addElementActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_MoveElementActivity(), theMlsdmPackage.getActivity(), null,
				"moveElementActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_ChangeAttributeActivity(), theMlsdmPackage.getActivity(), null,
				"changeAttributeActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_TransformForwardActivity(), theMlsdmPackage.getActivity(), null,
				"transformForwardActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_TransformMappingActivity(), theMlsdmPackage.getActivity(), null,
				"transformMappingActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_TransformBackwardActivity(), theMlsdmPackage.getActivity(), null,
				"transformBackwardActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_ConflictCheckForwardActivity(), theMlsdmPackage.getActivity(), null,
				"conflictCheckForwardActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_ConflictCheckMappingActivity(), theMlsdmPackage.getActivity(), null,
				"conflictCheckMappingActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_ConflictCheckBackwardActivity(), theMlsdmPackage.getActivity(), null,
				"conflictCheckBackwardActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_SynchronizeForwardActivity(), theMlsdmPackage.getActivity(), null,
				"synchronizeForwardActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_SynchronizeBackwardActivity(), theMlsdmPackage.getActivity(), null,
				"synchronizeBackwardActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_RepairForwardActivity(), theMlsdmPackage.getActivity(), null,
				"repairForwardActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaClassRule_r1_RepairBackwardActivity(), theMlsdmPackage.getActivity(), null,
				"repairBackwardActivity", null, 1, 1, ecore2javaClassRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = initEOperation(getecore2javaClassRule_r1__AddElement__EMap(), theMote2Package.getTGGNode(), "addElement",
				1, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(theHelpersPackage.getMapEntry());
		g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(ecorePackage.getEJavaObject());
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "parameters", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__ChangeAttributeValues__TGGNode_EMap(),
				ecorePackage.getEBoolean(), "changeAttributeValues", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "correspondenceNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(theHelpersPackage.getMapEntry());
		g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(ecorePackage.getEJavaObject());
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "ruleParameters", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__MoveElement__TGGNode_TGGNode_TGGNode(),
				ecorePackage.getEBoolean(), "moveElement", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "correspondenceNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "oldPreviousCorrespondenceNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "newPreviousCorrespondenceNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__TransformForward__TGGNode_boolean_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "transformForward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "considerAllLhsCorrNodes", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__TransformMapping__TGGNode_boolean_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "transformMapping", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "considerAllLhsCorrNodes", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__TransformBackward__TGGNode_boolean_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "transformBackward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "considerAllLhsCorrNodes", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__ConflictCheckForward__TGGNode(), ecorePackage.getEObject(),
				"conflictCheckForward", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__ConflictCheckMapping__TGGNode(), ecorePackage.getEObject(),
				"conflictCheckMapping", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__ConflictCheckBackward__TGGNode(), ecorePackage.getEObject(),
				"conflictCheckBackward", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__SynchronizeForward__TGGNode_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "synchronizeForward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__SynchronizeBackward__TGGNode_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "synchronizeBackward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__RepairForward__TGGNode_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "repairForward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaClassRule_r1__RepairBackward__TGGNode_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "repairBackward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		initEClass(ecore2javaAttributeRule_r1EClass, ecore2javaAttributeRule_r1.class, "ecore2javaAttributeRule_r1",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getecore2javaAttributeRule_r1_AddElementActivity(), theMlsdmPackage.getActivity(), null,
				"addElementActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_MoveElementActivity(), theMlsdmPackage.getActivity(), null,
				"moveElementActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_ChangeAttributeActivity(), theMlsdmPackage.getActivity(), null,
				"changeAttributeActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_TransformForwardActivity(), theMlsdmPackage.getActivity(), null,
				"transformForwardActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_TransformMappingActivity(), theMlsdmPackage.getActivity(), null,
				"transformMappingActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_TransformBackwardActivity(), theMlsdmPackage.getActivity(), null,
				"transformBackwardActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_ConflictCheckForwardActivity(), theMlsdmPackage.getActivity(),
				null, "conflictCheckForwardActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_ConflictCheckMappingActivity(), theMlsdmPackage.getActivity(),
				null, "conflictCheckMappingActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_ConflictCheckBackwardActivity(), theMlsdmPackage.getActivity(),
				null, "conflictCheckBackwardActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_SynchronizeForwardActivity(), theMlsdmPackage.getActivity(), null,
				"synchronizeForwardActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_SynchronizeBackwardActivity(), theMlsdmPackage.getActivity(), null,
				"synchronizeBackwardActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_RepairForwardActivity(), theMlsdmPackage.getActivity(), null,
				"repairForwardActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getecore2javaAttributeRule_r1_RepairBackwardActivity(), theMlsdmPackage.getActivity(), null,
				"repairBackwardActivity", null, 1, 1, ecore2javaAttributeRule_r1.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = initEOperation(getecore2javaAttributeRule_r1__AddElement__EMap(), theMote2Package.getTGGNode(),
				"addElement", 1, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(theHelpersPackage.getMapEntry());
		g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(ecorePackage.getEJavaObject());
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "parameters", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__ChangeAttributeValues__TGGNode_EMap(),
				ecorePackage.getEBoolean(), "changeAttributeValues", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "correspondenceNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(theHelpersPackage.getMapEntry());
		g2 = createEGenericType(ecorePackage.getEString());
		g1.getETypeArguments().add(g2);
		g2 = createEGenericType(ecorePackage.getEJavaObject());
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "ruleParameters", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__MoveElement__TGGNode_TGGNode_TGGNode(),
				ecorePackage.getEBoolean(), "moveElement", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "correspondenceNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "oldPreviousCorrespondenceNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "newPreviousCorrespondenceNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__TransformForward__TGGNode_boolean_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "transformForward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "considerAllLhsCorrNodes", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__TransformMapping__TGGNode_boolean_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "transformMapping", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "considerAllLhsCorrNodes", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__TransformBackward__TGGNode_boolean_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "transformBackward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "considerAllLhsCorrNodes", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__ConflictCheckForward__TGGNode(), ecorePackage.getEObject(),
				"conflictCheckForward", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__ConflictCheckMapping__TGGNode(), ecorePackage.getEObject(),
				"conflictCheckMapping", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__ConflictCheckBackward__TGGNode(), ecorePackage.getEObject(),
				"conflictCheckBackward", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__SynchronizeForward__TGGNode_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "synchronizeForward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__SynchronizeBackward__TGGNode_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "synchronizeBackward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__RepairForward__TGGNode_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "repairForward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());

		op = initEOperation(getecore2javaAttributeRule_r1__RepairBackward__TGGNode_boolean(),
				theOperationalTGGPackage.getErrorCodeEnum(), "repairBackward", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMote2Package.getTGGNode(), "inputTggNode", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEBoolean(), "checkAttributeFormulae", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theHelpersPackage.getTransformationException());
	}

} //GeneratedPackageImpl
