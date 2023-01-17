package com.tai.game.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tai.game.manager.FileFolderManager;
import com.tai.game.model.GameModel;

public class ValoriseAndMoveActionExecuter extends ActionExecuterAbstractBase {
	
	private static Log LOG = LogFactory.getLog(ValoriseAndMoveActionExecuter.class);
	
	public static final String NAME = "valorise-and-move";
	public static final String ERROR_ON = "ERROR ON ";
	public static final String EMPTY_STRING = "EMPTY STRING";
	
	//private final List<String> types = List.of("weapon", "operator", "operatorClass");
	
	protected NodeService nodeService;
	protected FileFolderService fileFolderService;
	protected DictionaryService dictionaryService;
	
	private FileFolderManager fileFolderManager;
	
	
	private List<String> typesLocalName() {
		List<String> typesLocalName = new ArrayList<>();
		
		for (QName typeQName : dictionaryService.getTypes(GameModel.MODEL_G_GAME_MODEL)) {
			typesLocalName.add(typeQName.getLocalName());
		}
		
		return typesLocalName;
	}
	
	private void addBlockedAspect(NodeRef nodeRef, Boolean isBlocked) {
		nodeService.addAspect(nodeRef, GameModel.ASPECT_G_BLOCKED, Collections.singletonMap(GameModel.PROP_G_IS_BLOCKED, isBlocked));
		LOG.debug("Added aspect " + "'"+GameModel.ASPECT_G_BLOCKED.getLocalName()+"'");
		LOG.debug("The 'isBlocked' property has been set to " + "'"+isBlocked+"'");
	}
	
	private void addSkinAspect(NodeRef nodeRef, String skinName) {
		nodeService.addAspect(nodeRef, GameModel.ASPECT_G_SKIN, Collections.singletonMap(GameModel.PROP_G_SKIN_NAME, skinName));
		LOG.debug("Added aspect " + "'"+GameModel.ASPECT_G_SKIN.getLocalName()+"'");
		LOG.debug("The 'skinName' property has been set to " + (skinName.isEmpty() ? EMPTY_STRING : "'"+skinName+"'"));
	}
	
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		// Init implementions
		FileFolderManager.setLog(LOG);
		
		NodeRef docLibFolder = fileFolderManager.getDocLibNodeRef(actionedUponNodeRef);
		
		String errorMessage = StringUtils.EMPTY;
		
		LOG.debug("Valorising properties on node " + actionedUponNodeRef);
		
		// Take the node type and the node properties from the file name
		String fullFileName = (String) nodeService.getProperty(actionedUponNodeRef, ContentModel.PROP_NAME);
		String noExtFileName = StringUtils.substringBefore(fullFileName, ".");
		String fixedFileName = noExtFileName.replaceAll("_{2,}", "_").replaceAll("^_|_$", StringUtils.EMPTY);
		
		String nodeType = StringUtils.substringBefore(fixedFileName, "_");
		String properties = StringUtils.substringAfter(fixedFileName, "_");
		String[] nodeProperties = StringUtils.split(properties, "_");
		
		LOG.debug("Node type and properties has been taken from the file name");
		
		// Verify the existence of the type
		if (!typesLocalName().contains(nodeType)) {
			errorMessage = "'"+nodeType+"'" + " is not a valid type";
			LOG.error(ERROR_ON + actionedUponNodeRef + ": " + errorMessage);
			fileFolderManager.moveToErrorsFolder(actionedUponNodeRef, errorMessage, fixedFileName);
			return;
		}
		
