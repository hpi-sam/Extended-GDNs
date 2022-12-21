package de.mdelab.mlsdm.interpreter.incremental.rete.projection;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.interpreter.incremental.rete.ReteNet;
import de.mdelab.mlsdm.interpreter.incremental.rete.dynamic.ReteContinuationDiscriminator;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteEdgeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteExpressionFilter;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteJoin;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteKeyProjector;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNodeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteParametrizedEdgeInput;
import de.mdelab.mlsdm.interpreter.incremental.rete.projection.RetePushPullAnalyser.PushSideEnum;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteUtil;
import de.mdelab.mlsdm.interpreter.searchModel.model.MLSDMSearchModel;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.LWExpressionInterpreterManager;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight.ocl.LWOCLExpressionInterpreter;
import de.mdelab.mlstorypatterns.AbstractStoryPatternLink;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.PatternConstraint;
import de.mdelab.sdm.interpreter.core.patternmatcher.searchModelBased.PatternNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

public class ReteFilterInserter {

  private final LWExpressionInterpreterManager expressionInterpreterManager;
  private final MLSDMSearchModel searchModel;
  
  private final boolean lazyInputs;
  
  public ReteFilterInserter(MLSDMSearchModel searchModel) {
	this(searchModel, false);
  }
  
  public ReteFilterInserter(MLSDMSearchModel searchModel, boolean lazyInputs) {
    this.searchModel = searchModel;
    this.expressionInterpreterManager = new LWExpressionInterpreterManager();
    this.expressionInterpreterManager.registerExpressionInterpreter(
        "OCL", new LWOCLExpressionInterpreter(Collections.emptyMap()));
    this.lazyInputs = lazyInputs;
  }

  public void insertExpressionFiltersOnly(ReteNet net, Map<ReteJoin, PushSideEnum> pushDirections) {
	  insertExpressionFiltersOnly(net.getRoot(), pushDirections, new ArrayList<ReteIndexer>(), getConstraints());
  }

  protected void insertExpressionFiltersOnly(
      ReteNode root,
      Map<ReteJoin, PushSideEnum> pushDirections,
      List<ReteIndexer> filters,
      Map<MLExpression, Collection<Integer>> constraints) {
    if (root.isJoin()) {
      ReteJoin join = (ReteJoin) root;

      //            Map<Collection<Integer>, Collection<ReteIndexer>> applicableFilters =
      // getApplicableFilters(root, filters);
      //            if(!applicableFilters.isEmpty()) {
      //                insertJoinFilterStructure(join, applicableFilters);
      //            }

      if (pushDirections.get(join) == PushSideEnum.PUSH_LEFT) {
        insertExpressionFiltersOnly(join.getLeftIndexer(), pushDirections, filters, constraints);

        insertExpressionFiltersOnly(join.getRightIndexer(), pushDirections, filters, constraints);
      } else {
        insertExpressionFiltersOnly(join.getRightIndexer(), pushDirections, filters, constraints);

        insertExpressionFiltersOnly(join.getLeftIndexer(), pushDirections, filters, constraints);
      }
    } else {
      for (ReteNode predecessor : root.getPredecessors()) {
        insertExpressionFiltersOnly(predecessor, pushDirections, filters, constraints);
      }
    }

    insertExpressionFilters(root, constraints);
  }

  public void insertFilters(ReteNet net, Map<ReteJoin, PushSideEnum> pushDirections) {
    insertFilters(net.getRoot(), pushDirections, new ArrayList<ReteIndexer>(), getConstraints());
  }

  private Map<MLExpression, Collection<Integer>> getConstraints() {
    Map<MLExpression, Collection<Integer>> constraints =
        new HashMap<MLExpression, Collection<Integer>>();
    for (PatternConstraint<
            StoryPattern,
            AbstractStoryPatternObject,
            AbstractStoryPatternLink,
            EClassifier,
            EStructuralFeature,
            MLExpression>
        pc : searchModel.getPatternConstraints()) {
      if (pc.getConstraint() instanceof MLExpression) {
        constraints.put((MLExpression) pc.getConstraint(), getDependencyIDs(pc));
      }
    }
    return constraints;
  }

