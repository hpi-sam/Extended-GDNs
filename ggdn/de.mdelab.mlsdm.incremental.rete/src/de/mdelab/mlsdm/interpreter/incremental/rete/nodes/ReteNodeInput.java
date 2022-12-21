package de.mdelab.mlsdm.interpreter.incremental.rete.nodes;

import java.util.Collections;

import org.eclipse.emf.ecore.EClass;

import de.mdelab.mlsdm.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.incremental.change.SDMNodeChange;
import de.mdelab.mlsdm.incremental.change.SDMChangeEvent.SDMChangeEnum;
import de.mdelab.mlsdm.interpreter.incremental.rete.StoryPatternModelIndex;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteNodeExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.nodes.execute.ReteNodeInputExecutor;
import de.mdelab.mlsdm.interpreter.incremental.rete.util.ReteTuple;
import de.mdelab.mlstorypatterns.AbstractStoryPatternObject;

public class ReteNodeInput extends ReteInput {

	private StoryPatternModelIndex modelIndex;
	private AbstractStoryPatternObject spo;
	
	public ReteNodeInput(AbstractStoryPatternObject spo, StoryPatternModelIndex modelIndex) {
		this.spo = spo;
		this.modelIndex = modelIndex;
	}

	@Override
	public void registerChange(SDMChangeEvent change) {
		if(change.getType() == spo.getType() ||
				(change.getType() instanceof EClass && ((EClass) change.getType()).getEAllSuperTypes().contains(spo.getType()))) {
			if(change.getModifier() == SDMChangeEnum.CREATE) {
				propagateAddition(new ReteTuple(Collections.singletonList(((SDMNodeChange) change).getObject())));
			}
			else if(change.getModifier() == SDMChangeEnum.DELETE) {
				propagateRemoval(new ReteTuple(Collections.singletonList(((SDMNodeChange) change).getObject())));
			}
		}
	}

	@Override
	public ReteNodeExecutor createExecutor() {
		return new ReteNodeInputExecutor(this, modelIndex.getDomain(spo).iterator());
	}
	
	public AbstractStoryPatternObject getSPO() {
		return spo;
	}
	
	@Override
	public boolean isNode() {
		return true;
	}

	public void setModelIndex(StoryPatternModelIndex modelIndex) {
		this.modelIndex = modelIndex;
	}

}
