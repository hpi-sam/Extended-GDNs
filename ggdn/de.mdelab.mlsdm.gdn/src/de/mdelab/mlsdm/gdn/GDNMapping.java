/**
 */
package de.mdelab.mlsdm.gdn;

import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>GDN Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.mdelab.mlsdm.gdn.GDNMapping#getParentVertex <em>Parent Vertex</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.GDNMapping#getChildVertex <em>Child Vertex</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.GDNMapping#getIndexPosition <em>Index Position</em>}</li>
 * </ul>
 *
 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNMapping()
 * @model
 * @generated
 */
public interface GDNMapping extends EObject {
	/**
	 * Returns the value of the '<em><b>Parent Vertex</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Vertex</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Vertex</em>' reference.
	 * @see #setParentVertex(AbstractStoryPatternObject)
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNMapping_ParentVertex()
	 * @model required="true"
	 * @generated
	 */
	AbstractStoryPatternObject getParentVertex();

	/**
	 * Sets the value of the '{@link de.mdelab.mlsdm.gdn.GDNMapping#getParentVertex <em>Parent Vertex</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Vertex</em>' reference.
	 * @see #getParentVertex()
	 * @generated
	 */
	void setParentVertex(AbstractStoryPatternObject value);

	/**
	 * Returns the value of the '<em><b>Child Vertex</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Child Vertex</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Child Vertex</em>' reference.
	 * @see #setChildVertex(AbstractStoryPatternObject)
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNMapping_ChildVertex()
	 * @model
	 * @generated
	 */
	AbstractStoryPatternObject getChildVertex();

	/**
	 * Sets the value of the '{@link de.mdelab.mlsdm.gdn.GDNMapping#getChildVertex <em>Child Vertex</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Child Vertex</em>' reference.
	 * @see #getChildVertex()
	 * @generated
	 */
	void setChildVertex(AbstractStoryPatternObject value);

	/**
	 * Returns the value of the '<em><b>Index Position</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Index Position</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Index Position</em>' attribute.
	 * @see #setIndexPosition(int)
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNMapping_IndexPosition()
	 * @model required="true"
	 * @generated
	 */
	int getIndexPosition();

	/**
	 * Sets the value of the '{@link de.mdelab.mlsdm.gdn.GDNMapping#getIndexPosition <em>Index Position</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Index Position</em>' attribute.
	 * @see #getIndexPosition()
	 * @generated
	 */
	void setIndexPosition(int value);

} // GDNMapping
