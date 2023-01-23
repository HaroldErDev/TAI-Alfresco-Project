{
	"Class" : {
		"Id" : "${id}",
		"Type" : "${type}",
		"Related Operators" : [
			<#list relatedOperators as operator>
				{
					"Name": "${operator.name}",
					"Link": "/alfresco/s/game/operator.json?id=${operator.id}"
				}<#if operator_has_next>,</#if>
			</#list>
		]
	}
}