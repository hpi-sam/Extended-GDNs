/**
 */
package de.mdelab.mlsdm.gdn;

import de.mdelab.mlstorypatterns.StoryPattern;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>GDN Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.mdelab.mlsdm.gdn.GDNNode#getDependencies <em>Dependencies</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.GDNNode#getPattern <em>Pattern</em>}</li>
 * </ul>
 *
 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNNode()
 * @model
 * @generated
 */
public interface GDNNode extends EObject {
	/**
	 * Returns the value of the '<em><b>Dependencies</b></em>' containment reference list.
	 * The list contents are of type {@link de.mdelab.mlsdm.gdn.GDNDependency}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dependencies</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dependencies</em>' containment reference list.
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNNode_Dependencies()
	 * @model containment="true"
	 * @generated
	 */
	EList<GDNDependency> getDependencies();

	/**
	 * Returns the value of the '<em><b>Pattern</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Pattern</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pattern</em>' containment reference.
	 * @see #setPattern(StoryPattern)
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNNode_Pattern()
	 * @model containment="true" required="true"
	 * @generated
	 */
	StoryPattern getPattern();

	/**
	 * Sets the value of the '{@link de.mdelab.mlsdm.gdn.GDNNode#getPattern <em>Pattern</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pattern</em>' containment reference.
	 * @see #getPattern()
	 * @generated
	 */
	void setPattern(StoryPattern value);

} // GDNNode
