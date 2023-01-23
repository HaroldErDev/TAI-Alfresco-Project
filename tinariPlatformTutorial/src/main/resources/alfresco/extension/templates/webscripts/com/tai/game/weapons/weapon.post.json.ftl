{
	"Weapon" : {
		"Id" : "${id}",
		"Name" : "${name}",
		"Type" : "${type}",
		"Ammo" : "${ammo}",
		"Fire Mode" : "${fireMode}",
		<#if (isBlocked == false)>
			"Blocked" : "${isBlocked?c}",
			"Skin" : "${skin}"
		<#else>
			"Blocked" : "${isBlocked?c}"
		</#if>
	}
}