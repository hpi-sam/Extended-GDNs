/**
 */
package de.mdelab.migmm.sample.ecore2java.impl;

import de.mdelab.migmm.sample.ecore2java.*;

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
public class Ecore2javaFactoryImpl extends EFactoryImpl implements Ecore2javaFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Ecore2javaFactory init() {
		try {
			Ecore2javaFactory theEcore2javaFactory = (Ecore2javaFactory) EPackage.Registry.INSTANCE
					.getEFactory(Ecore2javaPackage.eNS_URI);
			if (theEcore2javaFactory != null) {
				return theEcore2javaFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new Ecore2javaFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Ecore2javaFactoryImpl() {
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
		case Ecore2javaPackage.CORR_AXIOM:
			return createCorrAxiom();
		case Ecore2javaPackage.CORR_CLASS:
			return createCorrClass();
		case Ecore2javaPackage.CORR_ATTRIBUTE:
			return createCorrAttribute();
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
	public CorrAxiom createCorrAxiom() {
		CorrAxiomImpl corrAxiom = new CorrAxiomImpl();
		return corrAxiom;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CorrClass createCorrClass() {
		CorrClassImpl corrClass = new CorrClassImpl();
		return corrClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CorrAttribute createCorrAttribute() {
		CorrAttributeImpl corrAttribute = new CorrAttributeImpl();
		return corrAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Ecore2javaPackage getEcore2javaPackage() {
		return (Ecore2javaPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static Ecore2javaPackage getPackage() {
		return Ecore2javaPackage.eINSTANCE;
	}

} //Ecore2javaFactoryImpl
