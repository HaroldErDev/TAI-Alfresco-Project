package com.tai.game.scripts;

import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.springframework.extensions.webscripts.Status;

public class NodeValidator {
	
	private static Log LOG;
	
	private NodeService nodeService;
	private FileFolderService fileFolderService;
	private DictionaryService dictionaryService;
	
	
	public static void setLog(Log logger) {
		LOG = logger;
	}
	
	public Boolean validate(NodeRef nodeRef, QName typeQName, Status status) {
		if (!nodeService.exists(nodeRef)) {
			status.setCode(404, "Node with id " + "'"+nodeRef.getId()+"'" + " has not been found");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return false;
		}
		
		if (!nodeService.getType(nodeRef).equals(typeQName)) {
			status.setCode(400, "The 'id' selected is not a " + typeQName.getLocalName() + " type");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return false;
		}
		
		return true;
	}
	
	public Boolean alreadyExists(NodeRef parentNodeRef, String nodeName, Status status) {
		if (fileFolderService.searchSimple(parentNodeRef, nodeName) != null) {
			status.setCode(400, "A node with name " + "'"+nodeName+"'" + " already exists inside the "
						   		+ fileFolderService.getFileInfo(parentNodeRef).getName() + " folder");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return true;
		}
		
		return false;
	}
	
	public Boolean blockedParamIsValid(String blocked, Status status) {
		if (!blocked.equals("true") && !blocked.equals("false")) {
			status.setCode(400, "'isBlocked' is neither true or false");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return false;
		}
		
		return true;
	}
	
	public Boolean constraintValueParamIsValid(String value, QName constraintQName, Status status) {
		Constraint constraint = dictionaryService.getConstraint(constraintQName).getConstraint();
		
		try {
			constraint.evaluate(value);
		} catch (ConstraintException e) {
			status.setCode(400, "'"+value+"'" + " is not a valid value | " + constraint.getParameters().entrySet().toArray()[0]);
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return false;
		}
		
		return true;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
	
}
