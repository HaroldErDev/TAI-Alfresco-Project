package com.tai.game.manager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import com.tai.game.model.GameModel;

public class FileFolderManager {
	
	private static Log LOG;
	
	public static final String __ERROR_ = "__ERROR_";
	public static final String ERROR_ON = "ERROR ON ";
	
	protected NodeService nodeService;
	protected FileFolderService fileFolderService;
	protected SiteService siteService;
	
	
	public static void setLog(Log logger) {
		LOG = logger;
	}
	
	public NodeRef getDocLibNodeRefFromSite() {
		return siteService.getContainer("Tutorial-Tinari", SiteService.DOCUMENT_LIBRARY);
	}
	
	public NodeRef getDocLibNodeRef(NodeRef nodeToStartSearch) {
		while (!fileFolderService.getFileInfo(nodeToStartSearch).getName().equals("documentLibrary") && nodeToStartSearch != null) {
			nodeToStartSearch = nodeService.getPrimaryParent(nodeToStartSearch).getParentRef();
		}
		
		return nodeToStartSearch;
	}
	
	public NodeRef findNodeByName(NodeRef rootFolder, String nodeName) {
		if (rootFolder == null) return null;
		
		NodeRef searchedNode = fileFolderService.searchSimple(rootFolder, nodeName);
		
		if (searchedNode != null) return searchedNode;
		
		for (FileInfo folderInfo : fileFolderService.listFolders(rootFolder)) {
			NodeRef nodeFound = findNodeByName(folderInfo.getNodeRef(), nodeName);
			
			if (nodeFound != null) return nodeFound;
		}
		
		return null;
	}
	
	public void move(NodeRef actionedUponNodeRef, String destinationFolderName, String originalFileName, String newFileName) {
		NodeRef docLibFolder = getDocLibNodeRef(actionedUponNodeRef);
		NodeRef destinationFolder = findNodeByName(docLibFolder, destinationFolderName);
		
		if (destinationFolder == null) {
			String gameFolderName = "Game";
			NodeRef gameFolder = findNodeByName(docLibFolder, gameFolderName);
			
			if (gameFolder == null) {
				gameFolder = fileFolderService.create(docLibFolder, gameFolderName, ContentModel.TYPE_FOLDER).getNodeRef();
				LOG.debug("the folder " + gameFolderName + " has been created inside the documentLibrary folder");
			}
			
			destinationFolder = fileFolderService.create(gameFolder, destinationFolderName, ContentModel.TYPE_FOLDER).getNodeRef();
			LOG.debug("the folder " + destinationFolderName + " has been created inside the " + gameFolderName + " folder");
		}
		
		if (fileFolderService.searchSimple(destinationFolder, newFileName) != null) {
			String errorMessage = originalFileName + " already exists in folder " + destinationFolderName + " with name " 
								+ "'"+newFileName+"'";
			LOG.error(ERROR_ON + actionedUponNodeRef + ": " + errorMessage);
			moveToErrorsFolder(actionedUponNodeRef, errorMessage, originalFileName);
		} else {
			try {
				fileFolderService.move(actionedUponNodeRef, destinationFolder, newFileName);
				LOG.debug(newFileName + " was successfully moved to the " + destinationFolderName + " folder");
			} catch (FileNotFoundException e) {
				LOG.error(e.getMessage(), e);
				return;
			}
		}
	}
	
	public void moveToErrorsFolder(NodeRef fileToMove, String errorMessage, String newFileName) {
		if (newFileName == null || newFileName.isEmpty()) {
			String fileName = (String) nodeService.getProperty(fileToMove, ContentModel.PROP_NAME);
			newFileName = StringUtils.substringBefore(fileName, ".");
		}
		newFileName += __ERROR_;
		
		String folderName = "Errors";
		NodeRef docLibFolder = getDocLibNodeRef(fileToMove);
		NodeRef destinationFolder = fileFolderService.searchSimple(docLibFolder, folderName);
		
		if (destinationFolder == null) {
			destinationFolder = fileFolderService.create(docLibFolder, folderName, ContentModel.TYPE_FOLDER).getNodeRef();
			LOG.debug("the folder " + folderName + " has been created inside the documentLibrary folder");
		}
		
		int count = 0;
		while (fileFolderService.searchSimple(destinationFolder, newFileName+count) != null) {
			count += 1;
		}
		newFileName += count;
		
		Map<QName, Serializable> errorAspectValues = new HashMap<QName, Serializable>();
		errorAspectValues.put(GameModel.PROP_G_ERROR_MESSAGE, errorMessage);
		nodeService.addAspect(fileToMove, GameModel.ASPECT_G_ERROR, errorAspectValues);
		LOG.debug("Added aspect " + "'"+GameModel.ASPECT_G_ERROR.getLocalName()+"'");
		
		try {
			fileFolderService.move(fileToMove, destinationFolder, newFileName);
			LOG.debug(newFileName + " was successfully moved to the " + folderName + " folder");
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
			return;
		}
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
	
}
