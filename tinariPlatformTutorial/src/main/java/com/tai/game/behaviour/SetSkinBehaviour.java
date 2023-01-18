package com.tai.game.behaviour;

import java.util.List;
import java.util.Random;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tai.game.model.GameModel;

public class SetSkinBehaviour {
	
	private static Log LOG = LogFactory.getLog(SetSkinBehaviour.class);
	
	public final List<String> operatorSkins = List.of("Tier 1", "Navalized", "Night Operations", "Mamba", "Apologist");
	public final List<String> weaponSkins = List.of("Obsidian", "Red Notice", "Carbon", "Tier 1", "Factory");
	
	private NodeService nodeService;
	private PolicyComponent policyComponent;
	
	private Behaviour onAddSkinAspect;
	
	
	public void init() {
		// Create behaviours
		this.onAddSkinAspect = new JavaBehaviour(this, "onAddSkinAspect", NotificationFrequency.FIRST_EVENT);
		
		// Bind behaviours to node policies
		this.policyComponent.bindClassBehaviour(NodeServicePolicies.OnAddAspectPolicy.QNAME,
												GameModel.ASPECT_G_SKIN,
												this.onAddSkinAspect);
	}
	
	public void onAddSkinAspect(NodeRef nodeRef, QName aspectTypeQName) {
		LOG.debug("Inside the onAddSkinAspect");
		
		// Verify the "emptyness" of the old skinName property
		String oldSkinName = (String) nodeService.getProperty(nodeRef, GameModel.PROP_G_SKIN_NAME);
		
		if (oldSkinName != null && !oldSkinName.isEmpty()) {
			LOG.debug("The " + "'"+GameModel.PROP_G_SKIN_NAME.getLocalName()+"'" + " property has already been set");
			return;
		}
		
		// Get a random skinName from a specified list based on the type of the node
		Random random = new Random();
		
		String newSkinName = StringUtils.EMPTY;
		String typeLocalName = nodeService.getType(nodeRef).getLocalName();
		
		if (typeLocalName.equals("operator")) {
			newSkinName = operatorSkins.get(random.nextInt(operatorSkins.size()));
		} else if (typeLocalName.equals("weapon")) {
			newSkinName = weaponSkins.get(random.nextInt(weaponSkins.size()));
		}
		
		// Set the new skinName property
		nodeService.setProperty(nodeRef, GameModel.PROP_G_SKIN_NAME, newSkinName);
		LOG.debug("The " + "'"+GameModel.PROP_G_SKIN_NAME.getLocalName()+"'" + " property has been set to " + "'"+newSkinName+"'");
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}
	
}
