/**
 */
package de.mdelab.mlsdm.gdn;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see de.mdelab.mlsdm.gdn.GdnPackage
 * @generated
 */
public interface GdnFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GdnFactory eINSTANCE = de.mdelab.mlsdm.gdn.impl.GdnFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>GDN Node</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>GDN Node</em>'.
	 * @generated
	 */
	GDNNode createGDNNode();

	/**
	 * Returns a new object of class '<em>GDN Dependency</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>GDN Dependency</em>'.
	 * @generated
	 */
	GDNDependency createGDNDependency();

	/**
	 * Returns a new object of class '<em>GDN Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>GDN Mapping</em>'.
	 * @generated
	 */
	GDNMapping createGDNMapping();

	/**
	 * Returns a new object of class '<em>GDN</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>GDN</em>'.
	 * @generated
	 */
	GDN createGDN();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	GdnPackage getGdnPackage();

} //GdnFactory
