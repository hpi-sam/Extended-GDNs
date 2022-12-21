package de.mdelab.mlsdm.interpreter.incremental.change;

public interface SDMChangeEvent {

	public static enum SDMChangeEnum {CREATE, DELETE, MODIFY};
	
	public SDMChangeEnum getModifier();

	public Object getType();
	
}
