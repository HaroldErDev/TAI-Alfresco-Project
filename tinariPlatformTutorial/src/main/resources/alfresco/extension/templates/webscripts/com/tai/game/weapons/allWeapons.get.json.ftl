<#assign uri="/alfresco/s/game/weapons.json">
{
	"Weapons" : [
		<#list weapons?values as weapon>
			{
				"Id" : "${weapon["id"]}",
				"Name" : "${weapon["name"]}",
				"Type" : "${weapon["type"]}",
				"Ammo" : "${weapon["ammo"]}",
				"Fire Mode" : "${weapon["fireMode"]}",
				<#if (weapon["isBlocked"] == false)>
					"Blocked" : "${weapon["isBlocked"]?c}",
					"Skin" : "${weapon["skin"]}"
				<#else>
					"Blocked" : "${weapon["isBlocked"]?c}"
				</#if>
			}<#if weapon_has_next>,</#if>
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