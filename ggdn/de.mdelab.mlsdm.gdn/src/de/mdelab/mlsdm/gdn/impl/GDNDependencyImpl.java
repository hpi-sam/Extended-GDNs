/**
 */
package de.mdelab.mlsdm.gdn.impl;

import de.mdelab.mlexpressions.MLStringExpression;

import de.mdelab.mlsdm.gdn.GDNDependency;
import de.mdelab.mlsdm.gdn.GDNMapping;
import de.mdelab.mlsdm.gdn.GDNNode;
import de.mdelab.mlsdm.gdn.GdnPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>GDN Dependency</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link de.mdelab.mlsdm.gdn.impl.GDNDependencyImpl#getNode <em>Node</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.impl.GDNDependencyImpl#getIndexConstraint <em>Index Constraint</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.impl.GDNDependencyImpl#getMappings <em>Mappings</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.impl.GDNDependencyImpl#isNegative <em>Negative</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GDNDependencyImpl extends MinimalEObjectImpl.Container implements GDNDependency {
	/**
	 * The cached value of the '{@link #getNode() <em>Node</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNode()
	 * @generated
	 * @ordered
	 */
	protected GDNNode node;

	/**
	 * The cached value of the '{@link #getIndexConstraint() <em>Index Constraint</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIndexConstraint()
	 * @generated
	 * @ordered
	 */
	protected MLStringExpression indexConstraint;

	/**
	 * The cached value of the '{@link #getMappings() <em>Mappings</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMappings()
	 * @generated
	 * @ordered
	 */
	protected EList<GDNMapping> mappings;

	/**
	 * The default value of the '{@link #isNegative() <em>Negative</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isNegative()
	 * @generated
	 * @ordered
	 */
	protected static final boolean NEGATIVE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isNegative() <em>Negative</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isNegative()
	 * @generated
	 * @ordered
	 */
	protected boolean negative = NEGATIVE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GDNDependencyImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GdnPackage.Literals.GDN_DEPENDENCY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GDNNode getNode() {
		if (node != null && node.eIsProxy()) {
			InternalEObject oldNode = (InternalEObject)node;
			node = (GDNNode)eResolveProxy(oldNode);
			if (node != oldNode) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GdnPackage.GDN_DEPENDENCY__NODE, oldNode, node));
			}
		}
		return node;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GDNNode basicGetNode() {
		return node;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNode(GDNNode newNode) {
		GDNNode oldNode = node;
		node = newNode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GdnPackage.GDN_DEPENDENCY__NODE, oldNode, node));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MLStringExpression getIndexConstraint() {
		if (indexConstraint != null && indexConstraint.eIsProxy()) {
			InternalEObject oldIndexConstraint = (InternalEObject)indexConstraint;
			indexConstraint = (MLStringExpression)eResolveProxy(oldIndexConstraint);
			if (indexConstraint != oldIndexConstraint) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GdnPackage.GDN_DEPENDENCY__INDEX_CONSTRAINT, oldIndexConstraint, indexConstraint));
			}
		}
		return indexConstraint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MLStringExpression basicGetIndexConstraint() {
		return indexConstraint;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIndexConstraint(MLStringExpression newIndexConstraint) {
		MLStringExpression oldIndexConstraint = indexConstraint;
		indexConstraint = newIndexConstraint;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GdnPackage.GDN_DEPENDENCY__INDEX_CONSTRAINT, oldIndexConstraint, indexConstraint));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<GDNMapping> getMappings() {
		if (mappings == null) {
			mappings = new EObjectContainmentEList<GDNMapping>(GDNMapping.class, this, GdnPackage.GDN_DEPENDENCY__MAPPINGS);
		}
		return mappings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isNegative() {
		return negative;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNegative(boolean newNegative) {
		boolean oldNegative = negative;
		negative = newNegative;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GdnPackage.GDN_DEPENDENCY__NEGATIVE, oldNegative, negative));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GdnPackage.GDN_DEPENDENCY__MAPPINGS:
				return ((InternalEList<?>)getMappings()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GdnPackage.GDN_DEPENDENCY__NODE:
				if (resolve) return getNode();
				return basicGetNode();
			case GdnPackage.GDN_DEPENDENCY__INDEX_CONSTRAINT:
				if (resolve) return getIndexConstraint();
				return basicGetIndexConstraint();
			case GdnPackage.GDN_DEPENDENCY__MAPPINGS:
				return getMappings();
			case GdnPackage.GDN_DEPENDENCY__NEGATIVE:
				return isNegative();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case GdnPackage.GDN_DEPENDENCY__NODE:
				setNode((GDNNode)newValue);
				return;
			case GdnPackage.GDN_DEPENDENCY__INDEX_CONSTRAINT:
				setIndexConstraint((MLStringExpression)newValue);
				return;
			case GdnPackage.GDN_DEPENDENCY__MAPPINGS:
				getMappings().clear();
				getMappings().addAll((Collection<? extends GDNMapping>)newValue);
				return;
			case GdnPackage.GDN_DEPENDENCY__NEGATIVE:
				setNegative((Boolean)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case GdnPackage.GDN_DEPENDENCY__NODE:
				setNode((GDNNode)null);
				return;
			case GdnPackage.GDN_DEPENDENCY__INDEX_CONSTRAINT:
				setIndexConstraint((MLStringExpression)null);
				return;
			case GdnPackage.GDN_DEPENDENCY__MAPPINGS:
				getMappings().clear();
				return;
			case GdnPackage.GDN_DEPENDENCY__NEGATIVE:
				setNegative(NEGATIVE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case GdnPackage.GDN_DEPENDENCY__NODE:
				return node != null;
			case GdnPackage.GDN_DEPENDENCY__INDEX_CONSTRAINT:
				return indexConstraint != null;
			case GdnPackage.GDN_DEPENDENCY__MAPPINGS:
				return mappings != null && !mappings.isEmpty();
			case GdnPackage.GDN_DEPENDENCY__NEGATIVE:
				return negative != NEGATIVE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (negative: ");
		result.append(negative);
		result.append(')');
		return result.toString();
	}

} //GDNDependencyImpl
