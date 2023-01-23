{
	"Operator" : {
		"Id" : "${id}",
		"Name" : "${name}",
		"Nationality" : "${nationality}",
		"Ability" : "${ability}",
		<#if (isBlocked == false)>
			"Blocked" : "${isBlocked?c}",
			"Skin" : "${skin}"
		<#else>
			"Blocked" : "${isBlocked?c}"
		</#if>
	}
}