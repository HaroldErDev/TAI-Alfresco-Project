package com.tai.game.scripts.operatorClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
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
	private DictionaryService dictionaryService;
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
		String[] relatedOperators = StringUtils.split(req.getParameter("relatedOperators"), ",");
		
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
			if (!nodeValidator.validate(nodeRef, GameModel.TYPE_G_OPERATOR_CLASS, status)) return model;
			
			// Update the properties
			LOG.debug("Updating properties...");
			Map<QName, Serializable> classProperties = nodeService.getProperties(nodeRef);
			List<NodeRef> operators = new ArrayList<>();
			NodeRef docLibNodeRef = fileFolderManager.getDocLibNodeRef(nodeRef);
			
			if (type != null && !type.isEmpty()) {
				Constraint classList = dictionaryService.getConstraint(GameModel.CONS_G_OPERATOR_CLASS_LIST).getConstraint();
				String typeUpperCase = type.toUpperCase();
				String typeNormalCase = type.charAt(0)+type.substring(1).toLowerCase();
				
				try {
					classList.evaluate(typeUpperCase);
				} catch (ConstraintException e) {
					status.setCode(400, "'"+typeUpperCase+"'" + " is not a valid value | " + classList.getParameters().entrySet()
																													  .toArray()[0]);
					status.setRedirect(true);
					
					LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
					return model;
				}
				
				if (fileFolderManager.findNodeByName(docLibNodeRef, typeNormalCase) != null) {
					status.setCode(400, "The type " + "'"+typeNormalCase+"'" + " already exists");
					status.setRedirect(true);
					
					LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
					return model;
				}
				
				try {
					fileFolderService.rename(nodeRef, typeNormalCase);
				} catch (FileNotFoundException e) {
					LOG.error(e.getMessage(), e);
					return model;
				}
				
				classProperties.put(GameModel.PROP_G_CLASS_TYPE, typeUpperCase);
				LOG.debug("New class type: " + typeUpperCase);
			}
			if (relatedOperators != null && relatedOperators.length != 0) {
				for (AssociationRef assocRef : nodeService.getTargetAssocs(nodeRef, GameModel.ASSOC_G_RELATED_OPERATORS)) {
					operators.add(assocRef.getTargetRef());
				}
				
				for (String operatorName : relatedOperators) {
					NodeRef operatorNodeRef = fileFolderManager.findNodeByName(docLibNodeRef, operatorName);
					
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
			
			nodeService.setProperties(nodeRef, classProperties);
			nodeService.setAssociations(nodeRef, GameModel.ASSOC_G_RELATED_OPERATORS, operators);
			
			LOG.debug("All selected properties has been updated");
		}
		
		// Fill the model
		model.put("id", id);
		
		return model;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
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
