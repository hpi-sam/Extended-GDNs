package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

public class MLSDMMatch implements Map<String, EObject>, Cloneable {

	private EObject[] mappings;
	private Map<String, Integer> ids;
	
	public MLSDMMatch() {
		
	}
	
	public MLSDMMatch(MLSDMLightweightSM sm) {
		mappings = new EObject[sm.getPatternNodes().size()];
		ids = new HashMap<String, Integer>();
		for(MLSDMLightweightPN pn:sm.getPatternNodes()) {
			ids.put(pn.getName(), pn.getId());
		}
	}

	public MLSDMMatch(EObject[] mappings, Map<String, Integer> ids) {
		this.mappings = mappings;
		this.ids = ids;
	}
	
	public void setMapping(int id, EObject mapping) {
		mappings[id] = mapping;
	}
	
	@Override
	public MLSDMMatch clone() {
		EObject[] cloneMappings = new EObject[mappings.length];
		System.arraycopy(mappings, 0, cloneMappings, 0, mappings.length);
		MLSDMMatch clone = new MLSDMMatch(cloneMappings, ids);
		return clone;
	}

	@Override
	public int size() {
		return ids.size();
	}

	@Override
	public boolean isEmpty() {
		return ids.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return ids.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		for(EObject mapping:mappings) {
			if(mapping.equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public EObject get(Object key) {
		if(!ids.containsKey(key)) {
			return null;
		}
		return mappings[ids.get(key)];
	}

	@Override
	public EObject put(String key, EObject value) {
		if(!ids.containsKey(key)) {
			throw new UnsupportedOperationException("No such query vertex: " + key);
		}
		EObject previous = get(key);
		setMapping(ids.get(key), value);
		return previous;
	}

	@Override
	public EObject remove(Object key) {
		throw new UnsupportedOperationException("Cannot remove from a match");
	}

	@Override
	public void putAll(Map<? extends String, ? extends EObject> m) {
		for(Entry<? extends String, ? extends EObject> entry:m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Cannot clear a match");
	}

	@Override
	public Set<String> keySet() {
		return ids.keySet();
	}

	@Override
	public Collection<EObject> values() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Set<Entry<String, EObject>> entrySet() {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
