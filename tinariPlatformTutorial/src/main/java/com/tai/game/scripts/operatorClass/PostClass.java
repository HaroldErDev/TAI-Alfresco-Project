package com.tai.game.scripts.operatorClass;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.tai.game.model.GameModel;

public class PostClass extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(PostClass.class);
	
	private FileFolderService fileFolderService;
	private SearchService searchService;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the PostClass Webscript");
		
		Map<String, Object> model = new HashMap<>();
		
		String type = req.getParameter("type");
		String[] relatedOperators = StringUtils.split(req.getParameter("relatedOperators"), ",");
		
		if (type == null || type.isEmpty()) {
			status.setCode(400, "Required properties has not been provided");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
			return model;
		}
		
		NodeRef dropzoneFolder = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
													 SearchService.LANGUAGE_FTS_ALFRESCO, 
													 "TYPE:'cm:folder' AND cm:name:'Dropzone'").getNodeRef(0);
		
		if (dropzoneFolder == null) {
			status.setCode(404, "There is no 'Dropzone' folder");
			status.setRedirect(true);
			
			LOG.error("Status Code " + status.getCode() + ": " + status.getMessage());
		} else {
			String concatProps = String.join("_", GameModel.TYPE_G_OPERATOR_CLASS.getLocalName(), type);
			for (String operatorName : relatedOperators) {
				concatProps = String.join("_", concatProps, operatorName);
			}
			NodeRef newOperator = fileFolderService.create(dropzoneFolder, concatProps, ContentModel.TYPE_CONTENT).getNodeRef();
			LOG.debug("Created new NodeRef: " + newOperator);
		}
		
		model.put("type", type.toUpperCase());
		model.put("relatedOperators", relatedOperators);
		
		return model;
	}
	
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}
	
}
