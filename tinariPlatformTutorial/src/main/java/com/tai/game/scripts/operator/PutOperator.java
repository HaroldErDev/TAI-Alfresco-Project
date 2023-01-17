package com.tai.game.scripts.operator;

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

public class PutOperator extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(PutOperator.class);
	
	private NodeService nodeService;
	private FileFolderService fileFolderService;
	private FileFolderManager fileFolderManager;
	private NodeValidator nodeValidator;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the PutOperator Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		FileFolderManager.setLog(LOG);
		NodeValidator.setLog(LOG);
		
		// Get all parameters from the URI query
		String id = req.getParameter("id");
		String name = req.getParameter("name");
		String nationality = req.getParameter("nationality");
		String ability = req.getParameter("ability");
		
		// Check if the id parameter was passed to the URI query
		if (id == null || id.isEmpty()) {
			status.setCode(400, "The required parameter 'id' has not been provided");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
		} else {
			// Get the node from id and update its properties
			LOG.debug("Getting NodeRef from id: " + id);
			NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, id);
			
			// Validate the node
			if (!nodeValidator.validate(nodeRef, GameModel.TYPE_G_OPERATOR, status)) return model;
			
			// Update the properties
			LOG.debug("Updating properties...");
			Map<QName, Serializable> operatorProperties = nodeService.getProperties(nodeRef);
			NodeRef docLibNodeRef = fileFolderManager.getDocLibNodeRef(nodeRef);
			
			if (name != null && !name.isEmpty()) {
				if (fileFolderManager.findNodeByName(docLibNodeRef, name) != null) {
					status.setCode(400, "An operator with name " + "'"+name+"'" + " already exists");
					status.setRedirect(true);
					
					LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
					return model;
				}
				
				try {
					fileFolderService.rename(nodeRef, name);
				} catch (FileNotFoundException e) {
					LOG.error(e.getMessage(), e);
					return model;
				}
				
				operatorProperties.put(GameModel.PROP_G_OPERATOR_NAME, name);
				LOG.debug("New operator name: " + name);
			}
			if (nationality != null && !nationality.isEmpty()) {
				operatorProperties.put(GameModel.PROP_G_NATIONALITY, nationality);
				LOG.debug("New operator nationality: " + nationality);
			}
			if (ability != null && !ability.isEmpty()) {
				operatorProperties.put(GameModel.PROP_G_SPECIAL_ABILITY, ability);
				LOG.debug("New operator ability: " + ability);
			}
			
			nodeService.setProperties(nodeRef, operatorProperties);
			
			LOG.debug("All selected properties has been updated");
		}
		
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
