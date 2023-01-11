{
	"Classes" : [
		<#list classes?values as class>
			{
				"classType" : "${class["g:classType"]}"
			}
			<#if class_has_next>,</#if>
		</#list>
	]
}