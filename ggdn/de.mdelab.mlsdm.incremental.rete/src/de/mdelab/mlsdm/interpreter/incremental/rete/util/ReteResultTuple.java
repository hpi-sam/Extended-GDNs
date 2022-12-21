package de.mdelab.mlsdm.interpreter.incremental.rete.util;

import java.util.List;

public class ReteResultTuple extends ReteTuple {

	private int[] mask;
	
	public ReteResultTuple(List<Object> tupleValue, int[] mask) {
		super(tupleValue);
		this.mask = mask;
	}

	@Override
	public Object get(int index) {
		return tupleValue.get(mask[index]);
	}

}
