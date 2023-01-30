package com.tai.game.scripts.operatorClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
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

public class PutClass extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(PutClass.class);
	
	private NodeService nodeService;
	private FileFolderService fileFolderService;
	private FileFolderManager fileFolderManager;
	private NodeValidator nodeValidator;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the PutClass Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		FileFolderManager.setLog(LOG);
		NodeValidator.setLog(LOG);
		
		// Get all parameters from the URI query
		String id = req.getParameter("id");
		String type = req.getParameter("type");
		String fullOperatorsToAdd = req.getParameter("addOperators");
		String fullOperatorsToRemove = req.getParameter("removeOperators");
		
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
		if (!nodeValidator.validate(nodeRef, GameModel.TYPE_G_OPERATOR_CLASS, status)) return model;
		
		// Update properties
		LOG.debug("Updating properties...");
		Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
		
		if (type != null && !type.isEmpty()) {
			String typeUpperCase = type.toUpperCase();
			String typeNormalCase = type.charAt(0)+type.substring(1).toLowerCase();
			if (!nodeValidator.constraintValueParamIsValid(typeUpperCase, GameModel.CONS_G_OPERATOR_CLASS_LIST, status)) return model;
			
			NodeRef classesFolder = fileFolderManager.findNodeByName("Classes");
			
			if (classesFolder == null) {
				status.setCode(404, "There is no 'Classes' folder");
				status.setRedirect(true);
					
				LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
				return model;
			}
			
			if (nodeValidator.alreadyExists(classesFolder, typeNormalCase, status)) return model;
			
			properties.put(GameModel.PROP_G_CLASS_TYPE, typeUpperCase);
			LOG.debug("New class type: " + typeUpperCase);
		}
		
		// Get all operators in the association
		List<NodeRef> operators = new ArrayList<>();
		for (AssociationRef assocRef : nodeService.getTargetAssocs(nodeRef, GameModel.ASSOC_G_RELATED_OPERATORS)) {
			operators.add(assocRef.getTargetRef());
		}
		
		// Remove from the association the operators passed to the URI query
		if (fullOperatorsToRemove != null && !fullOperatorsToRemove.isEmpty()) {
			String fixedOperatorsToRemove = fullOperatorsToRemove.replaceAll(",{2,}", ",").replaceAll("^,|,$", StringUtils.EMPTY);
			
			List<String> operatorsToRemove = Arrays.asList(StringUtils.split(fixedOperatorsToRemove, ","));
			Iterator<NodeRef> iterator = operators.iterator();
			
			while (iterator.hasNext()) {
				String operatorName = (String) nodeService.getProperty(iterator.next(), GameModel.PROP_G_OPERATOR_NAME);
				
				if (operatorsToRemove.contains(operatorName)) iterator.remove();
			}
		}
		
		// Add to the association the operators passed to the URI query
		if (fullOperatorsToAdd != null && !fullOperatorsToAdd.isEmpty()) {
			String fixedOperatorsToAdd = fullOperatorsToAdd.replaceAll(",{2,}", ",").replaceAll("^,|,$", StringUtils.EMPTY);
			String[] operatorsToAdd = StringUtils.split(fixedOperatorsToAdd, ",");
			
			NodeRef operatorsFolder = fileFolderManager.findNodeByName("Operators");
			
			if (operatorsFolder == null) {
				status.setCode(404, "There is no 'Operators' folder");
				status.setRedirect(true);
					
				LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
				return model;
			}
			
			for (String operatorName : operatorsToAdd) {
				NodeRef operatorNodeRef = fileFolderService.searchSimple(operatorsFolder, operatorName);
				
				if (operatorNodeRef == null) {
					status.setCode(404, "'"+operatorName+"'" + " operator does not exists");
					status.setRedirect(true);
					
					LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
					return model;
				}
				
				if (!operators.contains(operatorNodeRef)) operators.add(operatorNodeRef);
			}
			
			LOG.debug("New operators added to relatedOperators list");
		}
		
		// Set properties
		nodeService.setProperties(nodeRef, properties);
		nodeService.setAssociations(nodeRef, GameModel.ASSOC_G_RELATED_OPERATORS, operators);
		
		// Raname node if type has been changed
		if (type != null && !type.isEmpty()) {
			try {
				fileFolderService.rename(nodeRef, type.charAt(0)+type.substring(1).toLowerCase());
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

	public void setFileFolderManager(FileFolderManager fileFolderManager) {
		this.fileFolderManager = fileFolderManager;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setNodeValidator(NodeValidator nodeValidator) {
		this.nodeValidator = nodeValidator;
	}
	
}