  private Collection<Integer> getDependencyIDs(
      PatternConstraint<
              StoryPattern,
              AbstractStoryPatternObject,
              AbstractStoryPatternLink,
              EClassifier,
              EStructuralFeature,
              MLExpression>
          pc) {
    List<Integer> ids = new ArrayList<Integer>();
    for (PatternNode<
            StoryPattern,
            AbstractStoryPatternObject,
            AbstractStoryPatternLink,
            EClassifier,
            EStructuralFeature,
            MLExpression>
        pn : pc.getDependencies()) {
      ids.add(pn.getId());
    }
    return ids;
  }

  protected void insertFilters(
      ReteNode root,
      Map<ReteJoin, PushSideEnum> pushDirections,
      List<ReteIndexer> filters,
      Map<MLExpression, Collection<Integer>> constraints) {
    if (root.isJoin()) {
      ReteJoin join = (ReteJoin) root;

      //			Map<Collection<Integer>, Collection<ReteIndexer>> applicableFilters =
      // getApplicableFilters(root, filters);
      //			if(!applicableFilters.isEmpty()) {
      //				insertJoinFilterStructure(join, applicableFilters);
      //			}

      if (pushDirections.get(join) == PushSideEnum.PUSH_LEFT) {
        insertFilters(join.getLeftIndexer(), pushDirections, filters, constraints);

        addFilter(filters, join.getLeftIndexer());

        insertFilters(join.getRightIndexer(), pushDirections, filters, constraints);
      } else {
        insertFilters(join.getRightIndexer(), pushDirections, filters, constraints);

        addFilter(filters, join.getRightIndexer());

        insertFilters(join.getLeftIndexer(), pushDirections, filters, constraints);
      }
    } else if (root.isInput()) {
      Map<Collection<Integer>, Collection<ReteIndexer>> applicableFilters =
          getApplicableFilters(root, filters);
      if (!applicableFilters.isEmpty()) {
        root = convertToParametrisedInput((ReteInput) root, applicableFilters);
      }
    } else {
      for (ReteNode predecessor : root.getPredecessors()) {
        insertFilters(predecessor, pushDirections, filters, constraints);
      }
    }

    insertExpressionFilters(root, constraints);
  }

  private void addFilter(List<ReteIndexer> filters, ReteIndexer newFilter) {
    for (Iterator<ReteIndexer> it = filters.iterator(); it.hasNext(); ) {
      ReteIndexer filter = it.next();
      if (includes(newFilter, filter)) {
        it.remove();
      }
    }
    filters.add(newFilter);
  }

  private boolean includes(ReteIndexer newFilter, ReteIndexer filter) {
    int[] newTableMask = newFilter.getTableMask();
    int[] filterTableMask = filter.getTableMask();
    for (int index = 0; index < newTableMask.length; index++) {
      if (newTableMask[index] == ReteContinuationDiscriminator.NO_MAPPING
          && filterTableMask[index] != ReteContinuationDiscriminator.NO_MAPPING) {
        return false;
      }
    }
    return true;
  }

  private void insertExpressionFilters(
      ReteNode root, Map<MLExpression, Collection<Integer>> constraints) {
    for (Entry<MLExpression, Collection<Integer>> constraint :
        new ArrayList<Entry<MLExpression, Collection<Integer>>>(constraints.entrySet())) {
      if (coversDependencies(root, constraint.getValue())) {
        ReteExpressionFilter filter = createExpressionFilter(root.getTableMask(), constraint);

        for (ReteNode successor : new ArrayList<ReteNode>(root.getSuccessors())) {
          root.removeSuccessor(successor);
          filter.addSuccessor(successor);
        }
        root.addSuccessor(filter);
        root.getNet().addNode(filter);

        constraints.remove(constraint.getKey());
      }
    }
  }

