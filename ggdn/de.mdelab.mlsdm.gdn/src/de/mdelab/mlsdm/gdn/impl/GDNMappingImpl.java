/**
 */
package de.mdelab.mlsdm.gdn.impl;

import de.mdelab.mlsdm.gdn.GDNMapping;
import de.mdelab.mlsdm.gdn.GdnPackage;

import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>GDN Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link de.mdelab.mlsdm.gdn.impl.GDNMappingImpl#getParentVertex <em>Parent Vertex</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.impl.GDNMappingImpl#getChildVertex <em>Child Vertex</em>}</li>
 *   <li>{@link de.mdelab.mlsdm.gdn.impl.GDNMappingImpl#getIndexPosition <em>Index Position</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GDNMappingImpl extends MinimalEObjectImpl.Container implements GDNMapping {
	/**
	 * The cached value of the '{@link #getParentVertex() <em>Parent Vertex</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParentVertex()
	 * @generated
	 * @ordered
	 */
	protected AbstractStoryPatternObject parentVertex;

	/**
	 * The cached value of the '{@link #getChildVertex() <em>Child Vertex</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChildVertex()
	 * @generated
	 * @ordered
	 */
	protected AbstractStoryPatternObject childVertex;

	/**
	 * The default value of the '{@link #getIndexPosition() <em>Index Position</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIndexPosition()
	 * @generated
	 * @ordered
	 */
	protected static final int INDEX_POSITION_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getIndexPosition() <em>Index Position</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIndexPosition()
	 * @generated
	 * @ordered
	 */
	protected int indexPosition = INDEX_POSITION_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GDNMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GdnPackage.Literals.GDN_MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AbstractStoryPatternObject getParentVertex() {
		if (parentVertex != null && parentVertex.eIsProxy()) {
			InternalEObject oldParentVertex = (InternalEObject)parentVertex;
			parentVertex = (AbstractStoryPatternObject)eResolveProxy(oldParentVertex);
			if (parentVertex != oldParentVertex) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GdnPackage.GDN_MAPPING__PARENT_VERTEX, oldParentVertex, parentVertex));
			}
		}
		return parentVertex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AbstractStoryPatternObject basicGetParentVertex() {
		return parentVertex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentVertex(AbstractStoryPatternObject newParentVertex) {
		AbstractStoryPatternObject oldParentVertex = parentVertex;
		parentVertex = newParentVertex;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GdnPackage.GDN_MAPPING__PARENT_VERTEX, oldParentVertex, parentVertex));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AbstractStoryPatternObject getChildVertex() {
		if (childVertex != null && childVertex.eIsProxy()) {
			InternalEObject oldChildVertex = (InternalEObject)childVertex;
			childVertex = (AbstractStoryPatternObject)eResolveProxy(oldChildVertex);
			if (childVertex != oldChildVertex) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GdnPackage.GDN_MAPPING__CHILD_VERTEX, oldChildVertex, childVertex));
			}
		}
		return childVertex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AbstractStoryPatternObject basicGetChildVertex() {
		return childVertex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setChildVertex(AbstractStoryPatternObject newChildVertex) {
		AbstractStoryPatternObject oldChildVertex = childVertex;
		childVertex = newChildVertex;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GdnPackage.GDN_MAPPING__CHILD_VERTEX, oldChildVertex, childVertex));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getIndexPosition() {
		return indexPosition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIndexPosition(int newIndexPosition) {
		int oldIndexPosition = indexPosition;
		indexPosition = newIndexPosition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GdnPackage.GDN_MAPPING__INDEX_POSITION, oldIndexPosition, indexPosition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GdnPackage.GDN_MAPPING__PARENT_VERTEX:
				if (resolve) return getParentVertex();
				return basicGetParentVertex();
			case GdnPackage.GDN_MAPPING__CHILD_VERTEX:
				if (resolve) return getChildVertex();
				return basicGetChildVertex();
			case GdnPackage.GDN_MAPPING__INDEX_POSITION:
				return getIndexPosition();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case GdnPackage.GDN_MAPPING__PARENT_VERTEX:
				setParentVertex((AbstractStoryPatternObject)newValue);
				return;
			case GdnPackage.GDN_MAPPING__CHILD_VERTEX:
				setChildVertex((AbstractStoryPatternObject)newValue);
				return;
			case GdnPackage.GDN_MAPPING__INDEX_POSITION:
				setIndexPosition((Integer)newValue);
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
			case GdnPackage.GDN_MAPPING__PARENT_VERTEX:
				setParentVertex((AbstractStoryPatternObject)null);
				return;
			case GdnPackage.GDN_MAPPING__CHILD_VERTEX:
				setChildVertex((AbstractStoryPatternObject)null);
				return;
			case GdnPackage.GDN_MAPPING__INDEX_POSITION:
				setIndexPosition(INDEX_POSITION_EDEFAULT);
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
			case GdnPackage.GDN_MAPPING__PARENT_VERTEX:
				return parentVertex != null;
			case GdnPackage.GDN_MAPPING__CHILD_VERTEX:
				return childVertex != null;
			case GdnPackage.GDN_MAPPING__INDEX_POSITION:
				return indexPosition != INDEX_POSITION_EDEFAULT;
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
		result.append(" (indexPosition: ");
		result.append(indexPosition);
		result.append(')');
		return result.toString();
	}

} //GDNMappingImpl
