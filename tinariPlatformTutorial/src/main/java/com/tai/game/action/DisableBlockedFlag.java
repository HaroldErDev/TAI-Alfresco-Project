package com.tai.game.action;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;

public class DisableBlockedFlag extends SetBlockedFlagActionExecuter {
	
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		action.setParameterValue(SetBlockedFlagActionExecuter.PARAM_BLOCKED, false);
		super.executeImpl(action, actionedUponNodeRef);
	}
	
}
