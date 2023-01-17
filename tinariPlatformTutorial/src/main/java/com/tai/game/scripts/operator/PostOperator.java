package com.tai.game.scripts.operator;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.tai.game.model.GameModel;

public class PostOperator extends DeclarativeWebScript {
	
	private static Log LOG = LogFactory.getLog(PostOperator.class);
	
	private FileFolderService fileFolderService;
	private SearchService searchService;
	
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) {
		LOG.debug("Inside the PostOperator Webscript");
		
		Map<String, Object> model = new HashMap<>();
		
		String name = req.getParameter("name");
		String nationality = req.getParameter("nationality");
		String ability = req.getParameter("ability");
		String isBlocked = req.getParameter("isBlocked");
		String skinName = req.getParameter("skinName");
		
		if (name == null || nationality == null || ability == null || isBlocked == null || 
			name.isEmpty() || nationality.isEmpty() || ability.isEmpty() || isBlocked.isEmpty()) {
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
			String concatProps = String.join("_", GameModel.TYPE_G_OPERATOR.getLocalName(), name, nationality, ability, isBlocked, skinName);
			NodeRef newOperator = fileFolderService.create(dropzoneFolder, concatProps, ContentModel.TYPE_CONTENT).getNodeRef();
			LOG.debug("Created new NodeRef: " + newOperator);
		}
		
		model.put("name", name);
		model.put("nationality", nationality);
		model.put("ability", ability);
		model.put("isBlocked", isBlocked);
		
		return model;
	}
	
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	
	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}
	
}
