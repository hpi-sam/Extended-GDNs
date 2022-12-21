/**
 */
package de.mdelab.mlsdm.gdn.impl;

import de.mdelab.mlsdm.gdn.*;

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
public class GdnFactoryImpl extends EFactoryImpl implements GdnFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static GdnFactory init() {
		try {
			GdnFactory theGdnFactory = (GdnFactory)EPackage.Registry.INSTANCE.getEFactory(GdnPackage.eNS_URI);
			if (theGdnFactory != null) {
				return theGdnFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new GdnFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GdnFactoryImpl() {
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
			case GdnPackage.GDN_NODE: return createGDNNode();
			case GdnPackage.GDN_DEPENDENCY: return createGDNDependency();
			case GdnPackage.GDN_MAPPING: return createGDNMapping();
			case GdnPackage.GDN: return createGDN();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GDNNode createGDNNode() {
		GDNNodeImpl gdnNode = new GDNNodeImpl();
		return gdnNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GDNDependency createGDNDependency() {
		GDNDependencyImpl gdnDependency = new GDNDependencyImpl();
		return gdnDependency;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GDNMapping createGDNMapping() {
		GDNMappingImpl gdnMapping = new GDNMappingImpl();
		return gdnMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GDN createGDN() {
		GDNImpl gdn = new GDNImpl();
		return gdn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GdnPackage getGdnPackage() {
		return (GdnPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static GdnPackage getPackage() {
		return GdnPackage.eINSTANCE;
	}

} //GdnFactoryImpl
