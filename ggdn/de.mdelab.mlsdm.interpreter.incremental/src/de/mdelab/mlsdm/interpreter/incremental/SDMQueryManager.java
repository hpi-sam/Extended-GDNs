package de.mdelab.mlsdm.interpreter.incremental;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlexpressions.MLStringExpression;
import de.mdelab.mlsdm.interpreter.incremental.change.SDMChangeEvent;
import de.mdelab.mlsdm.interpreter.incremental.change.SDMIndexChange;
import de.mdelab.mlsdm.interpreter.incremental.fragment.SDMIndexFragment;
import de.mdelab.mlsdm.interpreter.incremental.change.SDMChangeEvent.SDMChangeEnum;
import de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.MLSDMMetadataIndex;
import de.mdelab.mlsdm.mlindices.IndexChangeNotification;
import de.mdelab.mlsdm.mlindices.IndexNotificationReceiver;
import de.mdelab.mlsdm.mlindices.IndexNotificationType;
import de.mdelab.mlsdm.mlindices.MlindicesFactory;
import de.mdelab.mlsdm.mlindices.MlindicesPackage;
import de.mdelab.mlsdm.mlindices.NotifyingIndex;
import de.mdelab.mlstorypatterns.StoryPattern;
import de.mdelab.sdm.icl.iCL.ICLConstraint;
import de.mdelab.sdm.icl.iCL.ICLExpression;
import de.mdelab.sdm.icl.iCL.ICLParameter;
import de.mdelab.sdm.icl.iCL.ICLVariable;
import de.mdelab.sdm.interpreter.core.SDMException;
import de.mdelab.expressions.interpreter.core.variables.Variable;
import de.mdelab.sdm.interpreter.icl.ICLExpressionInterpreter;

public class SDMQueryManager extends IncrementalQueryManager implements IndexNotificationReceiver {

	private MLSDMMetadataIndex metadataIndex;
	
	protected Map<StoryPattern, SDMQuery> registeredQueries;
	private ICLExpressionInterpreter<MLExpression> iclInterpreter;
	private Map<MLExpression, ICLExpression> iclExpressions;
	
	public SDMQueryManager() {
		this(new SDMLogger() {

			@Override
			public void print(String s) {
				
			}
			
		});
	}

	public SDMQueryManager(SDMLogger logger) {
		this.metadataIndex = new MLSDMMetadataIndex();
		this.registeredQueries = new HashMap<StoryPattern, SDMQuery>();
		this.notificationReceivers = new ArrayList<QueryManagerNotificationReceiver>();
		this.iclInterpreter = new ICLExpressionInterpreter<MLExpression>();
		this.iclExpressions = new HashMap<MLExpression, ICLExpression>();
	}
	
	public MLSDMMetadataIndex getMetadataIndex() {
		return metadataIndex;
	}

	@Override
	public void registerEObjects(Collection<EObject> eObjects) {
		super.registerEObjects(eObjects);
		metadataIndex.registerEObjects(eObjects);
	}
	
	@Override
	public void removeEObject(EObject eObject) {
		super.removeEObject(eObject);
		metadataIndex.removeEObject(eObject);
	}
	
	public boolean isRegistered(StoryPattern storyPattern) {
		return registeredQueries.containsKey(storyPattern);
	}

	public Iterator<? extends Map<String, Object>> getMatches(StoryPattern storyPattern) throws SDMException {
		SDMQuery query = registeredQueries.get(storyPattern);
		if(query == null) {
			throw new SDMException("query not registered: " + storyPattern.getName());
		}
		return query.getMatches();
	}
	
	public SDMPatternQuery createPatternQuery(StoryPattern storyPattern, Map<String, StoryPattern> dependencies, Collection<Variable<EClassifier>> inputParameters) throws SDMException, IOException {
		FlushStatus previousFlushing = flushing;
		flushing = FlushStatus.IGNORE;
		for(Entry<String, StoryPattern> entry:dependencies.entrySet()) {
			if(!registeredQueries.containsKey(entry.getValue())) {
				throw new SDMException("required query not registered: " + storyPattern.getName());
			}
		}
		
		Collection<Variable<EClassifier>> allInputParameters = new ArrayList<Variable<EClassifier>>(inputParameters);
		Map<NotifyingIndex, SDMIndexFragment> dependencyIndexFragments = new HashMap<NotifyingIndex, SDMIndexFragment>();
		
		for(MLExpression expression:storyPattern.getConstraints()) {
			if(expression.getExpressionLanguage().equals("ICL")) {
				ICLExpression ast = getAST(expression);
				StoryPattern dependencyPattern = dependencies.get(ast.getIndexID());
				if(dependencyPattern == null) {
					continue;
				}
				
				SDMQuery dependency = registeredQueries.get(dependencyPattern);
				NotifyingIndex index = MlindicesFactory.eINSTANCE.createStagedHashIndex();
				
				Map<String, Integer> positions = new HashMap<String, Integer>();
				int position = 0;
				for(ICLParameter parameter:((ICLConstraint) ast).getParameters().getList()) {
					if(!(parameter instanceof ICLVariable)) {
						throw new SDMException("invalid index parameter: " + parameter);
					}
					else {
						positions.put(((ICLVariable) parameter).getName(), position);
						position++;
					}
				}
				
				SDMInterfaceIndex interfaceIndex = new SDMInterfaceIndex(index, positions, ((ICLConstraint) ast).getParameters().getList().size());
				dependency.registerInterfaceIndex(interfaceIndex);
				interfaceIndex.registerNotificationReceiver(this);
				
				dependencyIndexFragments.put(index, new SDMIndexFragment((ICLConstraint) ast));
				allInputParameters.add(new Variable<EClassifier>(ast.getIndexID(), MlindicesPackage.Literals.INDEX, index));
			}
		}
		
		SDMPatternQuery query = createQuery(storyPattern, dependencyIndexFragments, allInputParameters);
		for(Entry<String, StoryPattern> entry:dependencies.entrySet()) {
			query.registerDependency(registeredQueries.get(entry.getValue()));
		}
		
		registeredQueries.put(storyPattern, query);
		addChangeListener(query);
		query.findInitialMatches();
		flushing = previousFlushing;
		return query;
	}
	
	protected SDMPatternQuery createQuery(StoryPattern storyPattern, Map<NotifyingIndex, SDMIndexFragment> dependencyIndexFragments, Collection<Variable<EClassifier>> allInputParameters) throws SDMException {
		return new SDMPatternQuery(this, storyPattern, dependencyIndexFragments, allInputParameters, logger);
	}

	protected ICLExpression getAST(MLExpression expression) throws IOException {
		if(iclExpressions.containsKey(expression)) {
			return iclExpressions.get(expression);
		}
		else {
			ICLExpression ast = iclInterpreter.parseExpression(((MLStringExpression) expression).getExpressionString());
			iclExpressions.put(expression, ast);
			return ast;
		}
	}
	
	@Override
	public void notifyIndexChanged(IndexChangeNotification notification) {
		SDMChangeEvent change =	new SDMIndexChange(notification.getIndex(),
									notification.getEntry(),
									notification.getType() == IndexNotificationType.ADD ? SDMChangeEnum.CREATE : SDMChangeEnum.DELETE);
		
		registerChange(change);
	}

}