		// Set all node properties for the type g:weapon
		if (nodeType.equals("weapon")) {
			nodeService.setType(actionedUponNodeRef, GameModel.TYPE_G_WEAPON);
			LOG.debug("Setted type: " + "'"+nodeType+"'");
			
			if (nodeProperties.length < 5) {
				errorMessage = "not all properties are implemented";
				LOG.error(ERROR_ON + actionedUponNodeRef + ": " + errorMessage);
				fileFolderManager.moveToErrorsFolder(actionedUponNodeRef, errorMessage, fixedFileName);
				return;
			}
			
			String weaponName = nodeProperties[0];
			String weaponType = nodeProperties[1];
			String totalAmmo = nodeProperties[2];
			String fireMode = nodeProperties[3].toUpperCase();
			
			String blocked = nodeProperties[4];
			if (!blocked.equalsIgnoreCase("true") && !blocked.equalsIgnoreCase("false")) {
				errorMessage = "the blocked property is neither true or false. Setted value: " + blocked;
				LOG.error(ERROR_ON + actionedUponNodeRef + ": " + errorMessage);
				fileFolderManager.moveToErrorsFolder(actionedUponNodeRef, errorMessage, fixedFileName);
				return;
			}
			Boolean isBlocked = Boolean.parseBoolean(blocked);
			
			String skinName = StringUtils.EMPTY;
			if (nodeProperties.length == 6) {
				skinName = nodeProperties[5];
			}
			
			nodeService.setProperty(actionedUponNodeRef, GameModel.PROP_G_WEAPON_NAME, weaponName);
			LOG.debug("Setted value " + "'"+weaponName+"'" + " on property " + "'"+GameModel.PROP_G_WEAPON_NAME.getLocalName()+"'");
			nodeService.setProperty(actionedUponNodeRef, GameModel.PROP_G_WEAPON_TYPE, weaponType);
			LOG.debug("Setted value " + "'"+weaponType+"'" + " on property " + "'"+GameModel.PROP_G_WEAPON_TYPE.getLocalName()+"'");
			nodeService.setProperty(actionedUponNodeRef, GameModel.PROP_G_TOTAL_AMMO, totalAmmo);
			LOG.debug("Setted value " + "'"+totalAmmo+"'" + " on property " + "'"+GameModel.PROP_G_TOTAL_AMMO.getLocalName()+"'");
			
			Constraint fireModeList = dictionaryService.getConstraint(GameModel.CONS_G_FIRE_MODE_LIST).getConstraint();
			try {
				fireModeList.evaluate(fireMode);
			} catch (ConstraintException e) {
				errorMessage = e.getMessage() + " | " + fireModeList.getParameters().entrySet().toArray()[0];
				LOG.error(ERROR_ON + actionedUponNodeRef + ": " + errorMessage, e);
				fileFolderManager.moveToErrorsFolder(actionedUponNodeRef, errorMessage, fixedFileName);
				return;
			}
			nodeService.setProperty(actionedUponNodeRef, GameModel.PROP_G_FIRE_MODE, fireMode);
			LOG.debug("Setted value " + "'"+fireMode+"'" + " on property " + "'"+GameModel.PROP_G_FIRE_MODE.getLocalName()+"'");
			
			addBlockedAspect(actionedUponNodeRef, isBlocked);
			if (!isBlocked) {
				addSkinAspect(actionedUponNodeRef, skinName);
			}
			
			// Move the node to the specified folder
			fileFolderManager.move(actionedUponNodeRef, "Weapons", fixedFileName, weaponName);
		}
		
		// Set all node properties for the type g:operator
		if (nodeType.equals("operator")) {
			nodeService.setType(actionedUponNodeRef, GameModel.TYPE_G_OPERATOR);
			LOG.debug("Setted type: " + "'"+nodeType+"'");
			
			if (nodeProperties.length < 4) {
				errorMessage = "not all properties are implemented";
				LOG.error(ERROR_ON + actionedUponNodeRef + ": " + errorMessage);
				fileFolderManager.moveToErrorsFolder(actionedUponNodeRef, errorMessage, fixedFileName);
				return;
			}
			
			String operatorName = nodeProperties[0];
			String nationality = nodeProperties[1];
			String spacialAbility = nodeProperties[2];
			
			String blocked = nodeProperties[3];
			if (!blocked.equalsIgnoreCase("true") && !blocked.equalsIgnoreCase("false")) {
				errorMessage = "the blocked property is neither true or false. Setted value: " + blocked;
				LOG.error(ERROR_ON + actionedUponNodeRef + ": " + errorMessage);
				fileFolderManager.moveToErrorsFolder(actionedUponNodeRef, errorMessage, fixedFileName);
				return;
			}
			Boolean isBlocked = Boolean.parseBoolean(blocked);
			
			String skinName = StringUtils.EMPTY;
			if (nodeProperties.length == 5) {
				skinName = nodeProperties[4];
			}
			
			nodeService.setProperty(actionedUponNodeRef, GameModel.PROP_G_OPERATOR_NAME, operatorName);
			LOG.debug("Setted value " + "'"+operatorName+"'" + " on property " + "'"+GameModel.PROP_G_OPERATOR_NAME.getLocalName()+"'");
			nodeService.setProperty(actionedUponNodeRef, GameModel.PROP_G_NATIONALITY, nationality);
			LOG.debug("Setted value " + "'"+nationality+"'" + " on property " + "'"+GameModel.PROP_G_NATIONALITY.getLocalName()+"'");
			nodeService.setProperty(actionedUponNodeRef, GameModel.PROP_G_SPECIAL_ABILITY, spacialAbility);
			LOG.debug("Setted value " + "'"+spacialAbility+"'" + " on property " + "'"+GameModel.PROP_G_SPECIAL_ABILITY.getLocalName()+"'");
			
			addBlockedAspect(actionedUponNodeRef, isBlocked);
			if (!isBlocked) {
				addSkinAspect(actionedUponNodeRef, skinName);
			}
			
			// Move the node to the specified folder
			fileFolderManager.move(actionedUponNodeRef, "Operators", fixedFileName, operatorName);
		}
		
