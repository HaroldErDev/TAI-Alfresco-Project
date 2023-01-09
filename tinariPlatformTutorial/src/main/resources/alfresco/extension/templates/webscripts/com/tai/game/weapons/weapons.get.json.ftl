{
	"weapons" : [
		<#list weapons as weapon>
			{
				"name" : "${weapon.properties.weaponName}",
				"fireMode" : "${weapon.properties.fireMode}",
				"totalAmmo" : "${weapon.properties.totalAmmo}",
				"type" : "${weapon.properties.weaponType}"
			}
			<#if weapons_has_next>,</#if>
		</#list>
	]
}