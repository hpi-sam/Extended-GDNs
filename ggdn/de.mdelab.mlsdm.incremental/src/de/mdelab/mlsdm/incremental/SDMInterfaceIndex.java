package de.mdelab.mlsdm.incremental;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import de.mdelab.mlsdm.mlindices.IndexAccessType;
import de.mdelab.mlsdm.mlindices.IndexChangeNotification;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.mlsdm.mlindices.IndexNotificationReceiver;
import de.mdelab.mlsdm.mlindices.NotifyingIndex;

public class SDMInterfaceIndex implements NotifyingIndex, IndexNotificationReceiver {

	private NotifyingIndex index;
	protected Map<String, Integer> parameterPositions;
	private Collection<IndexNotificationReceiver> receivers;
	private int interfaceSize;
	
	public SDMInterfaceIndex(NotifyingIndex index, Map<String, Integer> parameterPositions, int interfaceSize) {
		this.receivers = new ArrayList<IndexNotificationReceiver>();
		this.parameterPositions = parameterPositions;
		this.interfaceSize = interfaceSize;
		this.index = index;
		this.index.registerNotificationReceiver(this);
	}


	public NotifyingIndex getWrappedIndex() {
		return index;
	}
	
	public IndexEntry addEntry(SDMQueryMatch match) {
		List<Object> entryList = getEntryList(match);
		return index.addEntry(entryList);
	}
	
	public void removeEntry(SDMQueryMatch match) {
		List<Object> entryList = getEntryList(match);
		index.remove(entryList);
	}
	
	protected List<Object> getEntryList(SDMQueryMatch match) {
		Object[] entry = new Object[parameterPositions.size() + 1];
		for(Entry<String, Integer> parameter:parameterPositions.entrySet()) {
			entry[parameter.getValue()] = match.get(parameter.getKey());
		}
		entry[parameterPositions.size()] = match.getIndexEntry();
		List<Object> entryList = Arrays.asList(entry);
		return entryList;
	}

	@Override
	public IndexAccessType getAccessType() {
		return index.getAccessType();
	}

	@Override
	public long size() {
		return index.size();
	}

	@Override
	public Iterator<IndexEntry> getEntries(List<Object> query) {
		return index.getEntries(query);
	}

	@Override
	public int estimateCardinality(List<Object> query) {
		return index.estimateCardinality(query);
	}

	@Override
	public IndexEntry addEntry(List<Object> key) {
		return index.addEntry(key);
	}

	@Override
	public void remove(List<Object> key) {
		index.remove(key);
	}

	@Override
	public EClass eClass() {
		return index.eClass();
	}

	@Override
	public Resource eResource() {
		return index.eResource();
	}

	@Override
	public EObject eContainer() {
		return index.eContainer();
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		return index.eContainingFeature();
	}

	@Override
	public EReference eContainmentFeature() {
		return index.eContainmentFeature();
	}

	@Override
	public EList<EObject> eContents() {
		return index.eContents();
	}

	@Override
	public TreeIterator<EObject> eAllContents() {
		return index.eAllContents();
	}

	@Override
	public boolean eIsProxy() {
		return index.eIsProxy();
	}

	@Override
	public EList<EObject> eCrossReferences() {
		return index.eCrossReferences();
	}

	@Override
	public Object eGet(EStructuralFeature feature) {
		return index.eGet(feature);
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		return index.eGet(feature, resolve);
	}

	@Override
	public void eSet(EStructuralFeature feature, Object newValue) {
		index.eSet(feature, newValue);
	}

	@Override
	public boolean eIsSet(EStructuralFeature feature) {
		return index.eIsSet(feature);
	}

	@Override
	public void eUnset(EStructuralFeature feature) {
		index.eUnset(feature);
	}

	@Override
	public Object eInvoke(EOperation operation, EList<?> arguments)
			throws InvocationTargetException {
		return index.eInvoke(operation, arguments);
	}

	@Override
	public EList<Adapter> eAdapters() {
		return index.eAdapters();
	}

	@Override
	public boolean eDeliver() {
		return index.eDeliver();
	}

	@Override
	public void eSetDeliver(boolean deliver) {
		index.eSetDeliver(deliver);
	}

	@Override
	public void eNotify(Notification notification) {
		index.eNotify(notification);
	}

	@Override
	public void registerNotificationReceiver(
			IndexNotificationReceiver receiver) {
		receivers.add(receiver);
	}

	@Override
	public void notifyIndexChanged(IndexChangeNotification notification) {
		if(notification.getEntry().getKey().size() == interfaceSize) {
			for(IndexNotificationReceiver receiver:receivers) {
				receiver.notifyIndexChanged(notification);
			}
		}
	}

	public Map<String, Integer> getParameterPositions() {
		return parameterPositions;
	}

}
