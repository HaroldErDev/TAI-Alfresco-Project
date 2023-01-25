package com.tai.game.scripts.operatorClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.tai.game.manager.FileFolderManager;
import com.tai.game.model.GameModel;

public class GetAllClasses extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(GetAllClasses.class);
	
	private NodeService nodeService;
	private SearchService searchService;
	private FileFolderManager fileFolderManager;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the GetAllClasses Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		Map<String, Object> classes = new HashMap<>();
		
		// Check the existence of the Classes folder and get its node reference
		NodeRef classesFolder = fileFolderManager.findNodeByName("Classes");
		
		// Check if the folder exists
		if (classesFolder == null) {
			status.setCode(404, "'Classes' folder was not found");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Find all nodes of type operatorClass inside the Classes folder
		ResultSet results = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
					   							SearchService.LANGUAGE_FTS_ALFRESCO, 
					   							"TYPE:'g:operatorClass' AND PARENT:"+"'"+classesFolder+"'");
		
		// Check if the query result is not empty
		if (results == null || results.length() == 0) {
			status.setCode(404, "There are no classes");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Get from each node all properties and add them to the model
		LOG.debug("Adding properties to the model...");
		
		for (NodeRef classNodeRef : results.getNodeRefs()) {
			Map<String, Object> classProperties = new HashMap<>();
			
			List<OperatorInfo> relatedOperators = new ArrayList<>();
			for (AssociationRef assocRef : nodeService.getTargetAssocs(classNodeRef, GameModel.ASSOC_G_RELATED_OPERATORS)) {
				NodeRef operator = assocRef.getTargetRef();
				relatedOperators.add(new OperatorInfo(operator.getId(), (String) nodeService.getProperty(operator, ContentModel.PROP_NAME)));
			}
			
			classProperties.put("id", classNodeRef.getId());
			classProperties.put("type", nodeService.getProperty(classNodeRef, GameModel.PROP_G_CLASS_TYPE));
			classProperties.put("relatedOperators", relatedOperators);
			
			classes.put(classNodeRef.getId(), classProperties);
		}
		
		LOG.debug("All properties added to the model with success");
		
		// Fill the model
		model.put("classes", classes);
		
		return model;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setFileFolderManager(FileFolderManager fileFolderManager) {
		this.fileFolderManager = fileFolderManager;
	}
	
}
