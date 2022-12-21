package de.mdelab.migmm.sample.git.delta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.Model;
import org.eclipse.modisco.java.discoverer.DiscoverJavaModelFromJavaProject;
import org.eclipse.ui.ISources;
import org.eclipse.ui.handlers.HandlerUtil;

import de.mdelab.emf.util.EMFUtil;
import de.mdelab.emf.util.delta.EAttributeChange;
import de.mdelab.emf.util.delta.EMFDelta;
import de.mdelab.emf.util.delta.EObjectCreation;
import de.mdelab.emf.util.delta.EObjectDeletion;
import de.mdelab.emf.util.delta.EReferenceCreation;
import de.mdelab.emf.util.delta.EReferenceDeletion;
import de.mdelab.migmm.history.timing.folding.InstanceMapping;
import de.mdelab.migmm.history.timing.folding.InstanceMappingStrategy;

public class MoDiscoDeltaCreator extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		TreeSelection selection = (TreeSelection) HandlerUtil.getVariable(event, ISources.ACTIVE_CURRENT_SELECTION_NAME);

		JavaProject project = (JavaProject) selection.getFirstElement();
		String projectName = project.getElementName();
		System.out.println("Creating version history");
		List<Model> versionHistory = createVersionHistory(project, "master");

		System.out.println("Creating initial model");
		Model current = versionHistory.get(0);
		long nextID = 0;

		Map<EObject, Long> ids = new LinkedHashMap<EObject, Long>();
		Map<Long, EObject> objects = new LinkedHashMap<Long, EObject>();
		for(EObject o:EMFUtil.collectReachableObjects(current)) {
			ids.put(o, nextID);
			objects.put(nextID, o);
			nextID++;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(EMFUtil.serializeToMDELabModel(ids));
		
		for(int i = 1; i < versionHistory.size(); i++) {
			System.out.println("Creating delta for commit " + (i + 1) + "/" + versionHistory.size());
			EMFDelta delta = createDelta(current, versionHistory.get(i), ids, objects, nextID);
			
			sb.append(EMFUtil.DELTA_SEPARATOR);
			sb.append("\n");
			sb.append(EMFUtil.serializeDelta(delta));
			
			delta.apply(objects, ids);
			nextID += delta.getObjectCreations().size();
		}
		
		try {
			FileWriter fw = new FileWriter(projectName + ".log");
			fw.write(sb.toString());
			System.out.println("Delta stored to " + new File(projectName).getAbsolutePath());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Delta extraction finished");
		
		return null;
	}

	private EMFDelta createDelta(Model current, Model model, Map<EObject, Long> ids, Map<Long, EObject> objects, long nextID) {
		InstanceMappingStrategy mappingStrategy = new MoDiscoInstanceMappingStrategy();
		InstanceMapping mapping = mappingStrategy.createInstanceMapping(current, model);
		Map<EObject, Long> tmpIDs = new LinkedHashMap<EObject, Long>();
		tmpIDs.putAll(ids);		
		
		EMFDelta delta = new EMFDelta();
		
		for(EObject unmappedTarget:mapping.getUnmappedTargets()) {
			delta.getObjectCreations().add(new EObjectCreation(unmappedTarget.eClass(), nextID));
			tmpIDs.put(unmappedTarget, nextID);
			for(EAttribute attribute:unmappedTarget.eClass().getEAllAttributes()) {
				delta.getAttributeChanges().add(new EAttributeChange(attribute, nextID, unmappedTarget.eGet(attribute)));
			}
			nextID++;
			
			for(Iterator<EObject> it = unmappedTarget.eAllContents(); it.hasNext();) {
				EObject unmappedChild = it.next();
				delta.getObjectCreations().add(new EObjectCreation(unmappedChild.eClass(), nextID));
				tmpIDs.put(unmappedChild, nextID);
				for(EAttribute attribute:unmappedChild.eClass().getEAllAttributes()) {
					delta.getAttributeChanges().add(new EAttributeChange(attribute, nextID, unmappedChild.eGet(attribute)));
				}
				nextID++;
			}
		}
		for(EObject unmappedTarget:mapping.getUnmappedTargets()) {
			long id = tmpIDs.get(unmappedTarget);
			for(EReference reference:unmappedTarget.eClass().getEAllReferences()) {
				Collection<EObject> targets = EMFUtil.getReferenceValues(unmappedTarget, reference);
				for(EObject target:targets) {
					EObject mappedTarget = mapping.getSource(target) != null ? mapping.getSource(target) : target;
					delta.getReferenceCreations().add(new EReferenceCreation(reference, id, tmpIDs.get(mappedTarget)));
				}
			}
			
			for(Iterator<EObject> it = unmappedTarget.eAllContents(); it.hasNext();) {
				EObject unmappedChild = it.next();
				long childID = tmpIDs.get(unmappedChild);
				for(EReference reference:unmappedChild.eClass().getEAllReferences()) {
					Collection<EObject> targets = EMFUtil.getReferenceValues(unmappedChild, reference);
					for(EObject target:targets) {
						EObject mappedTarget = mapping.getSource(target) != null ? mapping.getSource(target) : target;
						delta.getReferenceCreations().add(new EReferenceCreation(reference, childID, tmpIDs.get(mappedTarget)));
					}
				}
			}
		}

		for(EObject unmappedSource:mapping.getUnmappedSources()) {
			long id = tmpIDs.get(unmappedSource);
			delta.getObjectDeletions().add(new EObjectDeletion(id));
			for(EReference reference:unmappedSource.eClass().getEAllReferences()) {
				Collection<EObject> targets = EMFUtil.getReferenceValues(unmappedSource, reference);
				for(EObject target:targets) {
					delta.getReferenceDeletions().add(new EReferenceDeletion(reference, id, tmpIDs.get(target)));
				}
			}
		}
		
		for(Entry<EObject, EObject> entry:mapping.getMapping().entrySet()) {
			EObject mappingSource = entry.getKey();
			EObject mappingTarget = entry.getValue();
			
			long id = tmpIDs.get(mappingSource);
			
			for(EReference reference:mappingSource.eClass().getEAllReferences()) {
				Collection<EObject> sourceTargets = EMFUtil.getReferenceValues(mappingSource, reference);
				Collection<EObject> targetTargets = EMFUtil.getReferenceValues(mappingTarget, reference);
				
				for(EObject sourceTarget:sourceTargets) {
					 EObject mappedSourceTarget = mapping.getTarget(sourceTarget);
					 if(mappedSourceTarget == null || !targetTargets.contains(mappedSourceTarget)) {
						 long sourceID = tmpIDs.get(sourceTarget);
						 delta.getReferenceDeletions().add(new EReferenceDeletion(reference, id, sourceID));
					 }
				}
				
				for(EObject targetTarget:targetTargets) {
					 EObject mappedTargetTarget = mapping.getSource(targetTarget);
					 if(mappedTargetTarget == null || !sourceTargets.contains(targetTarget)) {
						 long targetID = mappedTargetTarget == null ? tmpIDs.get(targetTarget) : tmpIDs.get(mappedTargetTarget);
						 delta.getReferenceCreations().add(new EReferenceCreation(reference, id, targetID));
					 }
				}
			}
			
			for(EAttribute attribute:mappingSource.eClass().getEAllAttributes()) {
				if(!equals(mappingSource.eGet(attribute), mappingTarget.eGet(attribute))) {
					delta.getAttributeChanges().add(new EAttributeChange(attribute, id, mappingTarget.eGet(attribute)));
				}
			}
		}
		
		return delta;
	}

	private boolean equals(Object o1, Object o2) {
		if(o1 == null && o2 == null) {
			return true;
		}
		else if(o1 != null) {
			return o1.equals(o2);
		}
		else {
			return false;
		}
	}

	private List<Model> createVersionHistory(JavaProject project, String branchName) {
		Git git = null;
		try {
			Repository repo = new FileRepositoryBuilder().setGitDir(findRepoDir(project)).build();
	
			git = new Git(repo);
			
			git.checkout().setName(branchName).call();
			Deque<RevCommit> commits = new LinkedList<RevCommit>();
			for(RevCommit commit:git.log().call()) {
				commits.addFirst(commit);
			}
			
			List<Model> history = new LinkedList<Model>();
			
			int processedCommits = 0;
			for(RevCommit commit:commits) {
				System.out.println("Extracting model for commit " + (processedCommits + 1) + "/" + commits.size());
				git.checkout().setName(commit.getName()).setForced(true).call();
				
				Model model = discoverModel(project);
				history.add(model);
				processedCommits++;
			}
			return history;
		} catch (IOException | DiscoveryException | GitAPIException e1) {
			System.out.println(e1.getMessage());
		} finally {
			if(git != null) {
				git.close();
			}
		}	
		return null;
	}
	
	private File findRepoDir(JavaProject project) {
		String projectPath = project.getPath().toString();
		String location = ResourcesPlugin.getWorkspace().getRoot().findMember(projectPath).getLocation().toString();
		File f = new File(location + "/.git");
		if(f.exists()) {
			return f;
		}
		else {
			return new File(new File(location).getParent() + "/.git");
		}
	}

	private Model discoverModel(JavaProject project) throws DiscoveryException {
		DiscoverJavaModelFromJavaProject discoverer = new DiscoverJavaModelFromJavaProject();
		discoverer.discoverElement(project, new NullProgressMonitor());
		Resource r = discoverer.getTargetModel();
		return (Model) r.getContents().get(0);
	}

}
