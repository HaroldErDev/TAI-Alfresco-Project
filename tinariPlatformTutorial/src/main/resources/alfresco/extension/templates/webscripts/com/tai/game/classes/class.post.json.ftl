{
	"Class" : {
		"Id" : "${id}",
		"Type" : "${type}",
		"Related Operators" : [
			<#list relatedOperators as operatorName>
				"${operatorName}"<#if operatorName_has_next>,</#if>
			</#list>
		]
	}
}