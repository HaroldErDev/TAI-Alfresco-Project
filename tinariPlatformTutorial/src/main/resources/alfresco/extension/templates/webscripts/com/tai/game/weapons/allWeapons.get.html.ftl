<html>
	<body>
		<h1>WEAPONS</h1>
		<#list weapons?values as weapon>
			<div>
				<strong>Id: </strong><span>${weapon["id"]}</span><br>
				<strong>Name: </strong><span>${weapon["name"]}</span><br>
				<strong>Type: </strong><span>${weapon["type"]}</span><br>
				<strong>Ammo: </strong><span>${weapon["ammo"]}</span><br>
				<strong>Fire Mode: </strong><span>${weapon["fireMode"]}</span><br>
				<strong>Blocked: </strong><span>${weapon["isBlocked"]?c}</span>
				<#if (weapon["isBlocked"] == false)>
					<br><strong>Skin: </strong><span>${weapon["skin"]}</span>
				</#if>
			</div>
			<#if weapon_has_next>
				<hr color="black">
			</#if>
		</#list>
		
		<br>
		
		<#if prevPage??>
			<span>
				<a href="${prevPage}">Previous Page</a>
				&emsp;
			</span>
		</#if>
		<#if nextPage??>
			<span>
				<a href="${nextPage}">Next Page</a>
			</span>
		</#if>
	</body>
</html>