package de.mdelab.mlsdm.interpreter.incremental.rete.util;

public class Tuple<E1, E2> {

	public E1 e1;
	public E2 e2;
	
	public Tuple(E1 e1, E2 e2) {
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	public int hashCode() {
		return e1.hashCode() ^ e2.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		
		if(!(o instanceof Tuple)) {
			return false;
		}
		
		return ((Tuple<?, ?>) o).e1.equals(e1) && ((Tuple<?, ?>) o).e2.equals(e2);
	}
}
