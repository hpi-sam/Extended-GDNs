package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMEdgeChange;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent.SDMChangeEnum;
import de.mdelab.mlsdm.interpreter.incremental.rete.StoryPatternModelIndex;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteEdgeInputExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteNodeExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.Tuple;
import de.mdelab.mlstorypatterns.StoryPatternLink;

public class ReteEdgeInput extends ReteInput {

	protected StoryPatternLink link;
	protected StoryPatternModelIndex modelIndex;

	public ReteEdgeInput(StoryPatternLink link, StoryPatternModelIndex modelIndex) {
		this.link = link;
		this.modelIndex = modelIndex;
	}

	@Override
	public void registerChange(SDMChangeEvent change) {
		if(change.getType() == link.getFeature() && change.getModifier() == SDMChangeEnum.CREATE) {
			List<Object> tuple = new ArrayList<Object>(2);
			tuple.add(((SDMEdgeChange)change).getSource());
			tuple.add(((SDMEdgeChange)change).getTarget());
			propagateAddition(new ReteTuple(tuple));
		}
		else if(change.getType() == link.getFeature() && change.getModifier() == SDMChangeEnum.DELETE) {
			List<Object> tuple = new ArrayList<Object>(2);
			tuple.add(((SDMEdgeChange)change).getSource());
			tuple.add(((SDMEdgeChange)change).getTarget());
			propagateRemoval(new ReteTuple(tuple));	//TODO retrieve old tuple!!!
		}
	}

	@Override
	public ReteNodeExecutor createExecutor() {
		return new ReteEdgeInputExecutor(this, modelIndex.getReferences(link).iterator());
	}

	public StoryPatternLink getLink() {
		return link;
	}
	
	@Override
	public boolean isEdge() {
		return true;
	}

	@Override
	public String toString() {
		return "INPUT <" + link.getSource().getName() + " -" + link.getFeature().getName() + "-> " + link.getTarget().getName() + ">";
	}
	
	public void checkTuples() {
		for(Collection<EObject> d:modelIndex.getDomains().values()) {
			for(EObject o:d) {
				checkObject(o);
			}
		}

		for(Collection<Tuple<EObject, EObject>> d:modelIndex.getReferences().values()) {
			for(Tuple<EObject, EObject> o:d) {
				checkObject(o.e1);
				checkObject(o.e2);
			}
		}
	}
	

	private void checkObject(Object o) {
		if(o instanceof EObject) {
			if(!checkSystemContainment((EObject) o)) {
				System.out.println("!");
			}
		}
	}

	private boolean checkSystemContainment(EObject o) {
		if(o.eClass().getName().equals("System")) {
			return true;
		}
		else if(o.eContainer() != null) {
			return checkSystemContainment(o.eContainer());
		}
		else {
			return false;
		}
	}

	public void setModelIndex(StoryPatternModelIndex modelIndex) {
		this.modelIndex = modelIndex;
	}
}
