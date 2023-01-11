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

public class GetOperators extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(GetOperators.class);
	
	private NodeService nodeService;
	private SearchService searchService;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the GetOperators Webscript");
		
		Map<String, Object> model = new HashMap<>();
		Map<String, Object> operators = new HashMap<>();
		
		SearchParameters searchParams = new SearchParameters();
		searchParams.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		searchParams.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		searchParams.setQuery("TYPE:'g:operator'");
		searchParams.setMaxItems(10);
		searchParams.setSkipCount(0);
		
		ResultSet allOperators = searchService.query(searchParams);
		
		if (allOperators == null || allOperators.length() == 0) {
			status.setCode(404);
			status.setMessage("There are no operators");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
		} else {
			for (NodeRef operatorNodeRef : allOperators.getNodeRefs()) {
				Map<String, Serializable> operatorProperties = new HashMap<>();
				NodeRef parentFolder = nodeService.getPrimaryParent(operatorNodeRef).getParentRef();
				
				if (!nodeService.getProperty(parentFolder, ContentModel.PROP_NAME).equals("Errors")) {
					operatorProperties.put("g:operatorName", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_OPERATOR_NAME));
					operatorProperties.put("g:nationality", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_NATIONALITY));
					operatorProperties.put("g:specialAbility", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_SPECIAL_ABILITY));
					
					operators.put(operatorNodeRef.getId(), operatorProperties);
					LOG.debug("Added properties for node " + operatorNodeRef);
				}
			}
		}
		
		model.put("operators", operators);
		
		return model;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
}
