package de.mdelab.mlsdm.interpreter.incremental;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.mdelab.mlsdm.mlindices.IndexEntry;

public class SDMQueryMatch implements Map<String, Object> {

	private IndexEntry indexEntry;
	private Map<String, Integer> parameterToIndex;

	public SDMQueryMatch(IndexEntry indexEntry,
			Map<String, Integer> parameterToIndex) {
		this.indexEntry = indexEntry;
		this.parameterToIndex = parameterToIndex;
	}

	public IndexEntry getIndexEntry() {
		return indexEntry;
	}
	
	@Override
	public Object get(Object key) {
		Integer index = parameterToIndex.get(key);
		if(index == null) {
			return null;
		}
		return indexEntry.getKey().get(index);
	}

	@Override
	public int size() {
		return indexEntry.getKey().size();
	}

	@Override
	public boolean isEmpty() {
		return indexEntry.getKey().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
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
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

}
