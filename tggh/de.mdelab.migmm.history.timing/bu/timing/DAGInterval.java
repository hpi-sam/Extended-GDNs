/**
 */
package de.mdelab.migmm.history.timing;

import de.mdelab.migmm.history.Interval;

import java.util.Set;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>DAG Interval</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.mdelab.migmm.history.timing.DAGInterval#getCts <em>Cts</em>}</li>
 *   <li>{@link de.mdelab.migmm.history.timing.DAGInterval#getDts <em>Dts</em>}</li>
 * </ul>
 *
 * @see de.mdelab.migmm.history.timing.TimingPackage#getDAGInterval()
 * @model
 * @generated
 */
public interface DAGInterval extends Interval {
	/**
	 * Returns the value of the '<em><b>Cts</b></em>' reference list.
	 * The list contents are of type {@link de.mdelab.migmm.history.timing.DAGVersion}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Cts</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cts</em>' reference list.
	 * @see de.mdelab.migmm.history.timing.TimingPackage#getDAGInterval_Cts()
	 * @model
	 * @generated
	 */
	EList<DAGVersion> getCts();

	/**
	 * Returns the value of the '<em><b>Dts</b></em>' reference list.
	 * The list contents are of type {@link de.mdelab.migmm.history.timing.DAGVersion}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dts</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dts</em>' reference list.
	 * @see de.mdelab.migmm.history.timing.TimingPackage#getDAGInterval_Dts()
	 * @model
	 * @generated
	 */
	EList<DAGVersion> getDts();
	
	/**
	 * @generated not
	 */
	Set<DAGVersion> computeCoveredVersions();

} // DAGInterval
