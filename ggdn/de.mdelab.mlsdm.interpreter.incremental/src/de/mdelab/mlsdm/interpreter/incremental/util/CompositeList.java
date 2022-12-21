package de.mdelab.mlsdm.interpreter.incremental.util;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;

public class CompositeList<E> extends AbstractList<E> implements EList<E> {

	private List<E> left;
	private List<E> right;

	public CompositeList(List<E> left, List<E> right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public int size() {
		return left.size() + right.size();
	}

	@Override
	public boolean isEmpty() {
		return left.isEmpty() && right.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return left.contains(o) || right.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			
			boolean onRight = false;
			Iterator<E> it = left.iterator();
			
			@Override
			public boolean hasNext() {
				if(!it.hasNext() && !onRight) {
					it = right.iterator();
					onRight = true;
				}
				return it.hasNext();
			}
			@Override
			public E next() {
				if(!it.hasNext() && !onRight) {
					it = right.iterator();
					onRight = true;
				}
				return it.next();
			}
			
		};
	}

	@Override
	public E get(int index) {
		return left.size() > index ? left.get(index) : right.get(index - left.size());
	}

	@Override
	public void move(int newPosition, E object) {
		//this is only a fake EList
		throw new UnsupportedOperationException();
	}

	@Override
	public E move(int newPosition, int oldPosition) {
		//this is only a fake EList
		throw new UnsupportedOperationException();
	}

}
