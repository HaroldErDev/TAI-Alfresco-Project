package com.tai.game.action;

import java.util.Collections;
import java.util.List;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tai.game.model.GameModel;

public class SetBlockedFlagActionExecuter extends ActionExecuterAbstractBase {
	
	private static final Log LOG = LogFactory.getLog(SetBlockedFlagActionExecuter.class);
	
	public static final String NAME = "set-blocked-flag-action";
	public static final String PARAM_BLOCKED = "blocked";
	
	protected NodeService nodeService;
	

	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		LOG.debug("Setting blocked flag on node " + actionedUponNodeRef);
		
		Boolean oldIsBlocked = (Boolean) nodeService.getProperty(actionedUponNodeRef, GameModel.PROP_G_IS_BLOCKED);
		Boolean newIsBlocked = (Boolean) action.getParameterValue(PARAM_BLOCKED);
		Boolean hasSkinAspect = nodeService.hasAspect(actionedUponNodeRef, GameModel.ASPECT_G_SKIN);
		
		// Set default value only when is null
		if (newIsBlocked == null) {
			newIsBlocked = true;
			LOG.debug("The " + "'"+GameModel.PROP_G_IS_BLOCKED.getLocalName()+"'" + " property has been set to default value (true)");
		}
		
		// Verify the presence of blocked aspect and set the new isBlocked property
		if (!nodeService.hasAspect(actionedUponNodeRef, GameModel.ASPECT_G_BLOCKED)) {
			nodeService.addAspect(actionedUponNodeRef, GameModel.ASPECT_G_BLOCKED, 
								  Collections.singletonMap(GameModel.PROP_G_IS_BLOCKED, newIsBlocked));
			LOG.debug("Added aspect " + "'"+GameModel.ASPECT_G_BLOCKED.getLocalName()+"'");
			LOG.debug("The " + "'"+GameModel.PROP_G_IS_BLOCKED.getLocalName()+"'" + " property has been set to " + "'"+newIsBlocked+"'");
		} else if (newIsBlocked != oldIsBlocked) {
			nodeService.setProperty(actionedUponNodeRef, GameModel.PROP_G_IS_BLOCKED, newIsBlocked);
			LOG.debug("The " + "'"+GameModel.PROP_G_IS_BLOCKED.getLocalName()+"'" + " property has been set to " + "'"+newIsBlocked+"'");
		} else {
			LOG.debug("The " + "'"+GameModel.PROP_G_IS_BLOCKED.getLocalName()+"'" + " property is already set to " + "'"+oldIsBlocked+"'");
		}
		
		// Properties setted correctly
		if (newIsBlocked && !hasSkinAspect) {
			LOG.debug("The " + "'"+GameModel.ASPECT_G_SKIN.getLocalName()+"'" + " aspect is already not implemented");
			return;
		}
		
		// Replace the skin aspect if already present (the new isBlocked property is set to false)
		if (!newIsBlocked && hasSkinAspect) {
			nodeService.removeAspect(actionedUponNodeRef, GameModel.ASPECT_G_SKIN);
			LOG.debug("Removed older aspect " + "'"+GameModel.ASPECT_G_SKIN.getLocalName()+"'");
			
			nodeService.addAspect(actionedUponNodeRef, GameModel.ASPECT_G_SKIN, null); // aspect properties will be set from behaviour
			LOG.debug("Added new aspect " + "'"+GameModel.ASPECT_G_SKIN.getLocalName()+"'");
			return;
		}
		
		// Properties not setted correctly
		if (newIsBlocked && hasSkinAspect) {
			nodeService.removeAspect(actionedUponNodeRef, GameModel.ASPECT_G_SKIN);
			LOG.debug("Removed aspect " + "'"+GameModel.ASPECT_G_SKIN.getLocalName()+"'");
		} else if (!newIsBlocked && !hasSkinAspect) {
			nodeService.addAspect(actionedUponNodeRef, GameModel.ASPECT_G_SKIN, null); // aspect properties will be set from behaviour
			LOG.debug("Added aspect " + "'"+GameModel.ASPECT_G_SKIN.getLocalName()+"'");
		}
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		paramList.add(
				new ParameterDefinitionImpl(
						PARAM_BLOCKED, 							// Parameter Name
						DataTypeDefinition.BOOLEAN, 			// Parameter's Data Type
						false, 									// Parameter Is Mandatory?
						getParamDisplayLabel(PARAM_BLOCKED)));	// Parameter Label
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
}
