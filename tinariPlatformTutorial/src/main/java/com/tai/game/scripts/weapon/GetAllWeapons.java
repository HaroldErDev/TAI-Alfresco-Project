package com.tai.game.scripts.weapon;

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

import com.tai.game.manager.FileFolderManager;
import com.tai.game.manager.PaginationManager;
import com.tai.game.model.GameModel;

public class GetAllWeapons extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(GetAllWeapons.class);
	
	public static final int MAX_ITEMS = 10;
	
	private NodeService nodeService;
	private SearchService searchService;
	private FileFolderManager fileFolderManager;
	private PaginationManager paginationManager;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the GetAllWeapons Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		Map<String, Object> weapons = new HashMap<>();
		
		// Check the existence of the Weapons folder and get its node reference
		NodeRef weaponsFolder = fileFolderManager.findNodeByName(fileFolderManager.getDocLibNodeRef(), "Weapons");
		
		// Check if the folder exists
		if (weaponsFolder == null) {
			status.setCode(404, "'Weapons' folder was not found");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Get page parameter from the URI query and set the pagination
		String pageParam = req.getParameter("page");
		int page = (pageParam == null || pageParam.isEmpty()) ? 0 : Integer.parseInt(pageParam);
		
		// Find all nodes of type weapon inside the Weapons folder
		SearchParameters sp = new SearchParameters();
		sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		sp.setQuery("TYPE:'g:weapon' AND PARENT:"+"'"+weaponsFolder+"'");
		sp.setMaxItems(MAX_ITEMS);
		sp.setSkipCount(page*MAX_ITEMS);
		
		ResultSet results = searchService.query(sp);
		
		// Check if the query result is not empty
		if (results == null || results.length() == 0) {
			status.setCode(404, "There are no weapons");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Get from each node all properties and add them to the model
		LOG.debug("Adding properties to the model (page "+page+")...");
		
		for (NodeRef weaponNodeRef : results.getNodeRefs()) {
			Map<String, Serializable> weaponProperties = new HashMap<>();
			
			weaponProperties.put("id", weaponNodeRef.getId());
			weaponProperties.put("name", nodeService.getProperty(weaponNodeRef, GameModel.PROP_G_WEAPON_NAME));
			weaponProperties.put("type", nodeService.getProperty(weaponNodeRef, GameModel.PROP_G_WEAPON_TYPE));
			weaponProperties.put("ammo", nodeService.getProperty(weaponNodeRef, GameModel.PROP_G_TOTAL_AMMO));
			weaponProperties.put("fireMode", nodeService.getProperty(weaponNodeRef, GameModel.PROP_G_FIRE_MODE));
			weaponProperties.put("isBlocked", nodeService.getProperty(weaponNodeRef, GameModel.PROP_G_IS_BLOCKED));
			weaponProperties.put("skin", nodeService.getProperty(weaponNodeRef, GameModel.PROP_G_SKIN_NAME));
			
			weapons.put(weaponNodeRef.getId(), weaponProperties);
		}
		
		LOG.debug("All properties added to the model with success");
		
		// Check the existence of next and prev pages and add them to the model
		if (paginationManager.hasNextPage(results)) model.put("nextPage", paginationManager.constructUriNextPage("weapons.html", page));
		if (paginationManager.hasPrevPage(page)) model.put("prevPage", paginationManager.constructUriPrevPage("weapons.html", page));
		
		// Fill the model
		model.put("weapons", weapons);
		
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
