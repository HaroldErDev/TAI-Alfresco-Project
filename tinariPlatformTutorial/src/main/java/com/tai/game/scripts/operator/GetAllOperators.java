package com.tai.game.scripts.operator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.AssociationRef;
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

import com.tai.game.manager.FileFolderManager;
import com.tai.game.manager.PaginationManager;
import com.tai.game.model.GameModel;

public class GetAllOperators extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(GetAllOperators.class);
	
	public static final int MAX_ITEMS = 10;
	
	private NodeService nodeService;
	private SearchService searchService;
	private FileFolderManager fileFolderManager;
	private PaginationManager paginationManager;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the GetAllOperators Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		Map<String, Object> operators = new HashMap<>();
		
		// Check the existence of the Operators folder and get its node reference
		NodeRef operatorsFolder = fileFolderManager.findNodeByName(fileFolderManager.getDocLibNodeRefFromSite(), "Operators");
		
		// Check if the folder exists
		if (operatorsFolder == null) {
			status.setCode(404, "'Operators' folder was not found");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Get page parameter from the URI query and set the pagination
		String pageParam = req.getParameter("page");
		int page = (pageParam == null || pageParam.isEmpty()) ? 0 : Integer.parseInt(pageParam);
		
		// Find all nodes of type operator inside the Operators folder
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		sp.setQuery("TYPE:'g:operator' AND PARENT:"+"'"+operatorsFolder+"'");
		sp.setMaxItems(MAX_ITEMS);
		sp.setSkipCount(page*MAX_ITEMS);
		
		ResultSet results = searchService.query(sp);
		
		// Check if the query result is not empty
		if (results == null || results.length() == 0) {
			status.setCode(404, "There are no operators");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Get from each node all properties and add them to the model
		LOG.debug("Adding properties to the model (page "+page+")...");
		
		for (NodeRef operatorNodeRef : results.getNodeRefs()) {
			Map<String, Object> operatorProperties = new HashMap<>();
			
			List<AssociationRef> assocRefs = nodeService.getSourceAssocs(operatorNodeRef, GameModel.ASSOC_G_RELATED_OPERATORS);
			if (!assocRefs.isEmpty()) {
				NodeRef operatorClass = assocRefs.get(0).getSourceRef();
				
				operatorProperties.put("relatedClass", new ClassInfo(operatorClass.getId(), 
																	 (String) nodeService.getProperty(operatorClass, 
																			 						  GameModel.PROP_G_CLASS_TYPE)));
			}
			
			operatorProperties.put("id", operatorNodeRef.getId());
			operatorProperties.put("name", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_OPERATOR_NAME));
			operatorProperties.put("nationality", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_NATIONALITY));
			operatorProperties.put("ability", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_SPECIAL_ABILITY));
			operatorProperties.put("isBlocked", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_IS_BLOCKED));
			operatorProperties.put("skin", nodeService.getProperty(operatorNodeRef, GameModel.PROP_G_SKIN_NAME));
				
			operators.put(operatorNodeRef.getId(), operatorProperties);
		}
		
		LOG.debug("All properties added to the model with success");
		
		// Check the existence of next and prev pages and add them to the model
		if (paginationManager.hasNextPage(results)) model.put("nextPage", paginationManager.constructUriNextPage("operators.html", page));
		if (paginationManager.hasPrevPage(page)) model.put("prevPage", paginationManager.constructUriPrevPage("operators.html", page));
		
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
	
	public void setFileFolderManager(FileFolderManager fileFolderManager) {
		this.fileFolderManager = fileFolderManager;
	}

	public void setPaginationManager(PaginationManager paginationManager) {
		this.paginationManager = paginationManager;
	}
	
}