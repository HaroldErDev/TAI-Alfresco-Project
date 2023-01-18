<html>
	<body>
		<h1>OPERATOR</h1>
		<div>
			<strong>Id: </strong><span>${id}</span><br>
			<strong>Name: </strong><span>${name}</span><br>
			<strong>Nationality: </strong><span>${nationality}</span><br>
			<strong>Ability: </strong><span>${ability!"None"}</span><br>
			<strong>Blocked: </strong><span>${isBlocked?c}</span>
			<#if (isBlocked == false)>
				<br><strong>Skin: </strong><span>${skin}</span>
			</#if>
		</div>
	</body>
</html>