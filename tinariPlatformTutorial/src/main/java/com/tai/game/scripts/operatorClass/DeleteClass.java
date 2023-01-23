package com.tai.game.scripts.operatorClass;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.tai.game.model.GameModel;
import com.tai.game.scripts.NodeValidator;

public class DeleteClass extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(DeleteClass.class);
	
	private FileFolderService fileFolderService;
	private NodeValidator nodeValidator;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the DeleteClass Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		NodeValidator.setLog(LOG);
		
		// Get all parameters from the URI query
		String id = req.getParameter("id");
		
		// Check if the id parameter was provided
		if (id == null || id.isEmpty()) {
			status.setCode(400, "The required parameter 'id' has not been provided");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Get the node from id and delete it from repository
		LOG.debug("Getting NodeRef from id: " + id);
		NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, id);
		
		// Validate the node
		if (!nodeValidator.validate(nodeRef, GameModel.TYPE_G_OPERATOR_CLASS, status)) return model;
		
		// Delete node from repository
		fileFolderService.delete(nodeRef);
		LOG.debug("'"+nodeRef+"'" + " was successfully deleted");
		
		// Fill the model
		model.put("id", id);
		
		return model;
	}
	
	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setNodeValidator(NodeValidator nodeValidator) {
		this.nodeValidator = nodeValidator;
	}
	
}
