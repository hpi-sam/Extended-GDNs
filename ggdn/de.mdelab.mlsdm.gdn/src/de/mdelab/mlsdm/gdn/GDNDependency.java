/**
 */
package de.mdelab.mlsdm.gdn;

import de.mdelab.mlexpressions.MLStringExpression;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>GDN Dependency</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.mdelab.mlsdm.gdn.GDNDependency#getNode <em>Node</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.GDNDependency#getIndexConstraint <em>Index Constraint</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.GDNDependency#getMappings <em>Mappings</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.GDNDependency#isNegative <em>Negative</em>}</li>
 * </ul>
 *
 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNDependency()
 * @model
 * @generated
 */
public interface GDNDependency extends EObject {
	/**
	 * Returns the value of the '<em><b>Node</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Node</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Node</em>' reference.
	 * @see #setNode(GDNNode)
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNDependency_Node()
	 * @model required="true"
	 * @generated
	 */
	GDNNode getNode();

	/**
	 * Sets the value of the '{@link de.mdelab.mlsdm.gdn.GDNDependency#getNode <em>Node</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Node</em>' reference.
	 * @see #getNode()
	 * @generated
	 */
	void setNode(GDNNode value);

	/**
	 * Returns the value of the '<em><b>Index Constraint</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Index Constraint</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Index Constraint</em>' reference.
	 * @see #setIndexConstraint(MLStringExpression)
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNDependency_IndexConstraint()
	 * @model required="true"
	 * @generated
	 */
	MLStringExpression getIndexConstraint();

	/**
	 * Sets the value of the '{@link de.mdelab.mlsdm.gdn.GDNDependency#getIndexConstraint <em>Index Constraint</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Index Constraint</em>' reference.
	 * @see #getIndexConstraint()
	 * @generated
	 */
	void setIndexConstraint(MLStringExpression value);

	/**
	 * Returns the value of the '<em><b>Mappings</b></em>' containment reference list.
	 * The list contents are of type {@link de.mdelab.mlsdm.gdn.GDNMapping}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mappings</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mappings</em>' containment reference list.
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNDependency_Mappings()
	 * @model containment="true"
	 * @generated
	 */
	EList<GDNMapping> getMappings();

	/**
	 * Returns the value of the '<em><b>Negative</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Negative</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Negative</em>' attribute.
	 * @see #setNegative(boolean)
	 * @see de.mdelab.mlsdm.gdn.GdnPackage#getGDNDependency_Negative()
	 * @model default="false" required="true"
	 * @generated
	 */
	boolean isNegative();

	/**
	 * Sets the value of the '{@link de.mdelab.mlsdm.gdn.GDNDependency#isNegative <em>Negative</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Negative</em>' attribute.
	 * @see #isNegative()
	 * @generated
	 */
	void setNegative(boolean value);

} // GDNDependency
