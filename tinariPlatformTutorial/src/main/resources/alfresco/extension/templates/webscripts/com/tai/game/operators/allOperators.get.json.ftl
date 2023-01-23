<#assign uri="/alfresco/s/game/operators.json">
{
	"Operators" : [
		<#list operators?values as operator>
			{
				"Id" : "${operator["id"]}",
				"Name" : "${operator["name"]}",
				"Nationality" : "${operator["nationality"]}",
				"Ability" : "${operator["ability"]}",
				"Blocked" : "${operator["isBlocked"]?c}",
				<#if (operator["isBlocked"] == false)>
					"Skin" : "${operator["skin"]}",
				</#if>
				<#if (operator["relatedClass"]??)>
					"Related Class" : {
						"Type": "${operator["relatedClass"].type}",
						"Link": "/alfresco/s/game/class.json?id=${operator["relatedClass"].id}"
					}
				<#else>
					"Related Class" : "NONE"
				</#if>
			}<#if operator_has_next>,</#if>
		</#list>
	],
	
	"Links" : {
		<#if prevPage?? && nextPage??>
			"prev" : "${uri}?page=${prevPage}",
			"next" : "${uri}?page=${nextPage}"
		<#else>
			<#if prevPage??>
				"prev" : "${uri}?page=${prevPage}"
			</#if>
			<#if nextPage??>
				"next" : "${uri}?page=${nextPage}"
			</#if>
		</#if>
	}
}