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

public class GetWeapons extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(GetWeapons.class);
	
	private NodeService nodeService;
	private SearchService searchService;
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the GetWeapons Webscript");
		
		Map<String, Object> model = new HashMap<>();
		Map<String, Object> weapons = new HashMap<>();
		
		SearchParameters searchParams = new SearchParameters();
		searchParams.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		searchParams.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
		searchParams.setQuery("TYPE:'g:weapon'");
		searchParams.setMaxItems(10);
		searchParams.setSkipCount(0);
		
		ResultSet allWeapons = searchService.query(searchParams);
		
		if (allWeapons == null || allWeapons.length() == 0) {
			status.setCode(404);
			status.setMessage("There are no weapons");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
		} else {
			for (NodeRef weaponNodeRef : allWeapons.getNodeRefs()) {
				Map<String, Serializable> weaponProperties = new HashMap<>();
				NodeRef parentFolder = nodeService.getPrimaryParent(weaponNodeRef).getParentRef();
				
				if (!nodeService.getProperty(parentFolder, ContentModel.PROP_NAME).equals("Errors")) {
					weaponProperties.put("g:weaponName", nodeService.getProperty(weaponNodeRef, GameModel.PROP_G_WEAPON_NAME));
					weaponProperties.put("g:fireMode", nodeService.getProperty(weaponNodeRef, GameModel.PROP_G_FIRE_MODE));
					weaponProperties.put("g:totalAmmo", nodeService.getProperty(weaponNodeRef, GameModel.PROP_G_TOTAL_AMMO));
					weaponProperties.put("g:weaponType", nodeService.getProperty(weaponNodeRef, GameModel.PROP_G_WEAPON_TYPE));
				}
				
				weapons.put(weaponNodeRef.getId(), weaponProperties);
				LOG.debug("Added properties for node " + weaponNodeRef);
			}
		}
		
		model.put("weapons", weapons);
		
		return model;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
}
