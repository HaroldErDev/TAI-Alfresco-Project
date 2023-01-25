package com.tai.game.scripts.operator;

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

public class PostOperator extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(PostOperator.class);
	
	private NodeService nodeService;
	private FileFolderService fileFolderService;
	private FileFolderManager fileFolderManager;
	private NodeValidator nodeValidator;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the PostOperator Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		NodeValidator.setLog(LOG);
		
		// Get all parameters from the URI query
		String name = req.getParameter("name");
		String nationality = req.getParameter("nationality");
		String ability = req.getParameter("ability");
		String blocked = req.getParameter("isBlocked");
		String skinName = req.getParameter("skinName");
		
		// Check if required parameters were passed to the URI query
		if (name == null || name.isEmpty() || nationality == null || nationality.isEmpty() ||
			ability == null || ability.isEmpty() || blocked == null || blocked.isEmpty()) {
			status.setCode(400, "Required parameters has not been provided");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Check if isBlocked parameter is setted correctly
		blocked = blocked.toLowerCase();
		if (!nodeValidator.blockedParamIsValid(blocked, status)) return model;
		
		// Get the Operators folder
		NodeRef operatorsFolder = fileFolderManager.findNodeByName("Operators");
		
		if (operatorsFolder == null) {
			status.setCode(404, "There is no 'Operators' folder");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Check if a operator with name passed to the URI query already exists
		if (nodeValidator.alreadyExists(operatorsFolder, name, status)) return model;
		
		// Create a new node of type operator and set all properties
		NodeRef newOperator = fileFolderService.create(operatorsFolder, name, GameModel.TYPE_G_OPERATOR).getNodeRef();
		LOG.debug("Created new NodeRef: " + newOperator);
		
		Boolean isBlocked = Boolean.parseBoolean(blocked);
		if (skinName == null) skinName = StringUtils.EMPTY;
		
		Map<QName, Serializable> properties = nodeService.getProperties(newOperator);
		properties.put(GameModel.PROP_G_OPERATOR_NAME, name);
		properties.put(GameModel.PROP_G_NATIONALITY, nationality);
		properties.put(GameModel.PROP_G_SPECIAL_ABILITY, ability);
		
		nodeService.setProperties(newOperator, properties);
		nodeService.addAspect(newOperator, GameModel.ASPECT_G_BLOCKED, Collections.singletonMap(GameModel.PROP_G_IS_BLOCKED, isBlocked));
		if (!isBlocked) {
			// skinName property will be added from behaviour
			nodeService.addAspect(newOperator, GameModel.ASPECT_G_SKIN, Collections.singletonMap(GameModel.PROP_G_SKIN_NAME, skinName));
		}
		LOG.debug("All properties setted with success");
		status.setCode(201, "The new operator with id " + newOperator.getId() + " has been successfully created");
		status.setRedirect(false);
		
		// Fill the model
		model.put("id", newOperator.getId());
		model.put("name", name);
		model.put("nationality", nationality);
		model.put("ability", ability);
		model.put("isBlocked", isBlocked);
		model.put("skin", nodeService.getProperty(newOperator, GameModel.PROP_G_SKIN_NAME));
		
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
