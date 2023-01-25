package com.tai.game.scripts.weapon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.tai.game.manager.FileFolderManager;
import com.tai.game.model.GameModel;
import com.tai.game.scripts.NodeValidator;

public class PutWeapon extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(PutWeapon.class);
	
	private NodeService nodeService;
	private FileFolderService fileFolderService;
	private FileFolderManager fileFolderManager;
	private NodeValidator nodeValidator;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the PutWeapon Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		FileFolderManager.setLog(LOG);
		NodeValidator.setLog(LOG);
		
		// Get all parameters from the URI query
		String id = req.getParameter("id");
		String name = req.getParameter("name");
		String type = req.getParameter("type");
		String ammo = req.getParameter("ammo");
		String fireMode = req.getParameter("fireMode");
		
		// Check if the id parameter was passed to the URI query
		if (id == null || id.isEmpty()) {
			status.setCode(400, "The required parameter 'id' has not been provided");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Get the node from id and update its properties
		LOG.debug("Getting NodeRef from id: " + id);
		NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, id);
		
		// Validate the node
		if (!nodeValidator.validate(nodeRef, GameModel.TYPE_G_WEAPON, status)) return model;
			
		// Update properties
		LOG.debug("Updating properties...");
		Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
		
		if (name != null && !name.isEmpty()) {
			NodeRef weaponsFolder = fileFolderManager.findNodeByName("Weapons");
			
			if (weaponsFolder == null) {
				status.setCode(404, "There is no 'Weapons' folder");
				status.setRedirect(true);
					
				LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
				return model;
			}
			
			if (nodeValidator.alreadyExists(weaponsFolder, name, status)) return model;
			
			properties.put(GameModel.PROP_G_WEAPON_NAME, name);
			LOG.debug("New weapon name: " + name);
		}
		
		if (type != null && !type.isEmpty()) {
			properties.put(GameModel.PROP_G_WEAPON_TYPE, type);
			LOG.debug("New weapon type: " + type);
		}
		
		if (ammo != null && !ammo.isEmpty()) {
			properties.put(GameModel.PROP_G_TOTAL_AMMO, ammo);
			LOG.debug("New weapon ammo: " + ammo);
		}
		
		if (fireMode != null && !fireMode.isEmpty()) {
			fireMode = fireMode.toUpperCase();
			if (!nodeValidator.constraintValueParamIsValid(fireMode, GameModel.CONS_G_FIRE_MODE_LIST, status)) return model;
			
			properties.put(GameModel.PROP_G_FIRE_MODE, fireMode);
			LOG.debug("New weapon fire mode: " + fireMode);
		}
		
		// Set properties
		nodeService.setProperties(nodeRef, properties);
		
		// Raname node if has been changed
		if (name != null && !name.isEmpty()) {
			try {
				fileFolderService.rename(nodeRef, name);
			} catch (FileNotFoundException e) {
				LOG.error(e.getMessage(), e);
				return model;
			}
		}
		
		LOG.debug("All selected properties has been updated");
		
		// Fill the model
		model.put("id", id);
		
		return model;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setFileFolderManager(FileFolderManager fileFolderManager) {
		this.fileFolderManager = fileFolderManager;
	}

	public void setNodeValidator(NodeValidator nodeValidator) {
		this.nodeValidator = nodeValidator;
	}
	
}
