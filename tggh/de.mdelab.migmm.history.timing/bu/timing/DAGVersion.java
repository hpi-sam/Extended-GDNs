/**
 */
package de.mdelab.migmm.history.timing;

import de.mdelab.migmm.history.TimingUnit;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>DAG Version</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link de.mdelab.migmm.history.timing.DAGVersion#getSuccessors <em>Successors</em>}</li>
 *   <li>{@link de.mdelab.migmm.history.timing.DAGVersion#getPredecessors <em>Predecessors</em>}</li>
 * </ul>
 *
 * @see de.mdelab.migmm.history.timing.TimingPackage#getDAGVersion()
 * @model
 * @generated
 */
public interface DAGVersion extends TimingUnit {
	/**
	 * Returns the value of the '<em><b>Successors</b></em>' reference list.
	 * The list contents are of type {@link de.mdelab.migmm.history.timing.DAGVersion}.
	 * It is bidirectional and its opposite is '{@link de.mdelab.migmm.history.timing.DAGVersion#getPredecessors <em>Predecessors</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Successors</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Successors</em>' reference list.
	 * @see de.mdelab.migmm.history.timing.TimingPackage#getDAGVersion_Successors()
	 * @see de.mdelab.migmm.history.timing.DAGVersion#getPredecessors
	 * @model opposite="predecessors"
	 * @generated
	 */
	EList<DAGVersion> getSuccessors();

	/**
	 * Returns the value of the '<em><b>Predecessors</b></em>' reference list.
	 * The list contents are of type {@link de.mdelab.migmm.history.timing.DAGVersion}.
	 * It is bidirectional and its opposite is '{@link de.mdelab.migmm.history.timing.DAGVersion#getSuccessors <em>Successors</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Predecessors</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Predecessors</em>' reference list.
	 * @see de.mdelab.migmm.history.timing.TimingPackage#getDAGVersion_Predecessors()
	 * @see de.mdelab.migmm.history.timing.DAGVersion#getSuccessors
	 * @model opposite="successors"
	 * @generated
	 */
	EList<DAGVersion> getPredecessors();

} // DAGVersion