  private ReteExpressionFilter createExpressionFilter(
      int[] tableMask, Entry<MLExpression, Collection<Integer>> constraint) {
    MLExpression expression = constraint.getKey();

    Map<String, Integer> indices = new HashMap<String, Integer>();
    for (int id : constraint.getValue()) {
      int index = tableMask[id];
      indices.put(searchModel.getNodeForId(id).getSpo().getName(), index);
    }

    return new ReteExpressionFilter(
        expression,
        expressionInterpreterManager.getInterpreter(expression.getExpressionLanguage()),
        indices);
  }

  private boolean coversDependencies(ReteNode root, Collection<Integer> dependencies) {
    int[] tableMask = root.getTableMask();
    for (int dependency : dependencies) {
      if (tableMask[dependency] == ReteContinuationDiscriminator.NO_MAPPING) {
        return false;
      }
    }
    return true;
  }

  private ReteNode convertToParametrisedInput(
      ReteInput input, Map<Collection<Integer>, Collection<ReteIndexer>> applicableFilters) {
    if (input.isEdge()) {
      ReteEdgeInput edgeInput = (ReteEdgeInput) input;
      EStructuralFeature feature = edgeInput.getLink().getFeature();
      int sourceId = getSourceId(edgeInput);
      int targetId = getTargetId(edgeInput);

      Entry<Collection<Integer>, Collection<ReteIndexer>> sourceEntry = null;
      Entry<Collection<Integer>, Collection<ReteIndexer>> sourceTargetEntry = null;
      Entry<Collection<Integer>, Collection<ReteIndexer>> targetEntry = null;

      for (Entry<Collection<Integer>, Collection<ReteIndexer>> entry :
          applicableFilters.entrySet()) {
        if (entry.getKey().size() > 1) {
          sourceTargetEntry = entry;
        } else {
          int id = entry.getKey().iterator().next();
          if (input.getTableMask()[id] == 0) {
            sourceEntry = entry;
          } else {
            targetEntry = entry;
          }
        }
      }

      if (sourceEntry == null
          && sourceTargetEntry == null
          && (targetEntry == null || !hasOpposite(feature))) {
        return input;
      }

      ReteNode sourceParameter =
          sourceEntry != null
              ? createAggregateFilter(sourceEntry.getValue(), sourceEntry.getKey())
              : null;
      ReteNode targetParameter =
          targetEntry != null && hasOpposite(feature)
              ? createAggregateFilter(targetEntry.getValue(), targetEntry.getKey())
              : null; // TODO we could add this as a filter afterwards (like sourceTarget)
      ReteNode sourceTargetParameter =
          sourceTargetEntry != null
              ? createAggregateFilter(sourceTargetEntry.getValue(), sourceTargetEntry.getKey())
              : null;

      ReteIndexer sourceIndexer = null;
      ReteIndexer targetIndexer = null;
      if (sourceParameter != null) {
        sourceIndexer = createIndexerWithKey(sourceParameter, Collections.singletonList(sourceId));
      }
      if (targetParameter != null && hasOpposite(edgeInput.getLink().getFeature())) {
        targetIndexer = createIndexerWithKey(targetParameter, Collections.singletonList(targetId));
      }

      if (sourceTargetParameter != null) {
        ReteIndexer sourceIntermediateIndexer =
            createIndexerWithKey(sourceTargetParameter, Collections.singletonList(sourceId));
        ReteKeyProjector sourceProjector = createProjector(sourceIntermediateIndexer);
        ReteIndexer sourceProjectionIndexer =
            createIndexerWithKey(sourceProjector, Collections.singletonList(sourceId));
        if (sourceIndexer == null) {
          sourceIndexer = sourceProjectionIndexer;
        } else {
          ReteJoin sourceIntermediateJoin = createJoin(sourceIndexer, sourceProjectionIndexer, 1);
          sourceIndexer =
              createIndexerWithKey(sourceIntermediateJoin, Collections.singletonList(sourceId));
        }

        if (hasOpposite(feature)) {
          ReteIndexer targetIntermediateIndexer =
              createIndexerWithKey(sourceTargetParameter, Collections.singletonList(targetId));
          ReteKeyProjector targetProjector = createProjector(targetIntermediateIndexer);
          ReteIndexer targetProjectionIndexer =
              createIndexerWithKey(targetProjector, Collections.singletonList(targetId));
          if (targetIndexer == null) {
            targetIndexer = targetProjectionIndexer;
          } else {
            ReteJoin targetIntermediateJoin = createJoin(targetIndexer, targetProjectionIndexer, 1);
            targetIndexer =
                createIndexerWithKey(targetIntermediateJoin, Collections.singletonList(targetId));
          }
        }
      }

      ReteNode parametrizedInput =
          new ReteParametrizedEdgeInput(edgeInput.getLink(), sourceIndexer, targetIndexer, lazyInputs);
      parametrizedInput.setTableMask(input.getTableMask());
      ReteNet net = input.getNet();
      Object partition = getPatternPartition(input);
      net.removeNode(input);
      net.addNode(partition, parametrizedInput);
      if (sourceIndexer != null) {
        sourceIndexer.addSuccessor(parametrizedInput);
      }
      if (targetIndexer != null) {
        targetIndexer.addSuccessor(parametrizedInput);
      }
      if (sourceTargetParameter != null) {
        ReteIndexer joinIndexer =
            createIndexerWithKey(parametrizedInput, Arrays.asList(sourceId, targetId));
        ReteIndexer sourceTargetIndexer =
            createIndexerWithKey(sourceTargetParameter, Arrays.asList(sourceId, targetId));
        parametrizedInput = createJoin(joinIndexer, sourceTargetIndexer, 2);
      }

      Collection<ReteNode> successors = new ArrayList<ReteNode>(input.getSuccessors());
      for (ReteNode successor : successors) {
        input.removeSuccessor(successor);
        parametrizedInput.addSuccessor(successor);
      }
      return parametrizedInput;
    } else if (input.isNode()) {
      ReteNodeInput nodeInput = (ReteNodeInput) input;
      int nodeId = getNodeId(nodeInput);
      Collection<ReteIndexer> filters = applicableFilters.get(Collections.singleton(nodeId));
      ReteNode aggregateFilter = createAggregateFilter(filters, Collections.singletonList(nodeId));
      ReteIndexer leftIndexer = createIndexerWithKey(nodeInput, Collections.singletonList(nodeId));
      ReteIndexer rightIndexer =
          createIndexerWithKey(aggregateFilter, Collections.singletonList(nodeId));

      ReteJoin filterJoin = createJoin(leftIndexer, rightIndexer, 1);
      Collection<ReteNode> successors = new ArrayList<ReteNode>(input.getSuccessors());
      for (ReteNode successor : successors) {
        if (successor != leftIndexer) {
          input.removeSuccessor(successor);
          filterJoin.addSuccessor(successor);
        }
      }
      return filterJoin;
    } else {
      return input;
    }
  }

