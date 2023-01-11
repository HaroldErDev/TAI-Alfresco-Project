<html>
	<body>
		<h1>OPERATORS</h1>
		<#list operators?values as operator>
			<div>
				<strong>Name: </strong><span>${operator["g:operatorName"]}</span><br>
				<strong>Nationality: </strong><span>${operator["g:nationality"]}</span><br>
				<strong>Spacial Ability: </strong><span>${operator["g:specialAbility"]!"None"}</span>
			</div>
			<#if operator_has_next>
				<hr color="black">
			</#if>
		</#list>
	</body>
</html>