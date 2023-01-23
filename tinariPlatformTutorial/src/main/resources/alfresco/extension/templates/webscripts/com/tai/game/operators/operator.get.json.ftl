{
	"Operator" : {
		"Name" : "${name}",
		"Nationality" : "${nationality}",
		"Ability" : "${ability}",
		"Blocked" : "${isBlocked?c}",
		<#if (isBlocked == false)>
			"Skin" : "${skin}",
		</#if>
		<#if (relatedClass??)>
			"Related Class" : {
				"Type": "${relatedClass.type}",
				"Link": "/alfresco/s/game/class.json?id=${relatedClass.id}"
			}
		<#else>
			"Related Class" : "NONE"
		</#if>
	}
}