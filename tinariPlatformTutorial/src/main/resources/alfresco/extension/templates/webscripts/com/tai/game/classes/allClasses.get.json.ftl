{
	"Classes" : [
		<#list classes?values as class>
			{
				"Id" : "${class["id"]}",
				"Type" : "${class["type"]}",
				"Related Operators" : [
					<#list class["relatedOperators"] as operator>
						{
							"Name": "${operator.name}",
							"Link": "/alfresco/s/game/operator.json?id=${operator.id}"
						}<#if operator_has_next>,</#if>
					</#list>
				]
			}<#if class_has_next>,</#if>
		</#list>
	]
}