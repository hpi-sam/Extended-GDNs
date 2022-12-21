package de.mdelab.mlsdm.incremental.rete.sdm;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.mdelab.mlsdm.incremental.SDMInterfaceIndex;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.mlsdm.incremental.SDMQueryMatch;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.mlindices.IndexChangeNotification;
import de.mdelab.mlsdm.mlindices.IndexEntry;
import de.mdelab.mlsdm.mlindices.IndexNotificationType;

public class SDMAntiJoinQuery extends SDMJoinQuery {

	public SDMAntiJoinQuery(SDMQueryManager manager,
			SDMInterfaceIndex leftInterface, SDMInterfaceIndex rightInterface,
			Map<String, Integer> spoNameToIndex, int joinCardinality,
			int leftInterfaceSize, int rightInterfaceSize) {
		super(manager, leftInterface, rightInterface, spoNameToIndex,
				joinCardinality, leftInterfaceSize, rightInterfaceSize);
	}

	@Override
	public Iterator<SDMQueryMatch> getMatches() {
		updateMatches();
		
		final Iterator<IndexEntry> leftIt = leftInterface.getEntries(leftIndexQuery);
		return new SDMAntiJoinResultIterator(leftIt, this);
	}

	@Override
	public void registerChange(SDMChangeEvent change) {
		
	}

	public boolean hasRightOpposite(IndexEntry indexEntry) {
		List<Object> rightQuery = completeQuery(createPointQuery(indexEntry.getKey().subList(0, joinCardinality)), rightIndexQuery);
		return rightInterface.getEntries(rightQuery).hasNext();
	}

	@Override
	public void notifyIndexChanged(IndexChangeNotification notification) {
		if(notification.getIndex() == leftInterface.getWrappedIndex()) {
			IndexEntry indexEntry = notification.getEntry();
			if(notification.getType() == IndexNotificationType.ADD) {
				if(!hasRightOpposite(indexEntry)) {
					addIndexEntry(indexEntry, indexEntry, null);
				}
			}
			else if(notification.getType() == IndexNotificationType.DELETE) {
				removeIndexEntries(indexEntry);
			}
		}
		else if(notification.getIndex() == rightInterface.getWrappedIndex()) {
			IndexEntry indexEntry = notification.getEntry();
			if(notification.getType() == IndexNotificationType.ADD) {
				//check whether opposite was present before to only delete newly invalidated entries
				List<Object> rightQuery = completeQuery(createPointQuery(indexEntry.getKey().subList(0, joinCardinality)), rightIndexQuery);;
				Iterator<IndexEntry> rightIt = rightInterface.getEntries(rightQuery);
				rightIt.next();
				if(!rightIt.hasNext()) {
					List<Object> leftQuery = completeQuery(createPointQuery(indexEntry.getKey().subList(0, joinCardinality)), leftIndexQuery);
					for(Iterator<IndexEntry> invalidEntryIt = leftInterface.getEntries(leftQuery); invalidEntryIt.hasNext();) {
						removeIndexEntries(invalidEntryIt.next());
					}
				}
			}
			else if(notification.getType() == IndexNotificationType.DELETE) {
				//check if overlap is still blocked
				List<Object> rightQuery = completeQuery(createPointQuery(indexEntry.getKey().subList(0, joinCardinality)), rightIndexQuery);;
				Iterator<IndexEntry> rightIt = rightInterface.getEntries(rightQuery);
				if(!rightIt.hasNext()) {
					List<Object> leftQuery = completeQuery(createPointQuery(indexEntry.getKey().subList(0, joinCardinality)), leftIndexQuery);
					for(Iterator<IndexEntry> entryIt = leftInterface.getEntries(leftQuery); entryIt.hasNext();) {
						IndexEntry leftEntry = entryIt.next();
						addIndexEntry(leftEntry, leftEntry, null);
					}
				}
			}
		}
	}

}
