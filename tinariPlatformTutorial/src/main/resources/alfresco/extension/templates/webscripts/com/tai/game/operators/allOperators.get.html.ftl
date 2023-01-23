<html>
	<body>
		<h1>OPERATORS</h1>
		<#list operators?values as operator>
			<div>
				<strong>Id: </strong><span>${operator["id"]}</span><br>
				<strong>Name: </strong><span>${operator["name"]}</span><br>
				<strong>Nationality: </strong><span>${operator["nationality"]}</span><br>
				<strong>Ability: </strong><span>${operator["ability"]!"None"}</span><br>
				<strong>Blocked: </strong><span>${operator["isBlocked"]?c}</span>
				<#if (operator["isBlocked"] == false)>
					<br><strong>Skin: </strong><span>${operator["skin"]}</span>
				</#if>
				<#if (operator["relatedClass"]??)>
					<br><strong>Related Class: </strong><a href="/alfresco/s/game/class?id=${operator["relatedClass"].id}">${operator["relatedClass"].type}</a>
				<#else>
					<br><strong>Related Class: </strong><span>NONE</span>
				</#if>
			</div>
			<#if operator_has_next>
				<hr color="black">
			</#if>
		</#list>
		
		<br>
		
		<#if prevPage??>
			<span>
				<a href="/alfresco/s/game/operators.html?page=${prevPage}">Previous Page</a>
				&emsp;
			</span>
		</#if>
		<#if nextPage??>
			<span>
				<a href="/alfresco/s/game/operators.html?page=${nextPage}">Next Page</a>
			</span>
		</#if>
	</body>
</html>