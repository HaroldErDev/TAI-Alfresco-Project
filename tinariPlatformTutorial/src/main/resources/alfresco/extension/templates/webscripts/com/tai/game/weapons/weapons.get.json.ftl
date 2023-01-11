{
	"Weapons" : [
		<#list weapons?values as weapon>
			{
				"name" : "${weapon["g:weaponName"]}",
				"fire mode" : "${weapon["g:fireMode"]}",
				"ammo" : "${weapon["g:totalAmmo"]}",
				"type" : "${weapon["g:weaponType"]}"
			}
			<#if weapon_has_next>,</#if>
		</#list>
	]
}