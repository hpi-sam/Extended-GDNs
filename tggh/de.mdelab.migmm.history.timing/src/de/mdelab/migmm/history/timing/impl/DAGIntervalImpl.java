/**
 */
package de.mdelab.migmm.history.timing.impl;

import de.mdelab.migmm.history.History;
import de.mdelab.migmm.history.Interval;
import de.mdelab.migmm.history.TimingUnit;

import de.mdelab.migmm.history.timing.DAGInterval;
import de.mdelab.migmm.history.timing.DAGVersion;
import de.mdelab.migmm.history.timing.TimingFactory;
import de.mdelab.migmm.history.timing.TimingPackage;

import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>DAG Interval</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link de.mdelab.migmm.history.timing.impl.DAGIntervalImpl#getCts <em>Cts</em>}</li>
 *   <li>{@link de.mdelab.migmm.history.timing.impl.DAGIntervalImpl#getDts <em>Dts</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DAGIntervalImpl extends MinimalEObjectImpl.Container implements DAGInterval {
	/**
	 * The cached value of the '{@link #getCts() <em>Cts</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCts()
	 * @generated
	 * @ordered
	 */
	protected EList<DAGVersion> cts;

	/**
	 * The cached value of the '{@link #getDts() <em>Dts</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDts()
	 * @generated
	 * @ordered
	 */
	protected EList<DAGVersion> dts;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DAGIntervalImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TimingPackage.Literals.DAG_INTERVAL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<DAGVersion> getCts() {
		if (cts == null) {
			cts = new EObjectResolvingEList<DAGVersion>(DAGVersion.class, this, TimingPackage.DAG_INTERVAL__CTS);
		}
		return cts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<DAGVersion> getDts() {
		if (dts == null) {
			dts = new EObjectResolvingEList<DAGVersion>(DAGVersion.class, this, TimingPackage.DAG_INTERVAL__DTS);
		}
		return dts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 */
	public Interval intersect(Interval i) {
		if(!(i.eClass() == TimingPackage.Literals.DAG_INTERVAL)) {
			return null;
		}
		
		DAGInterval other = (DAGInterval) i;
		
		Set<DAGVersion> covered = computeCoveredVersions();
		covered.retainAll(other.computeCoveredVersions());

		return createEncoding(covered);
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 */
	public Interval minus(Interval i) {
		if(!(i.eClass() == TimingPackage.Literals.DAG_INTERVAL)) {
			return null;
		}
		
		DAGInterval other = (DAGInterval) i;
		
		Set<DAGVersion> covered = computeCoveredVersions();
		covered.removeAll(other.computeCoveredVersions());

		return createEncoding(covered);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 */
	public Interval union(Interval i) {
		if(!(i.eClass() == TimingPackage.Literals.DAG_INTERVAL)) {
			return null;
		}
		
		DAGInterval other = (DAGInterval) i;
		
		Set<DAGVersion> covered = computeCoveredVersions();
		covered.addAll(other.computeCoveredVersions());

		return createEncoding(covered);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 */
	public boolean contains(TimingUnit t) {
		return computeCoveredVersions().contains(t);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 */
	public boolean isEmpty() {
		return computeCoveredVersions().isEmpty();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 */
	@Override
	public EList<?> getPresentTiming() {
		return new BasicEList<DAGVersion>(computeCoveredVersions());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 */
	@Override
	public EList<?> getDeletedTiming() {
		Set<DAGVersion> deletedVersions = new LinkedHashSet<DAGVersion>();
		deletedVersions.addAll(getDts());
		Queue<DAGVersion> queue = new LinkedList<DAGVersion>();
		queue.addAll(getDts());
		
		while(!queue.isEmpty()) {
			DAGVersion current = queue.poll();
			for(DAGVersion successor:current.getSuccessors()) {
				if(!deletedVersions.contains(successor) && !getCts().contains(successor)) {
					deletedVersions.add(successor);
					queue.add(successor);
				}
			}
		}
		
		return new BasicEList<DAGVersion>(deletedVersions);		
//		Set<DAGVersion> coveredVersions = computeCoveredVersions();
//		
//		Set<DAGVersion> deletedVersions = computeReachableVersions();
//		deletedVersions.removeAll(coveredVersions);
//		
//		return new BasicEList<DAGVersion>(deletedVersions);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EList<?> getAbsentTiming() {
		Set<DAGVersion> reachableVersions = computeReachableVersions();
		
		Set<DAGVersion> absentVersions = new LinkedHashSet<DAGVersion>((Collection<? extends DAGVersion>) ((History) getCts().get(0).eContainer()).getOwnedTimingUnits());
		absentVersions.removeAll(reachableVersions);
		
		return new BasicEList<DAGVersion>(absentVersions);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 * 
	 */
	public Set<DAGVersion> computeCoveredVersions() {		
		Set<DAGVersion> coveredVersions = new LinkedHashSet<DAGVersion>();
		coveredVersions.addAll(getCts());
		Queue<DAGVersion> queue = new LinkedList<DAGVersion>();
		queue.addAll(getCts());
		
		while(!queue.isEmpty()) {
			DAGVersion current = queue.poll();
			for(DAGVersion successor:current.getSuccessors()) {
				if(!coveredVersions.contains(successor) && !getDts().contains(successor)) {
					coveredVersions.add(successor);
					queue.add(successor);
				}
			}
		}
		
		return coveredVersions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 * 
	 */
	private Set<DAGVersion> computeReachableVersions() {
		Set<DAGVersion> reachableVersions = new LinkedHashSet<DAGVersion>();
		reachableVersions.addAll(getCts());
		Queue<DAGVersion> queue = new LinkedList<DAGVersion>();
		queue.addAll(getCts());
		
		while(!queue.isEmpty()) {
			DAGVersion current = queue.poll();
			for(DAGVersion successor:current.getSuccessors()) {
				if(!reachableVersions.contains(successor)) {
					reachableVersions.add(successor);
					queue.add(successor);
				}
			}
		}
		return reachableVersions;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TimingPackage.DAG_INTERVAL__CTS:
				return getCts();
			case TimingPackage.DAG_INTERVAL__DTS:
				return getDts();
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
			case TimingPackage.DAG_INTERVAL__CTS:
				getCts().clear();
				getCts().addAll((Collection<? extends DAGVersion>)newValue);
				return;
			case TimingPackage.DAG_INTERVAL__DTS:
				getDts().clear();
				getDts().addAll((Collection<? extends DAGVersion>)newValue);
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
			case TimingPackage.DAG_INTERVAL__CTS:
				getCts().clear();
				return;
			case TimingPackage.DAG_INTERVAL__DTS:
				getDts().clear();
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
			case TimingPackage.DAG_INTERVAL__CTS:
				return cts != null && !cts.isEmpty();
			case TimingPackage.DAG_INTERVAL__DTS:
				return dts != null && !dts.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case TimingPackage.DAG_INTERVAL___INTERSECT__INTERVAL:
				return intersect((Interval)arguments.get(0));
			case TimingPackage.DAG_INTERVAL___MINUS__INTERVAL:
				return minus((Interval)arguments.get(0));
			case TimingPackage.DAG_INTERVAL___UNION__INTERVAL:
				return union((Interval)arguments.get(0));
			case TimingPackage.DAG_INTERVAL___CONTAINS__TIMINGUNIT:
				return contains((TimingUnit)arguments.get(0));
			case TimingPackage.DAG_INTERVAL___IS_EMPTY:
				return isEmpty();
			case TimingPackage.DAG_INTERVAL___GET_PRESENT_TIMING:
				return getPresentTiming();
			case TimingPackage.DAG_INTERVAL___GET_DELETED_TIMING:
				return getDeletedTiming();
			case TimingPackage.DAG_INTERVAL___GET_ABSENT_TIMING:
				return getAbsentTiming();
		}
		return super.eInvoke(operationID, arguments);
	}

	public static DAGInterval createEncoding(Set<DAGVersion> timepoints) {
		Set<DAGVersion> cts = new LinkedHashSet<DAGVersion>();
		Set<DAGVersion> dts = new LinkedHashSet<DAGVersion>();
		
		for(DAGVersion version:timepoints) {
			boolean hasPredecessor = false;
			for(DAGVersion predecessor:version.getPredecessors()) {
				if(timepoints.contains(predecessor)) {
					hasPredecessor = true;
					break;
				}
			}
			if(!hasPredecessor) {
				cts.add(version);
			}
			
			for(DAGVersion successor:version.getSuccessors()) {
				if(!timepoints.contains(successor)) {
					dts.add(successor);
				}
			}
		}
		
		return create(cts, dts);
	}
	
	public static DAGInterval create(Collection<DAGVersion> cts, Collection<DAGVersion> dts) {
		DAGInterval i = TimingFactory.eINSTANCE.createDAGInterval();
		i.getCts().addAll(cts);
		i.getDts().addAll(dts);
		return i;
	}

} //DAGIntervalImpl