  private boolean hasOpposite(EStructuralFeature feature) {
    return feature.eClass() == EcorePackage.Literals.EREFERENCE
        && ((EReference) feature).getEOpposite() != null;
  }

  private Object getPatternPartition(ReteNode node) {
    for (Entry<Object, ReteNode> entry : node.getNet().getNodes().entrySet()) {
      if (entry.getValue() == node) {
        return entry.getKey();
      }
    }
    return null;
  }

  private int getSourceId(ReteEdgeInput edgeInput) {
    int tableMask[] = edgeInput.getTableMask();
    for (int id = 0; id < tableMask.length; id++) {
      if (tableMask[id] == 0) {
        return id;
      }
    }
    return -1;
  }

  private int getTargetId(ReteEdgeInput edgeInput) {
    int tableMask[] = edgeInput.getTableMask();
    for (int id = 0; id < tableMask.length; id++) {
      if (tableMask[id] == 1) {
        return id;
      }
    }
    return -1;
  }

  private int getNodeId(ReteNodeInput nodeInput) {
    int tableMask[] = nodeInput.getTableMask();
    for (int id = 0; id < tableMask.length; id++) {
      if (tableMask[id] != ReteContinuationDiscriminator.NO_MAPPING) {
        return id;
      }
    }
    return -1;
  }

