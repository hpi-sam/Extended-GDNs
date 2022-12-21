package de.mdelab.mlsdm.interpreter.incremental;

public interface QueryManagerNotificationReceiver {

	public void notifyStartUpdate();

	public void notifyEndUpdate();
	
	public void notifyStartRecompute();

	public void notifyEndRecompute();

	public void notifyCustom(Object... args);

	public void notifyStartRepopulate();
	
	public void notifyEndRepopulate();
	
}