		// Set all node properties for the type g:operatorClass
		if (nodeType.equals("operatorClass")) {
			nodeService.setType(actionedUponNodeRef, GameModel.TYPE_G_OPERATOR_CLASS);
			LOG.debug("Setted type: " + "'"+nodeType+"'");
			
			if (nodeProperties.length < 1) {
				errorMessage = "not all properties are implemented";
				LOG.error(ERROR_ON + actionedUponNodeRef + ": " + errorMessage);
				fileFolderManager.moveToErrorsFolder(actionedUponNodeRef, errorMessage, fixedFileName);
				return;
			}
			
			String classType = nodeProperties[0].toUpperCase();
			Map<NodeRef, String> relatedOperators = new HashMap<>();
			
			Constraint operatorClassList = dictionaryService.getConstraint(GameModel.CONS_G_OPERATOR_CLASS_LIST).getConstraint();
			try {
				operatorClassList.evaluate(classType);
			} catch (ConstraintException e) {
				errorMessage = e.getMessage() + " | " + operatorClassList.getParameters().entrySet().toArray()[0];
				LOG.error(ERROR_ON + actionedUponNodeRef + ": " + errorMessage, e);
				fileFolderManager.moveToErrorsFolder(actionedUponNodeRef, errorMessage, fixedFileName);
				return;
			}
			nodeService.setProperty(actionedUponNodeRef, GameModel.PROP_G_CLASS_TYPE, classType);
			LOG.debug("Setted value " + "'"+classType+"'" + " on property " + "'"+GameModel.PROP_G_CLASS_TYPE.getLocalName()+"'");
			
			for (int i=1; i<nodeProperties.length; i++) {
				String operatorName = nodeProperties[i];
				NodeRef operatorNodeRef = fileFolderManager.findNodeByName(docLibFolder, operatorName);
				
				if (operatorNodeRef != null) {
					List<AssociationRef> sourceAssocsRef = nodeService.getSourceAssocs(operatorNodeRef, GameModel.ASSOC_G_RELATED_OPERATORS);
					
					if (sourceAssocsRef.size() == 0) {
						relatedOperators.put(operatorNodeRef, operatorName);
					} else {
						errorMessage = "the operator " + operatorName + " is already part of another association";
						LOG.error(ERROR_ON + actionedUponNodeRef + ": " + errorMessage);
						fileFolderManager.moveToErrorsFolder(actionedUponNodeRef, errorMessage, fixedFileName);
						return;
					}
				} else {
					errorMessage = "the operator " + operatorName + " was not found";
					LOG.error(ERROR_ON + actionedUponNodeRef + ": " + errorMessage);
					fileFolderManager.moveToErrorsFolder(actionedUponNodeRef, errorMessage, fixedFileName);
					return;
				}
			}
			nodeService.setAssociations(actionedUponNodeRef, GameModel.ASSOC_G_RELATED_OPERATORS, 
										new ArrayList<>(relatedOperators.keySet()));
			LOG.debug("Setted operators " + relatedOperators.values() + " on association " 
					+ "'"+GameModel.ASSOC_G_RELATED_OPERATORS.getLocalName()+"'");
			
			// Move the node to the specified folder
			fileFolderManager.move(actionedUponNodeRef, "Classes", fixedFileName, classType.charAt(0)+classType.substring(1).toLowerCase());
		}
	}
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		// TODO Auto-generated method stub

	}
	
	public void setFileFolderManager(FileFolderManager fileFolderManager) {
		this.fileFolderManager = fileFolderManager;
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
