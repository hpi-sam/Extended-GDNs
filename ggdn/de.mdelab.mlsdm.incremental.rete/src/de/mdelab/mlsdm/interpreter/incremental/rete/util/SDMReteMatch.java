package de.mdelab.mlsdm.interpreter.incremental.rete.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;

public class SDMReteMatch implements Map<AbstractStoryPatternObject, Object> {

	private final Map<AbstractStoryPatternObject, Object> match;
	private final boolean isPreliminary;
	
	public SDMReteMatch(Map<AbstractStoryPatternObject, Object> match, boolean isPreliminary) {
		this.match = match;
		this.isPreliminary = isPreliminary;
	}

	@Override
	public void clear() {
		match.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return match.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return match.containsValue(value);
	}

	@Override
	public Set<Entry<AbstractStoryPatternObject, Object>> entrySet() {
		return match.entrySet();
	}

	@Override
	public Object get(Object key) {
		return match.get(key);
	}

	@Override
	public boolean isEmpty() {
		return match.isEmpty();
	}

	@Override
	public Set<AbstractStoryPatternObject> keySet() {
		return match.keySet();
	}

	@Override
	public Object put(AbstractStoryPatternObject key, Object value) {
		return match.put(key, value);
	}

	@Override
	public void putAll(Map<? extends AbstractStoryPatternObject, ? extends Object> m) {
		match.putAll(m);
	}

	@Override
	public Object remove(Object key) {
		return match.remove(key);
	}

	@Override
	public int size() {
		return match.size();
	}

	@Override
	public Collection<Object> values() {
		return match.values();
	}

	public boolean isPreliminary() {
		return isPreliminary;
	}

}
