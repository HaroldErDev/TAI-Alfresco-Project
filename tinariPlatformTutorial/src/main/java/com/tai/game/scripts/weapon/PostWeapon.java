package com.tai.game.scripts.weapon;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.tai.game.manager.FileFolderManager;
import com.tai.game.model.GameModel;
import com.tai.game.scripts.NodeValidator;

public class PostWeapon extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(PostWeapon.class);
	
	private NodeService nodeService;
	private FileFolderService fileFolderService;
	private FileFolderManager fileFolderManager;
	private NodeValidator nodeValidator;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the PostWeapon Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		NodeValidator.setLog(LOG);
		
		// Get all parameters from the URI query
		String name = req.getParameter("name");
		String type = req.getParameter("type");
		String ammo = req.getParameter("ammo");
		String fireMode = req.getParameter("fireMode");
		String blocked = req.getParameter("isBlocked");
		String skinName = req.getParameter("skinName");
		
		// Check if required parameters were passed to the URI query
		if (name == null || name.isEmpty() || type == null || type.isEmpty() || ammo == null || ammo.isEmpty() ||
			fireMode == null || fireMode.isEmpty() || blocked == null || blocked.isEmpty()) {
			status.setCode(400, "Required parameters has not been provided");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Check if blocked parameter is setted correctly
		blocked = blocked.toLowerCase();
		if (!nodeValidator.blockedParamIsValid(blocked, status)) return model;
		
		// Check if fireMode parameter is a valid constraint value
		fireMode = fireMode.toUpperCase();
		if (!nodeValidator.constraintValueParamIsValid(fireMode, GameModel.CONS_G_FIRE_MODE_LIST, status)) return model;
		
		// Get the Weapons folder
		NodeRef weaponsFolder = fileFolderManager.findNodeByName("Weapons");
		
		if (weaponsFolder == null) {
			status.setCode(404, "There is no 'Weapons' folder");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Check if a weapon with name passed to the URI query already exists
		if (nodeValidator.alreadyExists(weaponsFolder, name, status)) return model;
		
		// Create a new node of type weapon and set all properties
		NodeRef newWeapon = fileFolderService.create(weaponsFolder, name, GameModel.TYPE_G_WEAPON).getNodeRef();
		LOG.debug("Created new NodeRef: " + newWeapon);
		
		Boolean isBlocked = Boolean.parseBoolean(blocked);
		if (skinName == null) skinName = StringUtils.EMPTY;
		
		Map<QName, Serializable> properties = nodeService.getProperties(newWeapon);
		properties.put(GameModel.PROP_G_WEAPON_NAME, name);
		properties.put(GameModel.PROP_G_WEAPON_TYPE, type);
		properties.put(GameModel.PROP_G_TOTAL_AMMO, ammo);
		properties.put(GameModel.PROP_G_FIRE_MODE, fireMode);
		
		nodeService.setProperties(newWeapon, properties);
		nodeService.addAspect(newWeapon, GameModel.ASPECT_G_BLOCKED, Collections.singletonMap(GameModel.PROP_G_IS_BLOCKED, isBlocked));
		if (!isBlocked) {
			// skinName property will be added from behaviour
			nodeService.addAspect(newWeapon, GameModel.ASPECT_G_SKIN, Collections.singletonMap(GameModel.PROP_G_SKIN_NAME, skinName));
		}
		LOG.debug("All properties setted with success");
		status.setCode(201, "The new weapon with id " + newWeapon.getId() + " has been successfully created");
		status.setRedirect(false);
		
		// Fill the model
		model.put("id", newWeapon.getId());
		model.put("name", name);
		model.put("type", type);
		model.put("ammo", ammo);
		model.put("fireMode", fireMode);
		model.put("isBlocked", isBlocked);
		model.put("skin", nodeService.getProperty(newWeapon, GameModel.PROP_G_SKIN_NAME));
		
		return model;
	}
	
	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setFileFolderManager(FileFolderManager fileFolderManager) {
		this.fileFolderManager = fileFolderManager;
	}

	public void setNodeValidator(NodeValidator nodeValidator) {
		this.nodeValidator = nodeValidator;
	}
	
}
