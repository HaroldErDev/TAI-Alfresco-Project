package com.tai.game.scripts;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.springframework.extensions.webscripts.Status;

public class NodeValidator {
	
	private static Log LOG;
	
	private NodeService nodeService;
	
	
	public static void setLog(Log logger) {
		LOG = logger;
	}
	
	public Boolean validate(NodeRef nodeRef, QName typeQName, Status status) {
		// Check if node exists in the repository
		if (!nodeService.exists(nodeRef)) {
			status.setCode(404, "Node " + "'"+nodeRef+"'" + " has not been found");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return false;
		}
		
		// Check if node type matches the specific type passed to the method
		if (!nodeService.getType(nodeRef).equals(typeQName)) {
			status.setCode(400, "The 'id' selected is not a " + typeQName.getLocalName() + " type");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return false;
		}
		
		return true;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
}
