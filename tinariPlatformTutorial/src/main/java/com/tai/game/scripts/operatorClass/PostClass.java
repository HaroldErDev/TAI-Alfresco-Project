package com.tai.game.scripts.operatorClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
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

public class PostClass extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(PostClass.class);
	
	private NodeService nodeService;
	private FileFolderService fileFolderService;
	private SearchService searchService;
	private FileFolderManager fileFolderManager;
	private NodeValidator nodeValidator;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the PostClass Webscript");
		
		// Init implementations
		Map<String, Object> model = new HashMap<>();
		NodeValidator.setLog(LOG);
		
		// Get all parameters from the URI query
		String type = req.getParameter("type");
		String[] relatedOperators = StringUtils.split(req.getParameter("relatedOperators"), ",");
		
		if (type == null || type.isEmpty()) {
			status.setCode(400, "Required parameters has not been provided");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Check if type parameter is a valid constraint value
		String typeUpperCase = type.toUpperCase();
		String typeNormalCase = type.charAt(0)+type.substring(1).toLowerCase();
		if (!nodeValidator.constraintValueParamIsValid(typeUpperCase, GameModel.CONS_G_OPERATOR_CLASS_LIST, status)) return model;
		
		// Get the Classes folder
		NodeRef classesFolder = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
													SearchService.LANGUAGE_FTS_ALFRESCO, 
													"TYPE:'cm:folder' AND cm:name:'Classes'").getNodeRef(0);
		
		if (classesFolder == null) {
			status.setCode(404, "There is no 'Classes' folder");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Check if a class with type passed to the URI query already exists
		if (nodeValidator.alreadyExists(classesFolder, typeNormalCase, status)) return model;
		
		// Get the Operators folder
		NodeRef operatorsFolder = fileFolderManager.findNodeByName(fileFolderManager.getDocLibNodeRef(classesFolder), "Operators");
		
		if (operatorsFolder == null) {
			status.setCode(404, "There is no 'Operators' folder");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		// Check if related operators exists in the repository and add them to the operators list
		List<NodeRef> operators = new ArrayList<>();
		
		if (relatedOperators != null) {
			for (String operatorName : relatedOperators) {
				NodeRef operatorNodeRef = fileFolderService.searchSimple(operatorsFolder, operatorName);
				
				if (operatorNodeRef == null) {
					status.setCode(404, "'"+operatorName+"'" + " operator does not exists");
					status.setRedirect(true);
					
					LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
					return model;
				}
				
				if (!operators.contains(operatorNodeRef)) operators.add(operatorNodeRef);
			}
		}
		
		// Create a new node of type operatorClass and set all properties
		NodeRef newClass = fileFolderService.create(classesFolder, typeNormalCase, GameModel.TYPE_G_OPERATOR_CLASS).getNodeRef();
		LOG.debug("Created new NodeRef: " + newClass);
		
		Map<QName, Serializable> properties = nodeService.getProperties(newClass);
		properties.put(GameModel.PROP_G_CLASS_TYPE, typeUpperCase);
		
		nodeService.setProperties(newClass, properties);
		nodeService.setAssociations(newClass, GameModel.ASSOC_G_RELATED_OPERATORS, operators);
		LOG.debug("All properties setted with success");
		
		// Fill the model
		model.put("id", newClass.getId());
		model.put("type", typeUpperCase);
		model.put("relatedOperators", (relatedOperators == null) ? Collections.EMPTY_LIST : relatedOperators);
		
		return model;
	}
	
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
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
