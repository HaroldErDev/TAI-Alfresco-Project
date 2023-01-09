package com.tai.game.action;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;

public class EnableBlockedFlag extends SetBlockedFlagActionExecuter {
	
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		action.setParameterValue(SetBlockedFlagActionExecuter.PARAM_BLOCKED, true);
		super.executeImpl(action, actionedUponNodeRef);
	}
	
}
