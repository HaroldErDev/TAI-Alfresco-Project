<html>
	<body>
		<h1>OPERATOR</h1>
		<div>
			<strong>Name: </strong><span>${name}</span><br>
			<strong>Nationality: </strong><span>${nationality}</span><br>
			<strong>Ability: </strong><span>${ability!"None"}</span><br>
			<strong>Blocked: </strong><span>${isBlocked?c}</span>
			<#if (isBlocked == false)>
				<br><strong>Skin: </strong><span>${skin}</span>
			</#if>
			<#if (relatedClass??)>
				<br><strong>Related Class: </strong><a href="/alfresco/s/game/class?id=${relatedClass.id}">${relatedClass.type}</a>
			<#else>
				<br><strong>Related Class: </strong><span>NONE</span>
			</#if>
		</div>
	</body>
</html>