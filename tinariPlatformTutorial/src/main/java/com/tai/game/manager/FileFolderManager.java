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
	
	/*public NodeRef getDocLibNodeRef(NodeRef nodeToStartSearch) {
		while (!fileFolderService.getFileInfo(nodeToStartSearch).getName().equals("documentLibrary") && nodeToStartSearch != null) {
			nodeToStartSearch = nodeService.getPrimaryParent(nodeToStartSearch).getParentRef();
		}
		
		return nodeToStartSearch;
	}*/
	
	/**
     * Gets the node reference of the "documentLibrary" folder from "Tutorial-Tinari" site.
     *
     * @return  noderef of "documentLibrary" folder
     */
	public NodeRef getDocLibNodeRef() {
		return siteService.getContainer("Tutorial-Tinari", SiteService.DOCUMENT_LIBRARY);
	}
	
	/**
     * Finds the node reference by its name.
     *
     * @param rootFolder  root node to start the search
     * @param nodeName  name of the node to find
     * @return  noderef of the node with "nodeName" or null if the node cannot be found
     */
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
	
	/**
     * Finds the node reference by its name, starting the search from the "documentLibrary" folder.
     * 
     * @param nodeName  name of the node to find
     * @return  noderef of the node with "nodeName" or null if the node cannot be found
     */
	public NodeRef findNodeByName(String nodeName) {
		return findNodeByName(getDocLibNodeRef(), nodeName);
	}
	
	/**
     * Moves the node to a specified folder with a new name, carrying out appropriate checks. If the destination folder
     * cannot be found, than it will be created inside the "Game" folder. If the node to move already exists in the
     * destination folder, than it will be moved to the "Errors" folder.
     * <p>
     * <b>Warning:</b> need to be {@link com.tai.game.manager.FileFolderManager#setLog(logger) set Log}
     * 
     * @param actionedUponNodeRef  node to move
     * @param destinationFolderName  name of the destination folder
     * @param originalFileName  the original node name
     * @param newFileName  the new name of the node
     */
	public void move(NodeRef actionedUponNodeRef, String destinationFolderName, String originalFileName, String newFileName) {
		NodeRef destinationFolder = findNodeByName(destinationFolderName);
		
		if (destinationFolder == null) {
			String gameFolderName = "Game";
			NodeRef gameFolder = findNodeByName(gameFolderName);
			
			if (gameFolder == null) {
				gameFolder = fileFolderService.create(getDocLibNodeRef(), gameFolderName, ContentModel.TYPE_FOLDER).getNodeRef();
				LOG.debug("the folder " + gameFolderName + " has been created inside the documentLibrary folder");
			}
			
			destinationFolder = fileFolderService.create(gameFolder, destinationFolderName, ContentModel.TYPE_FOLDER).getNodeRef();
			LOG.debug("the folder " + destinationFolderName + " has been created inside the " + gameFolderName + " folder");
		}
		
		if (fileFolderService.searchSimple(destinationFolder, newFileName) != null) {
			String errorMessage = "'"+newFileName+"'" + " already exists in folder " + destinationFolderName;
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
	
	/**
     * Moves the node to the "Errors" folder, carrying out appropriate checks. If the "Errors" folder cannot be found,
     * than it will be created inside the "documentLibrary" folder.
     * <p>
     * <b>Warning:</b> need to be {@link com.tai.game.manager.FileFolderManager#setLog(logger) set Log}
     * 
     * @param fileToMove  node to move
     * @param errorMessage  an explanation of the error
     * @param originalFileName  the original node name
     */
	public void moveToErrorsFolder(NodeRef fileToMove, String errorMessage, String originalFileName) {
		if (originalFileName == null || originalFileName.isEmpty()) {
			String fileName = (String) nodeService.getProperty(fileToMove, ContentModel.PROP_NAME);
			originalFileName = StringUtils.substringBefore(fileName, ".");
		}
		originalFileName += __ERROR_;
		
		String folderName = "Errors";
		NodeRef docLibFolder = getDocLibNodeRef();
		NodeRef destinationFolder = fileFolderService.searchSimple(docLibFolder, folderName);
		
		if (destinationFolder == null) {
			destinationFolder = fileFolderService.create(docLibFolder, folderName, ContentModel.TYPE_FOLDER).getNodeRef();
			LOG.debug("the folder " + folderName + " has been created inside the documentLibrary folder");
		}
		
		int count = 0;
		while (fileFolderService.searchSimple(destinationFolder, originalFileName+count) != null) {
			count += 1;
		}
		originalFileName += count;
		
		Map<QName, Serializable> errorAspectValues = new HashMap<QName, Serializable>();
		errorAspectValues.put(GameModel.PROP_G_ERROR_MESSAGE, errorMessage);
		nodeService.addAspect(fileToMove, GameModel.ASPECT_G_ERROR, errorAspectValues);
		LOG.debug("Added aspect " + "'"+GameModel.ASPECT_G_ERROR.getLocalName()+"'");
		
		try {
			fileFolderService.move(fileToMove, destinationFolder, originalFileName);
			LOG.debug(originalFileName + " was successfully moved to the " + folderName + " folder");
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
