package de.mdelab.mlsdm.interpreter.incremental.rete.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

public class ReteMatch implements Map<String, EObject> {

	private ReteTuple mappings;
	private Map<String, Integer> indices;
	
	public ReteMatch(Map<String, Integer> indices) {
		this.indices = indices;
	}
	
	public Map<String, Integer> getIndices() {
		return indices;
	}
	
	public void setMappings(ReteTuple mappings) {
		this.mappings = mappings;
	}
	
	@Override
	public int size() {
		return indices.size();
	}

	@Override
	public boolean isEmpty() {
		return indices.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return indices.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return indices.containsValue(value);
	}

	@Override
	public EObject get(Object key) {
		Integer index = indices.get(key);
		return index != null ? (EObject) mappings.get(index) : null;
	}

	@Override
	public EObject put(String key, EObject value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EObject remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends EObject> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<EObject> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, EObject>> entrySet() {
		throw new UnsupportedOperationException();
	}

}
