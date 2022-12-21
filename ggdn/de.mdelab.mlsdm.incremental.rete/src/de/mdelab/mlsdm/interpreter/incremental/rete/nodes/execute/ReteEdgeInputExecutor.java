package de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteEdgeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Tuple;

public class ReteEdgeInputExecutor extends ReteNodeExecutor {

	private ReteEdgeInput node;
	private Iterator<Tuple<EObject, EObject>> iterator;

	public ReteEdgeInputExecutor(ReteEdgeInput node,
			Iterator<Tuple<EObject, EObject>> iterator) {
		this.node = node;
		this.iterator = iterator;
	}

	@Override
	public boolean hasNextTuple() {
		return iterator.hasNext();
	}

	@Override
	public void generateNextTuple() {
		if(!hasNextTuple()) {
            throw new NoSuchElementException();
		}
		Tuple<EObject, EObject> reference = iterator.next();
		List<Object> tuple = new ArrayList<Object>(2);
		tuple.add(reference.e1);
		tuple.add(reference.e2);
		node.propagateAddition(new ReteTuple(tuple));
	}

}
