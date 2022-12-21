package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNotificationReceiver;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNotifier;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.SDMReteMatch;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ReteSDMResultAdapter implements ReteNotificationReceiver {

  private List<AbstractStoryPatternObject> spos;
  private ReteResultProvider resultProvider;
  private Collection<ReteTuple> newTuples;

  public ReteSDMResultAdapter(
      ReteResultProvider resultProvider, List<AbstractStoryPatternObject> spos) {
    this.resultProvider = resultProvider;
    this.resultProvider.addNotificationReceiver(this);
    this.newTuples = new HashSet<ReteTuple>();
    this.newTuples.addAll(getAllTuples());

    this.spos = spos;
  }

  private Collection<? extends ReteTuple> getAllTuples() {
    return resultProvider.getTuples(Collections.emptyList());
  }

  public Collection<Map<AbstractStoryPatternObject, Object>> getMatches() {
    return wrapMatches(getAllTuples());
  }

  public Collection<Map<AbstractStoryPatternObject, Object>> getNewMatches() {
    Collection<Map<AbstractStoryPatternObject, Object>> matches = wrapMatches(newTuples);
    newTuples.clear();
    return matches;
  }

  public Collection<Map<AbstractStoryPatternObject, Object>> wrapMatches(
      Collection<? extends ReteTuple> tuples) {
    Collection<Map<AbstractStoryPatternObject, Object>> matches =
        new ArrayList<Map<AbstractStoryPatternObject, Object>>();
    for (ReteTuple tuple : tuples) {
      matches.add(wrapMatch(tuple));
    }
    return matches;
  }

  public SDMReteMatch wrapMatch(ReteTuple tuple) {
    Map<AbstractStoryPatternObject, Object> match =
        new HashMap<AbstractStoryPatternObject, Object>();
    for (int i = 0; i < tuple.size(); i++) {
      match.put(spos.get(i), tuple.get(i));
    }
    return new SDMReteMatch(match, tuple.isPreliminary());
  }
  
  @Override
  public void notifyAddition(ReteTuple tuple, ReteNotifier notifier) {
    newTuples.add(tuple);
  }

  @Override
  public void notifyRemoval(ReteTuple tuple, ReteNotifier notifier) {
    newTuples.remove(tuple);
  }
}