  private ReteNode createAggregateFilter(Collection<ReteIndexer> filters, Collection<Integer> key) {
    List<Integer> orderedKey = new ArrayList<Integer>(key);
    List<ReteKeyProjector> projectors = new ArrayList<ReteKeyProjector>();
    for (ReteIndexer filter : filters) {
      List<Integer> filterKey = getKey(filter);
      if (filterKey.containsAll(key)) {
        if (key.containsAll(filterKey)) {
          // key is already correct, no intermediate projector required
          ReteKeyProjector projector = createProjector(filter);
          projectors.add(projector);
        } else {
          // intermediate projector required
          ReteKeyProjector intermediateProjector = createProjector(filter);
          ReteIndexer intermediateIndexer = createIndexerWithKey(intermediateProjector, orderedKey);
          ReteKeyProjector projector = createProjector(intermediateIndexer);
          projectors.add(projector);
        }
      } else {
        // completely new indexer required
        ReteIndexer intermediateIndexer = createIndexerWithKey(filter, orderedKey);
        ReteKeyProjector projector = createProjector(intermediateIndexer);
        projectors.add(projector);
      }
    }

    ReteNode baseNode = projectors.get(0);
    for (int i = 1; i < projectors.size(); i++) {
      ReteKeyProjector projector = projectors.get(i);
      ReteIndexer leftIndexer = createIndexerWithKey(baseNode, orderedKey);
      ReteIndexer rightIndexer = createIndexerWithKey(projector, orderedKey);
      baseNode = createJoin(leftIndexer, rightIndexer, orderedKey.size());
    }
    return baseNode;
  }

  private ReteJoin createJoin(ReteIndexer leftIndexer, ReteIndexer rightIndexer, int joinSize) {
    ReteJoin join = new ReteJoin(leftIndexer, rightIndexer, joinSize);

    int[] leftTableMask = leftIndexer.getTableMask();
    int[] rightTableMask = rightIndexer.getTableMask();
    int tableMaskSize = leftTableMask.length;
    int rightOffset = leftIndexer.getMask().length - joinSize;

    int tableMask[] = createEmptyTableMask(tableMaskSize);
    // add left nodes to mask
    for (int id = 0; id < tableMaskSize; id++) {
      if (leftTableMask[id] != ReteContinuationDiscriminator.NO_MAPPING) {
        tableMask[id] = leftTableMask[id];
      } else if (rightTableMask[id] != ReteContinuationDiscriminator.NO_MAPPING) {
        tableMask[id] = rightTableMask[id] + rightOffset;
      }
    }
    join.setTableMask(tableMask);

    leftIndexer.getNet().addNode(join);
    return join;
  }

  private ReteIndexer createIndexerWithKey(ReteNode predecessor, List<Integer> requiredKey) {
    int[] predecessorTableMask = predecessor.getTableMask();
    List<Integer> maskList = new ArrayList<Integer>();
    for (int id : requiredKey) {
      maskList.add(predecessorTableMask[id]);
    }
    for (int id = 0; id < predecessorTableMask.length; id++) {
      if (predecessorTableMask[id] != ReteContinuationDiscriminator.NO_MAPPING
          && !requiredKey.contains(id)) {
        maskList.add(predecessorTableMask[id]);
      }
    }
    int[] mask = new int[maskList.size()];
    for (int i = 0; i < maskList.size(); i++) {
      mask[i] = maskList.get(i);
    }
    ReteIndexer indexer =
        new ReteIndexer(mask, requiredKey.size(), getProjectionIndexerName(requiredKey));
    predecessor.addSuccessor(indexer);
    indexer.setTableMask(deriveTableMask(mask, predecessorTableMask));
    predecessor.getNet().addNode(indexer);
    return indexer;
  }

