package com.tai.game.scripts.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.tai.game.action.SetBlockedFlagActionExecuter;
import com.tai.game.model.GameModel;

public class PutBlockedFlag extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(PutBlockedFlag.class);
	
	private NodeService nodeService;
	private ActionService actionService;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the PutBlockedFlag Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		
		// Get all parameters from the URI query
		String id = req.getParameter("id");
		String blockedParam = req.getParameter("blockedParam").toLowerCase();
		
		// Check if the id and blockedParam parameters were passed to the URI query
		if (id == null || id.isEmpty() || blockedParam == null || blockedParam.isEmpty()) {
			status.setCode(400, "Required parameters has not been provided");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
		} else if (!blockedParam.equals("true") && !blockedParam.equals("false")) {
			status.setCode(400, "'blockedParam' is neither true or false");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
		} else {
			// Get the node from id and execute the set-blocked-flag action
			LOG.debug("Getting NodeRef from id: " + id);
			NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, id);
			
			// Check if the node exists in the repository and if its node type is not the operatorClass type
			if (!nodeService.exists(nodeRef)) {
				status.setCode(404, "Node with id " + "'"+id+"'" + " has not been found");
				status.setRedirect(true);
				
				LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			} else if (nodeService.getType(nodeRef).equals(GameModel.TYPE_G_OPERATOR_CLASS)) {
				status.setCode(400, "Node with id " + "'"+id+"'" + " is not a weapon or operator type");
				status.setRedirect(true);
				
				LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			} else {
				// Create and execute the set-blocked-flag action
				Map<String, Serializable> actionParams = new HashMap<>();
				actionParams.put(SetBlockedFlagActionExecuter.PARAM_BLOCKED, Boolean.parseBoolean(blockedParam));
				
				Action action = actionService.createAction(SetBlockedFlagActionExecuter.NAME, actionParams);
					
				if (action == null) {
					status.setCode(404, "Could not create " + SetBlockedFlagActionExecuter.NAME + " action");
					status.setRedirect(true);
					
					LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
				} else {
					actionService.executeAction(action, nodeRef);
					LOG.debug(SetBlockedFlagActionExecuter.NAME + " action executed with success");
				}
			}
		}
		
		// Fill the model
		model.put("blockedParam", blockedParam);
		model.put("id", id);
		
		return model;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setActionService(ActionService actionService) {
		this.actionService = actionService;
	}
	
}
