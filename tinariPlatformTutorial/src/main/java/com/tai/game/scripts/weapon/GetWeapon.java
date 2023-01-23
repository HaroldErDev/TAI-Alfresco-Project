package com.tai.game.scripts.weapon;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.tai.game.model.GameModel;
import com.tai.game.scripts.NodeValidator;

public class GetWeapon extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(GetWeapon.class);
	
	private NodeService nodeService;
	private NodeValidator nodeValidator;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the GetWeapon Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		NodeValidator.setLog(LOG);
		
		// Get all parameters from the URI query
		String id = req.getParameter("id");
		
		// Check if the id parameter was passed to the URI query
		if (id == null || id.isEmpty()) {
			status.setCode(400, "The required parameter 'id' has not been provided");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Get the node from id and add to the model its properties
		LOG.debug("Getting NodeRef from id: " + id);
		NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, id);
		
		// Validate the node
		if (!nodeValidator.validate(nodeRef, GameModel.TYPE_G_WEAPON, status)) return model;
		
		// Fill the model
		model.put("name", nodeService.getProperty(nodeRef, GameModel.PROP_G_WEAPON_NAME));
		model.put("type", nodeService.getProperty(nodeRef, GameModel.PROP_G_WEAPON_TYPE));
		model.put("ammo", nodeService.getProperty(nodeRef, GameModel.PROP_G_TOTAL_AMMO));
		model.put("fireMode", nodeService.getProperty(nodeRef, GameModel.PROP_G_FIRE_MODE));
		model.put("isBlocked", nodeService.getProperty(nodeRef, GameModel.PROP_G_IS_BLOCKED));
		model.put("skin", nodeService.getProperty(nodeRef, GameModel.PROP_G_SKIN_NAME));
		
		LOG.debug("All properties added to the model");
		
		return model;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setNodeValidator(NodeValidator nodeValidator) {
		this.nodeValidator = nodeValidator;
	}
	
}
