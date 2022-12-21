package de.mdelab.mlsdm.interpreter.incremental.rete.projection;

import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNet;
import de.mdelab.mlsdm.interpreter.incremental.rete.diameter.PatternPartition;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteEdgeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteJoin;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Node;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPatternLink;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

public class RetePushPullAnalyser {

  public static enum PushSideEnum {
    PUSH_LEFT,
    PUSH_RIGHT
  };

  private Map<AbstractStoryPatternObject, Integer> edgeFilters;
  private Collection<AbstractStoryPatternObject> anchors;

  public Map<ReteJoin, PushSideEnum> analyzeReteNet(
      ReteNet net, AbstractStoryPatternObject... anchors) {
    this.edgeFilters = new HashMap<AbstractStoryPatternObject, Integer>();
    this.anchors = new LinkedHashSet<AbstractStoryPatternObject>();
    this.anchors.addAll(Arrays.asList(anchors));
    Map<ReteIndexer, PatternPartition> partitions = getIndexerPartitions(net);
    Map<ReteJoin, PushSideEnum> result = new HashMap<ReteJoin, PushSideEnum>();
    analyzeReteNet(net.getRoot(), partitions, result);
    return result;
  }

  private void analyzeReteNet(
      ReteNode root,
      Map<ReteIndexer, PatternPartition> partitions,
      Map<ReteJoin, PushSideEnum> result) {
    if (root.isJoin()) {
      ReteJoin join = (ReteJoin) root;
      ReteIndexer leftIndexer = join.getLeftIndexer();
      ReteIndexer rightIndexer = join.getRightIndexer();

      PatternPartition leftPartition = partitions.get(leftIndexer);
      double leftFilters = getFilters(leftPartition);
      PatternPartition rightPartition = partitions.get(rightIndexer);
      double rightFilters = getFilters(rightPartition);

      if (!isAnchored(leftPartition)) {
        result.put(join, PushSideEnum.PUSH_RIGHT);

        analyzeReteNet(rightIndexer, partitions, result);
        analyzeReteNet(leftIndexer, partitions, result);
      } else if (!isAnchored(rightPartition)) {
        result.put(join, PushSideEnum.PUSH_LEFT);

        analyzeReteNet(leftIndexer, partitions, result);
        analyzeReteNet(rightIndexer, partitions, result);
      } else if (leftFilters >= rightFilters) {
        result.put(join, PushSideEnum.PUSH_LEFT);

        analyzeReteNet(leftIndexer, partitions, result);
        analyzeReteNet(rightIndexer, partitions, result);
      } else {
        result.put(join, PushSideEnum.PUSH_RIGHT);

        analyzeReteNet(rightIndexer, partitions, result);
        analyzeReteNet(leftIndexer, partitions, result);
      }
    } else if (root.isInput() && ((ReteInput) root).isEdge()) {
      AbstractStoryPatternObject sourceSPO = ((ReteEdgeInput) root).getLink().getSource();
      if (!edgeFilters.containsKey(sourceSPO)) {
        edgeFilters.put(sourceSPO, 0);
      }
      edgeFilters.put(sourceSPO, edgeFilters.get(sourceSPO) + 1);

      AbstractStoryPatternObject targetSPO = ((ReteEdgeInput) root).getLink().getTarget();
      if (!edgeFilters.containsKey(targetSPO)) {
        edgeFilters.put(targetSPO, 0);
      }
      edgeFilters.put(targetSPO, edgeFilters.get(targetSPO) + 1);

      anchors.add(sourceSPO);
      anchors.add(targetSPO);
    } else {
      for (ReteNode predecessor : root.getPredecessors()) {
        analyzeReteNet(predecessor, partitions, result);
      }
    }
  }

  private boolean isAnchored(PatternPartition partition) { // TODO requires EOpposite for all edges!
    for (Node<AbstractStoryPatternObject, StoryPatternLink> node : partition.nodes) {
      if (anchors.contains(node.value)) {
        return true;
      }
    }
    return false;
  }

  private double getFilters(PatternPartition partition) {
    int edgeFilters = getEdgeFilters(partition);
    return Math.max(0, (partition.edges.size() - partition.nodes.size()) + 1)
        + partition.nacs.size()
        + partition.expressionConstraints.size()
        + edgeFilters;
  }

  private int getEdgeFilters(PatternPartition partition) {
    int filters = 0;
    for (Node<AbstractStoryPatternObject, StoryPatternLink> node : partition.nodes) {
      if (edgeFilters.containsKey(node.value)) {
        filters += edgeFilters.get(node.value);
      }
    }
    return filters;
  }

  private Map<ReteIndexer, PatternPartition> getIndexerPartitions(ReteNet net) {
    Map<ReteIndexer, PatternPartition> partitions = new HashMap<ReteIndexer, PatternPartition>();
    for (ReteNode node : net.getNodes().values()) {
      if (node.isIndexer()) {
        PatternPartition partition = getPartition(node);
        partitions.put((ReteIndexer) node, partition);
      }
    }
    return partitions;
  }

  @SuppressWarnings("unchecked")
  private PatternPartition getPartition(ReteNode node) {
    PatternPartition partition = null;
    for (Entry<Object, ReteNode> entry : node.getNet().getNodes().entrySet()) {
      if (entry.getValue() == node) {
        if (entry.getKey() instanceof Node
            && ((Node<PatternPartition, ?>) entry.getKey()).value instanceof PatternPartition) {
          partition = ((Node<PatternPartition, ?>) entry.getKey()).value;
        }
        break;
      }
    }

    if (partition == null) {
      for (ReteNode predecessor : node.getPredecessors()) {
        partition = getPartition(predecessor);
        if (partition != null) {
          break;
        }
      }
    }

    return partition;
  }
}
