package de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNodeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;

public class ReteNodeInputExecutor extends ReteNodeExecutor {

	private ReteNodeInput node;
	private Iterator<EObject> iterator;

	public ReteNodeInputExecutor(ReteNodeInput node,
			Iterator<EObject> iterator) {
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
		ReteTuple tuple = new ReteTuple(Collections.singletonList(iterator.next()));
		node.propagateAddition(tuple);
	}

}
