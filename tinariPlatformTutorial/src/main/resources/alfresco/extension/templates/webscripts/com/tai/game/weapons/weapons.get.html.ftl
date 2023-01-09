<html>
	<body>
		<h1>WEAPONS</h1>
		<#list weapons as weapon>
			<div>
				<srong>Name: </strong><span>${weapon.properties["g:weaponName"]}</span>
				<srong>Fire Mode: </strong><span>${weapon.properties["g:fireMode"]}</span>
				<srong>Ammo: </strong><span>${weapon.properties["g:totalAmmo"]}</span>
				<srong>Type: </strong><span>${weapon.properties["g:weaponType"]}</span>
			</div>
			<#if weapons_has_next>
				<hr color="black">
			</#if>
		</#list>
	</body>
</html>