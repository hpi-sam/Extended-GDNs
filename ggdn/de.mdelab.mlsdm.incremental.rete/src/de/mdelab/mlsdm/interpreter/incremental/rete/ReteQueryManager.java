package de.mdelab.mlsdm.interpreter.incremental.rete;

import de.mdelab.mlsdm.incremental.ChangeListener;
import de.mdelab.mlsdm.incremental.IncrementalQueryManager;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteIndexer;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteNode;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.ReteTupleGenerator;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteNodeExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.FeatureSpecification;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Tuple;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.mlstorypatterns.StoryPatternLink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class ReteQueryManager extends IncrementalQueryManager {

  protected StoryPatternModelIndex modelIndex;

  public ReteQueryManager() {
    this.modelIndex = new StoryPatternModelIndex();
  }

  @Override
  protected void doAddEObject(EObject eObject) {
    super.doAddEObject(eObject);
    modelIndex.addEObject(eObject);
  }

  @Override
  protected void doRemoveEObject(EObject eObject) {
    super.doRemoveEObject(eObject);
    modelIndex.removeEObject(eObject);
  }

  @Override
  protected void doAddReference(EObject source, EReference reference, EObject target) {
    super.doAddReference(source, reference, target);
    modelIndex.addReference(source, reference, target);
  }

  @Override
  protected void doRemoveReference(EObject source, EReference reference, EObject target) {
    super.doRemoveReference(source, reference, target);
    modelIndex.removeReference(source, reference, target);
  }

  public void observePatternStatistics(StoryPattern sp) {
    modelIndex.observePatternStatistics(sp);
  }

  public Map<EClassifier, Collection<EObject>> getDomains() {
    return modelIndex.getDomains();
  }

  public Map<FeatureSpecification, Collection<Tuple<EObject, EObject>>> getReferences() {
    return modelIndex.getReferences();
  }

  public Collection<Tuple<EObject, EObject>> getReferences(StoryPatternLink link) {
    return modelIndex.getReferences(link);
  }

  public Collection<EObject> getDomain(AbstractStoryPatternObject spo) {
    return modelIndex.getDomain(spo);
  }

  public StoryPatternModelIndex getModelIndex() {
    return modelIndex;
  }

  public void registerReteNet(ReteNet net) {
    for (ReteNode node : net.getNodes().values()) {
      if (node instanceof ChangeListener) {
        addChangeListener((ChangeListener) node);
      }
    }
    for(ReteNode node:net.getNodes().values()) {
    	if(node.isInput()) {
    		ReteNodeExecutor executor = ((ReteTupleGenerator) node).createExecutor();
        	while(executor.hasNextTuple()) {
        		executor.generateNextTuple();
        	}
    	}
    }
//    for(ReteTupleGenerator generator:orderGeneratorsTopologically(net)) {	//TODO delete?
//    	ReteNodeExecutor executor = generator.createExecutor();
//    	while(executor.hasNextTuple()) {
//    		executor.generateNextTuple();
//    	}
//    }
  }
  
	protected List<ReteTupleGenerator> orderGeneratorsTopologically(ReteNet net) {
		//TODO only works for DAG (no recursion)
		List<ReteTupleGenerator> topologicalOrder = new ArrayList<ReteTupleGenerator>();
		LinkedList<ReteTupleGenerator> nodeStack = new LinkedList<ReteTupleGenerator>();
		for(ReteTupleGenerator generator:getPreviousGenerators(net.getRoot())) {
			nodeStack.push(generator);
		}
		
		while(!nodeStack.isEmpty()) {
			ReteTupleGenerator generator = nodeStack.pop();
			topologicalOrder.add(generator);
			
			for(ReteTupleGenerator previous:getPreviousGenerators(generator)) {
				nodeStack.push(previous);
			}
		}
		Collections.reverse(topologicalOrder);
		return topologicalOrder;
	}

	protected Collection<ReteIndexer> getNextIndexers(ReteNode node) {
		Collection<ReteIndexer> indexers = new HashSet<ReteIndexer>();
		for(ReteNode successor:node.getSuccessors()) {
			if(successor.isIndexer()) {
				indexers.add((ReteIndexer) successor);
			}
			else {
				indexers.addAll(getNextIndexers(successor));
			}
		}
		return indexers;
	}

	private Collection<ReteTupleGenerator> getPreviousGenerators(ReteNode node) {
		Collection<ReteTupleGenerator> generators = new HashSet<ReteTupleGenerator>();
		for(ReteNode predecessor:node.getPredecessors()) {
			if(predecessor.isTupleGenerator()) {
				generators.add((ReteTupleGenerator) predecessor);
			}
			else {
				generators.addAll(getPreviousGenerators(predecessor));
			}
		}
		return generators;
	}

}
