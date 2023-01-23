<html>
	<body>
		<h1>CLASS</h1>
		<div>
			<strong>Type: </strong><span>${type}</span><br>
			<strong>Related Operators: </strong><span>[
				<#list relatedOperators as operator>
					<a href="/alfresco/s/game/operator?id=${operator.id}">${operator.name}</a><#if operator_has_next>,</#if>
				</#list>
			]</span>
		</div>
	</body>
</html>