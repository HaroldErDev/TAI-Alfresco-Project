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
			"prev" : "${prevPage.json}",
			"next" : "${nextPage.json}"
		<#else>
			<#if prevPage??>
				"prev" : "${prevPage.json}"
			</#if>
			<#if nextPage??>
				"next" : "${nextPage.json}"
			</#if>
		</#if>
	}
	
	"Links" : {
		
	}
}