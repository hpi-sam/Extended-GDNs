/**
 */
package de.mdelab.mlsdm.gdn;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>GDN</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.mdelab.mlsdm.gdn.GDN#getOwnedNodes <em>Owned Nodes</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.GDN#getRoot <em>Root</em>}</li>
 * </ul>
 *
 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDN()
 * @model
 * @generated
 */
public interface GDN extends EObject {
	/**
	 * Returns the value of the '<em><b>Owned Nodes</b></em>' containment reference list.
	 * The list contents are of type {@link de.mdelab.mlsdm.gdn.GDNNode}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Owned Nodes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Owned Nodes</em>' containment reference list.
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDN_OwnedNodes()
	 * @model containment="true"
	 * @generated
	 */
	EList<GDNNode> getOwnedNodes();

	/**
	 * Returns the value of the '<em><b>Root</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Root</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Root</em>' reference.
	 * @see #setRoot(GDNNode)
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDN_Root()
	 * @model required="true"
	 * @generated
	 */
	GDNNode getRoot();

	/**
	 * Sets the value of the '{@link de.mdelab.mlsdm.gdn.GDN#getRoot <em>Root</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Root</em>' reference.
	 * @see #getRoot()
	 * @generated
	 */
	void setRoot(GDNNode value);

} // GDN