  private String getProjectionIndexerName(List<Integer> requiredKey) {
    StringBuffer sb = new StringBuffer();
    sb.append("PROJECTION (");
    for (int i = 0; i < requiredKey.size(); i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(Integer.toString(requiredKey.get(i)));
    }
    sb.append(")");
    return sb.toString();
  }

  private int[] deriveTableMask(int[] mask, int[] predecessorTableMask) {
    int[] tableMask = createEmptyTableMask(predecessorTableMask.length);
    for (int index = 0; index < mask.length; index++) {
      int predecessorIndex = mask[index];
      int id;
      for (id = 0; id < predecessorTableMask.length; id++) {
        if (predecessorTableMask[id] == predecessorIndex) {
          break;
        }
      }
      tableMask[id] = index;
    }
    return tableMask;
  }

  private int[] createEmptyTableMask(int maskSize) {
    int[] tableMask = new int[maskSize];
    for (int i = 0; i < maskSize; i++) {
      tableMask[i] = ReteContinuationDiscriminator.NO_MAPPING;
    }
    return tableMask;
  }

  private ReteKeyProjector createProjector(ReteIndexer filter) {
    ReteKeyProjector projector = new ReteKeyProjector(filter);
    filter.getNet().addNode(projector);
    projector.setTableMask(getKeyTableMask(filter));
    filter.addSuccessor(projector);
    return projector;
  }

  private int[] getKeyTableMask(ReteIndexer filter) {
    int[] filterTableMask = filter.getTableMask();
    int[] tableMask = new int[filterTableMask.length];
    for (int i = 0; i < filterTableMask.length; i++) {
      if (filterTableMask[i] != ReteContinuationDiscriminator.NO_MAPPING
          && filterTableMask[i] < filter.getKeySize()) {
        tableMask[i] = filterTableMask[i];
      } else {
        tableMask[i] = ReteContinuationDiscriminator.NO_MAPPING;
      }
    }
    return tableMask;
  }

  private List<Integer> getKey(ReteIndexer filter) {
    List<Integer> key = new ArrayList<Integer>();
    int[] tableMask = filter.getTableMask();
    for (int i = 0; i < filter.getKeySize(); i++) {
      for (int j = 0; j < tableMask.length; j++) {
        if (tableMask[j] == i) {
          key.add(tableMask[j]);
          break;
        }
      }
    }
    return key;
  }

  private void insertJoinFilterStructure(
      ReteJoin join, Map<Collection<Integer>, Collection<ReteIndexer>> applicableFilters) {
    // TODO create appropriate projections, joins, etc...
    // TODO does this even make sense? In many cases, filtering will be done right after this join
    // anyway
  }

  private Map<Collection<Integer>, Collection<ReteIndexer>> getApplicableFilters(
      ReteNode node, List<ReteIndexer> filters) {
    Map<Collection<Integer>, Collection<ReteIndexer>> applicableFilters =
        new HashMap<Collection<Integer>, Collection<ReteIndexer>>();
    Set<Integer> coveredIds = getCoveredIds(node);
    for (int i = filters.size() - 1; i >= 0; i--) {
      ReteIndexer filter = filters.get(i);
      Set<Integer> filterIds = getCoveredIds(filter);
      Set<Integer> combination = ReteUtil.intersect(coveredIds, filterIds);

      if (!combination.isEmpty()) {
        if (!applicableFilters.containsKey(combination)) {
          applicableFilters.put(combination, new ArrayList<ReteIndexer>());
        }
        applicableFilters.get(combination).add(filter);
      }
    }
    return applicableFilters;
  }

  private Set<Integer> getCoveredIds(ReteNode node) {
    Set<Integer> coveredIds = new HashSet<Integer>();
    int[] tableMask = node.getTableMask();
    for (int i = 0; i < tableMask.length; i++) {
      if (tableMask[i] != ReteContinuationDiscriminator.NO_MAPPING) {
        coveredIds.add(i);
      }
    }
    return coveredIds;
  }
}
