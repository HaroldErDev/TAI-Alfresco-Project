{
	"Operators" : [
		<#list operators?values as operator>
			{
				"operatorName" : "${operator["g:operatorName"]}",
				"nationality" : "${operator["g:nationality"]}",
				"specialAbility" : "${operator["g:specialAbility"]}"
			}
			<#if operator_has_next>,</#if>
		</#list>
	]
}