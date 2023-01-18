<html>
	<body>
		<h1>WEAPON</h1>
		<div>
			<strong>Id: </strong><span>${id}</span><br>
			<strong>Name: </strong><span>${name}</span><br>
			<strong>Type: </strong><span>${type}</span><br>
			<strong>Ammo: </strong><span>${ammo}</span><br>
			<strong>Fire Mode: </strong><span>${fireMode}</span><br>
			<strong>Blocked: </strong><span>${isBlocked?c}</span>
			<#if (isBlocked == false)>
				<br><strong>Skin: </strong><span>${skin}</span>
			</#if>
		</div>
	</body>
</html>