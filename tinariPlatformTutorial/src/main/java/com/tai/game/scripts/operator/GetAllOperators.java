package com.tai.game.scripts.operator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

import com.tai.game.manager.PaginationManager;
import com.tai.game.model.GameModel;

public class GetAllOperators extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(GetAllOperators.class);
	
	private NodeService nodeService;
	private SearchService searchService;
	private PaginationManager paginationManager;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the GetAllOperators Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		Map<String, Object> operators = new HashMap<>();
		
		// Check the existence of the Operators folder and return its node reference
		NodeRef operatorsFolder = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
													  SearchService.LANGUAGE_FTS_ALFRESCO, 
													  "TYPE:'cm:folder' AND cm:name:'Operators'").getNodeRef(0);
		
		if (operatorsFolder == null) {
			status.setCode(404, "'Operators' folder was not found");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
		} else {
			// Get page parameter from the URI query and set the pagination
			String pageParam = req.getParameter("page");
			int page = (pageParam == null || pageParam.isEmpty()) ? 0 : Integer.parseInt(pageParam);
			
			SearchParameters sp = new SearchParameters();
			sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
			sp.setQuery("TYPE:'g:operator' AND PARENT:"+"'"+operatorsFolder+"'");
			sp.setMaxItems(10);
			sp.setSkipCount(page*10);
			
			// Find all nodes of type operator inside the Operators folder
			ResultSet results = searchService.query(sp);
			
			// Check if the query result is not empty
			if (results == null || results.length() == 0) {
				status.setCode(404, "There are no operators");
				status.setRedirect(true);
				
				LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			} else {
				// Get from each node all properties and add them to the model
				LOG.debug("Adding properties to the model (page " + page + ")...");
				
				for (NodeRef operatorNodeRef : results.getNodeRefs()) {
					Map<String, Serializable> operatorProperties = new HashMap<>();
					
					operatorProperties.put("id", operatorNodeRef.getId());
					operatorProperties.put("name", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_OPERATOR_NAME));
					operatorProperties.put("nationality", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_NATIONALITY));
					operatorProperties.put("ability", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_SPECIAL_ABILITY));
					operatorProperties.put("isBlocked", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_IS_BLOCKED));
					operatorProperties.put("skin", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_SKIN_NAME));
						
					operators.put(operatorNodeRef.getId(), operatorProperties);
				}
				
				LOG.debug("All properties added to the model with success");
			}
			
			// Check the existence of next and prev pages and add them to the model
			if (paginationManager.hasNextPage(results)) model.put("nextPage", paginationManager.getNextPage(page));
			if (paginationManager.hasPrevPage(page)) model.put("prevPage", paginationManager.getPrevPage(page));
		}
		
		// Fill the model
		model.put("operators", operators);
		
		return model;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setPaginationManager(PaginationManager paginationManager) {
		this.paginationManager = paginationManager;
	}
	
}