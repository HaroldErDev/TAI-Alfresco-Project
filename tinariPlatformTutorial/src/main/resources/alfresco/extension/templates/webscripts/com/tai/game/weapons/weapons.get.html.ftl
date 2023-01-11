<html>
	<body>
		<h1>WEAPONS</h1>
		<#list weapons?values as weapon>
			<div>
				<strong>Name: </strong><span>${weapon["g:weaponName"]}</span><br>
				<strong>Fire Mode: </strong><span>${weapon["g:fireMode"]}</span><br>
				<strong>Ammo: </strong><span>${weapon["g:totalAmmo"]}</span><br>
				<strong>Type: </strong><span>${weapon["g:weaponType"]}</span>
			</div>
			<#if weapon_has_next>
				<hr color="black">
			</#if>
		</#list>
	</body>
</html>