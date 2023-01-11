package com.tai.game.scripts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.tai.game.model.GameModel;

public class GetClasses extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(GetClasses.class);
	
	private NodeService nodeService;
	private SearchService searchService;
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the GetClasses Webscript");
		
		Map<String, Object> model = new HashMap<>();
		Map<String, Object> classes = new HashMap<>();
		
		SearchParameters searchParams = new SearchParameters();
		searchParams.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		searchParams.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		searchParams.setQuery("TYPE:'g:operatorClass'");
		searchParams.setMaxItems(10);
		searchParams.setSkipCount(0);
		
		ResultSet allClasses = searchService.query(searchParams);
		
		if (allClasses == null || allClasses.length() == 0) {
			status.setCode(404);
			status.setMessage("There are no classes");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
		} else {
			for (NodeRef classNodeRef : allClasses.getNodeRefs()) {
				Map<String, Serializable> classProperties = new HashMap<>();
				NodeRef parentFolder = nodeService.getPrimaryParent(classNodeRef).getParentRef();
				
				if (!nodeService.getProperty(parentFolder, ContentModel.PROP_NAME).equals("Errors")) {
					classProperties.put("g:classType", nodeService.getProperty(classNodeRef, GameModel.PROP_G_CLASS_TYPE));
				}
				
				classes.put(classNodeRef.getId(), classProperties);
				LOG.debug("Added properties for node " + classNodeRef);
			}
		}
		
		model.put("classes", classes);
		
		return model;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
}
