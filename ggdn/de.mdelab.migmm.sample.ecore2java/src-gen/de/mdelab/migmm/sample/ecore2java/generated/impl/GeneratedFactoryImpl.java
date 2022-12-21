/**
 */
package de.mdelab.migmm.sample.ecore2java.generated.impl;

import de.mdelab.migmm.sample.ecore2java.generated.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class GeneratedFactoryImpl extends EFactoryImpl implements GeneratedFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static GeneratedFactory init() {
		try {
			GeneratedFactory theGeneratedFactory = (GeneratedFactory) EPackage.Registry.INSTANCE
					.getEFactory(GeneratedPackage.eNS_URI);
			if (theGeneratedFactory != null) {
				return theGeneratedFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new GeneratedFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GeneratedFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case GeneratedPackage.ECORE2JAVA_OPERATIONAL_TGG:
			return createecore2javaOperationalTGG();
		case GeneratedPackage.ECORE2JAVA_AXIOM:
			return createecore2javaAxiom();
		case GeneratedPackage.ECORE2JAVA_CLASS_RULE:
			return createecore2javaClassRule();
		case GeneratedPackage.ECORE2JAVA_ATTRIBUTE_RULE:
			return createecore2javaAttributeRule();
		case GeneratedPackage.ECORE2JAVA_AXIOM_R1:
			return createecore2javaAxiom_r1();
		case GeneratedPackage.ECORE2JAVA_CLASS_RULE_R1:
			return createecore2javaClassRule_r1();
		case GeneratedPackage.ECORE2JAVA_ATTRIBUTE_RULE_R1:
			return createecore2javaAttributeRule_r1();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ecore2javaOperationalTGG createecore2javaOperationalTGG() {
		ecore2javaOperationalTGGImpl ecore2javaOperationalTGG = new ecore2javaOperationalTGGImpl();
		return ecore2javaOperationalTGG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ecore2javaAxiom createecore2javaAxiom() {
		ecore2javaAxiomImpl ecore2javaAxiom = new ecore2javaAxiomImpl();
		return ecore2javaAxiom;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ecore2javaClassRule createecore2javaClassRule() {
		ecore2javaClassRuleImpl ecore2javaClassRule = new ecore2javaClassRuleImpl();
		return ecore2javaClassRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ecore2javaAttributeRule createecore2javaAttributeRule() {
		ecore2javaAttributeRuleImpl ecore2javaAttributeRule = new ecore2javaAttributeRuleImpl();
		return ecore2javaAttributeRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ecore2javaAxiom_r1 createecore2javaAxiom_r1() {
		ecore2javaAxiom_r1Impl ecore2javaAxiom_r1 = new ecore2javaAxiom_r1Impl();
		return ecore2javaAxiom_r1;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ecore2javaClassRule_r1 createecore2javaClassRule_r1() {
		ecore2javaClassRule_r1Impl ecore2javaClassRule_r1 = new ecore2javaClassRule_r1Impl();
		return ecore2javaClassRule_r1;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ecore2javaAttributeRule_r1 createecore2javaAttributeRule_r1() {
		ecore2javaAttributeRule_r1Impl ecore2javaAttributeRule_r1 = new ecore2javaAttributeRule_r1Impl();
		return ecore2javaAttributeRule_r1;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GeneratedPackage getGeneratedPackage() {
		return (GeneratedPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static GeneratedPackage getPackage() {
		return GeneratedPackage.eINSTANCE;
	}

} //GeneratedFactoryImpl
